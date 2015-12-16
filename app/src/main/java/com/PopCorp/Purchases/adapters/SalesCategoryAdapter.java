package com.PopCorp.Purchases.adapters;

import android.content.Context;
import android.support.v7.util.SortedList;

import com.PopCorp.Purchases.callbacks.RecyclerCallback;
import com.PopCorp.Purchases.decorators.SaleCategoryDecorator;
import com.PopCorp.Purchases.decorators.SaleDecorator;
import com.PopCorp.Purchases.model.Sale;

import java.util.ArrayList;
import java.util.Comparator;

public class SalesCategoryAdapter extends SalesAdapter{

    public SalesCategoryAdapter(Context context, RecyclerCallback callback, ArrayList<Sale> objects, Comparator<SaleDecorator> saleComparator) {
        super(context, callback, objects, saleComparator);
    }

    @Override
    protected ArrayList<Sale> getFilterResults(CharSequence constraint) {
        ArrayList<Sale> result = new ArrayList<>();
        if (constraint.equals("")){
            return objects;
        }
        for (Sale sale : objects){
            if (constraint.equals(String.valueOf(sale.getCategory().getName()))){
                result.add(sale);
            }
        }
        return result;
    }

    @Override
    protected void update(ArrayList<Sale> sales) {
        ArrayList<SaleDecorator> arrayForRemove = new ArrayList<>();
        for (Sale sale : sales) {
            boolean finded = false;
            for (int i = 0; i < publishItems.size(); i++) {
                SaleDecorator decorator = publishItems.get(i);
                if (decorator.isHeader()) {
                    continue;
                }
                if (!sales.contains(decorator.getSale())) {
                    arrayForRemove.add(decorator);
                }
                if (decorator.getSale().equals(sale)) {
                    finded = true;
                }
            }
            if (!finded) {
                publishItems.add(new SaleCategoryDecorator(sale, false, sale.getCategory()));
            }
        }
        for (SaleDecorator decorator : arrayForRemove) {
            publishItems.remove(decorator);
        }
        ArrayList<SaleCategoryDecorator> headers = new ArrayList<>();
        for (int i = 0; i < publishItems.size(); i++) {
            SaleDecorator decorator = publishItems.get(i);
            if (decorator.isHeader() || decorator.getSale().getCategory() == null){
                continue;
            }
            SaleCategoryDecorator header = new SaleCategoryDecorator(null, true, decorator.getSale().getCategory());
            if (!headers.contains(header)){
                headers.add(header);
            }
        }
        for (SaleCategoryDecorator decorator : headers){
            if (publishItems.indexOf(decorator) == SortedList.INVALID_POSITION) {
                publishItems.add(decorator);
            }
        }
        arrayForRemove.clear();
        for (int i = 0; i < publishItems.size(); i++) {
            SaleDecorator decorator = publishItems.get(i);
            if (decorator.isHeader() && !headers.contains(decorator)) {
                arrayForRemove.add(decorator);
            }
        }
        for (SaleDecorator decorator : arrayForRemove) {
            publishItems.remove(decorator);
        }
    }

    @Override
    public int indexOf(Sale sale){
        SaleCategoryDecorator decorator = new SaleCategoryDecorator(sale, false, sale.getCategory());
        return publishItems.indexOf(decorator);
    }
}