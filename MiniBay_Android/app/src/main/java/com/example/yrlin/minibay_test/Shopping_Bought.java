package com.example.yrlin.minibay_test;


import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class Shopping_Bought extends Fragment {

    ListView list;
    ArrayList<String> shoppingList = new ArrayList<>();
    ArrayList<String> bought_items = new ArrayList<>();
    ArrayList<String> bought_date = new ArrayList<>();

    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference shoppingRef = db.getReference().child("shopping");

    public Shopping_Bought() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shopping__bought, container, false);


        Resources res = getResources();
//        bought_items = res.getStringArray(R.array.bought_items);
//        bought_date = res.getStringArray(R.array.bought_date);



        list = (ListView)view.findViewById(R.id.bought_items_view);

        final MyAdapter adapter = new MyAdapter(this.getActivity(), bought_items, bought_date);
        shoppingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);
                shoppingList.clear();
                bought_date.clear();
                bought_items.clear();
                for (String key : map.keySet()) {
                    shoppingList.add(key);
                    Map<String, String> tempMap = (Map)map.get(key);
                    String isBought = tempMap.get("isBought");
                    if (!isBought.equals("No")) {
                        bought_items.add(key);
                        bought_date.add(tempMap.get("Date"));
                        Log.v("这里有话说","啪啪啪");
                    }
                }
                if (bought_items.size() == 0) {
                    bought_items.add("Nothing have been bought!");
                    bought_date.add("No date!");
                    Log.v("这里没话说","哈哈哈");
                }

                list.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return view;
    }

    class MyAdapter extends ArrayAdapter<String> {

        Context context;
        ArrayList<String> myTitles;
        ArrayList<String> myDate;
        LayoutInflater layoutInflater;

        MyAdapter(Context c, ArrayList<String> titles, ArrayList<String> date) {
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

            nameTv.setText(myTitles.get(position));
            dateTv.setText(myDate.get(position));

            return convertView;
        }
    }

}
