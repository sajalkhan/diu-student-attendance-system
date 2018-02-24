package com.newdeveloper.new_database_project;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import static com.newdeveloper.new_database_project.Show_attendance_graph.table;
import static com.newdeveloper.new_database_project.MainActivity.check_total_add_or_delete_item;

/**
 * Created by User on 6/12/2017.
 */

public class Barcode_scanner extends AppCompatActivity {

    private SQLiteDatabase db;
    private static long back_pressed;
    private static int SPLASH_TIME_OUT = 2500;
    private static final int TIME_DELAY = 2000;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode_scanner);

        db = openOrCreateDatabase("TEACHER_db", android.content.Context.MODE_PRIVATE, null);
        //barcode er kaj korci

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("SCAN STUDENT ID CARD");
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {

                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();

                //show display at a specific time
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        finish();
                    }
                }, 1);


            } else {
                //Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();

                if (table == null) {
                    Toast.makeText(this, "Please First Choost Subject and code", Toast.LENGTH_LONG).show();
                    return;
                }

                Cursor cursor = db.rawQuery("select * from " + table + " where Student_id = ?", new String[]{result.getContents()});
                Cursor cursor1 = db.rawQuery("select * from " + table + " where Student_id = ?", new String[]{result.getContents() + "."});
                boolean exists = cursor.moveToFirst();
                boolean exists1 = cursor1.moveToFirst();

                if (exists == true || exists1 == true) {

                    TextView newId = (TextView) findViewById(R.id.studentid_from_barcod_scanner);
                    TextView newName = (TextView) findViewById(R.id.studentname_from_barcode_scanner);

                    if (exists == true) newName.setText(cursor.getString(1));
                    if (exists1 == true) newName.setText(cursor1.getString(1));

                    newId.setText(result.getContents());

                    // Log.e(newName.getText().toString()," new name");

                    cursor.close();
                    cursor1.close();


                    AlertDialog.Builder builder = new AlertDialog.Builder(Barcode_scanner.this);
                    View mView = getLayoutInflater().inflate(R.layout.alert_give_attendance_from_barcode_activity, null);
                    TextView student_id = (TextView) mView.findViewById(R.id.studentid_from_give_attendance_activity);
                    TextView student_Name = (TextView) mView.findViewById(R.id.studentName_from_give_attendance_activity);

                    student_id.setText(newId.getText().toString());
                    student_Name.setText(newName.getText().toString());

                    builder.setView(mView);
                    builder.setCancelable(false);
                    final AlertDialog alert1 = builder.create();
                    alert1.show();


                    TextView no = (TextView) mView.findViewById(R.id.no_from_giveAttendance_layout);
                    TextView yes = (TextView) mView.findViewById(R.id.yes_from_giveAttendance_layout);

                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            alert1.dismiss();


                            // give attendance by barcode here
                            final String studentid = result.getContents().replace(".", "");
                            //final String studentname = present_studentName.getText().toString();

                            //calendar
                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat df = new SimpleDateFormat("MMM_dd");
                            final String Todays_Date = df.format(c.getTime());
                            Log.e(Todays_Date, "Today date");

                            //create new table
                            try {
                                Log.e(Todays_Date, " new table holo");

                                String upgradeQuery = "ALTER TABLE " + table + "Graph" + " ADD COLUMN " + Todays_Date + " text";
                                db.execSQL(upgradeQuery);
                            } catch (SQLException ex) {
                                Log.e("Column ache", "oh no");
                            }

                            final String col = Todays_Date;
                            final String present = "P";

                            //kono nirdisto item kono column a ache kina seta check korci
                            final Cursor cursor_new = db.rawQuery("select * from " + table + "Graph" + " where Student_id = ?", new String[]{studentid});
                            Boolean exists = cursor_new.moveToFirst();

                            if (exists == true) {

                                Log.e(studentid, " ache");


                                String marks = cursor_new.getString(3);
                                int Marks = Integer.parseInt(marks);

                                //nirdisto student present ache kina check korci.. jodi present na thake tahole marks dibo and present dibo
                                Cursor cursor1 = db.rawQuery("select * from " + table + "Graph" + " where Student_id = ? and " + Todays_Date + " = ?", new String[]{studentid, "P"});
                                Boolean exist = cursor1.moveToFirst();

                                if (exist == false) {
                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put(col, present);
                                    contentValues.put("Marks", ++Marks);
                                    db.update(table + "Graph", contentValues, "Student_id = ?", new String[]{studentid});
                                }
                                cursor1.close();

                            } else {

                                Log.e(studentid, " nai");

                                Cursor cursor1 = db.rawQuery("select * from " + table + "Graph" + " where Student_id = ? and " + Todays_Date + " = ?", new String[]{studentid, "P"});
                                Boolean exist = cursor1.moveToFirst();

                                if (exist == false) {

                                    Log.e(studentid, " student");
                                    String marks = cursor1.getString(3);
                                    int Marks = Integer.parseInt(marks);

                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put(col, present);
                                    contentValues.put("Marks", ++Marks);
                                    db.update(table + "Graph", contentValues, "Student_id = ?", new String[]{studentid});
                                }

                                cursor1.close();


                            }

                            // show display at a specific time
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    finish();

                                }
                            }, SPLASH_TIME_OUT);


                        }
                    });

                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert1.dismiss();
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    finish();
                                }
                            }, 1);
                        }
                    });


                } else {

                    //----------------------------------Not Registered student Info-----------------------------------//

                    AlertDialog.Builder builder = new AlertDialog.Builder(Barcode_scanner.this);
                    View mView = getLayoutInflater().inflate(R.layout.student_not_found_alert_from_barcode, null);
                    TextView studentid = (TextView) mView.findViewById(R.id.not_found_studentid);
                    final String Id = result.getContents();


                    builder.setView(mView);
                    builder.setCancelable(false);
                    final AlertDialog alert1 = builder.create();
                    alert1.show();

                    TextView no = (TextView) mView.findViewById(R.id.no_from_barcodeScanner_layout);
                    TextView yes = (TextView) mView.findViewById(R.id.yes_from_barcodeScanner_layout);

                    studentid.setText(Id + " - Id Not Registered!");

                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(Barcode_scanner.this);
                            View mView = getLayoutInflater().inflate(R.layout.alert_add_new_student_by_barcode, null);
                            final EditText etname = (EditText) mView.findViewById(R.id.Student_name_by_barcod);
                            final EditText etId = (EditText) mView.findViewById(R.id.Student_Id_by_barcode);

                            TextView No = (TextView) mView.findViewById(R.id.no_from_barcode_layout);
                            TextView Yes = (TextView) mView.findViewById(R.id.yes_from_barcode_layout);

                            etId.setText(Id.toString());

                            builder.setView(mView);
                            builder.setCancelable(false);
                            final AlertDialog alert = builder.create();
                            alert.show();

                            Yes.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    alert.dismiss();
                                    alert1.dismiss();


                                    try {
                                        Cursor result = db.rawQuery("SELECT * FROM " + table, null);
                                        if (result.getCount() == 0) {
                                            Toast.makeText(Barcode_scanner.this, "No data found!!", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    } catch (SQLException exception) {
                                        Toast.makeText(Barcode_scanner.this, table + " doesn't exist in our database..!", Toast.LENGTH_LONG).show();
                                        return;
                                    }

                                    if (etname.getText().toString().equals("") || etId.getText().toString().equals("") || etId.length() != 11) {
                                        Toast.makeText(Barcode_scanner.this, "Please Give All information Correctly", Toast.LENGTH_LONG).show();
                                        return;
                                    }

                                    Cursor cursor1 = db.rawQuery("select * from " + table + " where Student_id = ?", new String[]{etId.getText().toString()});
                                    Boolean exists=cursor1.moveToFirst();

                                    ContentValues contentValues = new ContentValues();
                                    if(exists==false)
                                    {
                                        contentValues.put("Name", etname.getText().toString());
                                        contentValues.put("Student_id", etId.getText().toString()+".");
                                        db.insert(table, null, contentValues);
                                        check_total_add_or_delete_item=1;

                                        //Toast.makeText(getActivity(), "Data insert Successfully..!!", Toast.LENGTH_LONG).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(Barcode_scanner.this,etId.getText().toString()+" Already Registered In This Course !!",Toast.LENGTH_LONG).show();
                                    }

                                    //ai student id jodi graph table ache kina check korci.. jodi na thake then add korbo
                                    Cursor cursor2 = db.rawQuery("select * from " + table + "Graph" + " where Student_id = ?", new String[]{etId.getText().toString()});
                                    Boolean check= cursor2.moveToFirst();
                                    if (check == false) {

                                        contentValues = new ContentValues();
                                        contentValues.put("Name", etname.getText().toString());
                                        contentValues.put("Student_id", etId.getText().toString());
                                        contentValues.put("Marks", 0);
                                        db.insert(table + "Graph", null, contentValues);

                                        check_total_add_or_delete_item=1;

                                        Toast.makeText(Barcode_scanner.this, "Information Update Successfully!!", Toast.LENGTH_LONG).show();
                                    }
                                    //db.update(table,contentValues,"Student_id = ?",new String[]{String.valueOf(student_id)});


                                    new Handler().postDelayed(new Runnable() {
                                        public void run() {
                                            finish();
                                        }
                                    }, 1);

                                }
                            });
                            No.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alert.dismiss();
                                    alert1.dismiss();

                                    new Handler().postDelayed(new Runnable() {
                                        public void run() {
                                            finish();
                                        }
                                    }, 1);
                                }
                            });

                        }


                    });

                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            alert1.dismiss();
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    finish();
                                }
                            }, 1);
                        }
                    });
                    //check.setText("Not registered in this course..!");

                }

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onBackPressed() {

        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), "Press once again to exit!",
                    Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }
}
