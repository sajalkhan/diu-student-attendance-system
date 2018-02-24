package com.newdeveloper.new_database_project;

/**
 * Created by User on 7/19/2017.
 */

public class delete_information_item {

    String student_Name;
    String student_Id;

    public delete_information_item(String student_Name, String student_Id) {
        this.student_Name = student_Name;
        this.student_Id = student_Id;
    }

    public String getStudent_Name() {
        return student_Name;
    }

    public void setStudent_Name(String student_Name) {
        this.student_Name = student_Name;
    }

    public String getStudent_Id() {
        return student_Id;
    }

    public void setStudent_Id(String student_Id) {
        this.student_Id = student_Id;
    }
}