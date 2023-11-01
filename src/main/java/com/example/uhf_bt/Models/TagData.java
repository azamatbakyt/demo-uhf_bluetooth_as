package com.example.uhf_bt.Models;

public class TagData {
    private String epc;
    private int amount;
    private String id;
    private String description;
    private String inventoryNumber;
    private String nomenclature;
    private String dateTimeFormatter;
    private String facility;
    private String premise;
    private String executor;
    private String type;

    public TagData() {
    }

    public TagData(String id, final String epc, String type, final String description, final String inventoryNumber
            , final String nomenclature, int amount) {
        this.id = id;
        this.epc = epc;
        this.type = type;
        this.description = description;
        this.inventoryNumber = inventoryNumber;
        this.nomenclature = nomenclature;
        this.amount = amount;
    }

    public TagData(String description, String inventoryNumber, String nomenclature) {
        this.description = description;
        this.inventoryNumber = inventoryNumber;
        this.nomenclature = nomenclature;
    }

    public TagData(String nomenclature, String epc, String description, String type, int amount) {
        this.nomenclature = nomenclature;
        this.epc = epc;
        this.description = description;
        this.type = type;
        this.amount = amount;
    }


    public TagData(String id, String epc, String type, String description, String inventoryNumber,
                   String nomenclature, int amount, String facility, String premise,
                   String dateTimeFormatter, String executor) {
        this.id = id;
        this.epc = epc;
        this.type = type;
        this.description = description;
        this.inventoryNumber = inventoryNumber;
        this.nomenclature = nomenclature;
        this.amount = amount;
        this.facility = facility;
        this.premise = premise;
        this.dateTimeFormatter = dateTimeFormatter;
        this.executor = executor;
    }


    public String getEpc() {
        return this.epc;
    }

    public void setEpc(final String epc) {
        this.epc = epc;
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(final int amount) {
        this.amount = amount;
    }

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getInventoryNumber() {
        return this.inventoryNumber;
    }

    public void setInventoryNumber(final String inventoryNumber) {
        this.inventoryNumber = inventoryNumber;
    }

    public String getNomenclature() {
        return this.nomenclature;
    }

    public void setNomenclature(final String nomenclature) {
        this.nomenclature = nomenclature;
    }

    public String getType() {
        return this.type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public String getPremise() {
        return premise;
    }

    public void setPremise(String premise) {
        this.premise = premise;
    }

    public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor;
    }

    public String getDateTimeFormatter() {
        return dateTimeFormatter;
    }


    public void setDateTimeFormatter(String dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }


    @Override
    public String toString() {
        return "TagData{" +
                "description='" + description + '\'' +
                ", inventoryNumber='" + inventoryNumber + '\'' +
                ", nomenclature='" + nomenclature + '\'' +
                '}';
    }
}
