package android.goscam.imageloader;

import android.content.Context;
import android.goscam.imageloader.listener.ImageSaveListener;
import android.widget.ImageView;

/**
 * Created by zzkong on 2017/7/6.
 */

public interface BaseImageLoaderStrategy {

    //自定义options
    void loadImage(String url, ImageView imageView, ImageLoaderOptions options);

    //无占位图
    void loadImage(String url, ImageView imageView, String signature);

    //有占位图
    void loadImage(String url, int placeholder, ImageView imageView);

    //加载圆形无边-网络资源
    void loadCircleImage(String url, int placeHolder, ImageView imageView);

    //加载圆形无边-本地drawable资源
    void loadCircleImage(int resId, int placeHolder, ImageView imageView);

    //加载圆形有边框
    void loadCircleBorderImage(String url, int placeHolder, ImageView imageView, float borderWidth, int borderColor);

    //加载Gif图
    void loadGifImage(String url, int placeHolder, ImageView imageView);

    //清除硬盘缓存
    void clearImageDiskCache(final Context context);

    //清除内存缓存
    void clearImageMemoryCache(Context context);

    //获取缓存大小
    String getCacheSize(Context context);

    //保存图片
    void saveImage(Context context, String url, String savePath, String saveFileName, ImageSaveListener listener);
}
