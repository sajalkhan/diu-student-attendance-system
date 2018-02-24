package com.newdeveloper.new_database_project;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.newdeveloper.new_database_project.Show_subject_list.subject_table;

/**
 * Created by User on 8/3/2017.
 */

public class create_pdf_and_send_mail extends AppCompatActivity {

    private static final int PERMS_REQUEST_CODE = 123;
    private SQLiteDatabase db;
    private PdfPCell cell;
    String path;


    //use to set background color
    BaseColor myColor = WebColors.getRGBColor("#9E9E9E");
    BaseColor myColor1 = WebColors.getRGBColor("#757575");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_pf_and_send_mail);

        db = openOrCreateDatabase("TEACHER_db", Context.MODE_PRIVATE, null);


        if (hasPermissions()) {
            // our app has permissions.
            createPDF();
        } else {
            //our app doesn't have permissions, So i m requesting permissions.
            requestPerms();
        }

        send_mail();

    }


    private void send_mail() {

        File file=new File(path+"/"+subject_table+".pdf");

        String[] mailto = {""};
        Uri uri=Uri.fromFile(file);

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, mailto);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Hi this was sent from Student attendance system app");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "here is your attendance sheet :)");
        emailIntent.setType("application/pdf");
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(emailIntent, "Send email using:"));

    }


    private boolean hasPermissions() {
        int res = 0;

        //string array of permissions,
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        for (String perms : permissions) {
            res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)) {
                return false;
            }
        }
        return true;
    }

    private void requestPerms() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean allowed = true;

        switch (requestCode) {
            case PERMS_REQUEST_CODE:

                for (int res : grantResults) {
                    // if user granted all permissions.
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }

                break;
            default:
                // if user not granted permissions.
                allowed = false;
                break;
        }

        if (allowed) {
            //user granted all permissions we can perform our task.
            createPDF();
        } else {
            // we will give warning to user that they haven't granted permissions.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "Storage Permissions denied.", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }


    public void createPDF() {
        Document doc = new Document();


        try {

            //File myFile = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), fileName);

            path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Attendance system file";

            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();

            //Log.e("PDFCreator", "PDF Path: " + path);
            dir.createNewFile();

            File file = new File(dir, subject_table+".pdf");
            FileOutputStream fOut = new FileOutputStream(file);

            PdfWriter.getInstance(doc, fOut);

            //open the document
            doc.open();


            //create table
            PdfPTable pt = new PdfPTable(3);
            pt.setWidthPercentage(100);
            float[] fl = new float[]{20, 45, 35};
            pt.setWidths(fl);
            cell = new PdfPCell();
            cell.setBorder(Rectangle.NO_BORDER);

            try {


                int count = 0;
                String course_name = null, section = null;
                Cursor result = db.rawQuery("SELECT * FROM " + subject_table, null);
                while (result.moveToNext()) {
                    if (count == 0) {
                        course_name = result.getString(result.getColumnIndex("Course_title"));
                        section = result.getString(result.getColumnIndex("Section"));
                    }
                    count++;
                }
                result.close();

                pt.addCell(cell);
                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);
                cell.addElement(new Paragraph(course_name + " ( " + section + " )"));

                cell.addElement(new Paragraph(""));
                cell.addElement(new Paragraph(""));
                pt.addCell(cell);
                cell = new PdfPCell(new Paragraph(""));
                cell.setBorder(Rectangle.NO_BORDER);
                pt.addCell(cell);

                PdfPTable pTable = new PdfPTable(1);
                pTable.setWidthPercentage(100);
                cell = new PdfPCell();
                cell.setColspan(1);
                cell.addElement(pt);
                pTable.addCell(cell);

                Cursor result2 = db.rawQuery("SELECT * FROM " + subject_table + "Graph", null);

                int total_column = result2.getColumnCount();
                PdfPTable table = new PdfPTable(total_column);

                float[] columnWidth = new float[total_column];
                for (int i = 0; i < total_column; i++) {
                    if (i == 0) columnWidth[0] = 8;
                    if (i == 1) columnWidth[i] = 40;
                    if (i == 2) columnWidth[i] = 25;
                    if (i > 2) columnWidth[i] = 10;
                }
                table.setWidths(columnWidth);

                cell = new PdfPCell();

                cell.setBackgroundColor(myColor);
                cell.setColspan(total_column);
                cell.addElement(pTable);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" "));
                cell.setColspan(total_column);
                table.addCell(cell);
                cell = new PdfPCell();
                cell.setColspan(total_column);

                cell.setBackgroundColor(myColor1);

                cell = new PdfPCell(new Phrase("#"));
                cell.setBackgroundColor(myColor1);
                table.addCell(cell);

                for (int i = 1; i < total_column; i++) {

                    if (i == 3) {
                        cell = new PdfPCell(new Phrase("present"));
                        cell.setBackgroundColor(myColor1);
                        table.addCell(cell);
                        continue;
                    }
                    cell = new PdfPCell(new Phrase(result2.getColumnName(i)));
                    cell.setBackgroundColor(myColor1);
                    table.addCell(cell);
                }

                //table.setHeaderRows(3);
                cell = new PdfPCell();
                cell.setColspan(total_column);

                while (result2.moveToNext()) {
                    table.addCell(result2.getString(0));
                    table.addCell(result2.getString(1));
                    table.addCell(result2.getString(2));

                    if ((total_column - 3) == 2) {
                        table.addCell(result2.getString(3));
                        table.addCell(result2.getString(4));
                    }

                    if ((total_column - 3) == 3) {
                        table.addCell(result2.getString(3));
                        table.addCell(result2.getString(4));
                        table.addCell(result2.getString(5));
                    }
                    if ((total_column - 3) == 4) {
                        table.addCell(result2.getString(3));
                        table.addCell(result2.getString(4));
                        table.addCell(result2.getString(5));
                        table.addCell(result2.getString(6));
                    }
                    if ((total_column - 3) == 5) {
                        table.addCell(result2.getString(3));
                        table.addCell(result2.getString(4));
                        table.addCell(result2.getString(5));
                        table.addCell(result2.getString(6));
                        table.addCell(result2.getString(7));
                    }
                    if ((total_column - 3) == 6) {
                        table.addCell(result2.getString(3));
                        table.addCell(result2.getString(4));
                        table.addCell(result2.getString(5));
                        table.addCell(result2.getString(6));
                        table.addCell(result2.getString(7));
                        table.addCell(result2.getString(8));
                    }
                    if ((total_column - 3) == 7) {
                        table.addCell(result2.getString(3));
                        table.addCell(result2.getString(4));
                        table.addCell(result2.getString(5));
                        table.addCell(result2.getString(6));
                        table.addCell(result2.getString(7));
                        table.addCell(result2.getString(8));
                        table.addCell(result2.getString(9));
                    }
                    if ((total_column - 3) == 8) {
                        table.addCell(result2.getString(3));
                        table.addCell(result2.getString(4));
                        table.addCell(result2.getString(5));
                        table.addCell(result2.getString(6));
                        table.addCell(result2.getString(7));
                        table.addCell(result2.getString(8));
                        table.addCell(result2.getString(9));
                        table.addCell(result2.getString(10));
                    }
                    if ((total_column - 3) == 9) {
                        table.addCell(result2.getString(3));
                        table.addCell(result2.getString(4));
                        table.addCell(result2.getString(5));
                        table.addCell(result2.getString(6));
                        table.addCell(result2.getString(7));
                        table.addCell(result2.getString(8));
                        table.addCell(result2.getString(9));
                        table.addCell(result2.getString(10));
                        table.addCell(result2.getString(11));
                    }
                    if ((total_column - 3) == 10) {
                        table.addCell(result2.getString(3));
                        table.addCell(result2.getString(4));
                        table.addCell(result2.getString(5));
                        table.addCell(result2.getString(6));
                        table.addCell(result2.getString(7));
                        table.addCell(result2.getString(8));
                        table.addCell(result2.getString(9));
                        table.addCell(result2.getString(10));
                        table.addCell(result2.getString(11));
                        table.addCell(result2.getString(12));
                    }
                    if ((total_column - 3) == 11) {
                        table.addCell(result2.getString(3));
                        table.addCell(result2.getString(4));
                        table.addCell(result2.getString(5));
                        table.addCell(result2.getString(6));
                        table.addCell(result2.getString(7));
                        table.addCell(result2.getString(8));
                        table.addCell(result2.getString(9));
                        table.addCell(result2.getString(10));
                        table.addCell(result2.getString(11));
                        table.addCell(result2.getString(12));
                        table.addCell(result2.getString(13));
                    }
                    if ((total_column - 3) == 12) {
                        table.addCell(result2.getString(3));
                        table.addCell(result2.getString(4));
                        table.addCell(result2.getString(5));
                        table.addCell(result2.getString(6));
                        table.addCell(result2.getString(7));
                        table.addCell(result2.getString(8));
                        table.addCell(result2.getString(9));
                        table.addCell(result2.getString(10));
                        table.addCell(result2.getString(11));
                        table.addCell(result2.getString(12));
                        table.addCell(result2.getString(13));
                        table.addCell(result2.getString(14));
                    }
                    if ((total_column - 3) == 13) {
                        table.addCell(result2.getString(3));
                        table.addCell(result2.getString(4));
                        table.addCell(result2.getString(5));
                        table.addCell(result2.getString(6));
                        table.addCell(result2.getString(7));
                        table.addCell(result2.getString(8));
                        table.addCell(result2.getString(9));
                        table.addCell(result2.getString(10));
                        table.addCell(result2.getString(11));
                        table.addCell(result2.getString(12));
                        table.addCell(result2.getString(13));
                        table.addCell(result2.getString(14));
                        table.addCell(result2.getString(15));
                    }
                    if ((total_column - 3) == 14) {
                        table.addCell(result2.getString(3));
                        table.addCell(result2.getString(4));
                        table.addCell(result2.getString(5));
                        table.addCell(result2.getString(6));
                        table.addCell(result2.getString(7));
                        table.addCell(result2.getString(8));
                        table.addCell(result2.getString(9));
                        table.addCell(result2.getString(10));
                        table.addCell(result2.getString(11));
                        table.addCell(result2.getString(12));
                        table.addCell(result2.getString(13));
                        table.addCell(result2.getString(14));
                        table.addCell(result2.getString(15));
                        table.addCell(result2.getString(16));
                    }
                    if ((total_column - 3) == 15) {
                        table.addCell(result2.getString(3));
                        table.addCell(result2.getString(4));
                        table.addCell(result2.getString(5));
                        table.addCell(result2.getString(6));
                        table.addCell(result2.getString(7));
                        table.addCell(result2.getString(8));
                        table.addCell(result2.getString(9));
                        table.addCell(result2.getString(10));
                        table.addCell(result2.getString(11));
                        table.addCell(result2.getString(12));
                        table.addCell(result2.getString(13));
                        table.addCell(result2.getString(14));
                        table.addCell(result2.getString(15));
                        table.addCell(result2.getString(16));
                        table.addCell(result2.getString(17));
                    }
                    if ((total_column - 3) == 16) {
                        table.addCell(result2.getString(3));
                        table.addCell(result2.getString(4));
                        table.addCell(result2.getString(5));
                        table.addCell(result2.getString(6));
                        table.addCell(result2.getString(7));
                        table.addCell(result2.getString(8));
                        table.addCell(result2.getString(9));
                        table.addCell(result2.getString(10));
                        table.addCell(result2.getString(11));
                        table.addCell(result2.getString(12));
                        table.addCell(result2.getString(13));
                        table.addCell(result2.getString(14));
                        table.addCell(result2.getString(15));
                        table.addCell(result2.getString(16));
                        table.addCell(result2.getString(17));
                        table.addCell(result2.getString(18));
                    }
                    if ((total_column - 3) == 17) {
                        table.addCell(result2.getString(3));
                        table.addCell(result2.getString(4));
                        table.addCell(result2.getString(5));
                        table.addCell(result2.getString(6));
                        table.addCell(result2.getString(7));
                        table.addCell(result2.getString(8));
                        table.addCell(result2.getString(9));
                        table.addCell(result2.getString(10));
                        table.addCell(result2.getString(11));
                        table.addCell(result2.getString(12));
                        table.addCell(result2.getString(13));
                        table.addCell(result2.getString(14));
                        table.addCell(result2.getString(15));
                        table.addCell(result2.getString(16));
                        table.addCell(result2.getString(17));
                        table.addCell(result2.getString(18));
                        table.addCell(result2.getString(19));
                    }
                    if ((total_column - 3) == 18) {
                        table.addCell(result2.getString(3));
                        table.addCell(result2.getString(4));
                        table.addCell(result2.getString(5));
                        table.addCell(result2.getString(6));
                        table.addCell(result2.getString(7));
                        table.addCell(result2.getString(8));
                        table.addCell(result2.getString(9));
                        table.addCell(result2.getString(10));
                        table.addCell(result2.getString(11));
                        table.addCell(result2.getString(12));
                        table.addCell(result2.getString(13));
                        table.addCell(result2.getString(14));
                        table.addCell(result2.getString(15));
                        table.addCell(result2.getString(16));
                        table.addCell(result2.getString(17));
                        table.addCell(result2.getString(18));
                        table.addCell(result2.getString(19));
                        table.addCell(result2.getString(20));
                    }
                    if ((total_column - 3) == 19) {
                        table.addCell(result2.getString(3));
                        table.addCell(result2.getString(4));
                        table.addCell(result2.getString(5));
                        table.addCell(result2.getString(6));
                        table.addCell(result2.getString(7));
                        table.addCell(result2.getString(8));
                        table.addCell(result2.getString(9));
                        table.addCell(result2.getString(10));
                        table.addCell(result2.getString(11));
                        table.addCell(result2.getString(12));
                        table.addCell(result2.getString(13));
                        table.addCell(result2.getString(14));
                        table.addCell(result2.getString(15));
                        table.addCell(result2.getString(16));
                        table.addCell(result2.getString(17));
                        table.addCell(result2.getString(18));
                        table.addCell(result2.getString(19));
                        table.addCell(result2.getString(20));
                        table.addCell(result2.getString(21));
                    }
                    if ((total_column - 3) == 20) {
                        table.addCell(result2.getString(3));
                        table.addCell(result2.getString(4));
                        table.addCell(result2.getString(5));
                        table.addCell(result2.getString(6));
                        table.addCell(result2.getString(7));
                        table.addCell(result2.getString(8));
                        table.addCell(result2.getString(9));
                        table.addCell(result2.getString(10));
                        table.addCell(result2.getString(11));
                        table.addCell(result2.getString(12));
                        table.addCell(result2.getString(13));
                        table.addCell(result2.getString(14));
                        table.addCell(result2.getString(15));
                        table.addCell(result2.getString(16));
                        table.addCell(result2.getString(17));
                        table.addCell(result2.getString(18));
                        table.addCell(result2.getString(19));
                        table.addCell(result2.getString(20));
                        table.addCell(result2.getString(21));
                        table.addCell(result2.getString(22));
                    }
                    if ((total_column - 3) == 21) {
                        table.addCell(result2.getString(3));
                        table.addCell(result2.getString(4));
                        table.addCell(result2.getString(5));
                        table.addCell(result2.getString(6));
                        table.addCell(result2.getString(7));
                        table.addCell(result2.getString(8));
                        table.addCell(result2.getString(9));
                        table.addCell(result2.getString(10));
                        table.addCell(result2.getString(11));
                        table.addCell(result2.getString(12));
                        table.addCell(result2.getString(13));
                        table.addCell(result2.getString(14));
                        table.addCell(result2.getString(15));
                        table.addCell(result2.getString(16));
                        table.addCell(result2.getString(17));
                        table.addCell(result2.getString(18));
                        table.addCell(result2.getString(19));
                        table.addCell(result2.getString(20));
                        table.addCell(result2.getString(21));
                        table.addCell(result2.getString(22));
                        table.addCell(result2.getString(23));
                    }
                    if ((total_column - 3) == 22) {
                        table.addCell(result2.getString(3));
                        table.addCell(result2.getString(4));
                        table.addCell(result2.getString(5));
                        table.addCell(result2.getString(6));
                        table.addCell(result2.getString(7));
                        table.addCell(result2.getString(8));
                        table.addCell(result2.getString(9));
                        table.addCell(result2.getString(10));
                        table.addCell(result2.getString(11));
                        table.addCell(result2.getString(12));
                        table.addCell(result2.getString(13));
                        table.addCell(result2.getString(14));
                        table.addCell(result2.getString(15));
                        table.addCell(result2.getString(16));
                        table.addCell(result2.getString(17));
                        table.addCell(result2.getString(18));
                        table.addCell(result2.getString(19));
                        table.addCell(result2.getString(20));
                        table.addCell(result2.getString(21));
                        table.addCell(result2.getString(22));
                        table.addCell(result2.getString(23));
                        table.addCell(result2.getString(24));
                    }
                    if ((total_column - 3) == 23) {
                        table.addCell(result2.getString(3));
                        table.addCell(result2.getString(4));
                        table.addCell(result2.getString(5));
                        table.addCell(result2.getString(6));
                        table.addCell(result2.getString(7));
                        table.addCell(result2.getString(8));
                        table.addCell(result2.getString(9));
                        table.addCell(result2.getString(10));
                        table.addCell(result2.getString(11));
                        table.addCell(result2.getString(12));
                        table.addCell(result2.getString(13));
                        table.addCell(result2.getString(14));
                        table.addCell(result2.getString(15));
                        table.addCell(result2.getString(16));
                        table.addCell(result2.getString(17));
                        table.addCell(result2.getString(18));
                        table.addCell(result2.getString(19));
                        table.addCell(result2.getString(20));
                        table.addCell(result2.getString(21));
                        table.addCell(result2.getString(22));
                        table.addCell(result2.getString(23));
                        table.addCell(result2.getString(24));
                        table.addCell(result2.getString(25));
                    }
                    if ((total_column - 3) == 24) {
                        table.addCell(result2.getString(3));
                        table.addCell(result2.getString(4));
                        table.addCell(result2.getString(5));
                        table.addCell(result2.getString(6));
                        table.addCell(result2.getString(7));
                        table.addCell(result2.getString(8));
                        table.addCell(result2.getString(9));
                        table.addCell(result2.getString(10));
                        table.addCell(result2.getString(11));
                        table.addCell(result2.getString(12));
                        table.addCell(result2.getString(13));
                        table.addCell(result2.getString(14));
                        table.addCell(result2.getString(15));
                        table.addCell(result2.getString(16));
                        table.addCell(result2.getString(17));
                        table.addCell(result2.getString(18));
                        table.addCell(result2.getString(19));
                        table.addCell(result2.getString(20));
                        table.addCell(result2.getString(21));
                        table.addCell(result2.getString(22));
                        table.addCell(result2.getString(23));
                        table.addCell(result2.getString(24));
                        table.addCell(result2.getString(25));
                        table.addCell(result2.getString(26));
                    }

                    if ((total_column - 3) == 25) {
                        table.addCell(result2.getString(3));
                        table.addCell(result2.getString(4));
                        table.addCell(result2.getString(5));
                        table.addCell(result2.getString(6));
                        table.addCell(result2.getString(7));
                        table.addCell(result2.getString(8));
                        table.addCell(result2.getString(9));
                        table.addCell(result2.getString(10));
                        table.addCell(result2.getString(11));
                        table.addCell(result2.getString(12));
                        table.addCell(result2.getString(13));
                        table.addCell(result2.getString(14));
                        table.addCell(result2.getString(15));
                        table.addCell(result2.getString(16));
                        table.addCell(result2.getString(17));
                        table.addCell(result2.getString(18));
                        table.addCell(result2.getString(19));
                        table.addCell(result2.getString(20));
                        table.addCell(result2.getString(21));
                        table.addCell(result2.getString(22));
                        table.addCell(result2.getString(23));
                        table.addCell(result2.getString(24));
                        table.addCell(result2.getString(25));
                        table.addCell(result2.getString(26));
                        table.addCell(result2.getString(27));
                    }

                    if ((total_column - 3) == 26) {
                        table.addCell(result2.getString(3));
                        table.addCell(result2.getString(4));
                        table.addCell(result2.getString(5));
                        table.addCell(result2.getString(6));
                        table.addCell(result2.getString(7));
                        table.addCell(result2.getString(8));
                        table.addCell(result2.getString(9));
                        table.addCell(result2.getString(10));
                        table.addCell(result2.getString(11));
                        table.addCell(result2.getString(12));
                        table.addCell(result2.getString(13));
                        table.addCell(result2.getString(14));
                        table.addCell(result2.getString(15));
                        table.addCell(result2.getString(16));
                        table.addCell(result2.getString(17));
                        table.addCell(result2.getString(18));
                        table.addCell(result2.getString(19));
                        table.addCell(result2.getString(20));
                        table.addCell(result2.getString(21));
                        table.addCell(result2.getString(22));
                        table.addCell(result2.getString(23));
                        table.addCell(result2.getString(24));
                        table.addCell(result2.getString(25));
                        table.addCell(result2.getString(26));
                        table.addCell(result2.getString(27));
                        table.addCell(result2.getString(28));
                    }
                    if ((total_column - 3) == 27) {
                        table.addCell(result2.getString(3));
                        table.addCell(result2.getString(4));
                        table.addCell(result2.getString(5));
                        table.addCell(result2.getString(6));
                        table.addCell(result2.getString(7));
                        table.addCell(result2.getString(8));
                        table.addCell(result2.getString(9));
                        table.addCell(result2.getString(10));
                        table.addCell(result2.getString(11));
                        table.addCell(result2.getString(12));
                        table.addCell(result2.getString(13));
                        table.addCell(result2.getString(14));
                        table.addCell(result2.getString(15));
                        table.addCell(result2.getString(16));
                        table.addCell(result2.getString(17));
                        table.addCell(result2.getString(18));
                        table.addCell(result2.getString(19));
                        table.addCell(result2.getString(20));
                        table.addCell(result2.getString(21));
                        table.addCell(result2.getString(22));
                        table.addCell(result2.getString(23));
                        table.addCell(result2.getString(24));
                        table.addCell(result2.getString(25));
                        table.addCell(result2.getString(26));
                        table.addCell(result2.getString(27));
                        table.addCell(result2.getString(28));
                        table.addCell(result2.getString(29));
                    }
                    if ((total_column - 3) == 28) {
                        table.addCell(result2.getString(3));
                        table.addCell(result2.getString(4));
                        table.addCell(result2.getString(5));
                        table.addCell(result2.getString(6));
                        table.addCell(result2.getString(7));
                        table.addCell(result2.getString(8));
                        table.addCell(result2.getString(9));
                        table.addCell(result2.getString(10));
                        table.addCell(result2.getString(11));
                        table.addCell(result2.getString(12));
                        table.addCell(result2.getString(13));
                        table.addCell(result2.getString(14));
                        table.addCell(result2.getString(15));
                        table.addCell(result2.getString(16));
                        table.addCell(result2.getString(17));
                        table.addCell(result2.getString(18));
                        table.addCell(result2.getString(19));
                        table.addCell(result2.getString(20));
                        table.addCell(result2.getString(21));
                        table.addCell(result2.getString(22));
                        table.addCell(result2.getString(23));
                        table.addCell(result2.getString(24));
                        table.addCell(result2.getString(25));
                        table.addCell(result2.getString(26));
                        table.addCell(result2.getString(27));
                        table.addCell(result2.getString(28));
                        table.addCell(result2.getString(29));
                        table.addCell(result2.getString(30));
                    }
                    if ((total_column - 3) == 29) {
                        table.addCell(result2.getString(3));
                        table.addCell(result2.getString(4));
                        table.addCell(result2.getString(5));
                        table.addCell(result2.getString(6));
                        table.addCell(result2.getString(7));
                        table.addCell(result2.getString(8));
                        table.addCell(result2.getString(9));
                        table.addCell(result2.getString(10));
                        table.addCell(result2.getString(11));
                        table.addCell(result2.getString(12));
                        table.addCell(result2.getString(13));
                        table.addCell(result2.getString(14));
                        table.addCell(result2.getString(15));
                        table.addCell(result2.getString(16));
                        table.addCell(result2.getString(17));
                        table.addCell(result2.getString(18));
                        table.addCell(result2.getString(19));
                        table.addCell(result2.getString(20));
                        table.addCell(result2.getString(21));
                        table.addCell(result2.getString(22));
                        table.addCell(result2.getString(23));
                        table.addCell(result2.getString(24));
                        table.addCell(result2.getString(25));
                        table.addCell(result2.getString(26));
                        table.addCell(result2.getString(27));
                        table.addCell(result2.getString(28));
                        table.addCell(result2.getString(29));
                        table.addCell(result2.getString(30));
                        table.addCell(result2.getString(31));
                    }
                }


                doc.add(table);
                //Toast.makeText(getApplicationContext(), "created PDF", Toast.LENGTH_LONG).show();
            } catch (DocumentException de) {
                Log.e("PDFCreator", "DocumentException:" + de);
            } finally {
                doc.close();
            }


        } catch (DocumentException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
        } finally {
            doc.close();
        }



        //show display at a specific time
        new Handler().postDelayed(new Runnable() {
            public void run() {
                finish();
            }
        }, 0);

    }


}
