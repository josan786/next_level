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
    private ArrayList<ViolationReport> violationReports = new ArrayList<ViolationReport>();

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
        FindViolationReportByID(ID).submitViolationToAuthorizedBody(context);
        return true;
    }

    public void addViolationReport(ViolationReport violationReport, Context context)
    {
        violationReports.add(violationReport);
        saveViolationReport(violationReport, context);
    }

    private void saveViolationReport(ViolationReport violationReport, Context context)
    {
        ViolationsDatabase violationsDatabase;
        violationsDatabase = new ViolationsDatabase(context);
        violationsDatabase.insert(violationReport);
    }

    public void loadViolations(Context context)
    {
        ViolationsDatabase violationsDatabase;
        violationsDatabase = new ViolationsDatabase(context);
        ArrayList<ViolationReport> allReports = violationsDatabase.selectAll();
        for (ViolationReport violationReport : allReports)
        {
            if(violationReport.senderAccount.ID == this.ID) violationReports.add(violationReport);
        }
    }

    private ViolationReport FindViolationReportByID(String ID)
    {
        for (ViolationReport violationReport : violationReports)
        {
            if(violationReport.getID().equals(ID)) return violationReport;
        }
        return null;
    }

    public ArrayList<ViolationReport> getViolationReports()
    {
        return violationReports;
    }
}
