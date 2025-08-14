package com.example.healthmanagement.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.example.healthmanagement.R;
import com.luck.picture.lib.engine.ImageEngine;
import com.luck.picture.lib.listener.OnImageCompleteCallback;
import com.luck.picture.lib.tools.MediaUtils;
import com.luck.picture.lib.widget.longimage.ImageSource;
import com.luck.picture.lib.widget.longimage.ImageViewState;
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView;

public class GlideEngine implements ImageEngine {
    private static GlideEngine instance;

    @Override
    public void loadImage(Context context, String url, ImageView imageView) {
        Glide.with(context).load(url).into(imageView);
    }

    @Override
    public void loadImage(Context context, String url, final ImageView imageView, final SubsamplingScaleImageView longImageView, final OnImageCompleteCallback callback) {
        Glide.with(context).asBitmap().load(url).into(new ImageViewTarget<Bitmap>(imageView) {
            @Override
            public void onLoadStarted(Drawable placeholder) {
                super.onLoadStarted(placeholder);
                OnImageCompleteCallback onImageCompleteCallback = callback;
                if (onImageCompleteCallback != null) {
                    onImageCompleteCallback.onShowLoading();
                }
            }

            @Override
            public void onLoadFailed(Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                OnImageCompleteCallback onImageCompleteCallback = callback;
                if (onImageCompleteCallback != null) {
                    onImageCompleteCallback.onHideLoading();
                }
            }

            public void setResource(Bitmap resource) {
                OnImageCompleteCallback onImageCompleteCallback = callback;
                if (onImageCompleteCallback != null) {
                    onImageCompleteCallback.onHideLoading();
                }
                if (resource != null) {
                    boolean eqLongImage = MediaUtils.isLongImg(resource.getWidth(), resource.getHeight());
                    int i = 8;
                    longImageView.setVisibility(eqLongImage ? View.VISIBLE : View.GONE);
                    imageView.setVisibility(View.VISIBLE);

                    if (!eqLongImage) {
                        i = 0;
                    }
                    imageView.setVisibility(i);
                    if (eqLongImage) {
                        longImageView.setQuickScaleEnabled(true);
                        longImageView.setZoomEnabled(true);
                        longImageView.setDoubleTapZoomDuration(100);
                        longImageView.setMinimumScaleType(2);
                        longImageView.setDoubleTapZoomDpi(2);
                        longImageView.setImage(ImageSource.bitmap(resource), new ImageViewState(0.0f, new PointF(0.0f, 0.0f), 0));
                        return;
                    }
                    imageView.setImageBitmap(resource);
                }
            }
        });
    }

    @Override
    public void loadImage(Context context, String url, final ImageView imageView, final SubsamplingScaleImageView longImageView) {
        Glide.with(context).asBitmap().load(url).into(new ImageViewTarget<Bitmap>(imageView) {
            public void setResource(Bitmap resource) {
                if (resource != null) {
                    boolean eqLongImage = MediaUtils.isLongImg(resource.getWidth(), resource.getHeight());
                    int i = 8;
                    longImageView.setVisibility(eqLongImage ? View.VISIBLE : View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                    if (!eqLongImage) {
                        i = 0;
                    }
                    imageView.setVisibility(i);
                    if (eqLongImage) {
                        longImageView.setQuickScaleEnabled(true);
                        longImageView.setZoomEnabled(true);
                        longImageView.setDoubleTapZoomDuration(100);
                        longImageView.setMinimumScaleType(2);
                        longImageView.setDoubleTapZoomDpi(2);
                        longImageView.setImage(ImageSource.bitmap(resource), new ImageViewState(0.0f, new PointF(0.0f, 0.0f), 0));
                        return;
                    }
                    imageView.setImageBitmap(resource);
                }
            }
        });
    }

    @Override
    public void loadFolderImage(final Context context, String url, final ImageView imageView) {
        ((RequestBuilder) ((RequestBuilder) ((RequestBuilder) Glide.with(context).asBitmap().load(url).override(SubsamplingScaleImageView.ORIENTATION_180, SubsamplingScaleImageView.ORIENTATION_180)).centerCrop()).sizeMultiplier(0.5f)).apply(new RequestOptions().placeholder(R.drawable.picture_image_placeholder)).into(new BitmapImageViewTarget(imageView) {
            @Override
            public void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCornerRadius(8.0f);
                imageView.setImageDrawable(circularBitmapDrawable);
            }
        });
    }

    @Override
    public void loadAsGifImage(Context context, String url, ImageView imageView) {
        Glide.with(context).asGif().load(url).into(imageView);
    }

    @Override
    public void loadGridImage(Context context, String url, ImageView imageView) {
        ((RequestBuilder) ((RequestBuilder) Glide.with(context).load(url).override(ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION)).centerCrop()).apply(new RequestOptions().placeholder(R.drawable.picture_image_placeholder)).into(imageView);
    }

    private GlideEngine() {
    }

    public static GlideEngine createGlideEngine() {
        if (instance == null) {
            synchronized (GlideEngine.class) {
                if (instance == null) {
                    instance = new GlideEngine();
                }
            }
        }
        return instance;
    }
}