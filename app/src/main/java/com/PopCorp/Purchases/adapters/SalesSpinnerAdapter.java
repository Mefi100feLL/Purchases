package com.PopCorp.Purchases.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.PopCorp.Purchases.R;

import java.util.ArrayList;

public class SalesSpinnerAdapter extends BaseAdapter {

    private final Context context;
    private ArrayList<String> objects;
    private String firstItem;

    public SalesSpinnerAdapter(Context context, ArrayList<String> objects, String firstItem) {
        this.context = context;
        this.objects = objects;
        this.firstItem = firstItem;
    }

    @Override
    public int getCount() {
        return objects.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        if (position == 0) {
            return firstItem;
        }
        return objects.get(position + 1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals("DROPDOWN")) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.toolbar_spinner_item_dropdown, parent, false);
            view.setTag("DROPDOWN");
        }

        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        if (position == 0) {
            textView.setText(firstItem);
        } else {
            textView.setText(objects.get(position - 1));
        }
        return view;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals("NON_DROPDOWN")) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.toolbar_spinner_item_actionbar, parent, false);
            view.setTag("NON_DROPDOWN");
        }
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        if (position == 0) {
            textView.setText(firstItem);
        } else {
            textView.setText(objects.get(position - 1));
        }
        return view;
    }
}
