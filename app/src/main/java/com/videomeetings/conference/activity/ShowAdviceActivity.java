package com.videomeetings.conference.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.videomeetings.conference.AdHelper.AdUtils;
import com.videomeetings.conference.R;
import com.videomeetings.conference.base.BaseActivity;
import com.videomeetings.conference.utils.Constants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class ShowAdviceActivity extends BaseActivity {

    @BindView(R.id.mTxtTitle)
    TextView mTxtTitle;
    @BindView(R.id.mTxtDesc)
    TextView mTxtDesc;
    int mPos = 0;
    ArrayList<String> mAdviceList = new ArrayList<>();
    ArrayList<String> mTitleList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_show_advice;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        AdUtils.showNativeAd(ShowAdviceActivity.this, Constants.adsJsonPOJO.getParameters().getNative_id().getDefaultValue().getValue(), findViewById(R.id.native_ads), true);

        mAdviceList.add(getResources().getString(R.string.advice_1));
        mAdviceList.add(getResources().getString(R.string.advice_2));
        mAdviceList.add(getResources().getString(R.string.advice_3));
        mAdviceList.add(getResources().getString(R.string.advice_4));
        mAdviceList.add(getResources().getString(R.string.advice_5));
        mAdviceList.add(getResources().getString(R.string.advice_6));
        mAdviceList.add(getResources().getString(R.string.advice_7));
        mAdviceList.add(getResources().getString(R.string.advice_8));

        mTitleList.add(getResources().getString(R.string.title_1));
        mTitleList.add(getResources().getString(R.string.title_2));
        mTitleList.add(getResources().getString(R.string.title_3));
        mTitleList.add(getResources().getString(R.string.title_4));
        mTitleList.add(getResources().getString(R.string.title_5));
        mTitleList.add(getResources().getString(R.string.title_6));
        mTitleList.add(getResources().getString(R.string.title_7));
        mTitleList.add(getResources().getString(R.string.title_8));

        mPos = getIntent().getIntExtra("POS", 0);

        mTxtTitle.setText("" + mTitleList.get(mPos));
        mTxtDesc.setText("" + mAdviceList.get(mPos));

    }


    @OnClick(R.id.mIVBack)
    public void onViewClicked() {
        finish();
    }
}