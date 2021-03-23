package ru.konstantin_starikov.samsung.izhhelper.models;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;

public class ViolationReport implements Serializable {
    protected long ID;
    private ViolationStatus status;

    public String place;
    public Time departureTime;
    public Account senderAccount;
    public ViolationType violationType;
    public CarNumber carNumber;

    public ArrayList<Bitmap> photos;

    public ViolationReport()
    {
        status = ViolationStatus.Created;
        photos = new ArrayList<Bitmap>();
    }

    public ViolationReport(String place, Time departureTime, Account senderAccount, ViolationType violationType)
    {
        this();
        this.place = place;
        this.departureTime = departureTime;
        this.senderAccount = senderAccount;
        this.violationType = violationType;
    }

    public ViolationReport(String place, Time departureTime, Account senderAccount)
    {
        this();
        this.place = place;
        this.departureTime = departureTime;
        this.senderAccount = senderAccount;
    }

    public ViolationReport(String place, Time departureTime, Account senderAccount, CarNumber carNumber, ArrayList<Bitmap> photos)
    {
        this();
        this.place = place;
        this.departureTime = departureTime;
        this.senderAccount = senderAccount;
        this.carNumber = carNumber;
        this.photos = photos;
    }

    public ViolationReport(ViolationReport violationReport)
    {
        this.ID = violationReport.GetID();
        this.place = violationReport.place;
        this.status = violationReport.GetStatus();
        this.departureTime = violationReport.departureTime;
        this.senderAccount = violationReport.senderAccount;
        this.carNumber = violationReport.carNumber;
        this.photos = violationReport.photos;
    }

    public ViolationReport(String place, Time departureTime, Account senderAccount, ViolationType violationType, CarNumber carNumber) {
        this.place = place;
        this.departureTime = departureTime;
        this.senderAccount = senderAccount;
        this.violationType = violationType;
        this.carNumber = carNumber;
    }

    public void AddPhoto(Bitmap photo)
    {
        photos.add(photo);
    }

    public boolean SubmitViolationToAuthorizedBody()
    {
        violationType.SubmitViolationToAuthorizedBody(this);
        return true;
    }

    public long GetID()
    {
        return ID;
    }

    public ViolationStatus GetStatus()
    {
        return status;
    }

    public void SetStatus(ViolationStatus status)
    {
        this.status = status;
    }
}
