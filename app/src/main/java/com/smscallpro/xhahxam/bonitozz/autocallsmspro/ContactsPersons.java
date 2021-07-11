package com.smscallpro.xhahxam.bonitozz.autocallsmspro;

public class ContactsPersons {

    private String nameContact;
    private String phoneContact;
    private boolean checked=false;



   ContactsPersons(String nameContact, String phoneContact) {
        this.nameContact = nameContact;
        this.phoneContact = phoneContact;
    }


    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getNameContact() {
        return nameContact;
    }

    public String getPhoneContact() {
        return phoneContact;
    }
}
