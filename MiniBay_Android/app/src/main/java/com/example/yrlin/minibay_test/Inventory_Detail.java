package com.example.yrlin.minibay_test;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class Inventory_Detail extends AppCompatActivity {
    private TextView mTextView;
    private Toolbar mToolbar;
    private Button send_reminder;
    private Button add_shopping;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference mRef = db.getReference();
    DatabaseReference reminderRef = mRef.child("reminder");
    DatabaseReference shoppingRef = mRef.child("shopping");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory__detail);


        mTextView = (TextView)findViewById(R.id.detail_name);
        mToolbar = (Toolbar)findViewById(R.id.detail_toolbar);
        send_reminder = (Button)findViewById(R.id.send_reminder);
        add_shopping = (Button)findViewById(R.id.add_to_shopping_list);

        send_reminder.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        add_shopping.setBackgroundColor(getResources().getColor(R.color.orange));
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final Bundle bundle = getIntent().getExtras();

        final String curItem;
        if (bundle != null) {
            setTitle(bundle.get("itemName").toString());
            curItem = bundle.get("itemName").toString();
            mTextView.setText(bundle.get("itemName").toString());
        }


        shoppingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);
                if (map.containsKey(bundle.get("itemName").toString())) {
                    add_shopping.setBackgroundColor(getResources().getColor(R.color.grey));
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
        ColorDrawable buttonColor = (ColorDrawable)add_shopping.getBackground();
        if (buttonColor.getColor() != getResources().getColor(R.color.grey)) {
            add_shopping.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference value = shoppingRef.child(bundle.get("itemName").toString());
                    DatabaseReference isBought = value.child("isBought");
                    isBought.setValue("No");
                    DatabaseReference dateRef = value.child("Date");
                    String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
                    dateRef.setValue(dateTime);
                }
            });
        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
