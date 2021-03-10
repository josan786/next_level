package ru.konstantin_starikov.samsung.izhhelper;

import java.util.ArrayList;

public class Account {
    public long ID;
    public String firstName;
    public String lastName;
    public String phoneNumber;
    public Address address;
    public ArrayList<ViolationReport> violationReports = new ArrayList<ViolationReport>();

    public Account()
    {
        ID = 0;
    }

    public Account(long ID, String firstName, String lastName, String phoneNumber, Address address)
    {
        this();
        this.ID = ID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public void CreateViolationReport(long ID)
    {
        violationReports.add(new ViolationReport());
    }

    public boolean SendViolationReport(long ID)
    {
        FindViolationReportByID(ID).SubmitViolationToAuthorizedBody();
        return true;
    }

    private ViolationReport FindViolationReportByID(long ID)
    {
        for (ViolationReport violationReport : violationReports)
        {
            if(violationReport.GetID() == ID) return violationReport;
        }
        return null;
    }
}
