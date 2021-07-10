package com.example.harpreetcambriancoursesapp;

public class professor {
    public String name;
    public String email;
    public String path;
    public String mobile;
    public String under_dept;
    professor(){
        name="";
        email = "";
    }
    public professor(String name,String email,String path , String mobile,String under_dept)
    {
        this.name=name;
        this.email = email;
        this.path = path;
        this.mobile=mobile;
        this.under_dept=under_dept;
    }


}