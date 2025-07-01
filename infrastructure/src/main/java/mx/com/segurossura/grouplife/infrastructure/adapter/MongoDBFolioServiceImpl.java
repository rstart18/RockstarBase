package mx.com.segurossura.grouplife.infrastructure.adapter;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.com.segurossura.grouplife.application.port.DbPort;
import mx.com.segurossura.grouplife.domain.model.coverage.CoverageDetail;
import mx.com.segurossura.grouplife.domain.model.coverage.InsuredSum;
import mx.com.segurossura.grouplife.domain.model.enums.StatusFolio;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.FolioNumber;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.FolioRecordEntity;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.MongoExpression;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MongoDBFolioServiceImpl implements DbPort {
    private static final String ERROR_DB = "VG-MDB-001";
    private static final String ERROR_CLIENT = "Error saving client";
    private final FolioRecordMapper folioRecordMapper;
    private final GroupMapper groupMapper;
    private final ClientMapper clientMapper;
    private final PersistenceRepository persistenceRepository;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    @Qualifier("saveFolioRecordCircuitBreaker")
    private final CircuitBreaker saveFolioRecordCircuitBreaker;
    @Qualifier("saveFolioRecordRetry")
    private final Retry retrySaveFolio;
    @Qualifier("mongoDbRetry")
    private final Retry mongoDbRetry;

    @Override
    public Mono<FolioRecord> createFolioRecord(final FolioRecord folioRecord) {
        return this.saveFolioRecord(folioRecord);
    }

    @Override
    public Mono<FolioRecordResponse> createCompany(final FolioRecord folioRecord) {
        return Mono.just(folioRecord)
                .flatMap(this::saveFolioRecordWithCompany)
                .switchIfEmpty(Mono.error(new FolioRecordException.ServerExceptionDB("VG-DB-001")));
    }

    private Mono<FolioRecordResponse> saveFolioRecordWithCompany(final FolioRecord folioRecord) {
        return this.persistenceRepository.save(this.folioRecordMapper.toEntityFolioRecord(folioRecord))
                .flatMap(savedEntity -> Mono.just(this.folioRecordMapper.toResponse(savedEntity)));
    }

    @Override
    public Mono<FolioRecord> findFolioRecord(final String numberFolio) {
        return this.persistenceRepository.findById_NumberFolio(numberFolio)
                .switchIfEmpty(Mono.error(new FolioRecordException.FolioRecordNotFound(ERROR_DB)))
                .map(this.folioRecordMapper::toModel)
                .transformDeferred(RetryOperator.of(this.mongoDbRetry));
    }

    private Mono<FolioRecord> saveFolioRecord(final FolioRecord folioRecord) {
        return Mono.fromCallable(() -> {
                    final FolioNumber folioNumber = new FolioNumber();
                    final String numberFolio = String.valueOf(folioRecord.folio().numberFolio());
                    folioNumber.setNumberFolio(numberFolio);
                    final FolioRecordEntity folioRecordEntity = this.folioRecordMapper.toEntity(folioRecord);
                    folioRecordEntity.setId(folioNumber);
                    folioRecordEntity.setCreatedAt(LocalDateTime.now());
                    folioRecordEntity.setStatus(StatusFolio.ABIERTO.getValue());
                    return folioRecordEntity;
                })
                .doFirst(() -> log.info("[createFolio] Preparing to save record to DB: {}", folioRecord))
                .flatMap(this.persistenceRepository::save)
                .map(this.folioRecordMapper::toDomain)
                .doOnSuccess(savedRecord -> log.info("[createFolio] Successfully saved record to DB: {}", savedRecord))
                .transformDeferred(RetryOperator.of(this.retrySaveFolio))
                .transformDeferred(CircuitBreakerOperator.of(this.saveFolioRecordCircuitBreaker));
    }

    @Override
    public Mono<List<GroupVg>> groups(final String numberFolio) {
        return this.persistenceRepository.findGroupsByNumberFolio(numberFolio)
                .transformDeferred(RetryOperator.of(this.mongoDbRetry))
                .switchIfEmpty(Mono.error(new FolioRecordException.FolioByGroupNotFound("VG-DB-02")))
                .flatMap(folioRecordEntity -> {
                    final List<GroupVg> groups = folioRecordEntity.getGroups() != null
                            ? new ArrayList<>(folioRecordEntity.getGroups())
                            : Collections.emptyList();

                    return Flux.fromIterable(groups)
                            .flatMap(this::fetchCoverageDetails)
                            .collectList();
                });
    }

    private Mono<GroupVg> fetchCoverageDetails(final GroupVg group) {
        final List<CoverageDetail> updatedCoverages = group.coverages().stream()
                .map(coverage -> {
                    final List<InsuredSum.InfoDoc> infoDocs = coverage.insuredSumCoverages();
                    return coverage.toBuilder().insuredSumCoverages(infoDocs).build();
                })
                .toList();

        return Mono.just(group.toBuilder().coverages(updatedCoverages).build());
    }

    @Override
    public Mono<FolioRecord> createGroup(final FolioRecord folioRecord) {
        return this.persistenceRepository.save(this.groupMapper.toEntityGroup(folioRecord))
                .map(this.groupMapper::toModel);
    }

    @Override
    public Mono<Void> updateFolioRecord(final FolioRecord folioRecord) {
        return this.persistenceRepository.save(this.folioRecordMapper.toEntityFolioRecord(folioRecord))
                .switchIfEmpty(Mono.error(new FolioRecordException.ServerExceptionDB("VG-DB-001")))
                .then();
    }

    @Override
    public Mono<FolioCompanyResponseDto> getFolio(final String numberFolio) {
        return this.persistenceRepository.findById_NumberFolio(numberFolio)
                .switchIfEmpty(Mono.error(new FolioRecordException.FolioRecordNotFound(ERROR_DB)))
                .transformDeferred(RetryOperator.of(this.mongoDbRetry))
                .map(this.folioRecordMapper::toEntityFolioRecordSideBar);
    }

    @Override
    public Mono<FolioRecord> findFolioRecover(final String numberFolio, final String officeId, final String email) {
        return this.persistenceRepository.findFolioRecover(numberFolio, officeId, email)
                .transformDeferred(RetryOperator.of(this.mongoDbRetry))
                .switchIfEmpty(Mono.error(new FolioRecordException.FolioRecordNotFound("VG-REC-001")))
                .map(this.folioRecordMapper::toModel);
    }

    @Override
    public Mono<GroupVg> uploadSalaryByGroup(String numberFolio, Integer groupNumber, List<Salary> salaries) {
        if (salaries == null || salaries.isEmpty()) {
            return Mono.error(new FolioRecordException.SalariesIsEmpty("VG-MDB-020"));
        }

        if (salaries.stream().anyMatch(salary -> salary.salary().doubleValue() < 0)) {
            return Mono.error(new FolioRecordException.SalaryIsNegative("VG-MDB-021"));
        }

        return this.persistenceRepository.findById_NumberFolio(numberFolio)
                .switchIfEmpty(Mono.error(new FolioRecordException.FolioRecordNotFound(ERROR_DB)))
                .flatMap(folio ->
                        folio.getGroups().stream()
                                .filter(group -> group.groupNumber().equals(groupNumber))
                                .findFirst()
                                .map(existingGroup -> {
                                    int expectedTotalInsured = existingGroup.numAdministrativeInsured() + existingGroup.numOperationalInsured();
                                    if (salaries.size() != expectedTotalInsured) {
                                        return Mono.error(new FolioRecordException.InvalidInsuredCount("VG-MDB-022")).cast(GroupVg.class);
                                    }

                                    long countAdministrative = salaries.stream()
                                            .filter(salary -> "ADMINISTRATIVOS".equals(salary.activity()))
                                            .count();

                                    long countOperational = salaries.stream()
                                            .filter(salary -> "OPERATIVOS".equals(salary.activity()))
                                            .count();

                                    if (countAdministrative != existingGroup.numAdministrativeInsured() ||
                                            countOperational != existingGroup.numOperationalInsured()) {
                                        return Mono.error(new FolioRecordException.InvalidInsuredCount("VG-MDB-023")).cast(GroupVg.class);
                                    }

                                    GroupVg updatedGroup = GroupVg.builder()
                                            .groupNumber(existingGroup.groupNumber())
                                            .name(existingGroup.name())
                                            .groupType(existingGroup.groupType())
                                            .numAdministrativeInsured(existingGroup.numAdministrativeInsured())
                                            .numOperationalInsured(existingGroup.numOperationalInsured())
                                            .administrativeInsuredSum(existingGroup.administrativeInsuredSum())
                                            .operationalInsuredSum(existingGroup.operationalInsuredSum())
                                            .salaryMonth(existingGroup.salaryMonth())
                                            .averageMonthlySalary(existingGroup.averageMonthlySalary())
                                            .salaries(salaries)
                                            .coverages(existingGroup.coverages())
                                            .build();

                                    folio.getGroups().remove(existingGroup);
                                    folio.getGroups().add(updatedGroup);

                                    return this.persistenceRepository.save(folio)
                                            .thenReturn(updatedGroup);
                                })
                                .orElse(Mono.error(new FolioRecordException.GroupNotFound("VG-MDB-010")).cast(GroupVg.class))
                );

    }

    @Override
    public Mono<Void> saveClient(final FolioRecord folioRecord) {
        return Mono.just(folioRecord)
                .map(this.clientMapper::toEntity)
                .flatMap(this.persistenceRepository::save)
                .then()
                .onErrorMap(e -> new FolioRecordException.ClientDataException(ERROR_DB, ERROR_CLIENT));
    }

    @Override
    public Mono<Void> saveFolio(final FolioRecord folioRecord) {
        return Mono.just(folioRecord)
                .map(this.folioRecordMapper::toEntityPolicy)
                .flatMap(this.persistenceRepository::save)
                .transformDeferred(RetryOperator.of(this.mongoDbRetry))
                .then()
                .onErrorMap(e -> new FolioRecordException.ClientDataException(ERROR_CLIENT, ""));
    }

    @Override
    public Mono<FolioRecord> saveFolioQuote(final FolioRecord folioRecord) {
        return Mono.just(folioRecord)
                .map(this.folioRecordMapper::toEntityPolicy)
                .flatMap(this.persistenceRepository::save)
                .transformDeferred(RetryOperator.of(this.mongoDbRetry))
                .map(this.folioRecordMapper::toModel)
                .onErrorMap(e -> new FolioRecordException.ClientDataException(ERROR_CLIENT, ""));
    }

    @Override
    public Mono<FolioRecord> createGroupVolunteer(final FolioRecord folioRecord) {
        return this.persistenceRepository.save(this.groupMapper.toEntityGroupVolunteer(folioRecord))
                .map(this.groupMapper::toModel);
    }

    @Override
    public Flux<FolioRecord> getFolioToStatusIssue() {

        final LocalDate today = LocalDate.now();
        final LocalDateTime startOfDay = today.atStartOfDay();
        final LocalDateTime endOfDay = today.atTime(23, 59, 59, 999999999);

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("status").is("Abierto").and("policy").ne(null).and("updatedAt").gte(startOfDay).lt(endOfDay)),

                Aggregation.addFields().addFieldWithValue("lastPolicy",
                        AggregationExpression.from(MongoExpression.create("$arrayElemAt: ['$policy', -1]"))
                ).build(),

                Aggregation.match(
                        new Criteria().orOperator(
                                Criteria.where("lastPolicy.statusIssue").is("PENDING"),
                                Criteria.where("lastPolicy.statusIssue").is("RUNNING"),
                                Criteria.where("lastPolicy.mailNotification").exists(false),
                                Criteria.where("lastPolicy.mailNotification").is(false)
                        )
                )
        );

        return reactiveMongoTemplate.aggregate(aggregation, "cotizaciones_vidaGrupo", FolioRecordEntity.class)
                .map(this.folioRecordMapper::toModel);
    }

    @Override
    public Flux<FolioRecord> getFolioIssueToSendMail() {

        final LocalDate today = LocalDate.now();
        final LocalDateTime startOfDay = today.minusWeeks(1).atStartOfDay();
        final LocalDateTime endOfDay = today.atTime(23, 59, 59, 999999999);

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("policy").ne(null).and("updatedAt").gte(startOfDay).lt(endOfDay)),

                Aggregation.addFields().addFieldWithValue("lastPolicy",
                        AggregationExpression.from(MongoExpression.create("$arrayElemAt: ['$policy', -1]"))
                ).build(),


                Aggregation.match(Criteria.where("lastPolicy.statusIssue").is("COMPLETED")
                        .and("lastPolicy.formPayment").is(8).and("lastPolicy.mailNotification").is(true)),

                Aggregation.match(
                        new Criteria().orOperator(
                                Criteria.where("lastPolicy.mailIssue").is(false),
                                Criteria.where("lastPolicy.mailIssue").exists(false)
                        )
                )
        );

        return reactiveMongoTemplate.aggregate(aggregation, "cotizaciones_vidaGrupo", FolioRecordEntity.class)
                .map(this.folioRecordMapper::toModel);
    }

    @Override
    public Flux<FolioRecord> getHistoyFolios(Integer pageSize, Integer page, String modality, String email, String userId, LocalDate startDate, LocalDate endDate, String folioNumber, String nameOrBusinessName) {

        Query query = new Query();

        // Filtros obligatorios
        if (email != null && !email.isEmpty()) {
            query.addCriteria(Criteria.where("agentData.email").is(email));
        }
        if (startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
            query.addCriteria(Criteria.where("createdAt").gte(startDateTime).lte(endDateTime));
        }
        query.addCriteria(Criteria.where("modality").is(modality));

        // Filtros opcionales
        if (userId != null && !userId.isEmpty()) {
            query.addCriteria(Criteria.where("agentData.userId").is(userId));
        }
        if (folioNumber != null && !folioNumber.isEmpty()) {
            query.addCriteria(Criteria.where("id.numberFolio").is(folioNumber));
        }
        if (nameOrBusinessName != null && !nameOrBusinessName.isEmpty()) {
            Criteria nameCriteria = new Criteria().orOperator(
                    Criteria.where("client.general.businessName").regex(nameOrBusinessName, "i"),
                    Criteria.where("client.general.name").regex(nameOrBusinessName, "i"),
                    Criteria.where("client.general.secondName").regex(nameOrBusinessName, "i"),
                    Criteria.where("client.general.surname").regex(nameOrBusinessName, "i"),
                    Criteria.where("client.general.secondSurname").regex(nameOrBusinessName, "i")
            );
            query.addCriteria(nameCriteria);
        }

        // Paginación
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        query.with(pageable);

        return reactiveMongoTemplate.find(query, FolioRecordEntity.class).map(this.folioRecordMapper::toModel);
    }

}
