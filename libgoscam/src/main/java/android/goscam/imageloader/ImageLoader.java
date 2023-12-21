package android.goscam.imageloader;

import android.content.Context;
import android.goscam.imageloader.listener.ImageSaveListener;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by zzkong on 2017/7/6.
 */

public class ImageLoader {
    public static final int PIC_DEFAULT_TYPE = 0;

    public static final int LOAD_STRATEGY_DEFAULT = 0;

    private static ImageLoader mInstance;

    private BaseImageLoaderStrategy mStrategy;

    //这里指定Glide 可以替换成Picasso/UIL等
    public ImageLoader(){
        mStrategy = new GlideImageLoaderStrategy();
    }

    public static ImageLoader getmInstance(){
        if (mInstance == null){
            synchronized (ImageLoader.class){
                if (mInstance == null){
                    mInstance = new ImageLoader();
                    return mInstance;
                }
            }
        }
        return mInstance;
    }

    public void loadImage(String url, ImageView imageView, ImageLoaderOptions options){
        mStrategy.loadImage(url, imageView, options);
    }

    public void loadImage(String url, ImageView imageView, String signature) {
        mStrategy.loadImage(url, imageView, signature);
    }

    public void loadImage(String url, int placeholder, ImageView imageView) {
        Log.e("zzkong", "loadImage: 这里到了么");
        mStrategy.loadImage(url, placeholder, imageView);
    }

    public void loadGifImage(String url, int placeholder, ImageView imageView) {
        mStrategy.loadGifImage(url, placeholder, imageView);
    }

    public void loadCircleImage(String url, int placeholder, ImageView imageView) {
        mStrategy.loadCircleImage(url,placeholder,imageView);
    }

    public void loadCircleImage(int resId, int placeholder, ImageView imageView){
        mStrategy.loadCircleImage(resId, placeholder, imageView);
    }

    public void loadCircleBorderImage(String url, int placeholder, ImageView imageView, float borderWidth, int borderColor) {
        mStrategy.loadCircleBorderImage(url, placeholder, imageView, borderWidth, borderColor);
    }

    /**
     * 清除图片磁盘缓存
     */
    public void clearImageDiskCache(final Context context) {
        mStrategy.clearImageDiskCache(context);
    }

    /**
     * 清除图片内存缓存
     */
    public void clearImageMemoryCache(Context context) {
        mStrategy.clearImageMemoryCache(context);
    }

    /**
     * 清除图片所有缓存
     */
    public void clearImageAllCache(Context context) {
        clearImageDiskCache(context.getApplicationContext());
        clearImageMemoryCache(context.getApplicationContext());
    }

    /**
     * 获取缓存大小
     *
     * @return CacheSize
     */
    public String getCacheSize(Context context) {
        return mStrategy.getCacheSize(context);
    }

    public void saveImage(Context context, String url, String savePath, String saveFileName, ImageSaveListener listener) {
        mStrategy.saveImage(context, url, savePath, saveFileName, listener);
    }

}
