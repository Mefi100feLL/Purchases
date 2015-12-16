package com.PopCorp.Purchases.activities.skidkaonline;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.PopCorp.Purchases.R;
import com.PopCorp.Purchases.adapters.CitysAdapter;
import com.PopCorp.Purchases.loaders.skidkaonline.CitysInternetLoader;
import com.PopCorp.Purchases.loaders.skidkaonline.CitysLoader;
import com.PopCorp.Purchases.model.skidkaonline.City;
import com.PopCorp.Purchases.services.OfflineSpiceService;
import com.PopCorp.Purchases.services.SkidkaOnlineSpiceService;
import com.PopCorp.Purchases.utils.PreferencesManager;
import com.PopCorp.Purchases.utils.ThemeHelper;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.PendingRequestListener;
import com.software.shell.fab.ActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;

public class CityActivity extends AppCompatActivity {

    private final SpiceManager dbSpiceManager = new SpiceManager(OfflineSpiceService.class);
    private final SpiceManager spiceManager = new SpiceManager(SkidkaOnlineSpiceService.class);

    private ActionButton fab;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefresh;
    private TextView emptyText;
    private ImageView emptyImage;
    private Button emptyButton;
    private LinearLayout emptyLayout;

    private CitysAdapter adapter;
    private ArrayList<City> objects = new ArrayList<>();
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(ThemeHelper.getInstance().getThemeRes());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        Toolbar toolBar = (Toolbar) findViewById(R.id.activity_city_toolbar);
        setSupportActionBar(toolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setPrimaryColor(ThemeHelper.getInstance().getPrimaryColor());

        listView = (ListView) findViewById(R.id.activity_city_listview);
        fab = (ActionButton) findViewById(R.id.activity_city_fab);
        progressBar = (ProgressBar) findViewById(R.id.activity_city_progress);
        emptyText = (TextView) findViewById(R.id.empty_text);
        emptyImage = (ImageView) findViewById(R.id.empty_image);
        emptyButton = (Button) findViewById(R.id.empty_button);
        emptyLayout = (LinearLayout) findViewById(R.id.empty_layout);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.activity_city_refresh);

        swipeRefresh.setColorSchemeResources(R.color.swipe_refresh_color_one, R.color.swipe_refresh_color_two, R.color.swipe_refresh_color_three);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshing(true);
                loadFromNetwork();
            }
        });

        adapter = new CitysAdapter(this, objects);
        listView.setAdapter(adapter);
        listView.setKeepScreenOn(PreferencesManager.getInstance().isDisplayNoOff());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSelectedCity(position);
                showFab();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideFab(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        PreferencesManager.getInstance().setCity(adapter.getSelectedCity().getUrl());
                        setResult(Activity.RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        });

        loadFromDB();
    }

    private void refreshing(boolean refresh) {
        swipeRefresh.setRefreshing(refresh);
        swipeRefresh.setEnabled(!refresh);
    }

    @Override
    public void onResume() {
        super.onResume();
        String city = PreferencesManager.getInstance().getCity();
        if (!city.isEmpty()) {
            fab.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showFab();
                }
            }, 300);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(this);
        dbSpiceManager.start(this);
    }

    @Override
    public void onStop() {
        fab.setVisibility(View.INVISIBLE);
        if (spiceManager.isStarted()) {
            spiceManager.shouldStop();
        }
        if (dbSpiceManager.isStarted()) {
            dbSpiceManager.shouldStop();
        }
        super.onStop();
    }

    private void loadFromDB() {
        dbSpiceManager.execute(new CitysLoader(), new PendingRequestListener<City[]>() {
            @Override
            public void onRequestNotFound() {

            }

            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }

            @Override
            public void onRequestSuccess(City[] citys) {
                for (City city : citys) {
                    if (!objects.contains(city)) {
                        objects.add(city);
                    }
                }
                if (objects.size() > 0) {
                    showListView();
                    refreshing(true);
                }
                adapter.setObjects(objects);
                adapter.notifyDataSetChanged();
                loadFromNetwork();
            }
        });
    }

    private void loadFromNetwork() {
        spiceManager.execute(new CitysInternetLoader(), new PendingRequestListener<City[]>() {
            @Override
            public void onRequestNotFound() {
                refreshing(false);
            }

            @Override
            public void onRequestFailure(SpiceException spiceException) {
                refreshing(false);
            }

            @Override
            public void onRequestSuccess(City[] citys) {
                refreshing(false);
                ArrayList<City> tmp = new ArrayList<>(Arrays.asList(citys));
                ListIterator<City> iterator = objects.listIterator();
                while (iterator.hasNext()) {
                    City city = iterator.next();
                    if (!tmp.contains(city)) {
                        city.removeFromDB();
                        iterator.remove();
                    }
                }
                for (City city : citys) {
                    if (!objects.contains(city)) {
                        objects.add(city);
                        city.updateOrAddToDB();
                    } else {
                        City existed = objects.get(objects.indexOf(city));
                        if (!city.contentEquals(existed)) {
                            existed.update(city);
                            existed.updateOrAddToDB();
                        }
                    }
                }
                if (objects.size() > 0) {
                    showListView();
                } else {
                    showEmpty(R.string.empty_no_shops_restart_app, R.drawable.ic_menu_gallery, R.string.button_restart_app, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            update();
                        }
                    });
                }
                adapter.setObjects(objects);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void update() {
        showProgress();
        loadFromNetwork();
    }

    private void showListView() {
        listView.setFastScrollAlwaysVisible(true);
        listView.setFastScrollEnabled(true);
        progressBar.setVisibility(View.INVISIBLE);
        emptyLayout.setVisibility(View.INVISIBLE);
    }

    private void showEmpty(int stringRes, int drawableRes, int buttonRes, View.OnClickListener listener) {
        listView.setFastScrollEnabled(false);
        progressBar.setVisibility(View.INVISIBLE);
        emptyLayout.setVisibility(View.VISIBLE);
        emptyText.setText(stringRes);
        emptyImage.setImageResource(drawableRes);
        emptyButton.setText(buttonRes);
        emptyButton.setOnClickListener(listener);
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        emptyLayout.setVisibility(View.INVISIBLE);
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
                setResult(Activity.RESULT_CANCELED);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void showFab() {
        if (fab.getAnimation() != null) {
            if (!fab.getAnimation().hasEnded()) {
                return;
            }
        }
        fab.setShowAnimation(ActionButton.Animations.SCALE_UP);
        fab.show();
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

    public void setPrimaryColor(int primaryColor) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(primaryColor));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(primaryColor);
        }
    }
}