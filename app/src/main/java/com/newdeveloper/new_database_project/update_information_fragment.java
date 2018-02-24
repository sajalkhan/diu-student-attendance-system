package com.newdeveloper.new_database_project;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static com.newdeveloper.new_database_project.Show_attendance_graph.table;
import static com.newdeveloper.new_database_project.MainActivity.check_total_add_or_delete_item;

/**
 * Created by User on 5/21/2017.
 */

public class update_information_fragment extends Fragment implements View.OnClickListener {

    EditText student_name, student_id;
    Button upload;
    String subject;
    private SQLiteDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.data_update, container, false);

        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        subject = preferences.getString("subject_code", null);
        //Log.e(subject, " shared data");

        student_name = (EditText) view.findViewById(R.id.update_name);
        student_id = (EditText) view.findViewById(R.id.update_id);
        upload = (Button) view.findViewById(R.id.upload_data);
        upload.setOnClickListener(this);

        db = getActivity().openOrCreateDatabase("TEACHER_db", android.content.Context.MODE_PRIVATE, null);
        return view;
    }

    @Override
    public void onClick(View v) {

        try {
            Cursor result = db.rawQuery("SELECT * FROM " + subject, null);
            if (result.getCount() == 0) {
                Toast.makeText(getActivity(), "No data found!!", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (SQLException exception) {
            Toast.makeText(getActivity(), subject + " doesn't exist in our database..!", Toast.LENGTH_LONG).show();
            return;
        }

        if (student_name.getText().toString().equals("") || student_id.getText().toString().equals("") || student_id.length() != 11) {
            Toast.makeText(getActivity(), "Please Give All information Correctly", Toast.LENGTH_LONG).show();
            return;
        }




        //create unregrestered student information
        try {
            Cursor cursor = db.query(subject+"UNREGISTERED_STUDENT_INFO_TABLE ", null, null, null, null, null, null);
            cursor.close();
        } catch (SQLException ex) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + subject+"UNREGISTERED_STUDENT_INFO_TABLE" + " (id integer primary key autoincrement,Student_id text,Name text)");
        }


        Cursor cursor1 = db.rawQuery("select * from " + table + " where Student_id = ?", new String[]{student_id.getText().toString()});
        Cursor cursor2 = db.rawQuery("select * from " + table + " where Student_id = ?", new String[]{student_id.getText().toString()+"."});
        Boolean exists=cursor1.moveToFirst();
        Boolean exists2=cursor2.moveToFirst();


        ContentValues contentValues = new ContentValues();
        if(exists==false && exists2==false)
        {
            contentValues.put("Name", student_name.getText().toString());
            contentValues.put("Student_id", student_id.getText().toString()+".");
            db.insert(subject, null, contentValues);

            //insert Unregistered student
            contentValues =new ContentValues();
            contentValues.put("Student_id",student_id.getText().toString());
            contentValues.put("Name",student_name.getText().toString());
            db.insert(subject+"UNREGISTERED_STUDENT_INFO_TABLE",null,contentValues);

            check_total_add_or_delete_item=1;
        }
        else
        {
            Toast.makeText(getActivity(),student_id.getText().toString()+" Already Registered In This Course !!",Toast.LENGTH_LONG).show();
        }

        //ai student id jodi graph table ache kina check korci.. jodi na thake then add korbo
        Cursor cursor3 = db.rawQuery("select * from " + table + "Graph" + " where Student_id = ?", new String[]{student_id.getText().toString()});
        Boolean check= cursor3.moveToFirst();
        if (check == false) {

            contentValues = new ContentValues();
            contentValues.put("Name", student_name.getText().toString());
            contentValues.put("Student_id", student_id.getText().toString());
            contentValues.put("Marks", 0);
            db.insert(table + "Graph", null, contentValues);

            Toast.makeText(getActivity(), "Data insert Successfully..!!", Toast.LENGTH_LONG).show();
        }
        //db.update(table,contentValues,"Student_id = ?",new String[]{String.valueOf(student_id)});

    }
}
