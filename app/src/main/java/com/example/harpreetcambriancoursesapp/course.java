package com.example.harpreetcambriancoursesapp;

public class course {
    public String name;
    public String description;
    public String path;
    public String under_dept;
    course(){
        name="";
        description = "";
    }
    public course(String name,String description,String path , String under_dept)
    {
        this.name=name;
        this.description = description;
        this.path = path;
        this.under_dept=under_dept;
    }


}
