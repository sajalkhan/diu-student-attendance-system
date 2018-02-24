package com.newdeveloper.new_database_project;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by User on 7/1/2017.
 */

public class Show_subject_list extends AppCompatActivity {

    Subject_List_item item;
    private RecyclerView recyclerView;
    private List<Subject_List_item>listItems;
    private RecyclerView.Adapter recycle_view_Adapter;

    ListView listView;
    private SQLiteDatabase db;
    public static String subject_table=null;
    private ArrayList<Subject_List_item> marks;
    private show_subject_list_listview adapter;
    private static int SPLASH_TIME_OUT = 2000;
    public  static String[] name_list=new String[100];


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_subject_list);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        //date

        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault());
        SimpleDateFormat df = new SimpleDateFormat("                                dd/MM/yyyy");
        String currentDate = df.format(c.getTime());
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(currentDate);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                onBackPressed();
            }
        });


        db = openOrCreateDatabase("TEACHER_db", Context.MODE_PRIVATE, null);


        recyclerView= (RecyclerView) findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listItems=new ArrayList<>();


        try {
            Cursor cursor = db.query("COURSE_TITLE", null, null, null, null, null, null);
            cursor.close();
        } catch (SQLException ex) {
            Log.e("COURSE_TITLE", " Table chilo na");
            db.execSQL("CREATE TABLE IF NOT EXISTS COURSE_TITLE "+ " (id integer primary key autoincrement,table_name text,section text,course_title text)");
        }

        Cursor result = db.rawQuery("SELECT * FROM COURSE_TITLE", null);

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
            String subject=result.getString(1);
            String course_name=result.getString(3)+" ( "+result.getString(2)+" )";
            item = new Subject_List_item(subject,course_name);
            listItems.add(item);
        }

        DefaultItemAnimator animator=new DefaultItemAnimator();
        animator.setRemoveDuration(500);
        animator.setMoveDuration(200);

        recycle_view_Adapter=new show_subject_list_listview(listItems,this);
        recyclerView.setItemAnimator(animator);
        recyclerView.setAdapter(recycle_view_Adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }


}
