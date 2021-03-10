package ru.konstantin_starikov.samsung.izhhelper;

public class ViolationType {
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
