package com.gocam.goscamdemopro.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gocam.goscamdemopro.R;


public class LoadingDialog {

    private static Dialog mLoadingDialog;

    public static Dialog showLoading(Activity context, String msg, boolean cancelable) {
        if(mLoadingDialog!=null){disDialog();}

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        TextView loadingText = (TextView) view.findViewById(R.id.id_tv_loading_dialog_text);
        loadingText.setText(msg);

        mLoadingDialog = new Dialog(context, R.style.CustomProgressDialog);
        mLoadingDialog.setCancelable(cancelable);
        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        if (!context.isFinishing() && !context.isDestroyed())
            mLoadingDialog.show();
        return mLoadingDialog;
    }

    public static Dialog showLoading(Activity context) {
        if(mLoadingDialog!=null){disDialog();}

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        TextView loadingText = (TextView) view.findViewById(R.id.id_tv_loading_dialog_text);
        loadingText.setText(R.string.play_load_ing);

        mLoadingDialog = new Dialog(context, R.style.CustomProgressDialog);
        mLoadingDialog.setCancelable(true);
        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        if (!context.isFinishing() && !context.isDestroyed())
            mLoadingDialog.show();
        return mLoadingDialog;
    }

    /**
     * 关闭加载对话框
     */
    public static void disDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.cancel();
            mLoadingDialog.dismiss();
            mLoadingDialog = null;

        }
    }

    public static void showLoadingNotCancel(Activity context){
        if(mLoadingDialog!=null&&mLoadingDialog.isShowing()){

        }else{
            showLoading(context);
        }
    };
}