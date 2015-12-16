package com.PopCorp.Purchases.activities.skidkaonline;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.PopCorp.Purchases.R;
import com.PopCorp.Purchases.adapters.SpinnerAdapter;
import com.PopCorp.Purchases.adapters.skidkaonline.ShopesAdapter;
import com.PopCorp.Purchases.callbacks.RecyclerCallback;
import com.PopCorp.Purchases.callbacks.skidkaonline.ShopFavoriteCallback;
import com.PopCorp.Purchases.loaders.skidkaonline.ShopesInternetLoader;
import com.PopCorp.Purchases.loaders.skidkaonline.ShopesLoader;
import com.PopCorp.Purchases.model.skidkaonline.Category;
import com.PopCorp.Purchases.model.skidkaonline.Shop;
import com.PopCorp.Purchases.services.OfflineSpiceService;
import com.PopCorp.Purchases.services.SkidkaOnlineSpiceService;
import com.PopCorp.Purchases.utils.PreferencesManager;
import com.PopCorp.Purchases.utils.ThemeHelper;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.PendingRequestListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;

public class ShopesActivity extends AppCompatActivity implements RecyclerCallback, ShopFavoriteCallback{

    public static final String CURRENT_CATEGORY = "current_category";

    private final SpiceManager dbSpiceManager = new SpiceManager(OfflineSpiceService.class);
    private final SpiceManager spiceManager = new SpiceManager(SkidkaOnlineSpiceService.class);

    private Toolbar toolBar;
    private Spinner spinner;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private TextView emptyText;
    private ImageView emptyImage;
    private Button emptyButton;
    private LinearLayout emptyLayout;

    private Category currentCategory;

    private ShopesAdapter adapter;
    private ArrayList<Shop> objects = new ArrayList<>();

