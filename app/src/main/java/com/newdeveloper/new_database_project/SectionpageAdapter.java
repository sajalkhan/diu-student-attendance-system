package com.newdeveloper.new_database_project;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by User on 6/6/2017.
 */

public class SectionpageAdapter extends FragmentPagerAdapter {

    public SectionpageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new blank_fragment();
        } else if (position == 1) {
            return new update_information_fragment();
        } else
            return new delete_information_fragment();

    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
