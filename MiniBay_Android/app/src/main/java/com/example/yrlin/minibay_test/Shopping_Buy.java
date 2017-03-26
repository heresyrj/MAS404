package com.example.yrlin.minibay_test;


import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class Shopping_Buy extends Fragment {

    ListView list;
    ArrayList<String> items = new ArrayList<>();
    ArrayList<Integer> imgs_fruit = new ArrayList<>();
    ArrayList<String> items_isBought = new ArrayList<>();
    private EditText mValueField;
    private Button mAddBtn;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference mRef = db.getReference();
    DatabaseReference shoppingRef = mRef.child("shopping");
   //{R.drawable.apple, R.drawable.banana, R.drawable.broccoli, R.drawable.carrot, R.drawable.eggplant};

    public Shopping_Buy() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shopping__buy, container, false);


        Resources res = getResources();
        //items = res.getStringArray(R.array.buy_items);



        list = (ListView)view.findViewById(R.id.buy_items_view);
        mValueField = (EditText) view.findViewById(R.id.input_shopping_text);
        mAddBtn = (Button) view.findViewById(R.id.send_shoppint_button);
        mAddBtn.setTextColor(getResources().getColor(android.R.color.white));
        mAddBtn.setBackground(getResources().getDrawable(R.drawable.button_add_shopping));


        final MyAdapter adapter = new MyAdapter(this.getActivity(), items, imgs_fruit, items_isBought);

        shoppingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);
                items.clear();
                imgs_fruit.clear();
                items_isBought.clear();
                if (map.size() != 0) {
                    for (String key : map.keySet()) {
                        items.add(key);
                        Map<String, String> tempMap = (Map)map.get(key);
                        items_isBought.add(tempMap.get("isBought"));
                        switch (key) {
                            case "Apple" : imgs_fruit.add(R.drawable.apple);
                                break;
                            case "Broccoli" : imgs_fruit.add(R.drawable.broccoli);
                                break;
                            case "Eggplant" : imgs_fruit.add(R.drawable.eggplant);
                                break;
                            case "Carrot" : imgs_fruit.add(R.drawable.carrot);
                                break;
                            case "Banana" : imgs_fruit.add(R.drawable.banana);
                        }
                    }
                }

                list.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = mValueField.getText().toString().trim();
                if (!value.equals("Apple")&&!value.equals("Carrot")&&!value.equals("Eggplant")&&!value.equals("Broccoli")&&!value.equals("Banana")) {
                    Context context = getActivity();
                    CharSequence text = "Item not Found, Please input again!";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                } else {
                    DatabaseReference tempItem = shoppingRef.child(value);
                    DatabaseReference tempIsBought = tempItem.child("isBought");
                    tempIsBought.setValue("No");
                    DatabaseReference tempDate = tempItem.child("Date");
                    String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
                    tempDate.setValue(dateTime);

                    shoppingRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                            Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);
                            items.clear();
                            imgs_fruit.clear();
                            items_isBought.clear();
                            if (map.size() != 0) {
                                for (String key : map.keySet()) {
                                    items.add(key);
                                    Map<String, String> tempMap = (Map)map.get(key);
                                    items_isBought.add(tempMap.get("isBought"));
                                    switch (key) {
                                        case "Apple" : imgs_fruit.add(R.drawable.apple);
                                            break;
                                        case "Broccoli" : imgs_fruit.add(R.drawable.broccoli);
                                            break;
                                        case "Eggplant" : imgs_fruit.add(R.drawable.eggplant);
                                            break;
                                        case "Carrot" : imgs_fruit.add(R.drawable.carrot);
                                            break;
                                        case "Banana" : imgs_fruit.add(R.drawable.banana);
                                    }
                                }
                            }

                            list.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
        list.setAdapter(adapter);
        return view;
    }

    class MyAdapter extends ArrayAdapter<String> {

        Context context;
        ArrayList<Integer> imgs;
        ArrayList<String> myTitles;
        ArrayList<String> isBoughtList;
        LayoutInflater layoutInflater;

        MyAdapter(Context c, ArrayList<String> titles, ArrayList<Integer> imgs, ArrayList<String> isBoughtList) {
            super(c, R.layout.buy_row, R.id.buy_item, titles);
            this.context = c;
            this.imgs = imgs;
            this.isBoughtList = isBoughtList;
            this.myTitles = titles;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.buy_row, null);
            }
            TextView nameTv = (TextView) convertView.findViewById(R.id.buy_item);
            ImageView img = (ImageView) convertView.findViewById(R.id.buy_icon);
            CheckBox buttonV = (CheckBox) convertView.findViewById(R.id.checkBox_buy);
            final int p = position;
            final String curItem = myTitles.get(position);
            buttonV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox cb = (CheckBox)v;
                    if (cb.isChecked()) {
                        isBoughtList.set(p, "Yes");
                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                        DatabaseReference tempRef = db.getReference();
                        DatabaseReference tempShoppingRef = tempRef.child("shopping");
                        tempShoppingRef.child(curItem).child("isBought").setValue("Yes");
                    } else {
                        isBoughtList.set(p, "No");
                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                        DatabaseReference tempRef = db.getReference();
                        DatabaseReference tempShoppingRef = tempRef.child("shopping");
                        tempShoppingRef.child(curItem).child("isBought").setValue("No");
                    }

                }
            });


            nameTv.setText(curItem);
            img.setImageResource(imgs.get(position));

            if (isBoughtList.get(position).equals("No")) {
                buttonV.setChecked(false);
            } else {
                buttonV.setChecked(true);

            }
            return convertView;
        }
    }

}
