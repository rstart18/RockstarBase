package mx.com.segurossura.grouplife.controller;

import mx.com.segurossura.grouplife.BaseIT;
import mx.com.segurossura.grouplife.controller.testdata.TestFixturesQuote;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.issue.QuoteIssueDataResponseDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.quote.PolicyResponse;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.quote.PolicyResponseDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.quote.insured.InsuredResponse;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.FolioRecordEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.insured.InsuredGroupEntity;
import mx.com.segurossura.grouplife.openapi.model.CreateClientResponseDto;
import mx.com.segurossura.grouplife.openapi.model.IssueRequestDto;
import mx.com.segurossura.grouplife.openapi.model.StandardResponseMessageDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

class IssueControllerIntegrationTest extends BaseIT {
    private static final String BASE_PATH = "/issue-request";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "pass";
    private static final String COMPANY = "/company";
    private static final String INSUREDS = "/insureds";
    private static final String CLIENT = "/client";
    private static final String QUOTE_ISSUE = "/quote-issue";

    @MockitoBean
    @Qualifier("quotationWebClient")
    private WebClient quotationWebClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private ArgumentCaptor<Function<UriBuilder, URI>> uriCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.uriCaptor = ArgumentCaptor.forClass(Function.class);
        when(this.quotationWebClient.post()).thenReturn(this.requestBodyUriSpec);
        doReturn(this.requestBodySpec)
                .when(this.requestBodyUriSpec)
                .uri(this.uriCaptor.capture());
        when(this.requestBodySpec.bodyValue(any())).thenAnswer(invocation -> this.requestBodySpec);
        when(this.requestBodySpec.retrieve()).thenReturn(this.responseSpec);
        when(this.requestHeadersUriSpec.uri(this.uriCaptor.capture())).thenReturn(this.requestHeadersSpec);
    }

    @Test
    @SuppressWarnings("unchecked")
    void test_issue_withDifferentPaths_withPolicyNotNull() {
        this.prepareMongoTestData();
        when(this.requestBodyUriSpec.uri((Function<UriBuilder, URI>) argThat(argument -> {
            if (!(argument instanceof Function)) {
                return false;
            }
            final Function<UriBuilder, URI> uriFunction = (Function<UriBuilder, URI>) argument;
            final UriBuilder uriBuilderMock = UriComponentsBuilder.fromUriString("http://localhost/8080");
            final URI uri = uriFunction.apply(uriBuilderMock);

            if (uri.getPath().contains(COMPANY)) {
                final PolicyResponse policyResponse = new PolicyResponse(new PolicyResponseDto(125666, 789999));
                when(this.responseSpec.bodyToMono(PolicyResponse.class))
                        .thenReturn(Mono.just(policyResponse));
                return true;
            } else if (uri.getPath().contains(INSUREDS)) {
                final InsuredResponse.DataResponse mockDataResponse = new InsuredResponse.DataResponse("INSERCION EXITOSA");
                final InsuredResponse mockInsuredResponse = new InsuredResponse();
                mockInsuredResponse.setData(mockDataResponse);
                when(this.responseSpec.bodyToMono(InsuredResponse.class))
                        .thenReturn(Mono.just(mockInsuredResponse));
                return true;
            } else if (uri.getPath().contains(CLIENT)) {
                String response = "OK";
                when(this.responseSpec.bodyToMono(String.class))
                        .thenReturn(Mono.just(response));
                return true;
            } else if (uri.getPath().contains(QUOTE_ISSUE)) {
                final QuoteIssueDataResponseDto.QuoteIssueResponse quoteIssueResponse = new QuoteIssueDataResponseDto.QuoteIssueResponse("97", "9", "PENDING");
                final QuoteIssueDataResponseDto data = new QuoteIssueDataResponseDto(quoteIssueResponse);
                when(this.responseSpec.bodyToMono(QuoteIssueDataResponseDto.class))
                        .thenReturn(Mono.just(data));
                return true;
            }
            return false;
        }))).thenReturn(this.requestBodySpec);

        final IssueRequestDto issueRequestDto = new IssueRequestDto();
        issueRequestDto.setNumberFolio("97");
        issueRequestDto.setPeriodicityPaymentId("12");
        issueRequestDto.setPaymentMethodId("1");
        issueRequestDto.setPaymentLink(true);

        final CreateClientResponseDto expect = new CreateClientResponseDto();
        expect.setData(new StandardResponseMessageDto("OK"));

        final CreateClientResponseDto response = this.webTestClient.post()
                .uri(BASE_PATH)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(issueRequestDto))
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(CreateClientResponseDto.class)
                .returnResult().getResponseBody();
        assertEquals(expect, response);
    }

    @Test
    @SuppressWarnings("unchecked")
    void test_issue_withDifferentPaths_withPolicyNull() {
        this.prepareMongoTestData();
        when(this.requestBodyUriSpec.uri((Function<UriBuilder, URI>) argThat(argument -> {
            if (!(argument instanceof Function)) {
                return false;
            }
            final Function<UriBuilder, URI> uriFunction = (Function<UriBuilder, URI>) argument;
            final UriBuilder uriBuilderMock = UriComponentsBuilder.fromUriString("http://localhost/8080");
            final URI uri = uriFunction.apply(uriBuilderMock);

            if (uri.getPath().contains(COMPANY)) {
                final PolicyResponse policyResponse = new PolicyResponse(new PolicyResponseDto(125666, 789998));
                when(this.responseSpec.bodyToMono(PolicyResponse.class))
                        .thenReturn(Mono.just(policyResponse));
                return true;
            } else if (uri.getPath().contains(INSUREDS)) {
                final InsuredResponse.DataResponse mockDataResponse = new InsuredResponse.DataResponse("INSERCION EXITOSA");
                final InsuredResponse mockInsuredResponse = new InsuredResponse();
                mockInsuredResponse.setData(mockDataResponse);
                when(this.responseSpec.bodyToMono(InsuredResponse.class))
                        .thenReturn(Mono.just(mockInsuredResponse));
                return true;
            } else if (uri.getPath().contains(CLIENT)) {
                String response = "OK";
                when(this.responseSpec.bodyToMono(String.class))
                        .thenReturn(Mono.just(response));
                return true;
            } else if (uri.getPath().contains(QUOTE_ISSUE)) {
                final QuoteIssueDataResponseDto.QuoteIssueResponse quoteIssueResponse = new QuoteIssueDataResponseDto.QuoteIssueResponse("98", "9", "PENDING");
                final QuoteIssueDataResponseDto data = new QuoteIssueDataResponseDto(quoteIssueResponse);
                when(this.responseSpec.bodyToMono(QuoteIssueDataResponseDto.class))
                        .thenReturn(Mono.just(data));
                return true;
            }
            return false;
        }))).thenReturn(this.requestBodySpec);

        final IssueRequestDto issueRequestDto = new IssueRequestDto();
        issueRequestDto.setNumberFolio("98");
        issueRequestDto.setPeriodicityPaymentId("12");
        issueRequestDto.setPaymentMethodId("1");
        issueRequestDto.setPaymentLink(true);

        final CreateClientResponseDto expect = new CreateClientResponseDto();
        expect.setData(new StandardResponseMessageDto("OK"));

        final CreateClientResponseDto response = this.webTestClient.post()
                .uri(BASE_PATH)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(issueRequestDto))
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(CreateClientResponseDto.class)
                .returnResult().getResponseBody();
        assertEquals(expect, response);
    }

    @Test
    @SuppressWarnings("unchecked")
    void test_issue_withDifferentPaths_withInsuredsTrue() {
        this.prepareMongoTestData();
        when(this.requestBodyUriSpec.uri((Function<UriBuilder, URI>) argThat(argument -> {
            if (!(argument instanceof Function)) {
                return false;
            }
            final Function<UriBuilder, URI> uriFunction = (Function<UriBuilder, URI>) argument;
            final UriBuilder uriBuilderMock = UriComponentsBuilder.fromUriString("http://localhost/8080");
            final URI uri = uriFunction.apply(uriBuilderMock);

            if (uri.getPath().contains(COMPANY)) {
                final PolicyResponse policyResponse = new PolicyResponse(new PolicyResponseDto(125666, 789998));
                when(this.responseSpec.bodyToMono(PolicyResponse.class))
                        .thenReturn(Mono.just(policyResponse));
                return true;
            } else if (uri.getPath().contains(INSUREDS)) {
                final InsuredResponse.DataResponse mockDataResponse = new InsuredResponse.DataResponse("INSERCION EXITOSA");
                final InsuredResponse mockInsuredResponse = new InsuredResponse();
                mockInsuredResponse.setData(mockDataResponse);
                when(this.responseSpec.bodyToMono(InsuredResponse.class))
                        .thenReturn(Mono.just(mockInsuredResponse));
                return true;
            } else if (uri.getPath().contains(CLIENT)) {
                String response = "OK";
                when(this.responseSpec.bodyToMono(String.class))
                        .thenReturn(Mono.just(response));
                return true;
            } else if (uri.getPath().contains(QUOTE_ISSUE)) {
                final QuoteIssueDataResponseDto.QuoteIssueResponse quoteIssueResponse = new QuoteIssueDataResponseDto.QuoteIssueResponse("99", "9", "PENDING");
                final QuoteIssueDataResponseDto data = new QuoteIssueDataResponseDto(quoteIssueResponse);
                when(this.responseSpec.bodyToMono(QuoteIssueDataResponseDto.class))
                        .thenReturn(Mono.just(data));
                return true;
            }
            return false;
        }))).thenReturn(this.requestBodySpec);

        final IssueRequestDto issueRequestDto = new IssueRequestDto();
        issueRequestDto.setNumberFolio("99");
        issueRequestDto.setPeriodicityPaymentId("12");
        issueRequestDto.setPaymentMethodId("1");
        issueRequestDto.setPaymentLink(true);

        final CreateClientResponseDto expect = new CreateClientResponseDto();
        expect.setData(new StandardResponseMessageDto("OK"));

        final CreateClientResponseDto response = this.webTestClient.post()
                .uri(BASE_PATH)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(issueRequestDto))
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(CreateClientResponseDto.class)
                .returnResult().getResponseBody();
        assertEquals(expect, response);
    }

    private void prepareMongoTestData() {
        final FolioRecordEntity folioRecordEntity = TestFixturesQuote.createFolioEntity();
        this.reactiveMongoTemplate.insert(folioRecordEntity)
                .doOnSuccess(result -> System.out.println("Inserted: " + result))
                .doOnError(error -> System.err.println("Error during insert: " + error.getMessage()))
                .block();

        final FolioRecordEntity folioRecordEntity2 = TestFixturesQuote.createFolioEntity2();
        this.reactiveMongoTemplate.insert(folioRecordEntity2)
                .doOnSuccess(result -> System.out.println("Inserted: " + result))
                .doOnError(error -> System.err.println("Error during insert: " + error.getMessage()))
                .block();

        final FolioRecordEntity folioRecordEntity3 = TestFixturesQuote.createFolioEntity3();
        this.reactiveMongoTemplate.insert(folioRecordEntity3)
                .doOnSuccess(result -> System.out.println("Inserted: " + result))
                .doOnError(error -> System.err.println("Error during insert: " + error.getMessage()))
                .block();

        final InsuredGroupEntity insuredGroupEntity = TestFixturesQuote.createGroupEntity();
        this.reactiveMongoTemplate.insert(insuredGroupEntity)
                .doOnSuccess(result -> System.out.println("Inserted: " + result))
                .doOnError(error -> System.err.println("Error during insert: " + error.getMessage()))
                .block();

        final InsuredGroupEntity insuredGroupEntity2 = TestFixturesQuote.createGroupEntity2();
        this.reactiveMongoTemplate.insert(insuredGroupEntity2)
                .doOnSuccess(result -> System.out.println("Inserted: " + result))
                .doOnError(error -> System.err.println("Error during insert: " + error.getMessage()))
                .block();

        final InsuredGroupEntity insuredGroupEntity3 = TestFixturesQuote.createGroupEntity3();
        this.reactiveMongoTemplate.insert(insuredGroupEntity3)
                .doOnSuccess(result -> System.out.println("Inserted: " + result))
                .doOnError(error -> System.err.println("Error during insert: " + error.getMessage()))
                .block();
    }

}
