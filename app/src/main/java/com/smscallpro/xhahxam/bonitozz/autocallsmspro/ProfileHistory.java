package com.smscallpro.xhahxam.bonitozz.autocallsmspro;

import java.util.Calendar;

public class ProfileHistory {
    private String profileName;
    private String message;
    private  int requestCode;
    private String[] phoneNumber;
    private String[] names;
    private  long date;
    private long savingDate;




    public ProfileHistory(String profileName, String message, int requestCode, String[] phoneNumber, String[] namesOfContacts, long date, long savingDate) {
        this.profileName = profileName;
        this.message = message;
        this.requestCode = requestCode;
        this.phoneNumber = phoneNumber;
        this.names = namesOfContacts;
        this.date = date;
        this.savingDate=savingDate;

    }
    public ProfileHistory(String profileName,ProfileHistory profileHistoryx) {
        this.profileName = profileName;
        this.message = profileHistoryx.getMessage();
        this.requestCode = profileHistoryx.getRequestCode();
        this.phoneNumber = profileHistoryx.phoneNumber;
        this.names = profileHistoryx.getNames();
        this.date = profileHistoryx.getDate();
        Calendar c=Calendar.getInstance();
       this.savingDate=c.getTimeInMillis();

    }

    public String getProfileName() {
        return profileName;
    }

    public String getMessage() {
        return message;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public long getSavingDate() {
        return savingDate;
    }

    public String[] getPhoneNumber() {
        return phoneNumber;
    }

    public String[] getNames() {
        return names;
    }

    public long getDate() {
        return date;
    }
}