package ru.konstantin_starikov.samsung.izhhelper.models;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;

public class Account implements Serializable {
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

    public boolean SendViolationReport(String ID, Context context)
    {
        FindViolationReportByID(ID).SubmitViolationToAuthorizedBody(context);
        return true;
    }

    private ViolationReport FindViolationReportByID(String ID)
    {
        for (ViolationReport violationReport : violationReports)
        {
            if(violationReport.GetID().equals(ID)) return violationReport;
        }
        return null;
    }
}
