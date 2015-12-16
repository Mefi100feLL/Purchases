package com.PopCorp.Purchases.activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.PopCorp.Purchases.R;
import com.PopCorp.Purchases.adapters.ListItemsAdapter;
import com.PopCorp.Purchases.callbacks.RecyclerCallback;
import com.PopCorp.Purchases.callbacks.SpinnerCallback;
import com.PopCorp.Purchases.controllers.SpinnersController;
import com.PopCorp.Purchases.db.DB;
import com.PopCorp.Purchases.loaders.skidkaonline.ShoppingListsLoader;
import com.PopCorp.Purchases.model.ListItem;
import com.PopCorp.Purchases.model.ShoppingList;
import com.PopCorp.Purchases.utils.PreferencesManager;
import com.PopCorp.Purchases.utils.ThemeHelper;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.ViewTransformer;
import com.software.shell.fab.ActionButton;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class ShoppingListActivity extends AppCompatActivity implements RecyclerCallback {

    public static final String CURRENT_LIST_ID = "current_list_id";

    private Toolbar toolBar;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyText;
    private ImageView emptyImage;
    private Button emptyButton;
    private LinearLayout emptyLayout;
    private ActionButton fab;

    private ShoppingList currentList;
    private ListItemsAdapter adapter;
    private BottomSheetLayout bottomSheet;

    protected void onCreate(Bundle savedInstanceState) {
        setTheme(ThemeHelper.getInstance().getThemeRes());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        toolBar = (Toolbar) findViewById(R.id.activity_list_toolbar);
        setSupportActionBar(toolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setPrimaryColor(ThemeHelper.getInstance().getPrimaryColor());

        long id = getIntent().getLongExtra(CURRENT_LIST_ID, -1);
        if (id == -1) {
            finish();
        }
        currentList = ShoppingListsLoader.getList(DB.getInstance().getList(id));

        progressBar = (ProgressBar) findViewById(R.id.activity_list_progress);
        recyclerView = (RecyclerView) findViewById(R.id.activity_list_recycler);
        emptyText = (TextView) findViewById(R.id.empty_text);
        emptyImage = (ImageView) findViewById(R.id.empty_image);
        emptyButton = (Button) findViewById(R.id.empty_button);
        emptyLayout = (LinearLayout) findViewById(R.id.empty_layout);
        fab = (ActionButton) findViewById(R.id.activity_list_fab);
        bottomSheet = (BottomSheetLayout) findViewById(R.id.activity_list_bottomsheet);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setItemAnimator(itemAnimator);
        adapter = new ListItemsAdapter(this, this, currentList.getItems(), currentList.getCurrency());
        recyclerView.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet(null);
            }
        });
    }

    private void showBottomSheet(ListItem item) {
        View view = LayoutInflater.from(ShoppingListActivity.this).inflate(R.layout.content_new_listitem, bottomSheet, false);
        view.findViewById(R.id.content_new_listitem_appbar).setBackgroundColor(ThemeHelper.getInstance().getPrimaryColor());
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.content_new_listitem_toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet.dismissSheet();
            }
        });
        toolbar.setBackgroundColor(ThemeHelper.getInstance().getPrimaryColor());

        EditText editName = (EditText) view.findViewById(R.id.content_new_listitem_name);
        final EditText editCount = (EditText) view.findViewById(R.id.content_new_listitem_count);
        Spinner spinnerEdizm = (Spinner) view.findViewById(R.id.content_new_listitem_edizm);
        final EditText editCoast = (EditText) view.findViewById(R.id.content_new_listitem_coast);
        TextView currency = (TextView) view.findViewById(R.id.content_new_listitem_currency);
        Spinner spinnerShop = (Spinner) view.findViewById(R.id.content_new_listitem_shop);
        CheckBox checkImportant = (CheckBox) view.findViewById(R.id.content_new_listitem_important);
        Spinner spinnerCategory = (Spinner) view.findViewById(R.id.content_new_listitem_category);
        EditText editComment = (EditText) view.findViewById(R.id.content_new_listitem_comment);

        ImageView minus = (ImageView) view.findViewById(R.id.content_new_listitem_count_minus);
        ImageView plus = (ImageView) view.findViewById(R.id.content_new_listitem_count_plus);
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BigDecimal count = new BigDecimal(editCount.getText().toString());
                count.subtract(new BigDecimal("1"));
                editCount.setText(count.toString());
            }
        });
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BigDecimal count = new BigDecimal(editCount.getText().toString());
                count.add(new BigDecimal("1"));
                editCount.setText(count.toString());
            }
        });

        String edizm = null;
        long category = -1;
        String shop = null;
        if (item != null) {
            edizm = item.getEdizm();
            category = item.getCategory().getId();
            shop = item.getShop();
        }
        SpinnersController spinnersController = new SpinnersController(this);
        spinnersController.initializeSpinnerEdizms(spinnerEdizm, new SpinnerCallback() {
            @Override
            public void onSelected(String value) {
                editCoast.setHint(getString(R.string.hint_listitem_coast).replace("edizm", value));
            }
        }, edizm);
        spinnersController.initializeSpinnerCategories(spinnerCategory, null, category);
        spinnersController.initializeSpinnerShops(spinnerShop, null, shop);

        if (item != null) {
            editName.setText(item.getName());
            editCount.setText(item.getCount().toString());
            editCoast.setHint(getString(R.string.hint_listitem_coast).replace("edizm", item.getEdizm()));
            editCoast.setText(item.getCoast().toString());
            checkImportant.setChecked(item.isImportant());
            editComment.setText(item.getComment());
        }
        editCoast.setHint(getString(R.string.hint_listitem_coast).replace("edizm", PreferencesManager.getInstance().getDefaultEdizm()));
        currency.setText(currentList.getCurrency());

        bottomSheet.setShouldDimContentView(true);
        bottomSheet.showWithSheetView(view);
        bottomSheet.expandSheet();
    }

    @Override
    public void onResume() {
        super.onResume();
        fab.postDelayed(new Runnable() {
            @Override
            public void run() {
                showFab();
            }
        }, 300);
        toolBar.setTitle(currentList.getName());
    }

    @Override
    public void onStop() {
        fab.setVisibility(View.INVISIBLE);
        super.onStop();
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
        getMenuInflater().inflate(R.menu.shopping_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (bottomSheet.isSheetShowing()) {
            bottomSheet.dismissSheet();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onItemClick(View v, int position) {

    }

    @Override
    public void onItemLongClick(View v, int position) {

    }

    @Override
    public void onEmpty(int stringRes, int drawableRes, int buttonRes, View.OnClickListener listener) {

    }

    public void setPrimaryColor(int primaryColor) {
        LinearLayout bottomBar = (LinearLayout) findViewById(R.id.activity_list_bottom_bar);
        bottomBar.setBackgroundColor(ThemeHelper.getInstance().getPrimaryColor());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(primaryColor));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(primaryColor);
        }
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
}
