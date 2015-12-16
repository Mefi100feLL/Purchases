package com.PopCorp.Purchases.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.PopCorp.Purchases.R;
import com.PopCorp.Purchases.model.ShopCategory;

public class CategoriesSpinnerAdapter extends ArrayAdapter<String> {

    private Context context;
    private ArrayList<ShopCategory> objects;

    public CategoriesSpinnerAdapter(Context context, ArrayList<ShopCategory> objects) {
        super(context, R.layout.item_shop_category);
        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_shop_category, parent, false);
        }
        view.findViewById(R.id.item_list_category_image).setBackgroundColor(objects.get(position).getColor());
        ((TextView) view.findViewById(R.id.item_list_category_name)).setText(objects.get(position).getName());
        return view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_shop_category, parent, false);
        }
        view.findViewById(R.id.item_list_category_image).setBackgroundColor(objects.get(position).getColor());
        ((TextView) view.findViewById(R.id.item_list_category_name)).setText(objects.get(position).getName());
        return view;
    }

    @Override
    public int getCount()
    {
        return objects.size();
    }

    @Override
    public String getItem(int position)
    {
        return objects.get(position).getName();
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    public int getItemPosition(Object obj){
        return objects.indexOf(obj);
    }
}
