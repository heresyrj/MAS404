package com.example.yrlin.minibay_test;


import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Shopping_Buy extends Fragment {

    ListView list;
    String[] items;

    int[] imgs_fruit = {R.drawable.apple, R.drawable.banana, R.drawable.broccoli, R.drawable.carrot, R.drawable.eggplant};

    public Shopping_Buy() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shopping__buy, container, false);


        Resources res = getResources();
        items = res.getStringArray(R.array.buy_items);



        list = (ListView)view.findViewById(R.id.buy_items_view);

        MyAdapter adapter = new MyAdapter(this.getActivity(), items, imgs_fruit);

        list.setAdapter(adapter);
        return view;
    }

    class MyAdapter extends ArrayAdapter<String> {

        Context context;
        int[] imgs;
        String[] myTitles;
        LayoutInflater layoutInflater;

        MyAdapter(Context c, String[] titles, int[] imgs) {
            super(c, R.layout.buy_row, R.id.buy_item, titles);
            this.context = c;
            this.imgs = imgs;
            this.myTitles = titles;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.buy_row, null);
            }
            TextView nameTv = (TextView) convertView.findViewById(R.id.buy_item);
            ImageView img = (ImageView) convertView.findViewById(R.id.buy_icon);

            nameTv.setText(myTitles[position]);
            img.setImageResource(imgs[position]);
            return convertView;
        }
    }

}
