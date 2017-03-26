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
public class Inventory extends Fragment {


    public Inventory() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_inventory, container, false);

        TabLayout tabLayout = (TabLayout) inflatedView.findViewById(R.id.tabLayout_inventory);
        tabLayout.addTab(tabLayout.newTab().setText("Instock"));
        tabLayout.addTab(tabLayout.newTab().setText("Run-out"));
        final ViewPager viewPager_Inventory = (ViewPager) inflatedView.findViewById(R.id.viewpager_inventory);

        viewPager_Inventory.setAdapter(new PagerAdapter_Inventory
                (getFragmentManager(), tabLayout.getTabCount()));
        viewPager_Inventory.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager_Inventory.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabLayout.setupWithViewPager(viewPager_Inventory);
        tabLayout.getTabAt(0).setText("Instock");
        tabLayout.getTabAt(1).setText("Run-out");
        return inflatedView;
    }
    public class PagerAdapter_Inventory extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public PagerAdapter_Inventory(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    Inventory_Instock tab1 = new Inventory_Instock();
                    return tab1;
                case 1:
                    Inventory_Runout tab2 = new Inventory_Runout();
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

