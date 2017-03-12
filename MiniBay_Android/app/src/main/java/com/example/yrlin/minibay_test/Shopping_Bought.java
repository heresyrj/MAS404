package com.example.yrlin.minibay_test;


import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Shopping_Bought extends Fragment {

    ListView list;
    String[] bought_items;
    String[] bought_date;



    public Shopping_Bought() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shopping__bought, container, false);


        Resources res = getResources();
        bought_items = res.getStringArray(R.array.bought_items);
        bought_date = res.getStringArray(R.array.bought_date);



        list = (ListView)view.findViewById(R.id.bought_items_view);

        MyAdapter adapter = new MyAdapter(this.getActivity(), bought_items, bought_date);

        list.setAdapter(adapter);
        return view;
    }

    class MyAdapter extends ArrayAdapter<String> {

        Context context;
        String[] myTitles;
        String[] myDate;
        LayoutInflater layoutInflater;

        MyAdapter(Context c, String[] titles, String[] date) {
            super(c, R.layout.bought_row, R.id.bought_item, titles);
            this.context = c;
            this.myDate = date;
            this.myTitles = titles;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.bought_row, null);
            }
            TextView nameTv = (TextView) convertView.findViewById(R.id.bought_item);
            TextView dateTv = (TextView) convertView.findViewById(R.id.bought_date);

            nameTv.setText(myTitles[position]);
            dateTv.setText(myDate[position]);

            return convertView;
        }
    }

}