    private String currentFilter = ShopesAdapter.FILTER_ALL;
    private String[] arraySizesTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(ThemeHelper.getInstance().getThemeRes());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopes_skidkaonline);

        toolBar = (Toolbar) findViewById(R.id.activity_shopes_toolbar);
        setSupportActionBar(toolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setPrimaryColor(ThemeHelper.getInstance().getPrimaryColor());

        currentCategory = getIntent().getParcelableExtra(CURRENT_CATEGORY);

        progressBar = (ProgressBar) findViewById(R.id.activity_shopes_progress);
        recyclerView = (RecyclerView) findViewById(R.id.activity_shopes_recycler);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.activity_shopes_refresh);
        emptyText = (TextView) findViewById(R.id.empty_text);
        emptyImage = (ImageView) findViewById(R.id.empty_image);
        emptyButton = (Button) findViewById(R.id.empty_button);
        emptyLayout = (LinearLayout) findViewById(R.id.empty_layout);

        spinner = (Spinner) findViewById(R.id.activity_shopes_toolbar_spinner);
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this, new String[]{getString(R.string.spinner_all_shops), getString(R.string.spinner_favorite_shops)});
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (objects.size() > 0) {
                    showRecyclerView();
                    if (position == 0) {
                        currentFilter = ShopesAdapter.FILTER_ALL;
                    } else {
                        currentFilter = ShopesAdapter.FILTER_FAVORITE;
                    }
                    adapter.getFilter().filter(currentFilter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        swipeRefresh.setColorSchemeResources(R.color.swipe_refresh_color_one, R.color.swipe_refresh_color_two, R.color.swipe_refresh_color_three);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshing(true);
                loadFromNetwork();
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(this, PreferencesManager.getInstance().getShopTableSize());

        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setItemAnimator(itemAnimator);
        adapter = new ShopesAdapter(this, objects);
        recyclerView.setAdapter(adapter);

        loadFromDB();
    }

    private void refreshing(boolean refresh) {
        swipeRefresh.setRefreshing(refresh);
        swipeRefresh.setEnabled(!refresh);
    }

    @Override
    public void onResume() {
        super.onResume();
        toolBar.setTitle(currentCategory.getName());
    }

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(this);
        dbSpiceManager.start(this);
    }

    @Override
    public void onStop() {
        if (spiceManager.isStarted()) {
            spiceManager.shouldStop();
        }
        if (dbSpiceManager.isStarted()) {
            dbSpiceManager.shouldStop();
        }
        super.onStop();
    }

    private void loadFromDB() {
        dbSpiceManager.execute(new ShopesLoader(currentCategory.getUrl()), new PendingRequestListener<Shop[]>() {
            @Override
            public void onRequestNotFound() {

            }

            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }

            @Override
            public void onRequestSuccess(Shop[] contacts) {
                for (Shop contact : contacts) {
                    if (!objects.contains(contact)) {
                        objects.add(contact);
                    }
                }
                if (objects.size() > 0) {
                    setFavoriteFilter();
                    showRecyclerView();
                    refreshing(true);
                    adapter.getFilter().filter(currentFilter);
                }
                loadFromNetwork();
            }
        });
    }

    private void loadFromNetwork() {
        spiceManager.execute(new ShopesInternetLoader(currentCategory), new PendingRequestListener<Shop[]>() {
            @Override
            public void onRequestNotFound() {
                refreshing(false);
            }

            @Override
            public void onRequestFailure(SpiceException spiceException) {
                refreshing(false);
            }

            @Override
            public void onRequestSuccess(Shop[] shopes) {
                refreshing(false);
                ArrayList<Shop> tmp = new ArrayList<>(Arrays.asList(shopes));
                ListIterator<Shop> iterator = objects.listIterator();
                while (iterator.hasNext()) {
                    Shop shop = iterator.next();
                    if (!tmp.contains(shop)) {
                        shop.removeFromDB();
                        iterator.remove();
                    }
                }
                for (Shop shop : shopes) {
                    if (!objects.contains(shop)) {
                        objects.add(shop);
                        shop.updateOrAddToDB();
                    } else {
                        Shop existed = objects.get(objects.indexOf(shop));
                        if (!shop.contentEquals(existed)) {
                            existed.update(shop);
                            existed.updateOrAddToDB();
                        }
                    }
                }
                if (objects.size() > 0) {
                    showRecyclerView();
                } else {
                    showEmpty(R.string.empty_no_shops_restart_app, R.drawable.ic_menu_gallery, R.string.button_restart_app, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            restartApp();
                        }
                    });
                }
                adapter.getFilter().filter(currentFilter);
            }
        });
    }

    private void restartApp() {

    }

    private void checkFilterToAll() {
        spinner.setSelection(0);
        showRecyclerView();
    }

    private void setFavoriteFilter() {
        if (objects.size() > 0) {
            spinner.setVisibility(View.VISIBLE);
            for (Shop shop : objects) {
                if (shop.isFavorite()) {
                    currentFilter = ShopesAdapter.FILTER_FAVORITE;
                    spinner.setSelection(1);
                    return;
                }
            }
            spinner.setSelection(0);
        }
    }

    private void showRecyclerView() {
        spinner.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        emptyLayout.setVisibility(View.INVISIBLE);
    }

    private void showEmpty(int stringRes, int drawableRes, int buttonRes, View.OnClickListener listener) {
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
    public void onItemClick(View v, int position) {
        Shop shop = adapter.getPublishItems().get(position);
        Intent intent = new Intent(this, SalesActivity.class);
        intent.putExtra(SalesActivity.CURRENT_SHOP, shop);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View v, int position) {

    }

    @Override
    public void onEmpty(int stringRes, int drawableRes, int buttonRes, View.OnClickListener listener) {
        if (listener == null) {
            if (currentFilter.equals(ShopesAdapter.FILTER_ALL)) {
                listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        restartApp();
                    }
                };
            } else {
                listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkFilterToAll();
                    }
                };
            }
        }
        showEmpty(stringRes, drawableRes, buttonRes, listener);
    }

    @Override
    public void onFavoriteChanged(Shop shop) {
        adapter.getFilter().filter(currentFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shops, menu);

        int groupId = 12;
        MenuItem item = menu.findItem(R.id.action_size_table);
        item.getSubMenu().clear();
        arraySizesTable = getResources().getStringArray(R.array.sizes_table_lists);
        for (String filterItem : arraySizesTable) {
            MenuItem addedItem = item.getSubMenu().add(groupId, filterItem.hashCode(), Menu.NONE, filterItem);
            if (filterItem.equals(String.valueOf(PreferencesManager.getInstance().getShopTableSize()))) {
                addedItem.setChecked(true);
            }
        }
        item.getSubMenu().setGroupCheckable(groupId, true, true);
        item.getSubMenu().setGroupEnabled(groupId, true);
        item.setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        for (String filterItem : arraySizesTable) {
            if (item.getItemId() == filterItem.hashCode()) {
                PreferencesManager.getInstance().putShopTableSize(Integer.parseInt(filterItem));
                item.setChecked(true);
                GridLayoutManager layoutManager = new GridLayoutManager(this, Integer.parseInt(filterItem));
                recyclerView.setLayoutManager(layoutManager);
            }
        }
        return true;
    }

    public void setPrimaryColor(int primaryColor) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(primaryColor));
        }
        AppBarLayout appBar = (AppBarLayout) findViewById(R.id.activity_shopes_appbar);
        appBar.setBackgroundColor(primaryColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(primaryColor);
        }
    }
}
