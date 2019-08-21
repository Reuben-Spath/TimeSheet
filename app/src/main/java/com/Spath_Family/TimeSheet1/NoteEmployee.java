package com.Spath_Family.TimeSheet1;

import com.google.firebase.database.Exclude;

public class NoteEmployee {

    private String documentId;

    private boolean ifLunch;
    private boolean ifHoliday;
    private boolean ifSick;

    private float start;
    private float finish;
    private String start_s;
    private String finish_s;
    private String empCode;

    NoteEmployee() {
        //public no-arg constructor needed
    }

    NoteEmployee(float start, float finish, boolean ifLunch, String start_s, String finish_s) {
        this.start = start;
        this.finish = finish;
        this.ifLunch = ifLunch;
        this.start_s = start_s;
        this.finish_s = finish_s;

    }

    @Exclude
    String getDocumentId() {
        return documentId;
    }

    void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    boolean isIfSick() {
        return ifSick;
    }

    void setIfSick(boolean ifSick) {
        this.ifSick = ifSick;
    }

    boolean isIfHoliday() {
        return ifHoliday;
    }

    void setIfHoliday(boolean ifHoliday) {
        this.ifHoliday = ifHoliday;
    }

    public float getStart() {
        return start;
    }

    public void setStart(float start) {
        this.start = start;
    }

    public float getFinish() {
        return finish;
    }

    public void setFinish(float finish) {
        this.finish = finish;
    }

    boolean isIfLunch() {
        return ifLunch;
    }

    void setIfLunch(Boolean lunch) {
        this.ifLunch = lunch;
    }

    String getStart_s() {
        return start_s;
    }

    public void setStart_s(String start_s) {
        this.start_s = start_s;
    }

    String getFinish_s() {
        return finish_s;
    }

    public void setFinish_s(String finish_s) {
        this.finish_s = finish_s;
    }

    public String getEmpcode() {
        return empCode;
    }

    public void setEmpcode(String empcode) {
        this.empCode = empcode;
    }
}