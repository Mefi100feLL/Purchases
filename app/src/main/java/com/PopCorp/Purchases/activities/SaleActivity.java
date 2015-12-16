package com.PopCorp.Purchases.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.PopCorp.Purchases.R;
import com.PopCorp.Purchases.db.DB;
import com.PopCorp.Purchases.fragments.SaleFragment;
import com.PopCorp.Purchases.loaders.SalesLoader;
import com.PopCorp.Purchases.model.Sale;
import com.PopCorp.Purchases.utils.PreferencesManager;
import com.PopCorp.Purchases.utils.ThemeHelper;
import com.PopCorp.Purchases.utils.UIL;
import com.PopCorp.Purchases.utils.ZoomOutPageTransformer;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.software.shell.fab.ActionButton;

import java.text.ParseException;
import java.util.ArrayList;

public class SaleActivity extends AppCompatActivity {

    public static final String CURRENT_SALE = "current_sale";
    public static final String ARRAY_SALES = "array_sales";

    private Sale currentSale;
    private ArrayList<Sale> sales = new ArrayList<>();
    private Toolbar toolBar;
    private ViewPager viewPager;
    private ImageView image;
    private ActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(ThemeHelper.getInstance().getThemeRes());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);

        try {
            currentSale = SalesLoader.getSale(DB.getInstance().getSale(getIntent().getStringExtra(CURRENT_SALE)));
            ArrayList<String> ids = getIntent().getStringArrayListExtra(ARRAY_SALES);
            for (String id : ids){
                Sale sale = SalesLoader.getSale(DB.getInstance().getSale(id));
                sales.add(sale);
            }
        } catch (ParseException e) {
            finish();
        }

        toolBar = (Toolbar) findViewById(R.id.activity_sale_toolbar);
        fab = (ActionButton) findViewById(R.id.activity_sale_fab);

        setSupportActionBar(toolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        int time = 0;
        if (Build.VERSION.SDK_INT >= 21) {
            image = (ImageView) findViewById(R.id.activity_sale_image);
            image.setTransitionName(String.valueOf(currentSale.getId()));
            ImageLoader.getInstance().displayImage(currentSale.getImageUrl(), image, UIL.getScaleImageOptions());
            time = 300;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                viewPager = (ViewPager) findViewById(R.id.activity_sale_viewpager);
                SampleFragmentPagerAdapter adapter = new SampleFragmentPagerAdapter(getSupportFragmentManager());
                viewPager.setAdapter(adapter);
                viewPager.addOnPageChangeListener(adapter);
                viewPager.setCurrentItem(sales.indexOf(currentSale));
            }
        }, time);
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolBar.setTitle((sales.indexOf(currentSale) + 1) + " " + getString(R.string.string_from) + " " + sales.size());
        if (viewPager != null) {
            viewPager.setKeepScreenOn(PreferencesManager.getInstance().isDisplayNoOff());
        }
    }

    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

        public SampleFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return sales.size();
        }

        @Override
        public Fragment getItem(int position) {
            SaleFragment fragment = new SaleFragment();
            Bundle args = new Bundle();
            args.putString(SaleFragment.CURRENT_SALE_TAG, String.valueOf(sales.get(position).getId()));
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            toolBar.setTitle((position + 1) + " " + getString(R.string.string_from) + " " + sales.size());
            if (Build.VERSION.SDK_INT >= 21) {
                image.setTransitionName(String.valueOf(sales.get(position).getId()));
                ImageLoader.getInstance().displayImage(sales.get(position).getImageUrl(), image, UIL.getScaleImageOptions());
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        hideFab(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                viewPager.setVisibility(View.INVISIBLE);
                Intent intent = new Intent();
                intent.putExtra(CURRENT_SALE, String.valueOf(sales.get(viewPager.getCurrentItem()).getId()));
                intent.putExtra(ARRAY_SALES, getIntent().getStringArrayListExtra(ARRAY_SALES));
                setResult(RESULT_OK, intent);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        super.onBackPressed();
    }

    public void hideFab(Animation.AnimationListener listener) {
        if (fab.isHidden()) {
            listener.onAnimationEnd(fab.getHideAnimation());
        } else {
            if (fab.getAnimation() != null) {
                if (!fab.getAnimation().hasEnded()) {
                    return;
                }
            }
            fab.setHideAnimation(ActionButton.Animations.SCALE_DOWN);
            fab.getHideAnimation().setAnimationListener(listener);
            fab.hide();
        }
    }
}
