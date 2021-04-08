package ru.konstantin_starikov.samsung.izhhelper.models;

import java.io.Serializable;

public enum ViolationStatusEnum implements Serializable {
    Created,
    Sent,
    Received,
    Accepted,
    Rejected,
    Saved
}
