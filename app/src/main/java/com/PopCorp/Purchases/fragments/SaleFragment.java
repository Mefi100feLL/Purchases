package com.PopCorp.Purchases.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.PopCorp.Purchases.R;
import com.PopCorp.Purchases.db.DB;
import com.PopCorp.Purchases.loaders.SalesLoader;
import com.PopCorp.Purchases.model.Sale;
import com.PopCorp.Purchases.utils.UIL;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.software.shell.fab.ActionButton;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class SaleFragment extends Fragment {

    public static final String CURRENT_SALE_TAG = "current_sale_tag";

    private Sale sale;
    private ActionButton fab;
    private SimpleDateFormat formatter = new SimpleDateFormat("d MMMM", new Locale("ru"));

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_sale, container, false);

        setHasOptionsMenu(true);
        setRetainInstance(true);

        try {
            sale = SalesLoader.getSale(DB.getInstance().getSale(getArguments().getString(CURRENT_SALE_TAG)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        fab = (ActionButton) getActivity().findViewById(R.id.activity_sale_fab);
        ImageView image = (ImageView) rootView.findViewById(R.id.sale_image);
        TextView name = (TextView) rootView.findViewById(R.id.sale_name);
        TextView comment = (TextView) rootView.findViewById(R.id.sale_comment);
        TextView coast = (TextView) rootView.findViewById(R.id.sale_coast);
        TextView period = (TextView) rootView.findViewById(R.id.sale_period);

        ImageLoader.getInstance().displayImage(sale.getImageUrl(), image, UIL.getScaleImageOptions(), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                setTopMargin(view.getMeasuredWidth());
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });

        name.setText(sale.getName());
        comment.setText(sale.getComment());
        comment.setVisibility(sale.getComment().isEmpty() ? View.GONE : View.VISIBLE);
        coast.setText(sale.getCount() + " " + getString(R.string.string_za) + " " + sale.getCoast() + " " + getString(R.string.string_in) + " " + sale.getShop().getName());
        String periodBegin = formatter.format(sale.getPeriodBegin());
        String periodFinish = formatter.format(sale.getPeriodFinish());
        period.setText(periodBegin.equals(periodFinish) ? periodBegin : periodBegin + " - " + periodFinish);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.sale, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sale_share) {
            shareSale();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setTopMargin(int margin){
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.RIGHT;
        params.setMargins(0, margin - getResources().getDimensionPixelSize(R.dimen.fab_size_normal), 0, 0);
        fab.setLayoutParams(params);
        showFab();
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

    private void shareSale() {
        File image = ImageLoader.getInstance().getDiskCache().get(sale.getImageUrl());
        if (image == null) {
            Toast.makeText(getActivity(), R.string.toast_no_founded_sale_image, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        String string = getString(R.string.string_for_share_sale);
        string = string.replace("shop", sale.getShop().getName());
        string = string.replace("name", sale.getName());
        if (!sale.getComment().isEmpty()){
            string = string.replace("comment", sale.getComment());
        } else{
            string = string.replace(" (comment)", "");
        }
        String periodBegin = formatter.format(sale.getPeriodBegin());
        String periodFinish = formatter.format(sale.getPeriodFinish());
        string = string.replace("period", periodBegin.equals(periodFinish) ? periodBegin : "c " + periodBegin + " по " + periodFinish);
        string = string.replace("coast", sale.getCoast());

        shareIntent.putExtra(Intent.EXTRA_TEXT, string);

        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(image));
        shareIntent.setType("image/jpeg");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, getString(R.string.string_send_sale_with_app)));
    }
}
