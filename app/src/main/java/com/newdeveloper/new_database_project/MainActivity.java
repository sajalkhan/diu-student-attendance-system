package com.newdeveloper.new_database_project;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

public class MainActivity extends AppCompatActivity {

    //EditText e1,e2;
    TextView date;
    private SQLiteDatabase db;
    public static final int requestcode = 1;
    public static int check_total_add_or_delete_item=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = openOrCreateDatabase("TEACHER_db", Context.MODE_PRIVATE, null);

        date = (TextView) findViewById(R.id.date);


        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault());
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = df.format(c.getTime());
        date.setText(currentDate);

    }


    public void file_upload(View view) {

        Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
        fileintent.setType("gagt/sdf");
        try {
            startActivityForResult(fileintent, requestcode);
        } catch (ActivityNotFoundException e) {
            Log.e("tag", "No activity can handle picking a file. Showing alternatives.");
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        switch (requestCode) {

            case requestcode:
                String filepath = data.getData().getPath();

                //file name check korci
                int ln = filepath.length();
                StringBuilder stringBuilder = new StringBuilder().append(filepath.charAt(ln - 3)).append(filepath.charAt(ln - 2)).append(filepath.charAt(ln - 1));
                String ck = stringBuilder.toString();

                if (!ck.equals("csv") && !ck.equals("xls")) {
                    Toast.makeText(this, "Only Microsoft Office Excel csv/xls file Allowed !!", Toast.LENGTH_LONG).show();
                    return;
                }

                if (ck.equals("csv")) {
                    if (resultCode == RESULT_OK) {

                        try {

                            FileReader file = new FileReader(filepath);
                            BufferedReader buffer = new BufferedReader(file);
                            ContentValues contentValues = new ContentValues();
                            String line = "";
                            int count = 0;
                            int check = 0;
                            int row = 0;
                            while ((line = buffer.readLine()) != null) {
                                String[] str = line.split(",", 11);
                                if (count == 0) {
                                    count++;
                                    continue;
                                }

                                row++;

                                String id = str[0].toString();
                                String name = str[1].toString();
                                String section = str[4].toString();
                                String course_title = str[6].toString();
                                String TABLE_NAME = str[7].toString();

                                if (count == 1) {
                                    TABLE_NAME = TABLE_NAME.replace(" ", "");
                                    TABLE_NAME = TABLE_NAME.toUpperCase();
                                    db.execSQL("drop table if exists " + TABLE_NAME + section);
                                    db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + section + " (id integer primary key autoincrement,Name text,Student_id text,Course_title text,Section text)");

                                    //db.execSQL("drop table if exists " + TABLE_NAME + "Graph");
                                    try {
                                        Cursor cursor = db.query(TABLE_NAME + section + "Graph", null, null, null, null, null, null);
                                        cursor.close();
                                    } catch (SQLException ex) {
                                        check = 1;
                                        Log.e(TABLE_NAME + "Graph", " Table chilo na");
                                        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + section + "Graph" + " (id integer primary key autoincrement,Name text,Student_id text,Marks integer)");
                                    }

                                    //table 3
                                    try {
                                        Cursor cursor = db.query("COURSE_TITLE", null, null, null, null, null, null);
                                        cursor.close();
                                    } catch (SQLException ex) {
                                        check = 1;
                                        Log.e("COURSE_TITLE", " Table chilo na");
                                        db.execSQL("CREATE TABLE IF NOT EXISTS COURSE_TITLE " + " (id integer primary key autoincrement,table_name text,section text,course_title text)");
                                    }

                                    //kono nirdisto item kono column a ache kina seta check korci
                                    Cursor cursor = db.rawQuery("select * from COURSE_TITLE " + " where table_name = ? And section = ? And course_title = ?", new String[]{TABLE_NAME, section, course_title});
                                    Boolean exists = cursor.moveToFirst();
                                    if (exists == false) {

                                        ContentValues contentValues2 = new ContentValues();
                                        contentValues2.put("table_name", TABLE_NAME);
                                        contentValues2.put("section", section);
                                        contentValues2.put("course_title", course_title);
                                        db.insert("COURSE_TITLE", null, contentValues2);
                                    }

                                }

                                contentValues.put("Name", name);
                                contentValues.put("Student_id", id);
                                contentValues.put("Course_title", course_title);
                                contentValues.put("Section", section);
                                db.insert(TABLE_NAME + section, null, contentValues);

                                if (check == 1) {
                                    ContentValues contentValues1 = new ContentValues();
                                    contentValues1.put("Name", name);
                                    contentValues1.put("Student_id", id);
                                    contentValues1.put("Marks", 0);
                                    db.insert(TABLE_NAME + section + "Graph", null, contentValues1);
                                }
                                count++;

                            }
                            Toast.makeText(this, "Data insert Successfully!! ", Toast.LENGTH_LONG).show();

                        } catch (IOException e) {
                            //Toast.makeText(this,"something worng!!",Toast.LENGTH_LONG).show();
                            if (db.inTransaction())
                                db.endTransaction();
                        }
                    } else {
                        if (db.inTransaction())
                            db.endTransaction();
                    }
                }

                //read xls file
                if (ck.equals("xls")) {
                    String TABLE_NAME = "";
                    if (resultCode == RESULT_OK) {
                        File file = new File(filepath);
                        Workbook w = null;
                        try {

                            WorkbookSettings ws = new WorkbookSettings();
                            ws.setGCDisabled(true);

                            w = Workbook.getWorkbook(file);
                            ContentValues contentValues = new ContentValues();
                            Sheet sheet = w.getSheet(0);
                            int row = sheet.getRows();


                            int check = 0;
                            for (int i = 1; i < row; i++) {

                                Cell A = sheet.getCell(0, i);
                                Cell B = sheet.getCell(1, i);
                                Cell C = sheet.getCell(4, i);
                                Cell D = sheet.getCell(6, i);
                                Cell E = sheet.getCell(7, i);

                                String id = A.getContents();
                                String name = B.getContents();
                                String section = C.getContents();
                                String course_title = D.getContents();

                                if (i == 1) {

                                    TABLE_NAME = E.getContents();
                                    TABLE_NAME = TABLE_NAME.replace(" ", "");
                                    TABLE_NAME = TABLE_NAME.toUpperCase();
                                    db.execSQL("Drop table if exists " + TABLE_NAME + section);
                                    db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + section + " (id integer primary key autoincrement,Name text,Student_id text,Course_title text,Section text)");

                                    //db.execSQL("drop table if exists " + TABLE_NAME + "Graph");
                                    try {
                                        Cursor cursor = db.query(TABLE_NAME + section + "Graph", null, null, null, null, null, null);
                                        cursor.close();
                                    } catch (SQLException ex) {
                                        check = 1;
                                        Log.e(TABLE_NAME + "Graph", " Table chilo na");
                                        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + section + "Graph" + " (id integer primary key autoincrement,Name text,Student_id text,Marks integer)");
                                    }

                                    //table 3
                                    try {
                                        Cursor cursor = db.query("COURSE_TITLE", null, null, null, null, null, null);
                                        cursor.close();
                                    } catch (SQLException ex) {
                                        check = 1;
                                        Log.e("COURSE_TITLE", " Table chilo na");
                                        db.execSQL("CREATE TABLE IF NOT EXISTS COURSE_TITLE " + " (id integer primary key autoincrement,table_name text,section text,course_title text)");
                                    }

                                    //kono nirdisto item kono column a ache kina seta check korci
                                    Cursor cursor = db.rawQuery("select * from COURSE_TITLE " + " where table_name = ? And section = ? And course_title = ?", new String[]{TABLE_NAME, section, course_title});
                                    Boolean exists = cursor.moveToFirst();
                                    if (exists == false) {
                                        ContentValues contentValues2 = new ContentValues();
                                        contentValues2.put("table_name", TABLE_NAME);
                                        contentValues2.put("section", section);
                                        contentValues2.put("course_title", course_title);
                                        db.insert("COURSE_TITLE", null, contentValues2);
                                    }

                                }

                                contentValues.put("Name", name);
                                contentValues.put("Student_id", id);
                                contentValues.put("Course_title", course_title);
                                contentValues.put("Section", section);
                                db.insert(TABLE_NAME + section, null, contentValues);

                                if (check == 1) {
                                    ContentValues contentValues1 = new ContentValues();
                                    contentValues1.put("Name", name);
                                    contentValues1.put("Student_id", id);
                                    contentValues1.put("Marks", 0);
                                    db.insert(TABLE_NAME + section + "Graph", null, contentValues1);
                                }

                            }
                            Toast.makeText(this, "Data insert Successfully!! ", Toast.LENGTH_LONG).show();

                        } catch (IOException e) {
                            Log.e(e.toString(), " error!");
                            e.printStackTrace();
                        } catch (BiffException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (db.inTransaction())
                            db.endTransaction();
                    }
                }
        }
    }

    public void give_attendance(View view) {

        Intent intent = new Intent(MainActivity.this, Show_subject_list.class);
        startActivity(intent);
    }
}
