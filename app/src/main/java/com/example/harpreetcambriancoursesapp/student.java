package com.example.harpreetcambriancoursesapp;

public class student {
    public String studentid;
    public String name;
    public String email;
    public String path;
    public String mobile;
    public String under_dept;
    student(){
        name="";
        email = "";
    }
    public student(String studentid ,String name,String email,String path , String mobile,String under_dept)
    {
        this.studentid = studentid;
        this.name=name;
        this.email = email;
        this.path = path;
        this.mobile=mobile;
        this.under_dept=under_dept;
    }


}