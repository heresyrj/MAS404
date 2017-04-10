package com.example.yrlin.minibay_test;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class Nutrition extends Fragment {

    Map<String, Integer> nutrition_quota = new HashMap<>();
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    ArrayList<String> runout_item = new ArrayList<>();
    ArrayList<String> nutrition_title = new ArrayList<>();
    ArrayList<String> nutrition_rate = new ArrayList<>();
    ArrayList<String> quota_per_week = new ArrayList<>();

    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference mInventory_runnout = db.getReference().child("inventory");

    public Nutrition() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        nutrition_quota.put("energy", 2000);
        nutrition_quota.put("fiber",789);
        nutrition_quota.put("calcium",876);
        nutrition_quota.put("vitaminc",524);
        View view = inflater.inflate(R.layout.fragment_nutrition, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.nutrition_recyclerview);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);





        mInventory_runnout.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);

                nutrition_title.clear();
                runout_item.clear();
                quota_per_week.clear();
                for (String key : map.keySet()) {
                    Map<String, Object> tempMap = (Map)map.get(key);
                    String putoutString = (String)tempMap.get("putoutDate");
                    if (putoutString.length() > 4) {
                        runout_item.add(key);
                    }
                }
                Map<String, Integer> nutrition_value = new HashMap<>();
                for (String item : runout_item) {
                    try {
                        JSONObject jsonObject = new JSONObject(loadJSONFromAsset("nutrition.json"));
                        JSONObject curObject = jsonObject.getJSONObject(item.toLowerCase());
                        JSONObject nutritionObj = curObject.getJSONObject("nutrition");
                        Map<String, String> nutritionMap = new HashMap<>();
                        parse(nutritionObj, nutritionMap);
                        for (String key : nutritionMap.keySet()) {
                            if (!nutrition_value.containsKey(key)) {
                                nutrition_value.put(key,Integer.parseInt(nutritionMap.get(key)));
                            } else {
                                nutrition_value.put(key, nutrition_value.get(key) + Integer.parseInt(nutritionMap.get(key)));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                for (String key : nutrition_value.keySet()) {
                    nutrition_title.add(key);
                    nutrition_rate.add((nutrition_value.get(key) * 100 / nutrition_quota.get(key)) + "");
                    quota_per_week.add(nutrition_quota.get(key) + "");
                }
                adapter = new Nutrition_RecyclerAdapter(nutrition_title,nutrition_rate, quota_per_week);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        return view;
    }

    public String loadJSONFromAsset(String fileName) {
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
    public static Map<String,String> parse(JSONObject json , Map<String,String> out) throws JSONException {
        Iterator<String> keys = json.keys();
        while(keys.hasNext()){
            String key = keys.next();
            String val = null;
            try{
                JSONObject value = json.getJSONObject(key);
                parse(value,out);
            }catch(Exception e){
                val = json.getString(key);
            }

            if(val != null){
                out.put(key,val);
            }
        }
        return out;
    }

}
