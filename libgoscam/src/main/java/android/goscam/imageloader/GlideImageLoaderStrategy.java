package android.goscam.imageloader;

import android.content.Context;
import android.content.Intent;
import android.goscam.imageloader.listener.ImageSaveListener;
import android.goscam.imageloader.transformation.GlideCircleTransform;
import android.goscam.utils.FileUtil;
import android.goscam.utils.FormatUtil;
import android.goscam.utils.PicUtil;
import android.net.Uri;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by zzkong on 2017/7/6.
 */

public class GlideImageLoaderStrategy implements BaseImageLoaderStrategy{


    @Override
    public void loadImage(String url, ImageView imageView, ImageLoaderOptions options) {
        DrawableTypeRequest request = Glide.with(imageView.getContext()).load(url);
        loadOptions(request, options).into(imageView);
    }

    @Override
    public void loadImage(String url, ImageView imageView, String signature) {
        Glide.with(imageView.getContext()).load(url)
                .placeholder(imageView.getDrawable()).diskCacheStrategy(DiskCacheStrategy.RESULT)
                .signature(new StringSignature(signature))
                .crossFade(300)
                .into(imageView);
    }

    @Override
    public void loadImage(String url, int placeholder, ImageView imageView) {
        loadNormal(imageView.getContext(), url, placeholder, imageView);
    }

    @Override
    public void loadCircleImage(String url, int placeHolder, ImageView imageView) {
        Glide.with(imageView.getContext()).load(url).placeholder(placeHolder)
                .dontAnimate().transform(new GlideCircleTransform(imageView.getContext()))
                .diskCacheStrategy(DiskCacheStrategy.RESULT).into(imageView);
    }

    @Override
    public void loadCircleImage(int resId, int placeHolder, ImageView imageView) {
        Glide.with(imageView.getContext()).load(resId).placeholder(placeHolder)
                .dontAnimate().transform(new GlideCircleTransform(imageView.getContext()))
                .diskCacheStrategy(DiskCacheStrategy.RESULT).into(imageView);
    }

    @Override
    public void loadCircleBorderImage(String url, int placeHolder, ImageView imageView, float borderWidth, int borderColor) {
        Glide.with(imageView.getContext()).load(url).placeholder(placeHolder).dontAnimate()
                .transform(new GlideCircleTransform(imageView.getContext(),borderWidth,borderColor))
                .diskCacheStrategy(DiskCacheStrategy.RESULT).into(imageView);
    }

    @Override
    public void loadGifImage(String url, int placeHolder, ImageView imageView) {
        loadGif(imageView.getContext(), url, placeHolder, imageView);
    }

    @Override
    public void clearImageDiskCache(final Context context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(context.getApplicationContext()).clearDiskCache();
                    }
                }).start();
            }else {
                Glide.get(context.getApplicationContext()).clearDiskCache();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void clearImageMemoryCache(Context context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) { //只能在主线程执行
                Glide.get(context.getApplicationContext()).clearMemory();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getCacheSize(Context context) {
        try {
            return FormatUtil.getFormatSize(FileUtil.getFolderSize(Glide.getPhotoCacheDir(context.getApplicationContext())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void saveImage(Context context, String url, String savePath, String saveFileName, ImageSaveListener listener) {
        if (!FileUtil.isSDCardExsit() || TextUtils.isEmpty(url)) {
            listener.onSaveFail();
            return;
        }
        InputStream fromStream = null;
        OutputStream toStream = null;
        try {
            File cacheFile = Glide.with(context).load(url).downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
            if (cacheFile == null || !cacheFile.exists()) {
                listener.onSaveFail();
                return;
            }
            File dir = new File(savePath);
            if (!dir.exists()) {
                dir.mkdir();
            }
            File file = new File(dir, saveFileName + PicUtil.getPicType(cacheFile.getAbsolutePath()));

            fromStream = new FileInputStream(cacheFile);
            toStream = new FileOutputStream(file);
            byte length[] = new byte[1024];
            int count;
            while ((count = fromStream.read(length)) > 0) {
                toStream.write(length, 0, count);
            }
            //用广播通知相册进行更新相册
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(file);
            intent.setData(uri);
            context.sendBroadcast(intent);
            listener.onSaveSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            listener.onSaveFail();
        } finally {
            if (fromStream != null) {
                try {
                    fromStream.close();
                    toStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    fromStream = null;
                    toStream = null;
                }
            }
        }
    }

    private void loadNormal(final Context ctx, final String url, int placeholder, ImageView imageView){
        final long startTime = System.currentTimeMillis();
        Glide.with(ctx).load(url)
                .placeholder(placeholder)
                .diskCacheStrategy(DiskCacheStrategy.RESULT).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                return false;
            }
        }).into(imageView);
    }

    private void loadGif(final Context ctx, String url, int placeholder, ImageView imageView){
        final long startTime = System.currentTimeMillis();
        Glide.with(ctx).load(url).asGif()
                .placeholder(placeholder).skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESULT).listener(new RequestListener<String, GifDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GifDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GifDrawable resource, String model, Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                return false;
            }
        }).into(imageView);
    }

    private DrawableTypeRequest loadOptions(DrawableTypeRequest request, ImageLoaderOptions options){
        if (options == null) {
            return request;
        }

        if (options.getPlaceHolder() != -1) {
            request.placeholder(options.getPlaceHolder());
        }
        if (options.getErrorDrawable() != -1) {
            request.error(options.getErrorDrawable());
        }
        if (options.isCrossFade()) {
            if(options.getDuration()>0){
                request.crossFade();
            }else{
                request.crossFade();
            }
        }
        if (options.isSkipMemoryCache()) {
            request.skipMemoryCache(options.isSkipMemoryCache());
        }
        if (options.getAnimator() != null) {
            request.animate(options.getAnimator());
        }
        if (options.getSize() != null) {
            Object size = options.getSize();
            request.override(options.getSize().reWidth, options.getSize().reHeight);
        }

        return request;
    }
}
