package mx.com.segurossura.grouplife.infrastructure.configuration;

import mx.com.segurossura.grouplife.application.port.CatalogPort;
import mx.com.segurossura.grouplife.application.port.CataloguePort;
import mx.com.segurossura.grouplife.application.port.CommissionPort;
import mx.com.segurossura.grouplife.application.port.DbComponentPort;
import mx.com.segurossura.grouplife.application.port.DbPort;
import mx.com.segurossura.grouplife.application.port.FolioSequencePort;
import mx.com.segurossura.grouplife.application.port.InsuredPort;
import mx.com.segurossura.grouplife.application.port.JasperPort;
import mx.com.segurossura.grouplife.application.port.MailPolicyPort;
import mx.com.segurossura.grouplife.application.port.MailSendGridPort;
import mx.com.segurossura.grouplife.application.port.PaymentLinkPort;
import mx.com.segurossura.grouplife.application.port.PricingPort;
import mx.com.segurossura.grouplife.application.port.PrintPort;
import mx.com.segurossura.grouplife.application.port.QuotationPort;
import mx.com.segurossura.grouplife.application.port.StoragePort;
import mx.com.segurossura.grouplife.application.port.UtilsPort;
import mx.com.segurossura.grouplife.application.port.ValidationAPIPort;
import mx.com.segurossura.grouplife.application.service.*;
import mx.com.segurossura.grouplife.application.validator.ClientValidator;
import mx.com.segurossura.grouplife.application.validator.CompanyValidator;
import mx.com.segurossura.grouplife.application.validator.GroupValidator;
import mx.com.segurossura.grouplife.application.validator.GroupValidatorModalityVolunteer;
import mx.com.segurossura.grouplife.application.validator.QuotationDetailsValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public GroupVgService groupVgService(final DbPort dbPort, final CompanyValidator companyValidator,
                                         final ServiceUtils serviceUtils, final GroupValidator groupValidator,
                                         final GroupValidatorModalityVolunteer groupValidatorModalityVolunteer) {
        return new GroupVgService(dbPort, companyValidator, serviceUtils, groupValidator, groupValidatorModalityVolunteer);
    }

    @Bean
    public QuotationDetailsService quotationDetailsService(final DbPort dbPort,
                                                           final QuotationDetailsValidator quotationDetailsValidator) {
        return new QuotationDetailsService(dbPort, quotationDetailsValidator);
    }

    @Bean
    public ServiceUtils serviceUtils(final DbPort dbPort, final CatalogPort catalogPort) {
        return new ServiceUtils(dbPort, catalogPort);
    }

    @Bean
    public FolioService folioService(final DbPort dbPort, final FolioSequencePort folioSequencePort, final ServiceUtils serviceUtils) {
        return new FolioService(dbPort, folioSequencePort, serviceUtils);
    }

    @Bean
    public FolioSequenceService folioSequenceService(final FolioSequencePort folioSequencePort) {
        return new FolioSequenceService(folioSequencePort);
    }

    @Bean
    public ComponentService componentService(final DbComponentPort dbComponentPort, final DbPort dbPort, final CatalogService catalogService, final CompanyValidator companyValidator) {
        return new ComponentService(dbComponentPort, dbPort, catalogService, companyValidator);
    }

    @Bean
    public CompanyService companyService(final DbPort dbPort, final CatalogService catalogService,
                                         final CompanyValidator companyValidator,
                                         final CommissionService commissionService) {
        return new CompanyService(dbPort, catalogService, companyValidator, commissionService);
    }

    @Bean
    public CommissionService commissionService(final CommissionPort commissionPort) {
        return new CommissionService(commissionPort);
    }

    @Bean
    public CatalogService catalogService(final CatalogPort catalogPort) {
        return new CatalogService(catalogPort);
    }

    @Bean
    public QuotationDetailsValidator quotationDetailsValidator() {
        return new QuotationDetailsValidator();
    }

    @Bean
    public CompanyValidator companyValidator() {
        return new CompanyValidator();
    }

    @Bean
    public GroupValidator groupValidator(final ServiceUtils serviceUtils) {
        return new GroupValidator(serviceUtils);
    }

    @Bean
    public PricingService pricingService(final PricingPort pricingPort, final DbPort dbPort, final ServiceUtils serviceUtils, final UtilsPort utilsPort, final InsuredPort insuredPort) {
        return new PricingService(pricingPort, dbPort, serviceUtils, utilsPort, insuredPort);
    }

    @Bean
    public ClientService clientService(final DbPort dbPort, final ClientValidator clientValidator,
                                       final ServiceUtils serviceUtils) {
        return new ClientService(dbPort, clientValidator, serviceUtils);
    }

    @Bean
    public ClientValidator clientValidator() {
        return new ClientValidator();
    }

    @Bean
    public PrintQuoteService printQuoteService(final JasperPort jasperPort, final PricingService pricingService, final ServiceUtils serviceUtils, final UtilsPort utilsPort, final CatalogPort catalogPort, final StoragePort storagePort) {
        return new PrintQuoteService(jasperPort, pricingService, serviceUtils, utilsPort, catalogPort, storagePort);
    }

    @Bean
    public ValidationAPIService validationAPIService(final ValidationAPIPort validationAPIPort) {
        return new ValidationAPIService(validationAPIPort);
    }

    @Bean
    public CatalogueService catalogueService(final CataloguePort cataloguePort) {
        return new CatalogueService(cataloguePort);
    }

    @Bean
    public GetPaymentLinkService getPaymentLinkService(final ServiceUtils serviceUtils,
                                                       final PaymentLinkPort paymentLinkPort,
                                                       final DbPort dbPort) {
        return new GetPaymentLinkService(serviceUtils, paymentLinkPort, dbPort);
    }

    @Bean
    public GroupValidatorModalityVolunteer groupValidatorModalityVolunteer() {
        return new GroupValidatorModalityVolunteer();
    }

    @Bean
    public IssueService issueService(final ServiceUtils serviceUtils, final QuotationPort quotationPort,
                                     final DbPort dbPort, final InsuredPort insuredPort) {
        return new IssueService(serviceUtils, quotationPort, dbPort, insuredPort);
    }

    @Bean
    public VerifyIssueService verifyIssueService(final DbPort dbPort, final QuotationPort quotationPort,
                                                 final GetPaymentLinkService getPaymentLinkService,
                                                 final PrintIssueService printIssueService,
                                                 final MailSendGridService mailSendGridService) {
        return new VerifyIssueService(dbPort, quotationPort, getPaymentLinkService, printIssueService, mailSendGridService);
    }

    @Bean
    public PrintIssueService printIssueService(final PrintPort printPort, final ServiceUtils serviceUtils) {
        return new PrintIssueService(printPort, serviceUtils);
    }

    @Bean
    public MailSendGridService mailSendGridService(final MailSendGridPort mailSendGridPort, final ServiceUtils serviceUtils) {
        return new MailSendGridService(mailSendGridPort, serviceUtils);
    }

    @Bean
    public VerifyIssueMailService verifyIssueMailService(final DbPort dbPort, final MailPolicyPort mailPolicyPort) {
        return new VerifyIssueMailService(dbPort, mailPolicyPort);
    }

    @Bean
    public HistoryFoliosService historyFoliosService(final DbPort dbPort) {
        return new HistoryFoliosService(dbPort);
    }

}
