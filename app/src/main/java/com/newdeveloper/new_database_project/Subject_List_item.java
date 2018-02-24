package com.newdeveloper.new_database_project;

/**
 * Created by User on 7/1/2017.
 */

public class Subject_List_item {

    String courseName;
    String courseTitle;

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public Subject_List_item(String courseName, String courseTitle) {
        this.courseName = courseName;
        this.courseTitle = courseTitle;
    }
}
