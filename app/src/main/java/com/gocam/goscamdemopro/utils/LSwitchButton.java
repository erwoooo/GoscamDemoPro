package com.gocam.goscamdemopro.utils;

import android.content.Context;
import android.util.AttributeSet;

import com.suke.widget.SwitchButton;

import java.lang.reflect.Field;

public class LSwitchButton extends SwitchButton {
    public LSwitchButton(Context context) {
        super(context);
    }

    public LSwitchButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LSwitchButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LSwitchButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    OnCheckedChangeListener  lListenr;
    @Override
    public void setOnCheckedChangeListener(OnCheckedChangeListener l) {
        lListenr = l;
        super.setOnCheckedChangeListener(l);
    }

    @Override
    public void setChecked(boolean checked) {
        try {
            if (checked != isChecked()) {
                super.setOnCheckedChangeListener(null);
                Field field = SwitchButton.class.getDeclaredField("isEventBroadcast");
                field.setAccessible(true);
                field.set(this, false);

                setEnableEffect(false);
                super.setChecked(checked);

                setEnableEffect(true);
                super.setOnCheckedChangeListener(lListenr);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
