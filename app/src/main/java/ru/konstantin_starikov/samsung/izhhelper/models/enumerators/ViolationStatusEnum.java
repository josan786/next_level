package ru.konstantin_starikov.samsung.izhhelper.models.enumerators;

import java.io.Serializable;

public enum ViolationStatusEnum implements Serializable {
    Created,
    Sent,
    Received,
    Accepted,
    Rejected,
    Saved
}
