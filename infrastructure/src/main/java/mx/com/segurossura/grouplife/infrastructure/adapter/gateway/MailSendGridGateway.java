package mx.com.segurossura.grouplife.infrastructure.adapter.gateway;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.com.segurossura.grouplife.application.port.MailSendGridPort;
import mx.com.segurossura.grouplife.domain.model.enums.Modality;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.mailsendgrid.MailSendGridRequest;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.mailsendgrid.MailSendGridResponse;
import mx.com.segurossura.grouplife.infrastructure.configuration.MailNotificationProperties;
import mx.com.segurossura.grouplife.infrastructure.configuration.MailSendGridProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailSendGridGateway implements MailSendGridPort {

    @Qualifier("mailSendGridWebClient")
    private final WebClient mailSendGridWebClient;

    @Qualifier("folioSequenceCircuitBreaker")
    private final CircuitBreaker circuitBreaker;

    @Qualifier("folioSequenceRetry")
    private final Retry retry;
    private final MailSendGridProperties mailSendGridProperties;
    private final MailNotificationProperties mailNotificationProperties;
    @Value("${agentPotal.url}")
    private String agentPortalUrl;

    @Override
    public Mono<String> sendNotificationMail(final String to, final String numberFolio, final boolean completed,
                                             final Integer paymentMethodId, final Boolean paymentLink, Modality modality) {

        Map<String, Object> dynamicData = getStringObjectMap(numberFolio, completed, paymentMethodId, paymentLink, modality);

        MailSendGridRequest mailSendGridRequest = new MailSendGridRequest(
                mailSendGridProperties.getFrom(),
                List.of(new MailSendGridRequest.MailSendGridEMailRequest(to)),
                dynamicData, mailSendGridProperties.getTemplateNotification()
        );
        log.info("Solicitud enviada al servicio de sendNotificationMail: {}", mailSendGridRequest);

        return Mono.defer(() -> this.mailSendGridWebClient.post()
                .bodyValue(mailSendGridRequest)
                .retrieve()
                .bodyToMono(MailSendGridResponse.class)
                .transformDeferred(RetryOperator.of(this.retry))
                .transformDeferred(CircuitBreakerOperator.of(this.circuitBreaker))
                .doOnNext(response -> log.info("Respuesta del servicio mail sendgrid: {}", response))
                .flatMap(response -> Mono.just(response.messages())));
    }

    private Map<String, Object> getStringObjectMap(final String numberFolio, final boolean completed,
                                                   final Integer paymentMethodId, final Boolean paymentLink, Modality modality) {
        String body = "";
        String subject = "";

        if (completed) {

            subject = mailNotificationProperties.getSubject();

            if (paymentMethodId == 3 && paymentLink) {
                body = modality.getMessageFormat().equals(Modality.TRADITIONAL.getMessageFormat()) ?
                        mailNotificationProperties.getSuccess().getBodyLinkTraditional().replace("{{FOLIO}}", numberFolio) :
                        mailNotificationProperties.getSuccess().getBodyLink().replace("{{FOLIO}}", numberFolio);
            } else if (paymentMethodId == 3) {
                body = modality.getMessageFormat().equals(Modality.TRADITIONAL.getMessageFormat()) ?
                        mailNotificationProperties.getSuccess().getBodyCardTraditional().replace("{{FOLIO}}", numberFolio) :
                        mailNotificationProperties.getSuccess().getBodyCard().replace("{{FOLIO}}", numberFolio);
            } else if (paymentMethodId == 8 && !paymentLink) {
                body = modality.getMessageFormat().equals(Modality.TRADITIONAL.getMessageFormat()) ?
                        mailNotificationProperties.getSuccess().getBodyTransferTraditional().replace("{{FOLIO}}", numberFolio) :
                        mailNotificationProperties.getSuccess().getBodyTransfer().replace("{{FOLIO}}", numberFolio);
            }

        } else {
            subject = mailNotificationProperties.getSubjectError().replace("{{FOLIO}}", numberFolio);
            body = modality.getMessageFormat().equals(Modality.TRADITIONAL.getMessageFormat()) ?
                    mailNotificationProperties.getError().getBodyTraditional().replace("{{FOLIO}}", numberFolio) :
                    mailNotificationProperties.getError().getBody().replace("{{FOLIO}}", numberFolio);
        }

        return Map.of(
                "SUBJECT", subject,
                "IMG_FONDO", completed ? mailNotificationProperties.getSuccess().getImg() : mailNotificationProperties.getError().getImg(),
                "TITULO_TEXTO", completed ? mailNotificationProperties.getSuccess().getTitle() : mailNotificationProperties.getError().getTitle(),
                "TITULO_COLOR", completed ? mailNotificationProperties.getSuccess().getTitleColor() : mailNotificationProperties.getError().getTitleColor(),
                "TEXTO", body,
                "URL_RECUPERAR", (paymentMethodId == 3 && !paymentLink) || !completed ? agentPortalUrl : ""
        );
    }

}
