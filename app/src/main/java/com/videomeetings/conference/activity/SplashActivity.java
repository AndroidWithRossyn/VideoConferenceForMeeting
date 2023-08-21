package com.videomeetings.conference.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.videomeetings.conference.AdHelper.AdUtils;
import com.videomeetings.conference.AdHelper.AdsJsonPOJO;
import com.videomeetings.conference.AdHelper.AppInterfaces;
import com.videomeetings.conference.AdHelper.AppPreferencesManger;
import com.videomeetings.conference.AdHelper.FirebaseUtils;
import com.videomeetings.conference.R;
import com.videomeetings.conference.base.BaseActivity;
import com.videomeetings.conference.dialogs.CoinsAlertDialog;
import com.videomeetings.conference.utils.Constants;
import com.videomeetings.conference.utils.Global;
import com.google.firebase.messaging.FirebaseMessaging;

import butterknife.BindView;

public class SplashActivity extends BaseActivity {
    @BindView(R.id.mIvLogo)
    ImageView mIvLogo;
    @BindView(R.id.mIvTitle)
    TextView mIvTitle;
    public static SplashActivity splashActivity;

    public static SplashActivity getInstance() {
        return splashActivity;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        splashActivity = this;

        AppPreferencesManger appPreferencesManger = new AppPreferencesManger(this);

        FirebaseMessaging.getInstance().subscribeToTopic(Constants.ADSJSON);

        Constants.adsJsonPOJO = Global.getAdsData(appPreferencesManger.getAdsModel());

        if (Constants.adsJsonPOJO != null && !Global.isEmptyStr(Constants.adsJsonPOJO.getParameters().getApp_open_ad().getDefaultValue().getValue())) {
            Constants.adsJsonPOJO = Global.getAdsData(appPreferencesManger.getAdsModel());
            Constants.hitCounter = Integer.parseInt(Constants.adsJsonPOJO.getParameters().getApp_open_ad().getDefaultValue().getHits());
            AdUtils.showAppOpenAd(splashActivity, new AppInterfaces.AppOpenADInterface() {
                @Override
                public void appOpenAdState(boolean state_load) {
//                    rltest.setVisibility(View.VISIBLE);
                    mStartAct();
                }
            });

        } else {
            FirebaseUtils.initiateAndStoreFirebaseRemoteConfig(splashActivity, new AppInterfaces.AdDataInterface() {
                @Override
                public void getAdData(AdsJsonPOJO adsJsonPOJO) {
                    //Need to call this only once per
                    appPreferencesManger.setAdsModel(adsJsonPOJO);
                    Constants.adsJsonPOJO = adsJsonPOJO;
                    Constants.hitCounter = Integer.parseInt(Constants.adsJsonPOJO.getParameters().getApp_open_ad().getDefaultValue().getHits());
                    AdUtils.showAppOpenAd(splashActivity, new AppInterfaces.AppOpenADInterface() {
                        @Override
                        public void appOpenAdState(boolean state_load) {
//                            rltest.setVisibility(View.VISIBLE);
                            mStartAct();
                        }
                    });
                }
            });

        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void mStartAct() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Global.isNetworkAvailable(SplashActivity.this)) {
                    startActivity(new Intent(SplashActivity.this, IntroActivity.class));
                    finish();
                } else {
                    String str = getResources().getString(R.string.nointernet);
                    CoinsAlertDialog coinsAlertDialog = new CoinsAlertDialog(str);
                    coinsAlertDialog.show(getSupportFragmentManager(), "");
                }
            }
        }, 1000);

    }
}