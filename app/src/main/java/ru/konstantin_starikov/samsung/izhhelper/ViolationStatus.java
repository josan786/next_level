package ru.konstantin_starikov.samsung.izhhelper;

import java.io.Serializable;

public enum ViolationStatus implements Serializable {
    Created,
    Sent,
    Received,
    Accepted,
    Rejected
}
