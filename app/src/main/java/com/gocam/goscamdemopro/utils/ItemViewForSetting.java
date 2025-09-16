package com.gocam.goscamdemopro.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.gocam.goscamdemopro.R;

//设置界面的ItemView
public class ItemViewForSetting extends FrameLayout implements View.OnClickListener {
    protected View view;
    protected TextView tvLeft;
    protected Switch switchButton;
    protected TextView tvRight;
    protected ImageView ivRightPic;
    protected View ivRightPicTwo;

    boolean leftIvShow;
    String leftText;
    float leftTextSize;
    boolean leftTextShow;
    boolean switchBtnShow;
    boolean switchBtnChecked;
    String rightText;
    boolean rightTextShow;
    int rightIvRes;
    boolean rightIvShow;
    int rightImgRes;
    boolean rightImgShow;

    boolean topLineShow;
    boolean bottomLineShow;

    public ItemViewForSetting(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ItemView);
        if (typedArray != null) {
            leftTextSize = typedArray.getDimensionPixelSize(R.styleable.ItemView_i_left_txt_size, 0);

            leftText = typedArray.getString(R.styleable.ItemView_i_left_txt);
            leftTextShow = typedArray.getBoolean(R.styleable.ItemView_i_left_txt_show, true);

            switchBtnShow = typedArray.getBoolean(R.styleable.ItemView_i_switch_btn_show, false);

            rightText = typedArray.getString(R.styleable.ItemView_i_right_txt);
            rightTextShow = typedArray.getBoolean(R.styleable.ItemView_i_right_txt_show, false);

            rightIvRes = typedArray.getResourceId(R.styleable.ItemView_i_right_image, 0);
            rightIvShow = typedArray.getBoolean(R.styleable.ItemView_i_right_image_show, true);
            rightImgRes = typedArray.getResourceId(R.styleable.ItemView_i_right_image_two, 0);
            rightImgShow = typedArray.getBoolean(R.styleable.ItemView_i_right_image_two_show, false);

            topLineShow = typedArray.getBoolean(R.styleable.ItemView_i_top_line, false);
            bottomLineShow = typedArray.getBoolean(R.styleable.ItemView_i_bottom_line, false);

            typedArray.recycle();
        }
    }

    public ItemViewForSetting(Context context) {
        super(context);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        view = View.inflate(getContext(), R.layout.layout_item_for_setting, null);
        addView(view);

        tvLeft = view.findViewById(R.id.tv_left_text);
        if (leftTextSize > 0) {
            tvLeft.setTextSize(TypedValue.COMPLEX_UNIT_PX, leftTextSize);
        }
        tvLeft.setText(leftText);
        tvLeft.setVisibility(leftTextShow ? View.VISIBLE : View.INVISIBLE);

        switchButton = view.findViewById(R.id.lsb);
        switchButton.setVisibility(switchBtnShow ? View.VISIBLE : View.GONE);
        switchButton.setChecked(switchBtnChecked);

        tvRight = view.findViewById(R.id.tv_right_text);
        tvRight.setText(rightText);
        tvRight.setVisibility(rightTextShow ? View.VISIBLE : View.GONE);

        ivRightPic = view.findViewById(R.id.iv_right_pic);
        ivRightPic.setImageResource(rightIvRes);
        ivRightPic.setVisibility(rightIvShow ? View.VISIBLE : View.GONE);

        ivRightPicTwo = view.findViewById(R.id.iv_right_pic_2);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(rightImgRes);
        drawable.setStroke(2, Color.parseColor("#000000"));
        ivRightPicTwo.setBackground(drawable);
        ivRightPicTwo.setVisibility(rightImgShow ? View.VISIBLE : View.GONE);

        findViewById(R.id.view_top_line).setVisibility(topLineShow ? View.VISIBLE : View.INVISIBLE);
        findViewById(R.id.view_bottom_line).setVisibility(bottomLineShow ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        if (mClickListener != null) {
            mClickListener.onClick(this);
        }
    }


    private OnClickListener mClickListener;

    @Override
    public void setOnClickListener(OnClickListener l) {
        view.setOnClickListener(this);
        this.mClickListener = l;
    }

    private OnCheckedChangeListener mCheckedChangeListener;

    public void setOnCheckedChangeListener(OnCheckedChangeListener l) {
        this.mCheckedChangeListener = l;
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                switchButton.setChecked(isChecked);
                mCheckedChangeListener.onCheckedChanged(ItemViewForSetting.this, isChecked);
            }
        });
    }

    public boolean isSwitchChecked() {
        return switchButton.isChecked();
    }

    public void setChecked(boolean isChecked) {
        switchBtnChecked = isChecked;
        if(switchButton!=null) {
            switchButton.setChecked(isChecked);
        }
    }

    public boolean isChecked() {
        return switchButton.isChecked();
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(ItemViewForSetting v, boolean isChecked);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        switchButton.setEnabled(enabled);
    }

    public void setLeftText(int strRes) {
        tvLeft.setText(strRes);
    }

    public void setLeftText(String strRes) {
        leftTextShow = true;
        leftText = strRes;
        if(tvLeft!=null) {
            tvLeft.setText(strRes);
        }
    }

    public void setRightText(int strRes) {
        tvRight.setText(strRes);
    }

    public void setRightText(String strRes) {
        tvRight.setText(strRes);
    }

    public void setRightGravity(int gravity){
        tvRight.setGravity(gravity);
    }

    public void setRightPicRes(int res) {
        ivRightPic.setImageResource(res);
    }

    public void setRightImage(String res) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(Color.parseColor(res));
        drawable.setStroke(2, Color.parseColor("#000000"));
        ivRightPicTwo.setBackground(drawable);
    }

    public boolean isRightPicSelected() {
        return ivRightPic.isSelected();
    }

    public void setRightPicSelected(boolean b) {
        ivRightPic.setSelected(b);
    }

    public void setRightIvShow(boolean b) {
        rightIvShow = b;
        if (ivRightPic!=null) {
            ivRightPic.setVisibility(b ? VISIBLE : GONE);
        }
    }

    public void setSwitchBtnShow(boolean isVisible){
        switchBtnShow = isVisible;
        if(switchButton!=null) {
            switchButton.setVisibility(isVisible ? VISIBLE : GONE);
        }
    }
}
