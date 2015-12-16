package com.PopCorp.Purchases.controllers;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.PopCorp.Purchases.R;
import com.PopCorp.Purchases.adapters.CategoriesSpinnerAdapter;
import com.PopCorp.Purchases.callbacks.SpinnerCallback;
import com.PopCorp.Purchases.db.DB;
import com.PopCorp.Purchases.model.ShopCategory;
import com.PopCorp.Purchases.utils.PreferencesManager;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class SpinnersController {

    private Context context;
    private ArrayList<String> edizmsForSpinner;
    private ArrayAdapter<String> adapterForSpinnerEdizm;
    private Spinner spinnerForEdizm;
    private ArrayList<String> shopesForSpinner;
    private ArrayAdapter<String> adapterForSpinnerShop;
    private Spinner spinnerForShop;
    private CategoriesSpinnerAdapter adapterForSpinnerCategory;
    private Spinner spinnerForCategory;

    public SpinnersController(Context context) {
        this.context = context;
    }

    public void initializeSpinnerEdizms(Spinner spinner, final SpinnerCallback callback, String defValue) {
        spinnerForEdizm = spinner;
        edizmsForSpinner = new ArrayList<>(PreferencesManager.getInstance().getEdizms());

        adapterForSpinnerEdizm = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, edizmsForSpinner);
        adapterForSpinnerEdizm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerForEdizm.setAdapter(adapterForSpinnerEdizm);
        if (defValue == null) {
            spinnerForEdizm.setSelection(getPositionForEdizm(PreferencesManager.getInstance().getDefaultEdizm()));
        } else {
            spinnerForEdizm.setSelection(getPositionForEdizm(defValue));
        }

        spinnerForEdizm.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                callback.onSelected((String) spinnerForEdizm.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }

    public void initializeSpinnerCategories(Spinner spinner, final SpinnerCallback callback, long categId) {
        ArrayList<ShopCategory> categories = new ArrayList<>();
        Cursor cursor = DB.getInstance().getShopCategories();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                categories.add(ShopCategory.create(cursor));
                while (cursor.moveToNext()) {
                    categories.add(ShopCategory.create(cursor));
                }
            }
            cursor.close();
        }

        adapterForSpinnerCategory = new CategoriesSpinnerAdapter(context, categories);
        adapterForSpinnerCategory.setDropDownViewResource(R.layout.item_shop_category);
        spinnerForCategory = spinner;
        spinnerForCategory.setAdapter(adapterForSpinnerCategory);
        if (categId != -1) {
            spinnerForCategory.setSelection(categories.indexOf(categId));
        } else {
            spinnerForCategory.setSelection(adapterForSpinnerCategory.getCount() - 1);
        }
    }

    public void initializeSpinnerShops(Spinner spinner, final SpinnerCallback callback, String shop) {
        shopesForSpinner = new ArrayList<>(PreferencesManager.getInstance().getShopes());
        shopesForSpinner.add(context.getResources().getString(R.string.string_no_shop));

        adapterForSpinnerShop = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, shopesForSpinner);
        adapterForSpinnerShop.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerForShop = spinner;
        spinnerForShop.setAdapter(adapterForSpinnerShop);
        if (shop != null) {
            spinnerForShop.setSelection(shopesForSpinner.size() - 1);
        } else {
            spinnerForShop.setSelection(getPositionForShop(shop));
        }
    }

    private int getPositionForEdizm(String edizm) {
        if (edizm == null) {
            return adapterForSpinnerEdizm.getCount() - 1;
        }
        if (!edizmsForSpinner.contains(edizm)) {
            if (edizm.equals("")) {
                return adapterForSpinnerEdizm.getCount() - 1;
            }
            edizmsForSpinner.add(0, edizm);
            addNewEdizmToPrefs(edizm);
        }
        return adapterForSpinnerEdizm.getPosition(edizm);
    }

    private void addNewEdizmToPrefs(final String newEdizm) {
        Set<String> edizmsFromPrefs = PreferencesManager.getInstance().getEdizms();
        if (edizmsFromPrefs != null) {
            edizmsFromPrefs.add(newEdizm);
        }
        PreferencesManager.getInstance().putEdizms(edizmsFromPrefs);
    }

    private int getPositionForShop(String shop) {
        if (shop == null) {
            return adapterForSpinnerShop.getCount() - 1;
        }
        if (!shopesForSpinner.contains(shop)) {
            if (shop.equals("")) {
                return adapterForSpinnerShop.getCount() - 1;
            }
            shopesForSpinner.add(0, shop);
            addNewShopToPrefs(shop);
        }
        return adapterForSpinnerShop.getPosition(shop);
    }

    private void addNewShopToPrefs(final String newShop) {
        Set<String> shopesFromPrefs = PreferencesManager.getInstance().getShopes();
        if (shopesFromPrefs != null) {
            shopesFromPrefs.add(newShop);
        }
        PreferencesManager.getInstance().putShopes(shopesFromPrefs);
    }
}
