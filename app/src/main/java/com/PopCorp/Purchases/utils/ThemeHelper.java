package com.PopCorp.Purchases.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.preference.PreferenceManager;

import com.PopCorp.Purchases.R;

public class ThemeHelper {

    private Context context;

    private static ThemeHelper instance;

    public static void setInstance(Context context) {
        instance = new ThemeHelper(context);
    }

    public static ThemeHelper getInstance() {
        return instance;
    }

    private ThemeHelper(Context context) {
        this.context = context;
    }


    public int getPrimaryColor() {
        return PreferencesManager.getInstance().getPrimaryColor();
    }

    public void setPrimaryColor(int position, int color) {
        PreferencesManager.getInstance().putPrimaryColor(color);
        setDialogTheme(position);
        setHeader(position);
    }

    public int getAccentColor() {
        return PreferencesManager.getInstance().getAccentColor();
    }

    public void setAccentColor(int color) {
        PreferencesManager.getInstance().putAccentColor(color);
    }

    public int getThemeRes() {
        final TypedArray ta = context.getResources().obtainTypedArray(R.array.themes);
        final int[] themes = new int[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            themes[i] = ta.getResourceId(i, 0);
        }
        ta.recycle();
        int theme = R.style.BrownTheme;
        if (PreferencesManager.getInstance().getTheme() > themes.length) {
            editPrefsWithThemes();
        }
        if (PreferencesManager.getInstance().getTheme() != -1 && PreferencesManager.getInstance().getTheme() < themes.length) {
            theme = themes[PreferencesManager.getInstance().getTheme()];
        }
        return theme;
    }

    public void setTheme(int position, int color) {
        PreferencesManager.getInstance().putTheme(position);
        setAccentColor(color);
    }

    public int getDialogThemeRes() {
        final TypedArray ta = context.getResources().obtainTypedArray(R.array.dialog_themes);
        final int[] themes = new int[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            themes[i] = ta.getResourceId(i, 0);
        }
        ta.recycle();
        int theme = R.style.TealThemeDialog;
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(context);
        if (PreferencesManager.getInstance().getDialogTheme() != -1) {
            theme = themes[PreferencesManager.getInstance().getDialogTheme()];
        }
        return theme;
    }

    public void setDialogTheme(int position) {
        PreferencesManager.getInstance().putDialogTheme(position);
    }

    public int getHeaderRes() {
        final TypedArray ta = context.getResources().obtainTypedArray(R.array.headers);
        final int[] headers = new int[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            headers[i] = ta.getResourceId(i, 0);
        }
        ta.recycle();
        int headerId = R.drawable.teal;
        if (PreferencesManager.getInstance().getHeader() < headers.length) {
            editPrefsWithThemes();
        }
        if (PreferencesManager.getInstance().getHeader() != -1 && PreferencesManager.getInstance().getHeader() < headers.length) {
            headerId = headers[PreferencesManager.getInstance().getHeader()];
        }
        return headerId;
    }

    public void setHeader(int position) {
        PreferencesManager.getInstance().putHeader(position);
    }

    public void editPrefsWithThemes() {
        int primaryColor = PreferencesManager.getInstance().getPrimaryColor();
        final TypedArray ta = context.getResources().obtainTypedArray(R.array.colors);
        final int[] colors = new int[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            colors[i] = ta.getColor(i, 0);
        }
        ta.recycle();
        int positionOfHeader = -1;
        for (int i = 0; i < colors.length - 1; i++) {
            if (colors[i] == primaryColor) {
                positionOfHeader = i;
            }
        }
        if (positionOfHeader != -1) {
            PreferencesManager.getInstance().putDialogTheme(positionOfHeader);
            PreferencesManager.getInstance().putHeader(positionOfHeader);
        }
        int accentColor = PreferencesManager.getInstance().getAccentColor();
        int positionOfTheme = -1;
        for (int i = 0; i < colors.length - 1; i++) {
            if (colors[i] == accentColor) {
                positionOfTheme = i;
            }
        }
        if (positionOfTheme != -1) {
            PreferencesManager.getInstance().putTheme(positionOfTheme);
        }
    }

    public int getPrimaryDarkColor() {
        return shiftColor(getPrimaryColor());
    }

    public static int shiftColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.9f;
        return Color.HSVToColor(hsv);
    }

}
