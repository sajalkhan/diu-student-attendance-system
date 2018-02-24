package com.newdeveloper.new_database_project;

import android.app.AlertDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import static com.newdeveloper.new_database_project.Show_attendance_graph.table;
import static com.newdeveloper.new_database_project.MainActivity.check_total_add_or_delete_item;

/**
 * Created by User on 7/19/2017.
 */

public class delete_information_listview extends RecyclerView.Adapter<delete_information_listview.ViewHolder> {

    private List<delete_information_item> listItems;
    private Context context;

    public delete_information_listview(List<delete_information_item> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row4_for_delete_info_activity, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final delete_information_item listItem = listItems.get(position);
        holder.studentId.setText(String.valueOf(position+1)+". "+listItem.getStudent_Id());
        holder.studentName.setText(listItem.getStudent_Name());

        final SQLiteDatabase db = context.openOrCreateDatabase("TEACHER_db", Context.MODE_PRIVATE, null);


        //alert dialog er kaj korci
        holder.delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View myView = LayoutInflater.from(context).inflate(R.layout.delete_dialog, null);

                builder.setView(myView);
                builder.setCancelable(false);
                final AlertDialog alert = builder.create();
                alert.show();

                TextView No = (TextView) myView.findViewById(R.id.no);
                TextView Yes = (TextView) myView.findViewById(R.id.yes);

                final int remove_item = 0;
                Yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        check_total_add_or_delete_item=1;

                        listItems.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, listItems.size());
                        //Toast.makeText(context,"Delete",Toast.LENGTH_LONG).show();

                        String studentId = listItem.getStudent_Id();

                        //Log.e(table, " subject from subject list view activity");

                        try {
                            db.delete(table + "UNREGISTERED_STUDENT_INFO_TABLE", "Student_id = ?", new String[]{studentId});
                            db.delete(table, "Student_id = ?", new String[]{studentId + "."});
                            db.delete(table + "Graph", " Student_id = ?", new String[]{studentId});
                        } catch (Exception e) {
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView studentName;
        public TextView studentId;
        public LinearLayout linearLayout;
        public Button delete_button;

        public ViewHolder(View itemView) {
            super(itemView);


            studentName = (TextView) itemView.findViewById(R.id.student_listName_from_row4);
            studentId = (TextView) itemView.findViewById(R.id.student_listId_from_row4);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.row4_layout);
            delete_button = (Button) itemView.findViewById(R.id.delete_item_from_row4);
        }
    }


}
