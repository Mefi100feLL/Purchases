package com.PopCorp.Purchases.decorators;

import android.text.SpannableStringBuilder;
import android.text.Spanned;

import com.PopCorp.Purchases.model.ListItem;
import com.PopCorp.Purchases.model.ShoppingList;
import com.PopCorp.Purchases.utils.EllipsizeLineSpan;

public class ShoppingListDecorator {

    private ShoppingList list;

    public ShoppingListDecorator(ShoppingList list) {
        setList(list);
    }

    public SpannableStringBuilder getItems() {
        SpannableStringBuilder spannableString = new SpannableStringBuilder("");
        for (ListItem item : list.getItems()) {
            spannableString.append(item.getName());
            if (item.isBuyed()) {
                spannableString.setSpan(new EllipsizeLineSpan(true), spannableString.length() - item.getName().length(), spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                spannableString.setSpan(new EllipsizeLineSpan(false), spannableString.length() - item.getName().length(), spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (list.getItems().indexOf(item) != list.getItems().size() - 1) {
                spannableString.append("\n");
            }
        }
        return spannableString;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ShoppingListDecorator)) return false;
        ShoppingListDecorator listDecorator = (ShoppingListDecorator) object;
        return getList().equals(listDecorator.getList());
    }

    public ShoppingList getList() {
        return list;
    }

    public void setList(ShoppingList list) {
        this.list = list;
    }
}
