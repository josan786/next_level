package ru.konstantin_starikov.samsung.izhhelper;

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

    public AuthorizedBody GetAuthorizedBody() {
        return authorizedBody;
    }

    public ViolationTypeEnum getViolationType() {
        return violationType;
    }
}