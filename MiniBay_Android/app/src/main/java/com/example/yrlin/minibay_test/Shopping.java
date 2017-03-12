package com.example.yrlin.minibay_test;


import android.os.Bundle;
import android.support.design.widget.*;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class Shopping extends Fragment {


    public Shopping() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_shopping, container, false);

        TabLayout tabLayout = (TabLayout) inflatedView.findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("To Buy"));
        tabLayout.addTab(tabLayout.newTab().setText("Bought"));
        final ViewPager viewPager = (ViewPager) inflatedView.findViewById(R.id.viewpager);

        viewPager.setAdapter(new PagerAdapter
                (getFragmentManager(), tabLayout.getTabCount()));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
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
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("To Buy");
        tabLayout.getTabAt(1).setText("Bought");
        return inflatedView;
    }
    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    Shopping_Buy tab1 = new Shopping_Buy();
                    return tab1;
                case 1:
                    Shopping_Bought tab2 = new Shopping_Bought();
                    return tab2;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }




}
