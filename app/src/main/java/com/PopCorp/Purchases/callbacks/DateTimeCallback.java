package com.PopCorp.Purchases.callbacks;

import java.util.Calendar;

public interface DateTimeCallback {

    void onDateSelected(Calendar date);
    void onTimeSelected(Calendar time);
}
