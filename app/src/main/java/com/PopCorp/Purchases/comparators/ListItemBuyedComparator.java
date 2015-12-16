package com.PopCorp.Purchases.comparators;

import com.PopCorp.Purchases.model.ListItem;
import com.PopCorp.Purchases.utils.PreferencesManager;

import java.util.Comparator;

public class ListItemBuyedComparator implements Comparator<ListItem>{

    @Override
    public int compare(ListItem lhs, ListItem rhs) {
        int result = 0;
        if (PreferencesManager.getInstance().replaceBuyed()){
            if (lhs.isBuyed() && !rhs.isBuyed()){
                result = -1;
            } else if (!lhs.isBuyed() && rhs.isBuyed()){
                result = 1;
            }
        }
        return result;
    }
}
