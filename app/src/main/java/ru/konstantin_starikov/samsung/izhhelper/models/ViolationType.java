package ru.konstantin_starikov.samsung.izhhelper.models;

import java.io.Serializable;

public class ViolationType implements Serializable {

    private ViolationTypeEnum violationType;

    public ViolationType(ViolationTypeEnum violationType)
    {
        this.violationType = violationType;
    }

    @Override
    public String toString() {
        switch (violationType) {
            case Pavement:
                return "Стоянка на тротуаре";
            case Lawn:
                return "Стоянка на газоне";
            case ParkingProhibited:
                return "Стоянка у знака \"Стоянка запрещена\"";
            case PedestrianCrossing:
                return "Стоянка на пешеходном переходе";
            case StoppingProhibited:
                return "Стоянка в зоне действия знака \"Остановка запрещена\"";
        }
        return "Неизвестное нарушение";
    }

    public ViolationTypeEnum getViolationType() {
        return violationType;
    }
}