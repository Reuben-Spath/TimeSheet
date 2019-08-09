package com.Spath_Family.TimeSheet1;

public class EmpDoc {

    private String empCode;
    private String name;

    EmpDoc(String name, String empCode) {
        this.name = name;
        this.empCode = empCode;
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
}

