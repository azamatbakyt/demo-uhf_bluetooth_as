package com.example.uhf_bt.Models;

public class Facility {
    private int id;
    private String name;
    private String address;
    private String note;

    public Facility() {
    }

    public Facility(String name, String address, String note) {
        this.name = name;
        this.address = address;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
