package ru.konstantin_starikov.samsung.izhhelper.models;

import java.io.Serializable;

public class ViolationType implements Serializable {

    private ViolationTypeEnum violationType;

    private AuthorizedBody authorizedBody;

    public ViolationType(ViolationTypeEnum violationType, AuthorizedBody authorizedBody)
    {
        this.violationType = violationType;
        this.authorizedBody = authorizedBody;
    }

    public boolean SubmitViolationToAuthorizedBody(ViolationReport violationReport)
    {
        authorizedBody.AddViolation(violationReport);
        return true;
    }

    @Override
    public String toString() {
        switch (violationType) {
            case Pavement:
                return "Стоянка на тротуаре";
            case Lawn:
                return "Стоянка на газоне";
            case ParkingProhibited:
                return "Стоянка у знака \\\"Стоянка запрещена\\\"";
            case PedestrianCrossing:
                return "Стоянка на пешеходном переходе";
            case StoppingProhibited:
                return "Стоянка в зоне действия знака \\\"Остановка запрещена\\\"";
        }
        return "Неизвестное нарушение";
    }

    public AuthorizedBody GetAuthorizedBody() {
        return authorizedBody;
    }

    public ViolationTypeEnum getViolationType() {
        return violationType;
    }
}