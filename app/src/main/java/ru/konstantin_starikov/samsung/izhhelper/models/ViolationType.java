package ru.konstantin_starikov.samsung.izhhelper.models;

import android.content.Context;

import java.io.Serializable;

import ru.konstantin_starikov.samsung.izhhelper.R;
import ru.konstantin_starikov.samsung.izhhelper.models.enumerators.ViolationTypeEnum;

public class ViolationType implements Serializable {

    private ViolationTypeEnum violationType;

    public ViolationType(ViolationTypeEnum violationType)
    {
        this.violationType = violationType;
    }

    public ViolationType(String violationTypeName)
    {
        switch (violationTypeName) {
            case "Стоянка на тротуаре":
                violationType = ViolationTypeEnum.Pavement;
                break;
            case "Стоянка на газоне":
                violationType = ViolationTypeEnum.Lawn;
                break;
            case "Стоянка у знака \"Стоянка запрещена\"":
                violationType = ViolationTypeEnum.ParkingProhibited;
                break;
            case "Стоянка на пешеходном переходе":
                violationType = ViolationTypeEnum.PedestrianCrossing;
                break;
            case "Стоянка в зоне действия знака \"Остановка запрещена\"":
                violationType = ViolationTypeEnum.StoppingProhibited;
                break;
        }
    }

    public static ViolationType getViolationTypeFromTranslatedName(String violationTypeName, Context context)
    {
        ViolationType result = null;
        if(violationTypeName.equals(context.getString(R.string.PavementViolationType)))
            result = new ViolationType("Стоянка на тротуаре");
        if(violationTypeName.equals(context.getString(R.string.LawnViolationType)))
            result = new ViolationType("Стоянка на газоне");
        if(violationTypeName.equals(context.getString(R.string.ParkingProhibitedViolationType)))
            result = new ViolationType("Стоянка у знака \"Стоянка запрещена\"");
        if(violationTypeName.equals(context.getString(R.string.PedestrianCrossingViolationType)))
            result = new ViolationType("Стоянка на пешеходном переходе");
        if(violationTypeName.equals(context.getString(R.string.StoppingProhibitedViolationType)))
            result = new ViolationType("Стоянка в зоне действия знака \"Остановка запрещена\"");
        return result;
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
        return "Другое нарушение";
    }

    public String toString(Context context) {
        switch (violationType) {
            case Pavement:
                return context.getString(R.string.PavementViolationType);
            case Lawn:
                return context.getString(R.string.LawnViolationType);
            case ParkingProhibited:
                return context.getString(R.string.ParkingProhibitedViolationType);
            case PedestrianCrossing:
                return context.getString(R.string.PedestrianCrossingViolationType);
            case StoppingProhibited:
                return context.getString(R.string.StoppingProhibitedViolationType);
        }
        return context.getString(R.string.OtherViolationType);
    }

    public ViolationTypeEnum getViolationType() {
        return violationType;
    }
}