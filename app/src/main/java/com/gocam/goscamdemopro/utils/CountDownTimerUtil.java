package com.gocam.goscamdemopro.utils;

import android.os.CountDownTimer;

public class CountDownTimerUtil {
    public static CountDownTimer timer;

    public interface CountDownTimerListener {
        void onTick(long millisUntilFinished);

        void onFinish();
    }

    public static void startCountDownTimer(long millisInFuture, long countDownInterval, final CountDownTimerListener listener) {
        timer = new CountDownTimer(millisInFuture, countDownInterval) {

            @Override
            public void onTick(long millisUntilFinished) {
                listener.onTick(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                listener.onFinish();
                timer = null;
            }
        };
        timer.start();

    }

    public static void cancelCountDownTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
