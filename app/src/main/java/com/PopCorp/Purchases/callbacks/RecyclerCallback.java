package com.PopCorp.Purchases.callbacks;

import android.view.View;

public interface RecyclerCallback {

    void onItemClick(View v, int position);
    void onItemLongClick(View v, int position);
    void onEmpty(int stringRes, int drawableRes, int buttonRes, View.OnClickListener listener);
}