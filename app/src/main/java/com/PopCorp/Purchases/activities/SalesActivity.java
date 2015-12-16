package com.PopCorp.Purchases.activities;

import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.PopCorp.Purchases.R;
import com.PopCorp.Purchases.adapters.SalesAdapter;
import com.PopCorp.Purchases.adapters.SalesCategoryAdapter;
import com.PopCorp.Purchases.adapters.SalesShopAdapter;
import com.PopCorp.Purchases.adapters.SalesSpinnerAdapter;
import com.PopCorp.Purchases.callbacks.RecyclerCallback;
import com.PopCorp.Purchases.comparators.SalesCategoryComparator;
import com.PopCorp.Purchases.comparators.SalesShopComparator;
import com.PopCorp.Purchases.loaders.SalesLoader;
import com.PopCorp.Purchases.model.Category;
import com.PopCorp.Purchases.model.Sale;
import com.PopCorp.Purchases.model.Shop;
import com.PopCorp.Purchases.services.LoadingSalesService;
import com.PopCorp.Purchases.services.OfflineSpiceService;
import com.PopCorp.Purchases.utils.PreferencesManager;
import com.PopCorp.Purchases.utils.ThemeHelper;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.PendingRequestListener;
import com.software.shell.fab.ActionButton;

import java.util.ArrayList;
import java.util.Collections;

public class SalesActivity extends AppCompatActivity implements RecyclerCallback {

    public static final String CURRENT_SHOP = "current_shop";
    public static final String CURRENT_CATEGORY = "current_category";

    public static final int REQUEST_CODE_FOR_VIEW_SALES = 1;

    private final SpiceManager dbSpiceManager = new SpiceManager(OfflineSpiceService.class);

    private Toolbar toolBar;
    private Spinner spinner;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyText;
    private ImageView emptyImage;
    private Button emptyButton;
    private LinearLayout emptyLayout;
    private ActionButton fab;

    private SalesAdapter adapter;
    private ArrayList<Sale> objects = new ArrayList<>();

    private String currentFilter = "";
    private ArrayList<String> arrayFilters = new ArrayList<>();
    private String[] arraySizesTable;

