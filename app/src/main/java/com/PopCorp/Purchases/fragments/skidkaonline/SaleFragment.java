package com.PopCorp.Purchases.fragments.skidkaonline;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.PopCorp.Purchases.R;
import com.PopCorp.Purchases.db.DB;
import com.PopCorp.Purchases.model.skidkaonline.Sale;
import com.PopCorp.Purchases.utils.UIL;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import java.io.File;

public class SaleFragment extends Fragment {

    public static final String CURRENT_SALE_TAG = "current_sale_tag";

    private Sale sale;
    private SubsamplingScaleImageView imageView;
    private CircularProgressView progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_sale_skidkaonline, container, false);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        sale = getArguments().getParcelable(CURRENT_SALE_TAG);

        progressBar = (CircularProgressView) rootView.findViewById(R.id.fragment_sale_progressbar);
        imageView = (SubsamplingScaleImageView) rootView.findViewById(R.id.fragment_sale_imageview);
        imageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM);
        imageView.setMaxScale(getResources().getDimension(R.dimen.image_maximum_scale));

        File smallFile = ImageLoader.getInstance().getDiskCache().get(sale.getSmallImageUrl());
        if (smallFile != null) {
            imageView.setImage(ImageSource.uri(smallFile.getAbsolutePath()));
            loadBigImage();
        } else {
            ImageLoader.getInstance().loadImage(sale.getSmallImageUrl(), null, UIL.getScaleImageOptions(), new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    File smallFile = ImageLoader.getInstance().getDiskCache().get(sale.getSmallImageUrl());
                    if (smallFile != null) {
                        imageView.setImage(ImageSource.uri(smallFile.getAbsolutePath()));
                    }
                    bitmap.recycle();
                    loadBigImage();
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            }, new ImageLoadingProgressListener() {
                @Override
                public void onProgressUpdate(String s, View view, int progress, int size) {
                    progressBar.setProgress(progress * 500 / size);
                }
            });
        }
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void loadBigImage() {
        ImageLoader.getInstance().loadImage(sale.getImageUrl(), null, UIL.getScaleImageOptions(), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                File file = ImageLoader.getInstance().getDiskCache().get(sale.getImageUrl());
                if (file != null) {
                    imageView.setImage(ImageSource.uri(file.getAbsolutePath()));
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String s, View view, int progress, int size) {
                progressBar.setProgress(500 + progress * 500 / size);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.sale_skidkaonline, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*if (item.getItemId() == R.id.action_sale_favorite) {
            sale.setFavorite(!sale.isFavorite());
            sale.updateInDB(db);
            if (sale.isFavorite()) {
                item.setIcon(R.drawable.ic_star_white_24dp);
                item.setTitle(R.string.action_sale_from_favorite);
            } else {
                item.setIcon(R.drawable.ic_star_outline_white_24dp);
                item.setTitle(R.string.action_sale_in_favorite);
            }
            return true;
        }
        if (item.getItemId() == R.id.action_sale_share) {
            shareSale();
            return true;
        }
        if (item.getItemId() == R.id.action_sale_crop) {
            Intent intent = new Intent(getActivity(), CropActivity.class);
            intent.putExtra(CropActivity.CURRENT_SALE_TAG, sale);
            startActivity(intent);
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    /*private void shareSale() {
        File image = imageLoader.getDiskCache().get(sale.getImageUrl());
        if (image == null) {
            image = imageLoader.getDiskCache().get(sale.getSmallImageUrl());
        }
        if (image == null) {
            Toast.makeText(getActivity(), R.string.error_no_cached_image, Toast.LENGTH_SHORT).show();
            return;
        }
        String[] split = sale.getShop().split("/");
        String shop = split[split.length - 1];
        Cursor cursor = db.getData(DB.TABLE_SHOPS, DB.COLUMNS_SHOPS, DB.KEY_SHOP_URL + "='" + sale.getShop() + "'");
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                shop = cursor.getString(cursor.getColumnIndex(DB.KEY_SHOP_NAME));
            }
            cursor.close();
        }

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.string_for_share_sale).replaceAll("shop", shop).replace("period", sale.getPeriod()));

        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(image));
        shareIntent.setType("image/jpeg");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, getString(R.string.string_chooser_send_in)));
    }*/
}