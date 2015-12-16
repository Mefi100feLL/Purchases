package com.PopCorp.Purchases.comparators;

import com.PopCorp.Purchases.model.ListItem;
import com.PopCorp.Purchases.utils.PreferencesManager;

import java.util.Comparator;

public class ListItemCategoryComparator implements Comparator<ListItem>{
    @Override
    public int compare(ListItem lhs, ListItem rhs) {
        int result = 0;
        if (PreferencesManager.getInstance().showCategories()){
            result = lhs.getCategory().compareTo(rhs.getCategory());
        }
        return result;
    }
}
