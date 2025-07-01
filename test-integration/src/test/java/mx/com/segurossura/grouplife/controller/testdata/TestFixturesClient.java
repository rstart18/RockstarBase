package mx.com.segurossura.grouplife.controller.testdata;

import mx.com.segurossura.grouplife.openapi.model.AddressDto;
import mx.com.segurossura.grouplife.openapi.model.GeneralInfoDto;
import mx.com.segurossura.grouplife.openapi.model.GeneralInfoTaxReformDto;
import mx.com.segurossura.grouplife.openapi.model.TypeLegalIdDto;

import java.time.LocalDate;

public class TestFixturesClient {

    public static GeneralInfoDto createTypePerson1WithoutDataOptional(final TypeLegalIdDto typePersonDto,
                                                                      final String rfc,
                                                                      final String legalRepresentativeBirthDate,
                                                                      final String email, final String phone,
                                                                      final String firstName,
                                                                      final String firstSurname,
                                                                      final String lastName,
                                                                      final String curp,
                                                                      final GeneralInfoDto.GenderEnum gender) {
        final GeneralInfoDto generalInfoDto = new GeneralInfoDto();
        generalInfoDto.setTypeLegalId(typePersonDto);
        generalInfoDto.setRfc(rfc);
        generalInfoDto.setBirthdate(LocalDate.parse(legalRepresentativeBirthDate));
        generalInfoDto.setEmail(email);
        generalInfoDto.setPhoneNumber(phone);
        generalInfoDto.setName(firstName);
        generalInfoDto.setSurname(firstSurname);
        generalInfoDto.setSecondSurname(lastName);
        generalInfoDto.setCurp(curp);
        generalInfoDto.setGender(gender);
        return generalInfoDto;
    }

    public static GeneralInfoDto createTypePerson1(final TypeLegalIdDto typePersonDto, final String rfc,
                                                   final String legalRepresentativeBirthDate,
                                                   final String email, final String phone, final String firstName,
                                                   final String secondName,
                                                   final String firstSurname,
                                                   final String lastName,
                                                   final String curp,
                                                   final GeneralInfoDto.GenderEnum gender) {
        final GeneralInfoDto generalInfoDto = new GeneralInfoDto();
        generalInfoDto.setTypeLegalId(typePersonDto);
        generalInfoDto.setRfc(rfc);
        generalInfoDto.setBirthdate(LocalDate.parse(legalRepresentativeBirthDate));
        generalInfoDto.setEmail(email);
        generalInfoDto.setPhoneNumber(phone);
        generalInfoDto.setName(firstName);
        generalInfoDto.setSecondName(secondName);
        generalInfoDto.setSurname(firstSurname);
        generalInfoDto.setSecondSurname(lastName);
        generalInfoDto.setCurp(curp);
        generalInfoDto.setGender(gender);
        return generalInfoDto;
    }

    public static GeneralInfoDto createTypePerson2(final TypeLegalIdDto typePersonDto, final String rfc,
                                                   final String businessName, final String constitutionDate,
                                                   final String legalRepresentativeName,
                                                   final String legalRepresentativeBirthDate, final String email,
                                                   final String phone) {
        final GeneralInfoDto generalInfoDto = new GeneralInfoDto();
        generalInfoDto.setTypeLegalId(typePersonDto);
        generalInfoDto.setRfc(rfc);
        generalInfoDto.setBusinessName(businessName);
        generalInfoDto.setConstitutionDate(LocalDate.parse(constitutionDate));
        generalInfoDto.setLegalRepresentativeName(legalRepresentativeName);
        generalInfoDto.setBirthdate(LocalDate.parse(legalRepresentativeBirthDate));
        generalInfoDto.setEmail(email);
        generalInfoDto.setPhoneNumber(phone);
        return generalInfoDto;
    }

    public static GeneralInfoDto createInvoicingType2(final TypeLegalIdDto typePersonDto, final String rfc,
                                                      final String businessName, final String constitutionDate,
                                                      final String legalRepresentativeName,
                                                      final String legalRepresentativeBirthDate, final String email,
                                                      final String phone, final GeneralInfoTaxReformDto taxRegimeDto,
                                                      final String postalCode, final GeneralInfoDto.GenderEnum gender) {
        final GeneralInfoDto invoicingDto = new GeneralInfoDto();
        invoicingDto.setTypeLegalId(typePersonDto);
        invoicingDto.setRfc(rfc);
        invoicingDto.setBusinessName(businessName);
        invoicingDto.setConstitutionDate(LocalDate.parse(constitutionDate));
        invoicingDto.setLegalRepresentativeName(legalRepresentativeName);
        invoicingDto.setBirthdate(LocalDate.parse(legalRepresentativeBirthDate));
        invoicingDto.setEmail(email);
        invoicingDto.setPhoneNumber(phone);
        invoicingDto.setTaxReform(taxRegimeDto);
        invoicingDto.setReceiverCode(postalCode);
        invoicingDto.setGender(GeneralInfoDto.GenderEnum.E);
        return invoicingDto;
    }

    public static AddressDto createAddressDto(final String street, final String exteriorNumber,
                                              final String interiorNumber, final String postalCode, final String state
            , final String municipality, final String neighborhood) {
        final AddressDto addressDto = new AddressDto();
        addressDto.setStreetName(street);
        addressDto.setStreetNumberExt(exteriorNumber);
        addressDto.setInternalDepartmentNumber(interiorNumber);
        addressDto.setZipCode(postalCode);
        addressDto.setStateId(state);
        addressDto.setMunicipality(municipality);
        addressDto.setColonyId(neighborhood);
        return addressDto;
    }

    public static AddressDto createAddressOptional(final String street, final String exteriorNumber,
                                                   final String postalCode, final String state
            , final String municipality, final String neighborhood) {
        final AddressDto addressDto = new AddressDto();
        addressDto.setStreetName(street);
        addressDto.setStreetNumberExt(exteriorNumber);
        addressDto.setZipCode(postalCode);
        addressDto.setStateId(state);
        addressDto.setMunicipality(municipality);
        addressDto.setColonyId(neighborhood);
        return addressDto;
    }

}
