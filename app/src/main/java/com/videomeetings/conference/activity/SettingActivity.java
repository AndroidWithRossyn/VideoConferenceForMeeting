package com.videomeetings.conference.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;

import com.videomeetings.conference.AdHelper.AdUtils;
import com.videomeetings.conference.R;
import com.videomeetings.conference.base.BaseActivity;
import com.videomeetings.conference.switchbutton.FSwitchButton;
import com.videomeetings.conference.switchbutton.SwitchButton;
import com.videomeetings.conference.utils.Constants;
import com.videomeetings.conference.utils.Global;

import butterknife.BindView;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {

    @BindView(R.id.mTxtNoti)
    TextView mTxtNoti;
    @BindView(R.id.mTxtdnd)
    TextView mTxtdnd;
    @BindView(R.id.mFBNoti)
    FSwitchButton mFBNoti;
    @BindView(R.id.mFBSound)
    FSwitchButton mFBSound;
    @BindView(R.id.mFBVibration)
    FSwitchButton mFBVibration;
    @BindView(R.id.mFBNDnd)
    FSwitchButton mFBNDnd;
    NotificationManager notificationManager;
    boolean mSound = true, mNOTIFY = true, mVIBRATE = true, mDND = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        AdUtils.showNativeAd(SettingActivity.this, Constants.adsJsonPOJO.getParameters().getNative_id().getDefaultValue().getValue(), findViewById(R.id.native_ads), false);

        mTxtNoti.getPaint().setShader(Global.getShader(mTxtNoti));
        mTxtdnd.getPaint().setShader(Global.getShader(mTxtdnd));

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNOTIFY = prefManager.getBoolean(Global.ISNOTIFY);
        mSound = prefManager.getBoolean(Global.ISOUND);
        mVIBRATE = prefManager.getBoolean(Global.ISVIBRATE);
        mDND = prefManager.getBoolean(Global.DND);

        if (mNOTIFY) {
            mFBNoti.setChecked(true, true, false);
        } else {
            mFBNoti.setChecked(false, true, true);
        }
        if (mSound) {
            mFBSound.setChecked(true, true, false);
        } else {
            mFBSound.setChecked(false, true, true);
        }
        if (mVIBRATE) {
            mFBVibration.setChecked(true, true, false);
        } else {
            mFBVibration.setChecked(false, true, true);
        }

        if (prefManager.getBoolean(Global.DND)) {
            mFBNDnd.setChecked(true, true, false);
        } else {
            mFBNDnd.setChecked(false, true, true);
        }
        mFBNoti.setOnCheckedChangeCallback(new SwitchButton.OnCheckedChangeCallback() {
            @Override
            public void onCheckedChanged(boolean checked, SwitchButton switchButton) {
                mNOTIFY = checked;
                prefManager.setBoolean(Global.ISNOTIFY, checked);
            }
        });
        mFBSound.setOnCheckedChangeCallback(new SwitchButton.OnCheckedChangeCallback() {
            @Override
            public void onCheckedChanged(boolean checked, SwitchButton switchButton) {
                mSound = checked;
                prefManager.setBoolean(Global.ISOUND, checked);
            }
        });

        mFBVibration.setOnCheckedChangeCallback(new SwitchButton.OnCheckedChangeCallback() {
            @Override
            public void onCheckedChanged(boolean checked, SwitchButton switchButton) {
                mVIBRATE = checked;
                prefManager.setBoolean(Global.ISVIBRATE, checked);
            }
        });
        mFBNDnd.setOnCheckedChangeCallback(new SwitchButton.OnCheckedChangeCallback() {
            @Override
            public void onCheckedChanged(boolean checked, SwitchButton switchButton) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!notificationManager.isNotificationPolicyAccessGranted()) {
                        Intent intent = new
                                Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                        startActivity(intent);
                        return;
                    }
                }
                ToggleDoNotDisturb(notificationManager, checked);
            }
        });
    }

    private void ToggleDoNotDisturb(NotificationManager notificationManager, boolean checked) {
        prefManager.setBoolean(Global.DND, checked);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checked) {
                mFBSound.setChecked(false, true, false);
                mFBSound.setClickable(false);
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
            } else {
                mFBSound.setChecked(mSound, true, false);
                mFBSound.setClickable(true);
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
            }
        }
    }

    @OnClick(R.id.mIVBack)
    public void onViewClicked() {
        finish();
    }

}