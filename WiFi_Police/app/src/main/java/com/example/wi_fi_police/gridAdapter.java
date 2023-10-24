package com.example.wi_fi_police;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class gridAdapter extends BaseAdapter {

    Context context;
    String[] optionName;
    int[] image;

    LayoutInflater inflater;

    public gridAdapter(Context context, String[] optionName, int[] image) {
        this.context = context;
        this.optionName = optionName;
        this.image = image;
    }

    @Override
    public int getCount() {
        return optionName.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {


        if(inflater==null)
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(view ==null){
            view = inflater.inflate(R.layout.grid_item,null);
        }

        ImageView imageView = view.findViewById(R.id.clickableImage);
        TextView textView =  view.findViewById(R.id.item_name);

        imageView.setImageResource(image[i]);
        textView.setText(optionName[i]);


        return view;
    }
}
