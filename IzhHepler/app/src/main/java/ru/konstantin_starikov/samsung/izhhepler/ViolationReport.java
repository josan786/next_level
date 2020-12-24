package ru.konstantin_starikov.samsung.izhhepler;

import android.graphics.Bitmap;

import java.sql.Time;
import java.util.ArrayList;

public class ViolationReport {
    protected long ID;
    private ViolationStatus status;

    public String place;
    public Time departureTime;
    public Account senderAccount;
    public ViolationType violationType;

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
        this.status = status;
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

    public ViolationReport(String place, Time departureTime, Account senderAccount, ArrayList<Bitmap> photos)
    {
        this();
        this.place = place;
        this.departureTime = departureTime;
        this.senderAccount = senderAccount;
        this.photos = photos;
    }

    public ViolationReport(ViolationReport violationReport)
    {
        this.ID = violationReport.GetID();
        this.place = violationReport.place;
        this.status = violationReport.GetStatus();
        this.departureTime = violationReport.departureTime;
        this.senderAccount = violationReport.senderAccount;
        this.photos = violationReport.photos;
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
