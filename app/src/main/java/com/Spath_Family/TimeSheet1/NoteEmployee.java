package com.Spath_Family.TimeSheet1;

import com.google.firebase.database.Exclude;

public class NoteEmployee {

    private String documentId;

    private float start;
    private float finish;

    NoteEmployee() {
        //public no-arg constructor needed
    }

    NoteEmployee(float start, float finish) {
        this.start = start;
        this.finish = finish;
    }

    @Exclude
    String getDocumentId() {
        return documentId;
    }

    void setDocumentId(String documentId) {
        this.documentId = documentId;
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

}