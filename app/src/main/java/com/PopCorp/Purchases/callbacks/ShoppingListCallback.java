package com.PopCorp.Purchases.callbacks;

import com.PopCorp.Purchases.model.ShoppingList;

public interface ShoppingListCallback {

    void onChange(ShoppingList list);
    void onRemove(ShoppingList list);
    void onShare(ShoppingList list);
    void onSetAlarm(ShoppingList list);
}
