package ru.konstantin_starikov.samsung.izhhelper.models;

import java.io.Serializable;
import java.util.ArrayList;

public class AuthorizedBody implements Serializable {
    private String name;
    private ArrayList<ViolationReport> receivedViolationReports;
    private ArrayList<ViolationReport> acceptedViolationReports;
    private ArrayList<RejectedViolationReport> rejectedViolationReports;
    private ArrayList<ProtestAgainstViolationRejection> protestsAgainstViolationRejection;

    public AuthorizedBody()
    {
        receivedViolationReports = new ArrayList<ViolationReport>();
        acceptedViolationReports = new ArrayList<ViolationReport>();
        rejectedViolationReports = new ArrayList<RejectedViolationReport>();
        protestsAgainstViolationRejection = new ArrayList<ProtestAgainstViolationRejection>();
    }

    public AuthorizedBody(String name)
    {
        this();
        this.name = name;
    }

    public boolean AddViolation(ViolationReport violationReport)
    {
        receivedViolationReports.add(violationReport);
        return true;
    }

    private void AcceptViolation(long ID)
    {
        FindViolationReportByID(ID, receivedViolationReports).SetStatus(ViolationStatus.Accepted);
    }

    public boolean AddProtestAgainstRejection(ProtestAgainstViolationRejection protest)
    {
        protestsAgainstViolationRejection.add(protest);
        return true;
    }

    private void  RejectViolation(long ID, String rejectionReason)
    {
        ViolationReport violationReport = FindViolationReportByID(ID, receivedViolationReports);
        violationReport.SetStatus(ViolationStatus.Rejected);
        rejectedViolationReports.add(new RejectedViolationReport(violationReport, rejectionReason));
    }

    private ViolationReport FindViolationReportByID(long ID, ArrayList<ViolationReport> violationReports)
    {
        for (ViolationReport violationReport : violationReports)
        {
            if(violationReport.GetID() == ID) return violationReport;
        }
        return null;
    }
}
