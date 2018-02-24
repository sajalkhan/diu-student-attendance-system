package com.newdeveloper.new_database_project;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.newdeveloper.new_database_project.Show_attendance_graph.table;
import static com.newdeveloper.new_database_project.Show_subject_list.subject_table;

/**
 * Created by User on 5/21/2017.
 */

public class blank_fragment extends Fragment implements View.OnClickListener {

    item item;
    public static int total_class = 1;
    FloatingActionButton savebutton;
    ListView listView;
    private String Todays_Date;
    private SQLiteDatabase db;
    private ArrayList<item> array;
    private ListViewAdapter adapter;
    ArrayList<String> selectedItem = new ArrayList<>();
    SharedPreferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_attendance, container, false);

        preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
       /* SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
*/


        //submit = (Button) view.findViewById(R.id.submit);
        //submit.setOnClickListener(this);
        savebutton = (FloatingActionButton) view.findViewById(R.id.save_button);
        savebutton.setOnClickListener(this);

        savebutton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4caf50")));

        //set layout info
        listView = (ListView) view.findViewById(R.id.list);
        //set_subject = (TextView) view.findViewById(R.id.attendance_subject_name);
        listView = (ListView) view.findViewById(R.id.list);

        array = new ArrayList<>();


        db = getActivity().openOrCreateDatabase("TEACHER_db", android.content.Context.MODE_PRIVATE, null);


        //use for refrash fragment
        //FragmentTransaction ft = getFragmentManager().beginTransaction();
        //ft.detach(blank_fragment.this).attach(blank_fragment.this).commit();

        adapter = new ListViewAdapter(getActivity(), R.layout.row, array);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                if (scrollState == SCROLL_STATE_FLING) {
                    savebutton.hide();
                } else {
                    savebutton.show();
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });


        table = subject_table;

        /// Get sharedpreferences value
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("subject_code", table);
        editor.commit();


/*          ---------------------send data--------------------
            Intent intent=new Intent(getActivity(),Show_attendance_graph.class);
            intent.putExtra("data",table);
            startActivity(intent);*/

        //Log.e("fuck table ", table);

        //Log.e("Button click", " save button");
        try {
            final Cursor result = db.rawQuery("SELECT * FROM " + table, null);
            if (result.getCount() == 0) {
                Toast.makeText(getActivity(), "No data found!!", Toast.LENGTH_SHORT).show();
            }


            //Total koyta class hoise seta ber korci
            Cursor cursor = db.rawQuery("select * from " + table + "Graph", null);
            if (cursor.getColumnCount() > 4) {
                total_class = cursor.getColumnCount() - 4;
            }

            //Log.e("Total class hoyce", String.valueOf(total_class));

            ////////////////////////////
            //
            //Count number of column
            //Cursor cursor = db.query(table, null, null, null, null, null, null);
            //int x = cursor.getColumnCount();
            //Log.e(Integer.toString(x), " total column");
            //
            ///////////////////////////


                /*Calendar c = Calendar.getInstance();

                SimpleDateFormat df = new SimpleDateFormat("MMM_dd");
                Todays_Date = df.format(c.getTime());
                Log.e(Todays_Date, "Today date");


                //create new table
                try {
                    String upgradeQuery = "ALTER TABLE " + table + "Graph" + " ADD COLUMN " + Todays_Date + " text";
                    db.execSQL(upgradeQuery);
                } catch (SQLException ex) {
                    Log.e("Column ache", "oh no");
                }
*/
            //add first item null
            item=new item("","",true);
            adapter.add(item);

            while (result.moveToNext()) {

                item = new item(result.getString(2), result.getString(1), true);
                adapter.add(item);
            }

        } catch (SQLException e) {
            Toast.makeText(getActivity(), "NO INFORMATION FOUND..!", Toast.LENGTH_LONG).show();
        }


        return view;
    }


    public void onClick(View v) {

        if (v.getId() == R.id.save_button) {

            /*if (choose_subject.getText().toString().equals("")) {
                Toast.makeText(getActivity(), "Please enter subject code!!", Toast.LENGTH_LONG).show();
                return;
            }*/

            String item = "";
            String new_table = table;


            int total_checked_item = 0;

            for (item i : adapter.getBox()) {
                if (i.isCheck()) total_checked_item++;
            }

            if (total_checked_item > 0) {

                //------------------------Attendance and graph table------------------------------------//
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("MMM_dd");
                Todays_Date = df.format(c.getTime());
                Log.e(Todays_Date, "Today date");


                //create new column
                try {
                    String upgradeQuery = "ALTER TABLE " + table + "Graph" + " ADD COLUMN " + Todays_Date + " text";
                    db.execSQL(upgradeQuery);
                } catch (SQLException ex) {
                    Log.e("Column ache", "oh no");
                }
                //----------------------------------------------------------------------------------------//


                for (item i : adapter.getBox()) {

                    if (i.isCheck  && !i.name.equals("")) {
                        item = i.name.replace(".", "");
                        String present = "P";
                        String Table = new_table + "Graph";
                        String col = Todays_Date;


                        //kono nirdisto item kono column a ache kina seta check korci
                        Cursor cursor = db.rawQuery("select * from " + Table + " where Student_id = ?", new String[]{item});
                        boolean exists = cursor.moveToFirst();

                        if (exists == true) {

                            //Log.e(item, " ache");
                            String marks = cursor.getString(3);
                            int Marks = Integer.parseInt(marks);

                            //nirdisto student present ache kina check korci.. jodi present na thake tahole marks dibo and present dibo
                            Cursor cursor1 = db.rawQuery("select * from " + Table + " where Student_id = ? and " + Todays_Date + " = ?", new String[]{item, "P"});
                            Boolean exist = cursor1.moveToFirst();

                            if (exist == false) {
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(col, present);
                                contentValues.put("Marks", ++Marks);
                                db.update(Table, contentValues, "Student_id = ?", new String[]{item});
                            }
                        } else {
                            //Log.e(item, " nai");

                            Cursor cursor1 = db.rawQuery("select * from " + Table + " where Student_id = ? and " + Todays_Date + " = ?", new String[]{item, "P"});
                            Boolean exist = cursor1.moveToFirst();

                            if (exist == false) {

                                String marks = cursor1.getString(3);
                                int Marks = Integer.parseInt(marks);

                                ContentValues contentValues = new ContentValues();
                                contentValues.put(col, present);
                                contentValues.put("Marks", ++Marks);
                                db.update(Table, contentValues, "Student_id = ?", new String[]{item});
                            }
                        }
                    }
                }


                //ekta nirdisto column a koto ta item ache seta ber korar jonno
                Cursor cursor = db.query(new_table + "Graph", new String[]{Todays_Date}, Todays_Date + " = 'P'", null, null, null, null);
                int total_item = cursor.getCount();
                //Log.e(Integer.toString(total_item), " found");
                cursor.close();

                Toast.makeText(getActivity(), "Attendance information update", Toast.LENGTH_SHORT).show();

            }

        }
    }

}