package com.example.yrlin.minibay_test;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Inventory_Detail extends AppCompatActivity {
    private TextView mTextView;
    private Toolbar mToolbar;
    private Button send_reminder;
    private Button add_shopping;
    private ImageView image_detail;
    private TextView freshness;

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
                freshness.setText("Freshness      "+percentage+"%");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });









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
                Toast imageToast = new Toast(getBaseContext());
                ImageView image = new ImageView(getBaseContext());

                image.setImageResource(R.drawable.reminder_send);

                imageToast.setView(image);
                imageToast.setDuration(Toast.LENGTH_LONG);
                imageToast.setGravity(Gravity.CENTER, 0, 0);
                imageToast.show();
                DatabaseReference tempKey = reminderRef.child("First");
                tempKey.setValue("You may need to buy some "+bundle.get("itemName").toString());
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
}
