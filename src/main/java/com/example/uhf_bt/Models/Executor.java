package com.example.uhf_bt.Models;

public class Executor {

    private int id;
    private String role;
    private String name;
    private String notation;

    public Executor() {
    }

    public Executor(String role, String name, String notation) {
        this.role = role;
        this.name = name;
        this.notation = notation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotation() {
        return notation;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }

    @Override
    public String toString() {
        return  name;
    }
}
