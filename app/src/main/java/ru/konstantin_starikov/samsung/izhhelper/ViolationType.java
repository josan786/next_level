package ru.konstantin_starikov.samsung.izhhelper;

import java.io.Serializable;

public class ViolationType implements Serializable {
    private AuthorizedBody authorizedBody;

    public ViolationType(AuthorizedBody authorizedBody)
    {
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
}
