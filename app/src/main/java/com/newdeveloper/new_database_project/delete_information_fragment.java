package com.newdeveloper.new_database_project;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 5/21/2017.
 */

public class delete_information_fragment extends Fragment{


    private SQLiteDatabase db;
    delete_information_item item;
    private RecyclerView recyclerView;
    private List<delete_information_item> listItems;
    private RecyclerView.Adapter recycle_view_Adapter;

    private delete_information_item adapter;
    String subject;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.delete_info, container, false);
        SharedPreferences preferences=getActivity().getPreferences(Context.MODE_PRIVATE);
        subject=preferences.getString("subject_code",null);

        db = getActivity().openOrCreateDatabase("TEACHER_db", android.content.Context.MODE_PRIVATE, null);


        recyclerView= (RecyclerView)view.findViewById(R.id.recycle_view_from_delete_info_activity);
        recyclerView.setHasFixedSize(true);

        listItems=new ArrayList<>();



        //create unregrestered student information
        try {
            Cursor cursor = db.query(subject+"UNREGISTERED_STUDENT_INFO_TABLE ", null, null, null, null, null, null);
            cursor.close();
        } catch (SQLException ex) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + subject+"UNREGISTERED_STUDENT_INFO_TABLE" + " (id integer primary key autoincrement,Student_id text,Name text)");
        }

        Cursor result = db.rawQuery("SELECT * FROM "+subject+"UNREGISTERED_STUDENT_INFO_TABLE", null);

        while(result.moveToNext())
        {
            String studentId=result.getString(1);
            String studentName=result.getString(2);
            item = new delete_information_item(studentName,studentId);
            listItems.add(item);
        }


        DefaultItemAnimator animator=new DefaultItemAnimator();
        animator.setRemoveDuration(500);
        animator.setMoveDuration(200);

        recycle_view_Adapter=new delete_information_listview(listItems,getActivity());
        recyclerView.setItemAnimator(animator);
        recyclerView.setAdapter(recycle_view_Adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        return view;
    }

}
