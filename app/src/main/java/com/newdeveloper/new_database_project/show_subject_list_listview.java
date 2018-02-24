package com.newdeveloper.new_database_project;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import static com.newdeveloper.new_database_project.Show_subject_list.subject_table;
/**
 * Created by User on 6/29/2017.
 */
/*
public class show_subject_list_listview extends ArrayAdapter<Subject_List_item> {

    private int id;
    private Activity context;
    ArrayList<Subject_List_item> array;

    public show_subject_list_listview(Activity context, int resource, ArrayList<Subject_List_item> objects) {
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
        final Subject_List_item item = array.get(position);
        TextView name = (TextView) convertView.findViewById(R.id.subject_listCode);
        TextView course_title = (TextView) convertView.findViewById(R.id.subject_listName);

        name.setText(item.courseName);
        course_title.setText(item.courseTitle);
        name_list[position]=name.getText().toString();

        return convertView;
    }
}*/

public class show_subject_list_listview extends RecyclerView.Adapter<show_subject_list_listview.ViewHolder>{

    private List<Subject_List_item>listItems;
    private Context context;

    public show_subject_list_listview(List<Subject_List_item> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.row3,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Subject_List_item listItem=listItems.get(position);
        holder.subject_name.setText(listItem.getCourseName());
        holder.subject_code.setText(listItem.getCourseTitle());


        //set recycle view background color manually
        //holder.linearLayout.setBackgroundColor(Color.parseColor("#eceff1"));

        //course e click korle sei class a jabe
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                subject_table=listItem.getCourseName();
                String section=listItem.getCourseTitle();
                int ln=section.length();
                subject_table+=section.charAt(ln-3);
                //Log.e(subject_table," subject from subject list view activity");

                context.startActivity(new Intent(context,Navigation_drawer.class)); // pass data
            }
        });


        final SQLiteDatabase db=context.openOrCreateDatabase("TEACHER_db", Context.MODE_PRIVATE, null);

        //alert dialog er kaj korci
        holder.delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                View myView = LayoutInflater.from(context).inflate(R.layout.delete_dialog, null);

                builder.setView(myView);
                builder.setCancelable(false);
                final AlertDialog alert=builder.create();
                alert.show();

                TextView No=(TextView)myView.findViewById(R.id.no);
                TextView Yes= (TextView) myView.findViewById(R.id.yes);

                final int remove_item=0;
                Yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        listItems.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position,listItems.size());
                        //Toast.makeText(context,"Delete",Toast.LENGTH_LONG).show();

                        subject_table=listItem.getCourseName();
                        String section=listItem.getCourseTitle();
                        int ln=section.length();
                        char Section=section.charAt(ln-3);


                        //Log.e(subject_table," subject from subject list view activity");

                        try
                        {
                            db.delete("COURSE_TITLE", "table_name = ? and section = ?", new String[] { subject_table, String.valueOf(Section)});
                            db.execSQL("drop table if exists " + subject_table + Section);
                            db.execSQL("drop table if exists " + subject_table + Section+"Graph");
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                        }

                        alert.dismiss();
                    }
                });
                No.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                     alert.dismiss();
                    }
                });

            }
        });

    }


    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView subject_name;
        public TextView subject_code;
        public LinearLayout linearLayout;
        public Button delete_button;

        public ViewHolder(View itemView) {
            super(itemView);

            subject_name= (TextView) itemView.findViewById(R.id.subject_listName);
            subject_code= (TextView) itemView.findViewById(R.id.subject_listDisc);
            linearLayout= (LinearLayout) itemView.findViewById(R.id.row3_layout);
            delete_button=(Button) itemView.findViewById(R.id.delete_item);
        }
    }
}