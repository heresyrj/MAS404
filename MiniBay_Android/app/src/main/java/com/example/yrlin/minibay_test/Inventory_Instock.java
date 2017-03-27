package com.example.yrlin.minibay_test;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by yrlin on 2017/3/23.
 */

public class Inventory_Instock extends Fragment {

    ListView list;
    ArrayList<String> items = new ArrayList<>();
    ArrayList<String> items_details = new ArrayList<>();
    ArrayList<Integer> imgs_fruit = new ArrayList<>();
    ImageView mImage;
    //{R.drawable.carrot, R.drawable.broccoli, R.drawable.eggplant, R.drawable.apple};

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mRef = database.getReference();
    DatabaseReference mInventory = mRef.child("inventory");
    DatabaseReference mTest = mRef.child("Test");
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory_instock, container, false);

        Resources res = getResources();



        list = (ListView)view.findViewById(R.id.inventory_instock_list);
        mImage = (ImageView)view.findViewById(R.id.inventory_image);
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PictureFromFridge.class);
                startActivity(intent);
            }
        });

        mTest.setValue("Funny!");

        final MyAdapter_Inventory adapter = new MyAdapter_Inventory(this.getActivity(), items, items_details, imgs_fruit);
        mInventory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);
                items.clear();
                items_details.clear();
                imgs_fruit.clear();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (String key : map.keySet()) {

                    Map<String, Object> tempMap = (Map)map.get(key);
                    String putinString = (String)tempMap.get("putinDate");
                    String putoutString = (String)tempMap.get("putoutDate");
                    Date putinDate = new Date();
                    try{
                        putinDate = simpleDateFormat.parse(putinString);
                        Log.v("啪啪啪","哪里跑");
                    } catch (ParseException ex){
                        ex.printStackTrace();
                    }

                    if (putoutString.length() < 4) {
                        items.add(key);
//                        try {
//                            Date putoutDate = simpleDateFormat.parse(putoutString);
//                        } catch (ParseException ex) {
//                            ex.printStackTrace();
//                        }


                        Date curTime = new Date();
                        long diff = curTime.getTime() - putinDate.getTime();
                        Log.v("啪啪啪你多大",diff+"");
                        items_details.add(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + " Days left");

                        switch (key) {
                            case "Apple" : imgs_fruit.add(R.drawable.apple);
                                break;
                            case "Broccoli" : imgs_fruit.add(R.drawable.broccoli);
                                break;
                            case "Eggplant" : imgs_fruit.add(R.drawable.eggplant);
                                break;
                            case "Carrot" : imgs_fruit.add(R.drawable.carrot);
                                break;
                        }
                    }

                    list.setAdapter(adapter);
                    }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), Inventory_Detail.class);
                intent.putExtra("itemName", list.getItemAtPosition(position).toString());
                startActivity(intent);
            }
        });




        return view;
    }

    class MyAdapter_Inventory extends ArrayAdapter<String> {
        Context context;
        ArrayList<Integer> imgs;
        ArrayList<String> myTitles;
        ArrayList<String> details;
        LayoutInflater layoutInflater;

        MyAdapter_Inventory(Context c, ArrayList<String> titles, ArrayList<String> details, ArrayList<Integer> imgs) {
            super(c, R.layout.inventory_row, R.id.inventory_item, titles);
            this.context = c;
            this.details = details;
            this.imgs = imgs;
            this.myTitles = titles;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.inventory_row, null);
            }
            TextView nameTv = (TextView) convertView.findViewById(R.id.inventory_item);
            TextView detailTv = (TextView) convertView.findViewById(R.id.inventory_item_detail);
            ImageView img = (ImageView) convertView.findViewById(R.id.inventory_icon);

            detailTv.setText(details.get(position));
            nameTv.setText(myTitles.get(position));
            img.setImageResource(imgs.get(position));
            return convertView;
        }
    }
}
