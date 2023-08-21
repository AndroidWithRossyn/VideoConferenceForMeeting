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
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.videomeetings.conference.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.videomeetings.conference.utils.Global.isEmptyStr;

@SuppressLint("ValidFragment")
public class JoinCallAlertDialog extends DialogFragment {
    AlertDialog alertDialog;
    EditText editText;
    OnClickListener onClickListener;

    public JoinCallAlertDialog(OnClickListener listener) {
        onClickListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = ((Activity) getActivity()).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_join_call, null);
        dialogBuilder.setView(dialogView);
        if (alertDialog == null) {
            alertDialog = dialogBuilder.create();
        }
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        alertDialog.setCancelable(false);
        ButterKnife.bind(this, dialogView);
        editText = dialogView.findViewById(R.id.codeBox);

        return alertDialog;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        alertDialog = null;
    }

    @OnClick({ R.id.mTxtJoin, R.id.mIVClose})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.mIVClose:
                alertDialog.dismiss();
                break;
            case R.id.mTxtJoin:
                mGetJoinString();
                break;
        }

    }

    private void mGetJoinString() {
        if (isEmptyStr(editText.getText().toString())) {
            Toast.makeText(getContext(), "Enter Code for Joining!", Toast.LENGTH_SHORT).show();
        } else {
            onClickListener.OnJoin(editText.getText().toString());
            alertDialog.dismiss();
        }
    }



    public interface OnClickListener {
        void OnJoin(String str);
    }
}
