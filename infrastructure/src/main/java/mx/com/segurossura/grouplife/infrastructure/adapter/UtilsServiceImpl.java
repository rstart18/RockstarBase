package mx.com.segurossura.grouplife.infrastructure.adapter;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.com.segurossura.grouplife.application.port.UtilsPort;
import mx.com.segurossura.grouplife.infrastructure.configuration.JasperQuotationProperties;
import mx.com.segurossura.grouplife.infrastructure.configuration.PropertiesConfiguration;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class UtilsServiceImpl implements UtilsPort {

    private final PropertiesConfiguration propertiesConfiguration;
    private final JasperQuotationProperties jasperQuotationProperties;

    @Override
    public String getPricingVersion() {
        return propertiesConfiguration.getPricingVersion();
    }

    @Override
    public String getH1() {
        return jasperQuotationProperties.getH1();
    }

    @Override
    public String getH1Traditional() {
        return jasperQuotationProperties.getH1Traditional();
    }

    @Override
    public String getBusinessLine() {
        return jasperQuotationProperties.getBusinessLine();
    }

    @Override
    public String getNomEmp() {
        return jasperQuotationProperties.getNomEmp();
    }

    @Override
    public String getDirEmp1() {
        return jasperQuotationProperties.getDirEmp1();
    }

    @Override
    public String getDirEmp2() {
        return jasperQuotationProperties.getDirEmp2();
    }

    @Override
    public String getDirEmp3() {
        return jasperQuotationProperties.getDirEmp3();
    }

    @Override
    public String getDirEmp4() {
        return jasperQuotationProperties.getDirEmp4();
    }

    @Override
    public String getTelEmp() {
        return jasperQuotationProperties.getTelEmp();
    }

    @Override
    public String getAcrEmp() {
        return jasperQuotationProperties.getAcrEmp();
    }

    @Override
    public String getRfcEmp() {
        return jasperQuotationProperties.getRfcEmp();
    }

    @Override
    public String getPagWeb() {
        return jasperQuotationProperties.getPagWeb();
    }

    @Override
    public String getTelEmp1() {
        return jasperQuotationProperties.getTelEmp1();
    }

    @Override
    public String getTelEmp2() {
        return jasperQuotationProperties.getTelEmp2();
    }

    @Override
    public String getCorrEmp() {
        return jasperQuotationProperties.getCorrEmp();
    }

    @Override
    public String getPagweb1() {
        return jasperQuotationProperties.getPagweb1();
    }

    @Override
    public String getCDMX() {
        return jasperQuotationProperties.getCdmx();
    }

    @Override
    public String getTelUnat() {
        return jasperQuotationProperties.getTelUnat();
    }

    @Override
    public String getTelUnatP() {
        return jasperQuotationProperties.getTelUnatP();
    }

    @Override
    public String getCorreoUnat() {
        return jasperQuotationProperties.getCorreoUnat();
    }

    @Override
    public String getSuscriber() {
        return jasperQuotationProperties.getSuscriber();
    }

    @Override
    public String getCurrency() {
        return jasperQuotationProperties.getCurrency();
    }

    @Override
    public String getFileName() {
        return jasperQuotationProperties.getFileName();
    }

    @Override
    public String getLogo() {
        return jasperQuotationProperties.getLogo();
    }

    @Override
    public String getPlantilla() {
        return jasperQuotationProperties.getPlantilla();
    }

    @Override
    public String getPlantillaVoluntario() {
        return jasperQuotationProperties.getPlantillaVoluntario();
    }

    @Override
    public String getSubReportDir() {
        return jasperQuotationProperties.getSubReportDir();
    }

}
