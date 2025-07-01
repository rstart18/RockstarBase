package mx.com.segurossura.grouplife.application.port;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

public interface DbPort {
    Mono<FolioRecord> createFolioRecord(FolioRecord folioRecord);

    Mono<FolioRecordResponse> createCompany(FolioRecord folioRecord);

    Mono<FolioRecord> findFolioRecord(String numberFolio);

    Mono<FolioRecord> createGroup(FolioRecord folioRecord);

    Mono<List<GroupVg>> groups(String numberFolio);

    Mono<Void> updateFolioRecord(FolioRecord folioRecord);

    Mono<FolioCompanyResponseDto> getFolio(String numberFolio);

    Mono<FolioRecord> findFolioRecover(String numberFolio, String officeId, String email);

    Mono<GroupVg> uploadSalaryByGroup(String numberFolio, Integer groupNumber, List<Salary> salaries);

    Mono<Void> saveClient(FolioRecord folioRecord);

    Mono<Void> saveFolio(FolioRecord folioRecord);

    Mono<FolioRecord> saveFolioQuote(FolioRecord folioRecord);

    Mono<FolioRecord> createGroupVolunteer(FolioRecord folioRecord);

    Flux<FolioRecord> getFolioToStatusIssue();

    Flux<FolioRecord> getFolioIssueToSendMail();

    Flux<FolioRecord> getHistoyFolios(Integer pageSize, Integer page, String modality, String email, String userId, LocalDate startDate, LocalDate endDate, String folioNumber, String nameOrBusinessName);
}
