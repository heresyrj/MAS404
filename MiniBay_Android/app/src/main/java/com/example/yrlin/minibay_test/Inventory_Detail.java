package com.example.yrlin.minibay_test;

import android.app.Dialog;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Inventory_Detail extends AppCompatActivity {
    private TextView mTextView;
    private Toolbar mToolbar;
    private Button send_reminder;
    private Button add_shopping;
    private ImageView image_detail;
    private TextView freshness;
    private TextView addedDate;
    private TextView bestBefore;
    private ListView nutritionListView;
    private LinearLayout nutritionLinearLayout;

    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference mRef = db.getReference();
    DatabaseReference reminderRef = mRef.child("reminder");
    DatabaseReference shoppingRef = mRef.child("shopping");
    DatabaseReference inventoryRef = mRef.child("inventory");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory__detail);


        //TextView = (TextView)findViewById(R.id.detail_name);
        mToolbar = (Toolbar)findViewById(R.id.detail_toolbar);
        send_reminder = (Button)findViewById(R.id.send_reminder);
        add_shopping = (Button)findViewById(R.id.add_to_shopping_list);
        image_detail = (ImageView)findViewById(R.id.image_detail);
        freshness = (TextView)findViewById(R.id.freshness);
        addedDate = (TextView)findViewById(R.id.addDate);
        bestBefore = (TextView)findViewById(R.id.bestBefore);
        nutritionListView = (ListView) findViewById(R.id.nutrition_list_view);
        nutritionLinearLayout = (LinearLayout) findViewById(R.id.nutrition_items_layout);

        send_reminder.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_shape_emphasis));
        add_shopping.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_shape_normal));
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final Bundle bundle = getIntent().getExtras();


            setTitle(bundle.get("itemName").toString());
            final String curItem = bundle.get("itemName").toString();
            //mTextView.setText(bundle.get("itemName").toString());

        switch (curItem) {
            case "Apple" :
                image_detail.setImageResource(R.drawable.apple_detail);
                break;
            case "Banana":
                image_detail.setImageResource(R.drawable.banana_detail);
                break;
            case "Eggplant" :
                image_detail.setImageResource(R.drawable.eggplant_detail);
                break;
            case "Carrot" :
                image_detail.setImageResource(R.drawable.carrot_detail);
                break;
            case "Watermelon" :
                image_detail.setImageResource(R.drawable.watermelon_detail);
                break;
            case "Broccoli":
                image_detail.setImageResource(R.drawable.broccoli_detail);
                break;
        }


        //get JSON from here





        DatabaseReference mCurrentRef = inventoryRef.child(curItem);
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mCurrentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = (Map<String, String>)dataSnapshot.getValue();
                String putinString = map.get("putinDate");
                Date putinDate = new Date();
                try{
                    putinDate = simpleDateFormat.parse(putinString);
                    Log.v("啪啪啪","哪里跑");
                } catch (ParseException ex){
                    ex.printStackTrace();
                }
                String fileName = "nutrition.json";
                String expireDaysString = "";
                int expireDays = 15;
                try {
                    JSONObject jsonObject = new JSONObject(loadJSONFromAsset(fileName));
                    JSONObject curObject = jsonObject.getJSONObject(curItem.toLowerCase());
                    expireDaysString = curObject.getString("expireDays");
                    expireDays = Integer.parseInt(expireDaysString);
                    Log.v("进来啦",fileName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Date curDate = new Date();
                long diff = curDate.getTime() - putinDate.getTime();
                long usedDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                long percentage = (expireDays - usedDays) * 100 / expireDays;
                if (percentage < 0) {
                    freshness.setText("0%");
                    freshness.setTextColor(getResources().getColor(R.color.bayError));
                } else {
                    freshness.setText(percentage + "%");
                }

                SimpleDateFormat format1 =  new SimpleDateFormat("MMM dd");
                addedDate.setText(format1.format(putinDate));

                Calendar dateputin = Calendar.getInstance();
                dateputin.setTime(putinDate);
                dateputin.add(Calendar.DATE, expireDays);

                Date bestBeforeDate = dateputin.getTime();
                bestBefore.setText(format1.format(bestBeforeDate));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        String fileName = "nutrition.json";
        ArrayList<String> nutritionlist = new ArrayList<>();
        try {
            // Read content from JSON File
            JSONObject jsonObject = new JSONObject(loadJSONFromAsset(fileName));
            JSONObject curObject = jsonObject.getJSONObject(curItem.toLowerCase());
            JSONObject nutritionObj = curObject.getJSONObject("nutrition");
            Map<String, String> nutritionMap = new HashMap<>();
            parse(nutritionObj, nutritionMap);

            // Add views for nutritionLinearLayout

            for (String key : nutritionMap.keySet()) {

                TextView key_textView = new TextView(this);
                TextView nutrition_value = new TextView(this);

                key_textView.setText(key);
                key_textView.setTextAppearance(this, R.style.itemCardText);
                nutrition_value.setText(nutritionMap.get(key) + " mg");
                nutrition_value.setTextAppearance(this, R.style.nutriCardNum);

                // Need to fix relative layout problem!!
                RelativeLayout rowRelativeLayout = new RelativeLayout(this);
                RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                nutrition_value.setLayoutParams(params1);

                rowRelativeLayout.addView(key_textView);
                rowRelativeLayout.addView(nutrition_value);


                nutritionLinearLayout.addView(rowRelativeLayout);


                String temp = key + "         " + nutritionMap.get(key) + " mg ";
                nutritionlist.add(temp);
            }
//            nutritionListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, nutritionlist));


        } catch (Exception e) {
            e.printStackTrace();
        }








        shoppingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);
                if (map.containsKey(bundle.get("itemName").toString())) {
                    add_shopping.setBackground(getResources().getDrawable(R.drawable.button_shape_disabled));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        send_reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                //show dialog
                showDialog(bundle);




            }
        });
        GradientDrawable buttonColor = (GradientDrawable) add_shopping.getBackground();
