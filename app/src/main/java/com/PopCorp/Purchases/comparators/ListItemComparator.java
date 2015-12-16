package com.PopCorp.Purchases.comparators;

import android.content.Context;

import com.PopCorp.Purchases.R;
import com.PopCorp.Purchases.model.ListItem;
import com.PopCorp.Purchases.utils.PreferencesManager;

import java.util.ArrayList;
import java.util.Comparator;

public class ListItemComparator implements Comparator<ListItem> {

    private ArrayList<Comparator<ListItem>> comparators = new ArrayList<>();
    private Context context;

    public ListItemComparator(Context context){
        super();
        this.context = context;
        comparators.add(new ListItemBuyedComparator());
        comparators.add(new ListItemCategoryComparator());
    }
    @Override
    public int compare(ListItem lhs, ListItem rhs) {
        int result = 0;
        for (Comparator<ListItem> comparator : comparators){
            result = comparator.compare(lhs, rhs);
            if (result != 0) {
                return result;
            }
        }
        if (PreferencesManager.getInstance().getSortingListItems().equals(context.getString(R.string.prefs_default_sort_listitem_one))){
            result = lhs.getName().compareToIgnoreCase(rhs.getName());
        } else if (PreferencesManager.getInstance().getSortingListItems().equals(context.getString(R.string.prefs_default_sort_listitem_two))){
            result = rhs.getName().compareToIgnoreCase(lhs.getName());
        } else if (PreferencesManager.getInstance().getSortingListItems().equals(context.getString(R.string.prefs_default_sort_listitem_three))){
            if (lhs.getId() < rhs.getId()){
                result = -1;
            } else if (lhs.getId() > rhs.getId()){
                result = 1;
            }
        }
        return result;
    }
}
