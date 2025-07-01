package mx.com.segurossura.grouplife.infrastructure.adapter.gateway;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.com.segurossura.grouplife.application.port.MailPolicyPort;
import mx.com.segurossura.grouplife.domain.model.mailpolicy.MailPolicyRequest;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.mailpolicy.MailPolicyResponseDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailPolicyGateway implements MailPolicyPort {

    @Qualifier("mailPolicyWebClient")
    private final WebClient mailPolicyWebClient;

    @Qualifier("folioSequenceCircuitBreaker")
    private final CircuitBreaker circuitBreaker;

    @Qualifier("folioSequenceRetry")
    private final Retry retry;

    private final MailPolicyMapper mailPolicyMapper;

    @Override
    public Mono<String> sendMailPolicy(final MailPolicyRequest mailPolicyRequest) {

        return Mono.defer(() -> this.mailPolicyWebClient.post()
                .bodyValue(this.mailPolicyMapper.mailPolicyRequestToDto(mailPolicyRequest))
                .retrieve()
                .bodyToMono(MailPolicyResponseDto.class)
                .transformDeferred(RetryOperator.of(this.retry))
                .transformDeferred(CircuitBreakerOperator.of(this.circuitBreaker))
                .doOnNext(response -> log.info("Respuesta del servicio mail policy: {}", response))
                .flatMap(response -> Mono.just(response.data().message())));
    }

}
