package com.PopCorp.Purchases.callbacks;

import com.PopCorp.Purchases.model.Category;

import java.util.ArrayList;

public interface DialogFavoriteCategoriesCallback {

    void onSelected(ArrayList<Category> categories);
}
