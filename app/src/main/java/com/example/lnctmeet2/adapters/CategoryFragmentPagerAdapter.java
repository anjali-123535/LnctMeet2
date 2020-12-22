package com.example.lnctmeet2.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.lnctmeet2.R;
import com.example.lnctmeet2.fragment.InternshipFragment;
import com.example.lnctmeet2.fragment.NoticeFragment;
import com.example.lnctmeet2.fragment.OthersFragment;
import com.example.lnctmeet2.fragment.SeminarFragment;
import com.example.lnctmeet2.fragment.WorkshopFragment;
import com.example.lnctmeet2.utils.Constants;

public class CategoryFragmentPagerAdapter extends FragmentPagerAdapter {
    Context context;
    public CategoryFragmentPagerAdapter(Context context,@NonNull FragmentManager fm) {
        super(fm);
        this.context=context;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        int id;
        switch (position){
            case Constants.Notice:
                id= R.string.tab_notice;
                break;
            case Constants.Internships:
                id=R.string.tab_internship;
                break;
            case Constants.Other:
                id=R.string.tab_other;
                break;
            case Constants.Seminars:
                id=R.string.tab_seminar;
                break;
            default:
                id=R.string.tab_workshop;
                break;
        }
        return context.getString(id);
    }

    @NonNull
    @Override
    public Fragment getItem(int position){
        switch (position){
            case Constants.Notice:
                return new NoticeFragment();
            case Constants.Workshops:
                return new WorkshopFragment();
            case Constants.Seminars:
                return new SeminarFragment();
            case Constants.Internships:
                return new InternshipFragment();
            case Constants.Other:
                return  new OthersFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 5;
    }
}
