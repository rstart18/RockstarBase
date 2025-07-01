package mx.com.segurossura.grouplife.controller.testdata;

import mx.com.segurossura.grouplife.infrastructure.adapter.dto.quote.insured.GroupInsuredsRequest;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.quote.insured.GroupRequestDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.quote.insured.InsuredRequestDto;

import java.util.ArrayList;
import java.util.List;

public class TestFixturesInsuredGroup {
    public static GroupInsuredsRequest createGroupVgRequest() {
        return new GroupInsuredsRequest(
                createGroupRequestDto()
        );
    }

    public static List<GroupRequestDto> createGroupRequestDto() {
        final List<GroupRequestDto> response = new ArrayList<>();
        final GroupRequestDto request = new GroupRequestDto("12" , "estandar", "SAFIJA", "ADMINISTRATIVOS",
                "12.12", createInsuredRequestDto()
        );
        response.add(request);
        return response;
    }

    public static List<InsuredRequestDto> createInsuredRequestDto() {
        final List<InsuredRequestDto> response = new ArrayList<>();
//        final InsuredRequestDto insuredRequestDto = new InsuredRequestDto(
//                "Andres", "lopez", "Gonzalez", "alex", "27/07/1989", 23, null, null, null, null, null, null,
//                null,
//                "" , ""
//        );
//        response.add(insuredRequestDto);
        return response;
    }

}
