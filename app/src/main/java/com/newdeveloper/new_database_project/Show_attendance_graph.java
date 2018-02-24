package com.newdeveloper.new_database_project;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.newdeveloper.new_database_project.Show_subject_list.subject_table;

public class Show_attendance_graph extends AppCompatActivity {

    public static String table=null;
    BarChart barChart;
    int[] data=new int[35];
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_attendance_graph);

        db = openOrCreateDatabase("TEACHER_db", Context.MODE_PRIVATE, null);


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

        if(table==null)
        {
            Toast.makeText(this,"No course found to show !!",Toast.LENGTH_LONG).show();
            return;
        }
        //Log.e(table," fuck");

        barChart= (BarChart) findViewById(R.id.barchart);
        ArrayList<BarEntry> barEntries=new ArrayList<>();

        //date
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());


        // table theke proti ta column er nam ber korci
        Cursor cursor = db.query(table+"Graph", null, null, null, null, null, null);
        String[] columnNames = cursor.getColumnNames();
        int len=columnNames.length;

        for(int i=0;i<31;i++)data[i]=0;
        for(int i=4;i<len;i++)
        {
            Cursor cursor1=db.query(table+"Graph", new String[] {columnNames[i]},columnNames[i]+" = 'P'",null,null,null,null);
            int total_item=cursor1.getCount();
            data[i-4]=total_item;
        }

        BarDataSet bardataset=new BarDataSet(barEntries,formattedDate);
        ArrayList<String> theDates=new ArrayList<>();

        barEntries.add(new BarEntry((float)data[0],0));
        barEntries.add(new BarEntry((float)data[1],1));
        barEntries.add(new BarEntry((float)data[2],2));
        barEntries.add(new BarEntry((float)data[3],3));
        barEntries.add(new BarEntry((float)data[4],4));
        barEntries.add(new BarEntry((float)data[5],5));
        barEntries.add(new BarEntry((float)data[6],6));
        barEntries.add(new BarEntry((float)data[7],7));
        barEntries.add(new BarEntry((float)data[8],8));
        barEntries.add(new BarEntry((float)data[9],9));
        barEntries.add(new BarEntry((float)data[10],10));
        barEntries.add(new BarEntry((float)data[11],11));
        barEntries.add(new BarEntry((float)data[12],12));
        barEntries.add(new BarEntry((float)data[13],13));
        barEntries.add(new BarEntry((float)data[14],14));
        barEntries.add(new BarEntry((float)data[15],15));
        barEntries.add(new BarEntry((float)data[16],16));
        barEntries.add(new BarEntry((float)data[17],17));
        barEntries.add(new BarEntry((float)data[18],18));
        barEntries.add(new BarEntry((float)data[19],19));
        barEntries.add(new BarEntry((float)data[20],20));
        barEntries.add(new BarEntry((float)data[21],21));
        barEntries.add(new BarEntry((float)data[22],22));
        barEntries.add(new BarEntry((float)data[23],23));
        barEntries.add(new BarEntry((float)data[24],24));
        barEntries.add(new BarEntry((float)data[25],25));
        barEntries.add(new BarEntry((float)data[26],26));
        barEntries.add(new BarEntry((float)data[27],27));
        barEntries.add(new BarEntry((float)data[28],28));
        barEntries.add(new BarEntry((float)data[29],29));
        barEntries.add(new BarEntry((float)data[30],30));

        for(int i=0;i<=30;i++)
        {
            theDates.add(String.valueOf(i));
        }

        BarData theData=new BarData(theDates,bardataset);
        barChart.setData(theData);

        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
    }


}
