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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.videomeetings.conference.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("ValidFragment")
public class WarnAlertDialog extends DialogFragment {
    AlertDialog alertDialog;
    OnClickListener onClickListener;

    public WarnAlertDialog(OnClickListener listener) {
        onClickListener = listener;
    }

    public interface OnClickListener {
        void OnClick();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = ((Activity) getActivity()).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_warn_alert, null);
        dialogBuilder.setView(dialogView);
        if (alertDialog == null) {
            alertDialog = dialogBuilder.create();
        }
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        alertDialog.setCancelable(false);
        ButterKnife.bind(this, dialogView);

        return alertDialog;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        alertDialog = null;
    }

    @OnClick({R.id.mTxtOk, R.id.mIVClose})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.mIVClose:
                alertDialog.dismiss();
                break;
            case R.id.mTxtOk:
                onClickListener.OnClick();
                alertDialog.dismiss();
                break;
        }

    }
}
