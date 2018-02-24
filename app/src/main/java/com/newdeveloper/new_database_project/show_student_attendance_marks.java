package com.newdeveloper.new_database_project;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.newdeveloper.new_database_project.Show_attendance_graph.table;
import static com.newdeveloper.new_database_project.Show_subject_list.subject_table;

/**
 * Created by User on 6/28/2017.
 */

public class show_student_attendance_marks extends AppCompatActivity {


    Marks_item item;
    ListView listView;
    private TextView subject;
    private SQLiteDatabase db;
    private ArrayList<Marks_item>marks;
    private Two_column_listview adapter;
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_attendance_marks_list);

        db = openOrCreateDatabase("TEACHER_db", Context.MODE_PRIVATE, null);

        listView = (ListView) findViewById(R.id.show_list);
        //subject= (TextView) findViewById(R.id.subjectTv);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        int count=0;
        String course_name=null,section=null;
        Cursor result = db.rawQuery("SELECT * FROM " + subject_table, null);
        while (result.moveToNext()) {
            if(count==0)
            {
                course_name=result.getString(result.getColumnIndex("Course_title"));
                section=result.getString(result.getColumnIndex("Section"));
                count++;
            }
        }

        course_name=course_name.replace("Programming and","");
        course_name+=" ( "+section+" )";
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(course_name);
        result.close();


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                onBackPressed();
            }
        });

        marks=new ArrayList<>();


        //Log.e("Table",String.valueOf(table));
        if(table==null)
        {
            Toast.makeText(this, "Please First Choose Any subject!!", Toast.LENGTH_LONG).show();
            //show display at a specific time
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    finish();
                }
            }, SPLASH_TIME_OUT);
            return;
        }

        adapter = new Two_column_listview(show_student_attendance_marks.this,R.layout.row2,marks);
        listView.setAdapter(adapter);

        result = db.rawQuery("SELECT * FROM " + table + "Graph", null);
        if (result.getCount() == 0) {
            Toast.makeText(this, "No data found!!", Toast.LENGTH_SHORT).show();
            //show display at a specific time
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    finish();
                }
            }, SPLASH_TIME_OUT);
            return;
        }

        while(result.moveToNext())
        {
            item = new Marks_item(result.getString(2), result.getInt(3));
            adapter.add(item);
        }
    }
}
