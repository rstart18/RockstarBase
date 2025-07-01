package mx.com.segurossura.grouplife.infrastructure.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class JasperQuotationProperties {
    @Value("${jasper-quotation.info.h1}")
    private String h1;
    @Value("${jasper-quotation.info.h1-traditional}")
    private String h1Traditional;
    @Value("${jasper-quotation.info.businessLine}")
    private String businessLine;
    @Value("${jasper-quotation.info.nomEmp}")
    private String nomEmp;
    @Value("${jasper-quotation.info.dirEmp1}")
    private String dirEmp1;
    @Value("${jasper-quotation.info.dirEmp2}")
    private String dirEmp2;
    @Value("${jasper-quotation.info.dirEmp3}")
    private String dirEmp3;
    @Value("${jasper-quotation.info.dirEmp4}")
    private String dirEmp4;
    @Value("${jasper-quotation.info.telEmp}")
    private String telEmp;
    @Value("${jasper-quotation.info.acrEmp}")
    private String acrEmp;
    @Value("${jasper-quotation.info.rfcEmp}")
    private String rfcEmp;
    @Value("${jasper-quotation.info.pagWeb}")
    private String pagWeb;
    @Value("${jasper-quotation.info.telEmp1}")
    private String telEmp1;
    @Value("${jasper-quotation.info.telEmp2}")
    private String telEmp2;
    @Value("${jasper-quotation.info.corrEmp}")
    private String corrEmp;
    @Value("${jasper-quotation.info.pagweb1}")
    private String pagweb1;
    @Value("${jasper-quotation.info.cdmx}")
    private String cdmx;
    @Value("${jasper-quotation.info.telUnat}")
    private String telUnat;
    @Value("${jasper-quotation.info.telUnatP}")
    private String telUnatP;
    @Value("${jasper-quotation.info.correoUnat}")
    private String correoUnat;
    @Value("${jasper-quotation.info.suscriber}")
    private String suscriber;
    @Value("${jasper-quotation.info.currency}")
    private String currency;
    @Value("${jasper-quotation.info.fileName}")
    private String fileName;
    @Value("${jasper-quotation.info.plantilla}")
    private String plantilla;
    @Value("${jasper-quotation.info.plantillaVoluntario}")
    private String plantillaVoluntario;
    @Value("${jasper-quotation.info.logo}")
    private String logo;
    @Value("${jasper-quotation.info.subReportDir}")
    private String subReportDir;

}

