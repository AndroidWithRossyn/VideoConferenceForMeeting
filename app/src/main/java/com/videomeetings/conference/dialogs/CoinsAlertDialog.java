package com.videomeetings.conference.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.videomeetings.conference.R;
import com.videomeetings.conference.activity.SplashActivity;
import com.videomeetings.conference.utils.Global;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("ValidFragment")
public class CoinsAlertDialog extends DialogFragment {
    AlertDialog alertDialog;
    @BindView(R.id.mTxtOk)
    TextView mTxtOk;
    @BindView(R.id.mTxtIntro)
    TextView mTxtIntro;
    String mStr = null;

    public CoinsAlertDialog() {
    }

    public CoinsAlertDialog(String text) {
        mStr = text;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = ((Activity) getActivity()).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_coin_alert, null);
        dialogBuilder.setView(dialogView);
        if (alertDialog == null) {
            alertDialog = dialogBuilder.create();
        }
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        alertDialog.setCancelable(false);
        ButterKnife.bind(this, dialogView);
        if (!Global.isEmptyStr(mStr)) {
            mTxtIntro.setText(mStr);
        }
        return alertDialog;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        alertDialog = null;
    }

    @OnClick(R.id.mTxtOk)
    public void onViewClicked() {
        alertDialog.dismiss();
        if (mStr != null) {
            if (mStr.equalsIgnoreCase(getResources().getString(R.string.nointernet))) {
                SplashActivity.getInstance().finish();
            }
        }
    }
}
