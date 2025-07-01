package mx.com.segurossura.grouplife.infrastructure.adapter.dto.quote.insured;

public class DtoConstants {
    public static final String NAME = "Nombre";
    public static final String CDIDEPER = "CDIDEPER";
    public static final String RFC = "Identificador";
    public static final String OTFISJUR = "OTFISJUR";
    public static final String TYPE_LEGAL_ID = "Tipo Persona";
    public static final String DS_NAME = "DSNOMBRE";
    public static final String DS_NAME_1 = "DSNOMBR1";
    public static final String DS_NAME_2 = "DSNOMBR2";
    public static final String LAST_NAME_1 = "DSAPELL1";
    public static final String LAST_NAME_2 = "DSAPELL2";
    public static final String BIRTH_DATE = "FENACIMI";
    public static final String OT_GENDER = "OTSEXO";

    /*Domicilio*/
    public static final String NMORDDOM = "NMORDDOM";
    public static final String DSDOMICI = "DSDOMICI";
    public static final String CDPOSTAL = "CDPOSTAL";
    public static final String NMTELEFO = "NMTELEFO";
    public static final String OTPOBLAC = "OTPOBLAC";
    public static final String CDCOLONI = "CDCOLONI";
    public static final String CDPROVIN = "CDPROVIN";
    public static final String OTPISO = "OTPISO";
    public static final String CDTIPDOM = "CDTIPDOM";
    public static final String CDIDIOMA = "CDIDIOMA";
    public static final String CDPAIS = "CDPAIS";
    public static final String NUMBER_ABROAD = "NUMERO EXTERIOR";
    public static final String NM_NUMBER = "NMNUMERO";
    public static final String COLONY = "COLONIA";
    public static final String NM_NUMBER_RTA = "NUMERO INTERIOR";
    public static final String ALCALDIA = "ALCALDIA";
    public static final String STATE = "ESTADO";

    public static final String OTVALOR02 = "OTVALOR02";
    public static final String OTVALOR02_DESC = "CURP";

    public static final String OTVALOR14 = "OTVALOR14";
    public static final String OTVALOR14_DESC = "E-MAIL 1";

    public static final String OTVALOR19 = "OTVALOR19";
    public static final String OTVALOR19_DESC = "TEL CONTACTO";

    public static final String OTVALOR20 = "OTVALOR20";
    public static final String OTVALOR20_DESC = "MAIL CONTACTO";

    // -> S/N
    public static final String OTVALOR39 = "OTVALOR39";
    public static final String OTVALOR39_DESC = "APODERADO";

    // nombre apoderado
    public static final String OTVALOR64 = "OTVALOR64";
    public static final String OTVALOR64_DESC = "NOMB. ADMINIST.";

    // fecha nacimiento apoderado
    public static final String OTVALOR89 = "OTVALOR89";
    public static final String OTVALOR89_DESC = "FEC NAC ADMIN";

    // regimen fiscal
    public static final String OTVALOR96 = "OTVALOR96";
    public static final String OTVALOR96_DESC = "ID REGIMEN FISC";

    // codigo postal receptor
    public static final String OTVALOR97 = "OTVALOR97";
    public static final String OTVALOR97_DESC = "CP DEL RECEPTOR";

    private DtoConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}