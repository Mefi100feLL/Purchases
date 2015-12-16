package com.PopCorp.Purchases.activities.skidkaonline;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
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
import com.PopCorp.Purchases.adapters.SalesSpinnerAdapter;
import com.PopCorp.Purchases.adapters.skidkaonline.SalesAdapter;
import com.PopCorp.Purchases.callbacks.RecyclerCallback;
import com.PopCorp.Purchases.loaders.skidkaonline.SalesInternetLoader;
import com.PopCorp.Purchases.model.skidkaonline.Sale;
import com.PopCorp.Purchases.model.skidkaonline.Shop;
import com.PopCorp.Purchases.services.OfflineSpiceService;
import com.PopCorp.Purchases.services.SkidkaOnlineSpiceService;
import com.PopCorp.Purchases.utils.PreferencesManager;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.PendingRequestListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;

public class SalesActivity extends AppCompatActivity implements RecyclerCallback {

    public static final String CURRENT_SHOP = "current_shop";

    public static final int REQUEST_CODE_FOR_VIEW_SALES = 1;

    private final SpiceManager dbSpiceManager = new SpiceManager(OfflineSpiceService.class);
    private final SpiceManager spiceManager = new SpiceManager(SkidkaOnlineSpiceService.class);

    private Toolbar toolBar;
    private Spinner spinner;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyText;
    private ImageView emptyImage;
    private Button emptyButton;
    private LinearLayout emptyLayout;
    private SwipeRefreshLayout swipeRefresh;

    private SalesAdapter adapter;
    private ArrayList<Sale> objects = new ArrayList<>();

    private String currentFilter = "";
    private ArrayList<String> arrayFilters = new ArrayList<>();
    private SalesSpinnerAdapter spinnerAdapter;
    private String[] arraySizesTable;

    private Shop currentShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_skidkaonline);

        toolBar = (Toolbar) findViewById(R.id.activity_sales_toolbar);

        setSupportActionBar(toolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        currentShop = getIntent().getParcelableExtra(CURRENT_SHOP);

        progressBar = (ProgressBar) findViewById(R.id.activity_sales_progress);
        recyclerView = (RecyclerView) findViewById(R.id.activity_sales_recycler);
        emptyText = (TextView) findViewById(R.id.empty_text);
        emptyImage = (ImageView) findViewById(R.id.empty_image);
        emptyButton = (Button) findViewById(R.id.empty_button);
        emptyLayout = (LinearLayout) findViewById(R.id.empty_layout);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.activity_sales_refresh);

        swipeRefresh.setColorSchemeResources(R.color.swipe_refresh_color_one, R.color.swipe_refresh_color_two, R.color.swipe_refresh_color_three);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshing(true);
                loadFromNetwork();
            }
        });

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

        GridLayoutManager layoutManager = new GridLayoutManager(this, PreferencesManager.getInstance().getSaleTableSize());

        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setItemAnimator(itemAnimator);
        adapter = new SalesAdapter(this, objects);
        adapter.setLayoutManager(layoutManager, PreferencesManager.getInstance().getSaleTableSize());

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
        toolBar.setTitle(currentShop.getName());
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
        dbSpiceManager.execute(new com.PopCorp.Purchases.loaders.skidkaonline.SalesLoader(currentShop.getUrl()), new PendingRequestListener<Sale[]>() {
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
                loadFromNetwork();
            }
        });
    }

    private void loadFromNetwork() {
        spiceManager.execute(new SalesInternetLoader(currentShop), new PendingRequestListener<Sale[]>() {
            @Override
            public void onRequestNotFound() {

            }

            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }

            @Override
            public void onRequestSuccess(Sale[] sales) {
                refreshing(false);
                ArrayList<Sale> tmp = new ArrayList<>(Arrays.asList(sales));
                ListIterator<Sale> iterator = objects.listIterator();
                while (iterator.hasNext()) {
                    Sale sale = iterator.next();
                    if (!tmp.contains(sale)) {
                        sale.removeFromDB();
                        iterator.remove();
                    }
                }
                for (Sale sale : sales) {
                    if (!objects.contains(sale)) {
                        objects.add(sale);
                        sale.updateOrAddToDB();
                    } else {
                        Sale existed = objects.get(objects.indexOf(sale));
                        if (!sale.contentEquals(existed)) {
                            existed.update(sale);
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

                        }
                    });
                }
                adapter.getFilter().filter(currentFilter);
            }
        });
    }

    private void calculateFilters() {
        arrayFilters.clear();
        for (Sale sale : objects) {
            if (sale.getGroupName() != null && !arrayFilters.contains(sale.getGroupName())) {
                arrayFilters.add(sale.getGroupName());
            }
        }
        if (arrayFilters.size() > 1) {
            spinner.setVisibility(View.VISIBLE);
            String firstItem = getString(R.string.spinner_all_categories);
            spinnerAdapter = new SalesSpinnerAdapter(this, arrayFilters, firstItem);
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

        Intent intent = new Intent(this, SaleActivity.class);
        intent.putExtra(SaleActivity.CURRENT_SALE_TAG, sales.get(position));
        intent.putParcelableArrayListExtra(SaleActivity.CURRENT_ARRAY_SALES_TAG, sales);

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
}