//        ColorDrawable buttonColor = (ColorDrawable)add_shopping.getBackground();
//        if (buttonColor.getColor() != getResources().getColor(R.color.grey)) {
//            add_shopping.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    DatabaseReference value = shoppingRef.child(bundle.get("itemName").toString());
//                    DatabaseReference isBought = value.child("isBought");
//                    isBought.setValue("No");
//                    DatabaseReference dateRef = value.child("Date");
//                    String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//                    dateRef.setValue(dateTime);
//                }
//            });
//        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public String loadJSONFromAsset(String fileName) {
        String json = null;
        try {
            InputStream is = getAssets().open(fileName);
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

    private void showDialog(final Bundle bundle) {
        final Dialog mydiag = new Dialog(this);
        mydiag.setTitle("some title");
        mydiag.setContentView(R.layout.message_dialog);
        Button button_cancel = (Button) mydiag.findViewById(R.id.dialog_cancel);
        final EditText message = (EditText)mydiag.findViewById(R.id.message_text);
        message.setText("You may need to buy some "+bundle.get("itemName").toString());

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mydiag.dismiss();
            }
        });

        Button button_send = (Button) mydiag.findViewById(R.id.dialog_send);

        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                DatabaseReference tempKey = reminderRef.child("First");
                String message_text = message.getText().toString();
                tempKey.setValue(message_text);


                Toast imageToast = new Toast(getBaseContext());
                ImageView image = new ImageView(getBaseContext());

                image.setImageResource(R.drawable.reminder_send);

                imageToast.setView(image);
                imageToast.setDuration(Toast.LENGTH_LONG);
                imageToast.setGravity(Gravity.CENTER, 0, 0);
                imageToast.show();
                mydiag.dismiss();
            }
        });

        mydiag.show();
    }
}
