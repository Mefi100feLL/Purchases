package com.PopCorp.Purchases.controllers;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.PopCorp.Purchases.R;
import com.PopCorp.Purchases.callbacks.DateTimeCallback;
import com.PopCorp.Purchases.callbacks.DialogFavoriteCategoriesCallback;
import com.PopCorp.Purchases.callbacks.DialogFavoriteShopsCallback;
import com.PopCorp.Purchases.callbacks.DialogRegionsCallback;
import com.PopCorp.Purchases.db.DB;
import com.PopCorp.Purchases.model.Category;
import com.PopCorp.Purchases.model.Region;
import com.PopCorp.Purchases.model.Shop;
import com.PopCorp.Purchases.utils.PreferencesManager;
import com.PopCorp.Purchases.utils.ThemeHelper;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class DialogController {

    public static void showDialogWithRegions(final Context context, final DialogRegionsCallback callback) {
        ArrayList<String> cities = new ArrayList<>();
        final ArrayList<String> citiesIds = new ArrayList<>();
        Cursor cursor = DB.getInstance().getAllData(Region.TABLE_CITIES);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                cities.add(cursor.getString(cursor.getColumnIndex(Region.KEY_CITY_NAME)));
                citiesIds.add(cursor.getString(cursor.getColumnIndex(Region.KEY_CITY_ID)));
                while (cursor.moveToNext()) {
                    cities.add(cursor.getString(cursor.getColumnIndex(Region.KEY_CITY_NAME)));
                    citiesIds.add(cursor.getString(cursor.getColumnIndex(Region.KEY_CITY_ID)));
                }
            }
            cursor.close();
        }
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.title(R.string.dialog_title_select_region);
        builder.items(cities.toArray(new String[cities.size()]));
        builder.itemsCallbackSingleChoice(citiesIds.indexOf(PreferencesManager.getInstance().getRegionId()), new MaterialDialog.ListCallbackSingleChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                if (which == -1){
                    Toast.makeText(context, R.string.toast_please_select_region, Toast.LENGTH_SHORT).show();
                    return false;
                }
                PreferencesManager.getInstance().putRegion(citiesIds.get(which));
                callback.onSelected();
                dialog.dismiss();
                return true;
            }
        });
        builder.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(MaterialDialog dialog, DialogAction which) {
                dialog.cancel();
            }
        });
        builder.positiveText(R.string.dialog_button_select);
        builder.negativeText(R.string.dialog_button_cancel);
        builder.autoDismiss(false);
        MaterialDialog dialog = builder.build();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public static void showDialogForFavoriteShops(final Context context, final ArrayList<Shop> shops, final DialogFavoriteShopsCallback callback){
        Collections.sort(shops);
        String[] names = new String[shops.size()];
        for (int i = 0; i < shops.size(); i++){
            Shop shop = shops.get(i);
            names[i] = shop.getName();
        }
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.title(R.string.dialog_title_select_favorite_shops);
        builder.items(names);
        builder.itemsCallbackMultiChoice(new Integer[]{}, new MaterialDialog.ListCallbackMultiChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                if (which.length == 0) {
                    Toast.makeText(context, R.string.toast_please_select_favorite_shops, Toast.LENGTH_SHORT).show();
                    return true;
                }
                ArrayList<Shop> result = new ArrayList<>();
                for (int i : which) {
                    Shop shop = shops.get(i);
                    result.add(shop);
                    shop.setFavorite(true);
                    shop.updateOrAddToDB();
                }
                callback.onSelected(result);
                dialog.dismiss();
                return true;
            }
        });
        builder.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(MaterialDialog dialog, DialogAction which) {
                dialog.cancel();
            }
        });
        builder.positiveText(R.string.dialog_button_select);
        builder.negativeText(R.string.dialog_button_cancel);
        builder.autoDismiss(false);
        MaterialDialog dialog = builder.build();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public static void showDialogForFavoriteCategories(final Context context, final ArrayList<Category> categories, final DialogFavoriteCategoriesCallback callback){
        Collections.sort(categories);
        String[] names = new String[categories.size()];
        for (int i = 0; i < categories.size(); i++){
            Category category = categories.get(i);
            names[i] = category.getName();
        }
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.title(R.string.dialog_title_select_favorite_categories);
        builder.items(names);
        builder.itemsCallbackMultiChoice(new Integer[]{}, new MaterialDialog.ListCallbackMultiChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                if (which.length == 0) {
                    Toast.makeText(context, R.string.toast_please_select_favorite_categories, Toast.LENGTH_SHORT).show();
                    return true;
                }
                ArrayList<Category> result = new ArrayList<>();
                for (int i : which) {
                    Category category = categories.get(i);
                    result.add(category);
                    category.setFavorite(true);
                    category.updateOrAddToDB();
                }
                callback.onSelected(result);
                dialog.dismiss();
                return true;
            }
        });
        builder.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(MaterialDialog dialog, DialogAction which) {
                dialog.cancel();
            }
        });
        builder.positiveText(R.string.dialog_button_select);
        builder.negativeText(R.string.dialog_button_cancel);
        builder.autoDismiss(false);
        MaterialDialog dialog = builder.build();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public static void showDatePickerDialog(Activity activity, Calendar date, final DateTimeCallback callback) {
        if (Build.VERSION.SDK_INT > 20) {
            showMaterialDatePickerDialog(activity, date, callback);
            return;
        }
        DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                Calendar date = Calendar.getInstance();
                date.set(year, monthOfYear, dayOfMonth);
                callback.onDateSelected(date);
            }
        };
        DatePickerDialog dialog = DatePickerDialog.newInstance(
                dateListener,
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH)
        );
        dialog.setAccentColor(ThemeHelper.getInstance().getPrimaryColor());
        dialog.setMaxDate(Calendar.getInstance());
        dialog.show(activity.getFragmentManager(), "Datepickerdialog");
    }

    private static void showMaterialDatePickerDialog(Context activity, Calendar date, final DateTimeCallback callback) {
        android.app.DatePickerDialog.OnDateSetListener dateListener = new android.app.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar date = Calendar.getInstance();
                date.set(year, monthOfYear, dayOfMonth);
                callback.onDateSelected(date);
            }
        };
        android.app.DatePickerDialog dialog = new android.app.DatePickerDialog(activity,
                ThemeHelper.getInstance().getDialogThemeRes(),
                dateListener,
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
        Calendar max = Calendar.getInstance();
        max.set(Calendar.HOUR_OF_DAY, 23);
        max.set(Calendar.MINUTE, 59);
        dialog.getDatePicker().setMaxDate(max.getTimeInMillis());
    }

    public static void showTimePickerDialog(Activity activity, Calendar date, final DateTimeCallback callback) {
        if (Build.VERSION.SDK_INT > 20) {
            showMaterialTimePickerDialog(activity, date, callback);
            return;
        }
        TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                Calendar date = Calendar.getInstance();
                date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                date.set(Calendar.MINUTE, minute);
                date.set(Calendar.SECOND, 0);
                callback.onTimeSelected(date);
            }
        };
        TimePickerDialog dialog = TimePickerDialog.newInstance(
                timeListener,
                date.get(Calendar.HOUR_OF_DAY),
                date.get(Calendar.MINUTE),
                true
        );
        dialog.setAccentColor(ThemeHelper.getInstance().getPrimaryColor());
        dialog.setThemeDark(false);
        dialog.show(activity.getFragmentManager(), "Timepickerdialog");
    }

    private static void showMaterialTimePickerDialog(Context activity, Calendar date, final DateTimeCallback callback) {
        android.app.TimePickerDialog dialog = new android.app.TimePickerDialog(activity,
                ThemeHelper.getInstance().getDialogThemeRes(),
                new android.app.TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Calendar date = Calendar.getInstance();
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
                        date.set(Calendar.SECOND, 0);
                        callback.onTimeSelected(date);
                    }
                }, date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), true);
        dialog.show();
    }
}
