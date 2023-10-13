package com.example.uhf_bt.Models;

public class FacilityAndPremiseRelations {
    private int id;
    private int facility_id;
    private int premise_id;

    public int getFacility_id() {
        return facility_id;
    }

    public void setFacility_id(int facility_id) {
        this.facility_id = facility_id;
    }

    public int getPremise_id() {
        return premise_id;
    }

    public void setPremise_id(int premise_id) {
        this.premise_id = premise_id;
    }
}
