package mx.com.segurossura.grouplife.utils;

import mx.com.segurossura.grouplife.infrastructure.repository.entity.FolioNumber;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.components.ComponentRecordEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.components.CurrentStep;

import java.util.List;

public class ComponentRecordEntityUtil {

    public static ComponentRecordEntity createBaseComponentRecordEntity(String numberFolio){

        final FolioNumber folioNumber = new FolioNumber(numberFolio);
        final ComponentRecordEntity componentRecordEntity = new ComponentRecordEntity();
        componentRecordEntity.setId(folioNumber);
        componentRecordEntity.setComponents(List.of());
        componentRecordEntity.setState(null);
        componentRecordEntity.setCurrentStep(new CurrentStep("1", List.of()));

        return componentRecordEntity;
    }

}
