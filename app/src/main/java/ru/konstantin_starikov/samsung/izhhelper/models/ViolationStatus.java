package ru.konstantin_starikov.samsung.izhhelper.models;

import android.graphics.Color;

import java.io.Serializable;

public class ViolationStatus implements Serializable {
    private ViolationStatusEnum violationStatusEnum;

    public ViolationStatus(ViolationStatusEnum violationStatusEnum) {
        this.violationStatusEnum = violationStatusEnum;
    }

    public ViolationStatus(String violationStatus) {
        switch (violationStatus)
        {
            case "Создано":
            {
                this.violationStatusEnum = ViolationStatusEnum.Created;
                break;
            }
            case "Отправлено":
            {
                this.violationStatusEnum = ViolationStatusEnum.Sent;
                break;
            }
            case "Получено":
            {
                this.violationStatusEnum = ViolationStatusEnum.Received;
                break;
            }
            case "Подтверждено":
            {
                this.violationStatusEnum = ViolationStatusEnum.Accepted;
                break;
            }
            case "Отклонено":
            {
                this.violationStatusEnum = ViolationStatusEnum.Rejected;
                break;
            }
            case "Сохранено":
            {
                this.violationStatusEnum = ViolationStatusEnum.Saved;
                break;
            }
        }
    }

    public int getStatusColor()
    {
        int result = 0;
        switch (violationStatusEnum)
        {
            case Created:
                result = Color.GRAY;
                break;
            case Sent:
                result = Color.GRAY;
                break;
            case Received:
                result = Color.GRAY;
                break;
            case Accepted:
                result = Color.GREEN;
                break;
            case Rejected:
                result = Color.RED;
                break;
            case Saved:
                result = Color.GRAY;
                break;
        }
        return result;
    }

    @Override
    public String toString() {
        String result = "";
        switch (violationStatusEnum)
        {
            case Created:
                result = "Создано";
                break;
            case Sent:
                result = "Отправлено";
                break;
            case Received:
                result = "Получено";
                break;
            case Accepted:
                result = "Подтверждено";
                break;
            case Rejected:
                result = "Отклонено";
                break;
            case Saved:
                result = "Сохранено";
                break;
        }
        return result;
    }
}
