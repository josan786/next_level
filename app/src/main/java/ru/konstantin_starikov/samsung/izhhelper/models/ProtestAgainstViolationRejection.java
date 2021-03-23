package ru.konstantin_starikov.samsung.izhhelper.models;

import java.io.Serializable;

public class ProtestAgainstViolationRejection implements Serializable {
    private String reasonForUserProtest;
    private RejectedViolationReport rejectedViolationReport;

    public ProtestAgainstViolationRejection(String reasonForUserProtest, RejectedViolationReport rejectedViolationReport)
    {
        this.reasonForUserProtest= reasonForUserProtest;
        this.rejectedViolationReport = rejectedViolationReport;
    }

    public void SendProtestToAuthorisedBody()
    {
        rejectedViolationReport.violationType.GetAuthorizedBody().AddProtestAgainstRejection(this);
    }

    public String GetReasonForUserProtest()
    {
        return reasonForUserProtest;
    }
}
