package com.newdeveloper.new_database_project;

/**
 * Created by User on 6/29/2017.
 */

public class Marks_item {

    String student_name;
    int marks;

    public Marks_item(String student_name, int marks) {
        this.student_name = student_name;
        this.marks = marks;
    }

    public String getStudent_name() {
        return student_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }

    public int getMarks() {
        return marks;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }
}
