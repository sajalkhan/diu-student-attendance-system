package com.newdeveloper.new_database_project;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.newdeveloper.new_database_project.blank_fragment.total_class;


/**
 * Created by User on 3/15/2017.
 */

public class ListViewAdapter extends ArrayAdapter<item> {

    private FragmentActivity context;
    ArrayList<item> array;
    private int id;

    public ListViewAdapter(FragmentActivity context, int resource, ArrayList<item> objects) {
        super(context, resource, objects);
        this.context = context;
        this.id = resource;
        this.array = objects;
    }

    static class ViewHolder {
        protected TextView name, sid;
        protected CheckBox checkBox;
    }

    static class ViewHolder2 {
        protected TextView class_number, date;
        protected CheckBox checkBox2;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        ViewHolder2 viewHolder2 = null;

        if (position == 0) {

            viewHolder2 = new ViewHolder2();

            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.attendance_layout_header, parent, false);

            viewHolder2.checkBox2 = (CheckBox) convertView.findViewById(R.id.select_all);
            viewHolder2.class_number = (TextView) convertView.findViewById(R.id.class_number);
            viewHolder2.date = (TextView) convertView.findViewById(R.id.head_time);


            viewHolder2.checkBox2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(((CheckBox)v).isChecked())
                    {
                        for (int i = 0; i < array.size(); i++) {
                            array.get(i).setCheck(true);
                            notifyDataSetChanged();
                        }
                    }
                    else
                    {
                        for (int i = 0; i < array.size(); i++) {
                            array.get(i).setCheck(false);
                            notifyDataSetChanged();
                        }
                    }
                }
            });

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy");

            convertView.setTag(viewHolder2);
            convertView.setTag(R.id.class_number, viewHolder2.class_number);
            convertView.setTag(R.id.select_all, viewHolder2.checkBox2);
            convertView.setTag(R.id.head_time, viewHolder2.date);

            viewHolder2.checkBox2.setTag(position);
            viewHolder2.checkBox2.setChecked(array.get(position).isCheck());
            viewHolder2.class_number.setText(total_class + "");
            viewHolder2.date.setText(df.format(c.getTime()));

        } else if (position != 0) {

            viewHolder = new ViewHolder();

            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(id, null);

            //final item item = array.get(position);

            viewHolder.name = (TextView) convertView.findViewById(R.id.Student_Name);
            viewHolder.sid = (TextView) convertView.findViewById(R.id.Student_Id);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.check_box);

            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    int getPosition = (Integer) buttonView.getTag();
                    array.get(getPosition).setCheck(buttonView.isChecked());

                    //getProduct((Integer) buttonView.getTag()).isCheck = isChecked;
                }
            });

            if (getItem(position).getName().length() == 12) {
                convertView.setBackgroundColor(Color.parseColor("#ff9800"));
            } else {
                convertView.setBackgroundColor(Color.parseColor("#9ccc65"));
            }

            convertView.setTag(viewHolder);
            convertView.setTag(R.id.Student_Name, viewHolder.name);
            convertView.setTag(R.id.Student_Id, viewHolder.sid);
            convertView.setTag(R.id.check_box, viewHolder.checkBox);


            viewHolder.checkBox.setTag(position);
            viewHolder.checkBox.setChecked(array.get(position).isCheck());
            viewHolder.name.setText(String.valueOf(position) + ". " + array.get(position).getName().replace(".", ""));
            viewHolder.sid.setText(array.get(position).getId());

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    item getProduct(int position) {
        return ((item) getItem(position));
    }

    ArrayList<item> getBox() {
        ArrayList<item> ckbox = new ArrayList<item>();
        for (item p : array) {
            if (p.isCheck)
                ckbox.add(p);
        }
        return ckbox;
    }
}
