package com.newdeveloper.new_database_project;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import static com.newdeveloper.new_database_project.blank_fragment.total_class;

/**
 * Created by User on 6/29/2017.
 */

public class Two_column_listview extends ArrayAdapter<Marks_item> {

    private int id;
    private Activity context;
    ArrayList<Marks_item> array;

    public Two_column_listview(Activity context, int resource, ArrayList<Marks_item> objects) {
        super(context, resource, objects);
        this.context = context;
        this.id = resource;
        this.array = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(id, null);
        }
        final Marks_item item = array.get(position);
        TextView name = (TextView) convertView.findViewById(R.id.show_student_id);
        TextView marks = (TextView) convertView.findViewById(R.id.marks);


        double final_marks=(7/(float)total_class)*item.marks;
        //Log.e(String.valueOf(7/(float)total_class),String.valueOf(item.marks));


        name.setText(String.valueOf(position+1)+". "+item.student_name);
        marks.setText(String.valueOf(Math.ceil(final_marks)));
        return convertView;
    }
}
