package com.example.timesheet;


public class NoteCompany {
    private String documentId;
    private String signInN;
    private String signOutN;

    private String empCode;
    private String name;
    private String email;
    private boolean company;

    public NoteCompany() {
        //public no-arg constructor needed
    }

    public NoteCompany(String name, String code, String email , boolean company) {
        this.name = name;
        this.empCode = code;
        this.email = email;
        this.company = company;
    }

    public String getEmpCode() {
        return empCode;
    }

    public void setEmpCode(String empCode) {
        this.empCode = empCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isCompany() {
        return company;
    }

    public void setCompany(boolean company) {
        this.company = company;
    }
}
