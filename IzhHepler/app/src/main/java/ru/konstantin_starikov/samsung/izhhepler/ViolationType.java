package ru.konstantin_starikov.samsung.izhhepler;

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
