package com.example.tripcalculator.ui.viewpager;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.tripcalculator.R;
import com.example.tripcalculator.fragments.DiaryFragment;
import com.example.tripcalculator.fragments.SummaryFragment;

import java.util.ArrayList;
import java.util.List;

public class SummaryViewPagerAdapter extends FragmentPagerAdapter {

    private List<String> pageTitles;
    private List<Fragment> pages;

    public SummaryViewPagerAdapter(@NonNull FragmentManager fm, int tripId, Context context) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        pageTitles = new ArrayList<>();
        pageTitles.add(context.getString(R.string.summary_tab_label));
        pageTitles.add(context.getString(R.string.diary_tab_label));
        pages = new ArrayList<>();
        pages.add(new SummaryFragment(tripId));
        pages.add(new DiaryFragment(tripId));
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return pages.get(position);
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return pageTitles.get(position);
    }
}
