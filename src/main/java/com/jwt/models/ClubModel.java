package com.jwt.models;

import javax.persistence.Column;

public class ClubModel {

    private String name;
    private String irishName;
    private String contactName;
    private String contactEmail;
    private String contactPhone;
    private String pitches;
    private String colours;
    private String abbrev;

    public ClubModel() {}

    public void setName(String name) {
        this.name = name;
    }
    public void setIrishName(String irishName) {
        this.irishName = irishName;
    }
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
    public void setPitches(String pitches) {
        this.pitches = pitches;
    }
    public void setColours(String colours) {
        this.colours = colours;
    }
    public void setAbbrev(String abbrev) {
        this.abbrev = abbrev;
    }

    public String getColours() {
        return colours;
    }
    public String getPitches() {
        return pitches;
    }
    public String getContactPhone() {
        return contactPhone;
    }
    public String getContactEmail() {
        return contactEmail;
    }
    public String getContactName() {
        return contactName;
    }
    public String getName() {
        return name;
    }
    public String getIrishName() {
        return irishName;
    }
    public String getAbbrev() {
        return abbrev;
    }

    public Club translateModelToClub(){
        Club club = new Club();
        club.setName(this.name);
        club.setIrishName(this.irishName);
        club.setContactName(this.contactName);
        club.setContactEmail(this.contactEmail);
        club.setContactPhone(this.contactPhone);
        club.setPitches(this.pitches);
        club.setColours(this.colours);
        club.setAbbrev(this.abbrev);
        return club;
    }

    // used in update operations
    public Club translateModelToClub(Long id){
        Club club = translateModelToClub();
        club.setId(id);
        return club;
    }
}