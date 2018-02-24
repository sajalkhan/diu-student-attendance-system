package com.newdeveloper.new_database_project;

/**
 * Created by User on 3/15/2017.
 */

public class item {
    String name;
    String id;
    boolean isCheck;

    public item(String name, String id, boolean isCheck) {
        this.name = name;
        this.id = id;
        this.isCheck = isCheck;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }
}
