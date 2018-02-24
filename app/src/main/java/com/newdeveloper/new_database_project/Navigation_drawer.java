package com.newdeveloper.new_database_project;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import static com.newdeveloper.new_database_project.Show_subject_list.subject_table;
import static com.newdeveloper.new_database_project.MainActivity.check_total_add_or_delete_item;

public class Navigation_drawer extends AppCompatActivity

        implements NavigationView.OnNavigationItemSelectedListener {

    private ViewPager viewPager;
    private DrawerLayout drawer;
    private TabLayout tabLayout;
    private SQLiteDatabase db;
    private String[] pageTitle = {"Give Attendance", "Add New Student", "Delete Student Info"};

    private int[] tabIcons = {
            R.drawable.giveattendance,
            R.drawable.updateinfo,
            R.drawable.deleteinfo
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        setSupportActionBar(toolbar);

        db = openOrCreateDatabase("TEACHER_db", android.content.Context.MODE_PRIVATE, null);

        int count = 0;
        String course_name = null, section = null;
        Cursor result = db.rawQuery("SELECT * FROM " + subject_table, null);
        while (result.moveToNext()) {
            if (count == 0) {
                course_name = result.getString(result.getColumnIndex("Course_title"));
                section = result.getString(result.getColumnIndex("Section"));
                count++;
            }
        }

        course_name = course_name.replace("Programming and", "");
        course_name += " ( " + section + " )";

        /*
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault());
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = df.format(c.getTime());*/

        getSupportActionBar().setTitle(course_name);

        //create default navigation drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        //setting Tab layout (number of Tabs = number of ViewPager pages)
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        for (int i = 0; i < 3; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(pageTitle[i]));
        }


        //set gravity for tab bar
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);

        //handling navigation view item event
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);


        //set viewpager adapter
        final SectionpageAdapter pagerAdapter = new SectionpageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(3);

        //change Tab selection when swipe ViewPager
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (check_total_add_or_delete_item == 1) {
                    check_total_add_or_delete_item = 0;
                    pagerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        //change ViewPager page when tab selected
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_show_graph) {
            Intent intent = new Intent(Navigation_drawer.this, Show_attendance_graph.class);
            startActivity(intent);
        }
        if (id == R.id.nav_blank) {
            viewPager.setCurrentItem(0);
        }
        if (id == R.id.nav_barcode) {
            Intent intent = new Intent(Navigation_drawer.this, Barcode_scanner.class);
            startActivity(intent);
        }
        if (id == R.id.nav_show_marks) {
            Intent intent = new Intent(Navigation_drawer.this, show_student_attendance_marks.class);
            startActivity(intent);
        }
        if (id == R.id.nav_mail) {
            Intent intent = new Intent(Navigation_drawer.this, create_pdf_and_send_mail.class);
            startActivity(intent);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
