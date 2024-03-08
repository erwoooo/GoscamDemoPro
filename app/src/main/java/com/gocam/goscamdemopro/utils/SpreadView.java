package com.gocam.goscamdemopro.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.gocam.goscamdemopro.R;

import java.util.ArrayList;
import java.util.List;

public class SpreadView extends View {
    private Paint centerPaint; //中心圆paint

    private Paint tvPaint; //中心圆文字paint

    private int radius = 60; //中心圆半径

    private Paint spreadPaint; //扩散圆paint

    private float centerX;//圆心x

    private float centerY;//圆心y

    private int distance = 25; //每次圆递增间距

    private int maxRadius = 80; //最大圆半径

    private int delayMilliseconds = 33;//扩散延迟间隔，越大扩散越慢

    private int count = 0; // 进度

    private List<Integer> spreadRadius = new ArrayList<>();//扩散圆层级数，元素为扩散的距离

    private List<Integer> alphas = new ArrayList<>();//对应每层圆的透明度

    public SpreadView(Context context) {
        super(context);
    }

    public SpreadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SpreadView);
        radius = a.getDimensionPixelSize(R.styleable.SpreadView_spread_radius, radius);
        maxRadius = a.getDimensionPixelSize(R.styleable.SpreadView_spread_max_radius, maxRadius);
        int centerColor = a.getColor(R.styleable.SpreadView_spread_center_color, ContextCompat.getColor(context, R.color.colorAccent));
        int textColor = a.getColor(R.styleable.SpreadView_spread_text_color, ContextCompat.getColor(context, R.color.white));
        int textSize = a.getDimensionPixelSize(R.styleable.SpreadView_spread_text_size, 20);
        int spreadColor = a.getColor(R.styleable.SpreadView_spread_spread_color, ContextCompat.getColor(context, R.color.colorAccent));
        distance = a.getDimensionPixelSize(R.styleable.SpreadView_spread_distance, distance);
        a.recycle();
        centerPaint = new Paint();
        centerPaint.setColor(centerColor);
        centerPaint.setAntiAlias(true);
        tvPaint = new Paint();
        tvPaint.setColor(textColor);
        tvPaint.setAntiAlias(true);
        tvPaint.setTextSize(textSize);
        tvPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        tvPaint.setTextAlign(Paint.Align.CENTER);
        //最开始不透明且扩散距离为0
        alphas.add(255);
        spreadRadius.add(0);
        spreadPaint = new Paint();
        spreadPaint.setAntiAlias(true);
        spreadPaint.setAlpha(255);
        spreadPaint.setColor(spreadColor);
    }

    public SpreadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //圆心位置
        centerX = w / 2;
        centerY = h / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < spreadRadius.size(); i++) {
            int alpha = alphas.get(i);
            spreadPaint.setAlpha(alpha);
            int width = spreadRadius.get(i);
            //绘制扩散的圆
            canvas.drawCircle(centerX, centerY, radius + width, spreadPaint);
            //每次扩散圆半径递增，圆透明度递减
            if (alpha > 0 && width < 300) {
                alpha = alpha - distance > 0 ? alpha - distance : 1;
                alphas.set(i, alpha);
                spreadRadius.set(i, width + distance);
            }
        }
        //当最外层扩散圆半径达到最大半径时添加新扩散圆
        if (spreadRadius.get(spreadRadius.size() - 1) > maxRadius) {
            spreadRadius.add(0);
            alphas.add(255);
        }
        //超过8个扩散圆，删除最先绘制的圆，即最外层的圆
        if (spreadRadius.size() >= 8) {
            alphas.remove(0);
            spreadRadius.remove(0);
        }
        //中间的圆
        canvas.drawCircle(centerX, centerY, radius, centerPaint);
        canvas.drawText(count + "%", centerX, centerY + 30, tvPaint);
        //TODO 可以在中间圆绘制文字或者图片
        //延迟更新，达到扩散视觉差效果
        postInvalidateDelayed(delayMilliseconds);
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
