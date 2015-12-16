package com.PopCorp.Purchases;

import android.app.Application;
import android.content.Context;

import com.PopCorp.Purchases.db.DB;
import com.PopCorp.Purchases.utils.PreferencesManager;
import com.PopCorp.Purchases.utils.ThemeHelper;
import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.File;

public class PurchasesApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PreferencesManager.setInstance(this);
        ThemeHelper.setInstance(this);
        DB.setInstance(this);
        initImageLoader(this);
        PreferencesManager.getInstance().firstStart();
    }

    public static void initImageLoader(Context context) {
        FileNameGenerator generator = new FileNameGenerator() {
            @Override
            public String generate(String s) {
                String[] split = s.split("/");
                String name = split[split.length - 1];
                return name;
            }
        };
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir == null) {
            cacheDir = context.getCacheDir();
        }
        DiskCache diskCache = new UnlimitedDiskCache(cacheDir, context.getCacheDir(), generator);

        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(context);
        builder.threadPriority(Thread.NORM_PRIORITY - 2);
        builder.denyCacheImageMultipleSizesInMemory();
        builder.diskCache(diskCache);
        builder.tasksProcessingOrder(QueueProcessingType.LIFO);
        if (BuildConfig.DEBUG) {
            builder.writeDebugLogs();
        }
        ImageLoaderConfiguration config = builder.build();
        ImageLoader.getInstance().init(config);
    }
}
