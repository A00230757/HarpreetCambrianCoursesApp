package com.example.harpreetcambriancoursesapp;

public class course {
    public String coursecode;
    public String name;
    public String description;
    public String path;
    public String under_dept;
    public String professor;
    course(){
        name="";
        description = "";
    }
    public course(String coursecode ,String name,String description,String path , String under_dept, String professor)
    {
        this.coursecode=coursecode;
        this.name=name;
        this.description = description;
        this.path = path;
        this.under_dept=under_dept;
        this.professor=professor;
    }


}