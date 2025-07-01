package mx.com.segurossura.grouplife.infrastructure.adapter.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.com.segurossura.grouplife.application.port.PaymentLinkPort;
import mx.com.segurossura.grouplife.domain.model.enums.Modality;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.payment_link_dto.PaymentUrlResponseDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.payment_link_dto.request.*;
import mx.com.segurossura.grouplife.infrastructure.exception.customs.GatewayException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentLinkGateway implements PaymentLinkPort {
    private static final String PAYMENT_LINK_CLIENT = "/issue-client-portal/get";
    private static final String PAYMENT_DIRECT_LINK_CLIENT = "/charges/issue-client";
    private static final String PARTNER_ID = "COTIZADORES_SURA";
    private static final String PRODUCT_TRADICIONAL = "VidaGrupoT";
    private static final String PRODUCT_VOLUNTARIO = "VidaGrupoV";
    private static final String CHANNEL = "agente";
    private static final String PHONE_CODE_MX = "+52";

    @Qualifier("paymentUrlWebClient")
    private final WebClient paymentUrlWebClient;

    private final PaymentLinkMapper paymentLinkMapper;

    @Qualifier("folioSequenceCircuitBreaker")
    private final CircuitBreaker circuitBreaker;

    @Qualifier("folioSequenceRetry")
    private final Retry retry;

    @Override
    public Mono<PaymentLinkResponseAggregate> getPaymentLink(final FolioRecord folioRecord) {
        return Mono.defer(() -> {
            try {

                final PaymentUrlRequestDto paymentUrlRequestDto = this.createPaymentUrlRequestDto(folioRecord);
                final ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                final String serializedRequest = objectMapper.writeValueAsString(paymentUrlRequestDto);
                log.info("Solicitud enviada al servicio de getPaymentLink: {}", serializedRequest);

                return this.paymentUrlWebClient.post()
                        .uri(uriBuilder -> uriBuilder
                                .path(PAYMENT_DIRECT_LINK_CLIENT)
                                .build())
                        .bodyValue(paymentUrlRequestDto)
                        .retrieve()
                        .bodyToMono(PaymentUrlResponseDto.class)
                        .map(result -> this.paymentLinkMapper.toModel(result.data()))
                        .transformDeferred(RetryOperator.of(this.retry))
                        .transformDeferred(CircuitBreakerOperator.of(this.circuitBreaker))
                        .doOnNext(response -> log.info("Respuesta del servicio: {}", response))
                        .onErrorMap(e -> {
                            log.error("Fallo servicio de pasarela de getPaymentLink:{}", folioRecord.folio().numberFolio(), e);
                            if (e instanceof final WebClientResponseException we) {
                                return new GatewayException.GatewayClientErrorException(
                                        String.format("Error del cliente: %s, detalles: %s", we.getStatusCode(), we.getResponseBodyAsString())
                                );
                            }
                            return new GatewayException("Error inesperado en el servicio pasarela de pago.", "VG" +
                                    "-GTW-PAYMENT_LINK");
                        });
            } catch (final JsonProcessingException e) {
                return Mono.error(new GatewayException("Error serializando la solicitud de asegurados.", "VG-GTW-SERIALIZATION"));
            }
        });
    }

    private PaymentUrlRequestDto createPaymentUrlRequestDto(final FolioRecord folioRecord) {

        Policy lastPolicy = folioRecord.policy().getLast();
        boolean isToClient = lastPolicy.paymentLink();
        final String product = folioRecord.modality().equals(Modality.TRADITIONAL) ? PRODUCT_TRADICIONAL : PRODUCT_VOLUNTARIO;

        final PaymentLinkConfigurationDto configuration = new PaymentLinkConfigurationDto(PARTNER_ID, product,
                folioRecord.folio().numberFolio(), folioRecord.folio().numberFolio(), CHANNEL);
        final PaymentLinkUserRequestDto user = this.paymentLinkMapper.toDtoUser(folioRecord);

        final PaymentLinkPolicyDetailRequestDto quote = this.paymentLinkMapper.toQuoteDto(lastPolicy);
        final PaymentLinkCostsRequestDto costs = this.paymentLinkMapper.toDto(lastPolicy.costs());

        List<PaymentLinkPolicyDetailRequestDto> issue = List.of(this.paymentLinkMapper.toIssueDto(lastPolicy));

        final PaymentLinkPolicyRequestDto policy = new PaymentLinkPolicyRequestDto(lastPolicy.currency(),
                String.valueOf(lastPolicy.formPayment()),
                String.valueOf(lastPolicy.periodicity()), quote, issue, costs);

        final PaymentLinkConfigurationDto configurationJsonIssue = PaymentLinkConfigurationDto.builder()
                .partnerId(PARTNER_ID)
                .product(product)
                .folioFromPartner(folioRecord.folio().numberFolio())
                .build();
        final PaymentLinkJsonIssuePolicyRequestDto policyJsonIssue = this.paymentLinkMapper.toDto(folioRecord);
        final PaymentLinkPersonalDataRequestDto personalData = this.paymentLinkMapper.toDto(folioRecord.client().general());
        final PaymentLinkAddressRequestDto address = this.paymentLinkMapper.toDto(folioRecord.client().address());
        final PaymentLinkClientRequestDto client = new PaymentLinkClientRequestDto(personalData, address);
        final PaymentLinkFromPaymentRequestDto formPayment = new PaymentLinkFromPaymentRequestDto(
                String.valueOf(lastPolicy.periodicity()), String.valueOf(lastPolicy.formPayment())
        );
        final PaymentLinkJsonIssueRequestDto jsonIssue =
                new PaymentLinkJsonIssueRequestDto(configurationJsonIssue, policyJsonIssue, client, formPayment);

        //Info Client
        String phone = lastPolicy.paymentLinkPhoneNumber() != null ? lastPolicy.paymentLinkPhoneNumber() : client.personalData().phoneNumber();
        String formattedPhone = phone.contains("+") ? phone : PHONE_CODE_MX + phone;
        final PaymentLinkInfoClient infoClient = new PaymentLinkInfoClient(
                client.personalData().name() + " " + client.personalData().surname(),
                formattedPhone
        );

        return new PaymentUrlRequestDto(configuration, user, policy, jsonIssue, infoClient, isToClient);
    }
}
