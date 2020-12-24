package ru.konstantin_starikov.samsung.izhhepler;

import android.graphics.Bitmap;

import java.sql.Time;
import java.util.ArrayList;

public class RejectedViolationReport extends ViolationReport {
    private String rejectionReason;

    public RejectedViolationReport(ViolationReport violationReport, String rejectionReason)
    {
        super(violationReport);
        this.rejectionReason = rejectionReason;
    }

    public void AppealAgainstViolationRejection(String reasonForUserProtest)
    {
        ProtestAgainstViolationRejection protestAgainstViolationRejection =
                new ProtestAgainstViolationRejection(reasonForUserProtest, this);
        protestAgainstViolationRejection.SendProtestToAuthorisedBody();
    }

    public String getRejectionReason()
    {
        return rejectionReason;
    }
}
