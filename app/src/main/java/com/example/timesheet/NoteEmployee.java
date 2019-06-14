package com.example.timesheet;

import com.google.firebase.database.Exclude;

public class NoteEmployee {

    private String documentId;
    private String signInN;
    private String signOutN;

    private String empCode;
    private String name;

    private String timeStr;
    private float timeInt;
    private boolean ifLunch;

    private long startH;
    private long startM;
    private long finishH;
    private long finishM;

    private long h4t;
    private long m4t;

    NoteEmployee() {
        //public no-arg constructor needed
    }

    NoteEmployee(String timeStr, float timeInt, boolean ifLunch, String signInN, String signOutN) {
        this.timeStr = timeStr;
        this.timeInt = timeInt;
        this.ifLunch = ifLunch;
        this.signInN = signInN;
        this.signOutN = signOutN;
    }

    @Exclude
    String getDocumentId() {
        return documentId;
    }

    void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    String getTimeStr() {
        return timeStr;
    }

    boolean getIfLunch() {
        return ifLunch;
    }
    void setIfLunch(Boolean lunch){
        this.ifLunch = lunch;
    }

    float getTimeInt() {
        return timeInt;
    }

    String getSignInN() {
        return signInN;
    }

    public void setSignInN(String signInN) {
        this.signInN = signInN;
    }

    String getSignOutN() {
        return signOutN;
    }

    public void setSignOutN(String signOutN) {
        this.signOutN = signOutN;
    }

    public String getEmpCode() {
        return empCode;
    }

    void setEmpCode(String empCode) {
        this.empCode = empCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    long getStartH() {
        return startH;
    }

    void setStartH(long startH) {
        this.startH = startH;
    }

    long getStartM() {
        return startM;
    }

    void setStartM(long startM) {
        this.startM = startM;
    }

    long getFinishH() {
        return finishH;
    }

    void setFinishH(long finishH) {
        this.finishH = finishH;
    }

    long getFinishM() {
        return finishM;
    }

    void setFinishM(long finishM) {
        this.finishM = finishM;
    }

    long getH4t() {
        return h4t;
    }

    void setH4t(long h4t) {
        this.h4t = h4t;
    }

    long getM4t() {
        return m4t;
    }

    void setM4t(long m4t) {
        this.m4t = m4t;
    }
}
