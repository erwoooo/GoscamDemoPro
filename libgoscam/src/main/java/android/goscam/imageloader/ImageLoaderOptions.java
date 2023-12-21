package android.goscam.imageloader;

import android.graphics.drawable.Drawable;

import com.bumptech.glide.request.animation.ViewPropertyAnimation;
import com.bumptech.glide.signature.StringSignature;

public class ImageLoaderOptions {
    private int placeHolderId = -1; //当没有成功加载的时候显示的图片
    private Drawable placeHolder;
    private ImageReSize size = null; //重新设定容器宽高
    private int errorDrawable = -1;  //加载错误的时候显示的drawable
    private boolean isCrossFade = false; //是否渐变平滑的显示图片
    private int duration;
    private boolean isSkipMemoryCache = false; //是否跳过内存缓存
    private ViewPropertyAnimation.Animator animator = null; // 图片加载动画
    private StringSignature signature;

    private ImageLoaderOptions(Builder builder) {
        this.placeHolderId = builder.placeHolderId;
        this.placeHolder = builder.placeHolder;
        this.size = builder.size;
        this.errorDrawable = builder.errorDrawable;
        this.isCrossFade = builder.isCrossFade;
        this.duration = builder.duration;
        this.isSkipMemoryCache = builder.isSkipMemoryCache;
        this.animator = builder.animator;
        this.signature = builder.signature;
    }

    public ImageReSize getSize() {
        return size;
    }

    public ViewPropertyAnimation.Animator getAnimator() {
        return animator;
    }

    public int getPlaceHolder() {
        return this.placeHolderId;
    }

    public int getErrorDrawable() {
        return errorDrawable;
    }

    public boolean isCrossFade() {
        return this.isCrossFade;
    }
    public int getDuration(){
        return this.duration;
    }

    public boolean isSkipMemoryCache() {
        return this.isSkipMemoryCache;
    }

    public class ImageReSize {
        int reWidth = 0;
        int reHeight = 0;

        public ImageReSize(int reWidth, int reHeight) {
            if (reHeight <= 0) {
                reHeight = 0;
            }
            if (reWidth <= 0) {
                reWidth = 0;
            }
            this.reHeight = reHeight;
            this.reWidth = reWidth;
        }
    }




    public static final class Builder {
        private int placeHolderId = -1;
        private Drawable placeHolder;
        private ImageReSize size = null;
        private int errorDrawable = -1;
        private StringSignature signature;
        private boolean isCrossFade = false;
        private int duration;
        private boolean isSkipMemoryCache = false;
        private ViewPropertyAnimation.Animator animator = null;

        public Builder placeHolder(int drawableId) {
            this.placeHolderId = drawableId;
            return this;
        }

        public Builder placeHolder(Drawable drawable){
            this.placeHolder = drawable;
            return this;
        }

        public Builder reSize(ImageReSize size) {
            this.size = size;
            return this;
        }

        public Builder anmiator(ViewPropertyAnimation.Animator animator) {
            this.animator = animator;
            return this;
        }

        public Builder errorDrawable(int errorDrawable) {
            this.errorDrawable = errorDrawable;
            return this;
        }

        public Builder signature(String signature){
            this.signature = new StringSignature(signature);
            return this;
        }

        public Builder crossFade(int duration) {
            this.isCrossFade = true;
            this.duration = duration;
            return this;
        }
        public Builder crossFade() {
            this.isCrossFade = true;
            return this;
        }

        public Builder isSkipMemoryCache(boolean isSkipMemoryCache) {
            this.isSkipMemoryCache = isSkipMemoryCache;
            return this;
        }

        public ImageLoaderOptions build() {
            return new ImageLoaderOptions(this);
        }
    }
}
