package com.pic.optimize.fresco;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.postprocessors.IterativeBoxBlurPostProcessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.pic.optimize.R;

/**
 * Created by zgf on 2017/3/2.
 */

public class FrescoUtils {

    private FrescoUtils() {
    }

    /**
     * 网络 url
     *
     * @param url
     */
    public static void loadImage(String url, SimpleDraweeView simpleDraweeView) {
        if (!TextUtils.isEmpty(url))
            simpleDraweeView.setImageURI(url);
    }

    /**
     * 网络 uri
     *
     * @param uri
     */
    public static void loadImage(Uri uri, SimpleDraweeView simpleDraweeView) {
        if (uri != null)
            simpleDraweeView.setImageURI(uri);
    }

    /**
     * 本地图片 id
     *
     * @param id
     */
    public static void loadImage(int id, SimpleDraweeView simpleDraweeView) {
        simpleDraweeView.setImageResource(id);
    }

    /**
     * 加载任意圆角图片
     *
     * @param context
     * @param picUrl
     * @param simpleDraweeView
     * @param topLeft
     * @param topRight
     * @param bottomLeft
     * @param bottomRight
     */
    public static void loadImage(Context context, String picUrl, final SimpleDraweeView simpleDraweeView, int topLeft, int topRight, int bottomLeft, int bottomRight) {
        Uri fileUri = Uri.parse(picUrl);
        RoundingParams params = RoundingParams.fromCornersRadii(
                DensityUtils.dip2px(context, topLeft),
                DensityUtils.dip2px(context, topRight),
                DensityUtils.dip2px(context, bottomLeft),
                DensityUtils.dip2px(context, bottomRight));
        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(context.getResources());
        if (params != null) {
            builder.setRoundingParams(params);
        }
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(300)
                .setPlaceholderImage(R.mipmap.ic_launcher)
                .setFailureImage(R.mipmap.ic_launcher)
                .setRetryImage(R.mipmap.ic_launcher)
                .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                .build();

        final ImageRequest requestBuilder = ImageRequestBuilder.newBuilderWithSource(fileUri)
                .setProgressiveRenderingEnabled(true)
                .build();

        final DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(requestBuilder)
                .setAutoPlayAnimations(true)
                .setTapToRetryEnabled(true)
                .build();
        simpleDraweeView.setHierarchy(hierarchy);
        simpleDraweeView.setController(controller);
    }

    /**
     * 以高斯模糊显示。
     *
     * @param draweeView View。
     * @param url        url.
     * @param iterations 迭代次数，越大越魔化。
     * @param blurRadius 模糊图半径，必须大于0，越大越模糊。
     */
    public static void loadImage(String url, SimpleDraweeView draweeView, int iterations, int blurRadius) {
        try {
            Uri uri = Uri.parse(url);
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setPostprocessor(new IterativeBoxBlurPostProcessor(iterations, blurRadius))// iterations, blurRadius
                    .build();
            AbstractDraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setOldController(draweeView.getController())
                    .setImageRequest(request)
                    .build();
            draweeView.setController(controller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载 GIF 图
     *
     * @param url
     * @param mSimpleDraweeView
     */
    public static void loadImageGif(String url, SimpleDraweeView mSimpleDraweeView) {
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse(url))
                .setAutoPlayAnimations(true)
                .build();
        mSimpleDraweeView.setController(controller);
    }


    /**
     * 加载"磁盘"资源
     * @param filePath
     * @param view
     */
    public static void loadImageFile(String filePath, SimpleDraweeView view) {
        String url = "file://" + filePath;
        loadImage(url, view);
    }

    /**
     * 直接从网络获取bitmap
     * @param context
     * @param url
     * @param listener 回调监听
     */
    public static void loadBitmap(Context context, String url, final BitmapListener listener) {
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setProgressiveRenderingEnabled(false)
                .build();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest, context);
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            protected void onNewResultImpl(Bitmap bitmap) {
                listener.onSuccess(bitmap);
            }

            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                listener.onFail();
            }
        }, CallerThreadExecutor.getInstance());
    }

    /**
     * bitmap回调
     */
    public interface BitmapListener {
        void onSuccess(Bitmap bitmap);
        void onFail();
    }
}