    private Shop currentShop;
    private Category currentCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(ThemeHelper.getInstance().getThemeRes());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);

        toolBar = (Toolbar) findViewById(R.id.activity_sales_toolbar);
        setSupportActionBar(toolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setPrimaryColor(ThemeHelper.getInstance().getPrimaryColor());

        currentShop = getIntent().getParcelableExtra(CURRENT_SHOP);
        currentCategory = getIntent().getParcelableExtra(CURRENT_CATEGORY);

        progressBar = (ProgressBar) findViewById(R.id.activity_sales_progress);
        recyclerView = (RecyclerView) findViewById(R.id.activity_sales_recycler);
        emptyText = (TextView) findViewById(R.id.empty_text);
        emptyImage = (ImageView) findViewById(R.id.empty_image);
        emptyButton = (Button) findViewById(R.id.empty_button);
        emptyLayout = (LinearLayout) findViewById(R.id.empty_layout);
        fab = (ActionButton) findViewById(R.id.activity_sales_fab);

        spinner = (Spinner) findViewById(R.id.activity_sales_toolbar_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    currentFilter = "";
                } else {
                    currentFilter = arrayFilters.get(position - 1);
                }
                adapter.getFilter().filter(currentFilter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoadingSales();
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(this, PreferencesManager.getInstance().getSaleTableSize());

        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setItemAnimator(itemAnimator);
        if (currentCategory != null) {
            adapter = new SalesShopAdapter(this, this, objects, new SalesShopComparator());
        } else {
            adapter = new SalesCategoryAdapter(this, this, objects, new SalesCategoryComparator());
        }
        adapter.setLayoutManager(layoutManager, PreferencesManager.getInstance().getSaleTableSize());

        recyclerView.setAdapter(adapter);

        loadFromDB();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(3);
        filter.addAction("com.PopCorp.Purchases.ACTION.UpdateSales");
        registerReceiver(receiver, filter);
    }

    private void startLoadingSales() {
        Intent intent = new Intent(this, LoadingSalesService.class);
        intent.putExtra(LoadingSalesService.EXTRA_SHOP_ID, currentShop.getId());
        startService(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        String title = getString(R.string.title_sales);
        if (currentShop != null) {
            title = currentShop.getName();
            fab.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showFab();
                }
            }, 300);
        } else if (currentCategory != null) {
            title = currentCategory.getName();
        }
        toolBar.setTitle(title);
    }

    @Override
    public void onStart() {
        super.onStart();
        dbSpiceManager.start(this);
    }

    @Override
    public void onStop() {
        if (dbSpiceManager.isStarted()) {
            dbSpiceManager.shouldStop();
        }
        fab.setVisibility(View.INVISIBLE);
        super.onStop();
    }

    private void loadFromDB() {
        SalesLoader loader = null;
        if (currentShop != null) {
            loader = new SalesLoader(currentShop);
        } else if (currentCategory != null) {
            loader = new SalesLoader(currentCategory);
        }
        if (loader == null) {
            throw new RuntimeException("Shop and Category is null");
        }
        dbSpiceManager.execute(loader, new PendingRequestListener<Sale[]>() {
            @Override
            public void onRequestNotFound() {

            }

            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }

            @Override
            public void onRequestSuccess(Sale[] sales) {
                for (Sale sale : sales) {
                    if (!objects.contains(sale)) {
                        objects.add(sale);
                    }
                }
                if (objects.size() > 0) {
                    calculateFilters();
                    showRecyclerView();
                }
                adapter.getFilter().filter(currentFilter);
            }
        });
    }

    private void calculateFilters() {
        arrayFilters.clear();
        if (currentShop != null) {
            ArrayList<Category> filters = new ArrayList<>();
            for (Sale sale : objects) {
                if (sale.getCategory() != null && !filters.contains(sale.getCategory())) {
                    filters.add(sale.getCategory());
                }
            }
            Collections.sort(filters);
            for (Category category : filters) {
                arrayFilters.add(category.getName());
            }
        } else if (currentCategory != null) {
            ArrayList<Shop> filters = new ArrayList<>();
            for (Sale sale : objects) {
                if (sale.getShop() != null && !filters.contains(sale.getShop())) {
                    filters.add(sale.getShop());
                }
            }
            Collections.sort(filters);
            for (Shop shop : filters) {
                arrayFilters.add(shop.getName());
            }
        }

        if (arrayFilters.size() > 0) {
            spinner.setVisibility(View.VISIBLE);
            String firstItem = getString(R.string.all);
            if (currentShop != null) {
                firstItem = getString(R.string.spinner_all_categories);
            } else if (currentCategory != null) {
                firstItem = getString(R.string.spinner_all_shops);
            }
            SalesSpinnerAdapter spinnerAdapter = new SalesSpinnerAdapter(this, arrayFilters, firstItem);
            spinner.setAdapter(spinnerAdapter);
            if (arrayFilters.contains(currentFilter)) {
                spinner.setSelection(arrayFilters.indexOf(currentFilter));
                return;
            }
            spinner.setSelection(0);
        } else {
            spinner.setVisibility(View.GONE);
        }
    }

    private void showRecyclerView() {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shops, menu);
        int groupId = 12;
        MenuItem item = menu.findItem(R.id.action_size_table);
        item.getSubMenu().clear();
        arraySizesTable = getResources().getStringArray(R.array.sizes_table_lists);
        for (String filterItem : arraySizesTable) {
            MenuItem addedItem = item.getSubMenu().add(groupId, filterItem.hashCode(), Menu.NONE, filterItem);
            if (filterItem.equals(String.valueOf(PreferencesManager.getInstance().getSaleTableSize()))) {
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
        }
        for (String filterItem : arraySizesTable) {
            if (item.getItemId() == filterItem.hashCode()) {
                PreferencesManager.getInstance().putSaleTableSize(Integer.parseInt(filterItem));
                item.setChecked(true);
                GridLayoutManager layoutManager = new GridLayoutManager(this, Integer.parseInt(filterItem));
                recyclerView.setLayoutManager(layoutManager);
                adapter.setLayoutManager(layoutManager, PreferencesManager.getInstance().getSaleTableSize());
            }
        }
        return true;
    }

    @Override
    public void onItemClick(View v, int position) {
        ArrayList<Sale> sales = adapter.getSales();
        ArrayList<String> ids = new ArrayList<>();
        for (Sale sale : sales) {
            ids.add(String.valueOf(sale.getId()));
        }

        Intent intent = new Intent(this, SaleActivity.class);
        intent.putExtra(SaleActivity.CURRENT_SALE, String.valueOf(adapter.getPublishItems().get(position).getSale().getId()));
        intent.putStringArrayListExtra(SaleActivity.ARRAY_SALES, ids);

        if (Build.VERSION.SDK_INT >= 21) {
            ArrayList<Pair<View, String>> pairs = new ArrayList<>();
            for (Sale sale : sales) {
                int pos = adapter.indexOf(sale);
                SalesAdapter.ViewHolder holder = (SalesAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(pos);
                if (holder != null) {
                    if (holder.image != null) {
                        pairs.add(new Pair<View, String>(holder.image, String.valueOf(sale.getId())));
                    }
                }
            }
            ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this, pairs.toArray(new Pair[pairs.size()]));
            startActivityForResult(intent, REQUEST_CODE_FOR_VIEW_SALES, transitionActivityOptions.toBundle());
            return;
        }

        if (Build.VERSION.SDK_INT >= 16) {
            Bundle bundle = null;
            try {
                Bitmap bitmap = ((BitmapDrawable) ((ImageView) v).getDrawable()).getBitmap();
                bundle = ActivityOptions.makeThumbnailScaleUpAnimation(v, bitmap, 0, 0).toBundle();
            } catch (Exception ignored) {
                startActivityForResult(intent, REQUEST_CODE_FOR_VIEW_SALES);
            }
            startActivityForResult(intent, REQUEST_CODE_FOR_VIEW_SALES, bundle);
            return;
        }

        startActivityForResult(intent, REQUEST_CODE_FOR_VIEW_SALES);
    }

    @Override
    public void onItemLongClick(View v, int position) {

    }

    @Override
    public void onEmpty(int stringRes, int drawableRes, int buttonRes, View.OnClickListener listener) {

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
        AppBarLayout appBar = (AppBarLayout) findViewById(R.id.activity_sales_appbar);
        appBar.setBackgroundColor(primaryColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(primaryColor);
        }
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            loadFromDB();
        }
    };
}
