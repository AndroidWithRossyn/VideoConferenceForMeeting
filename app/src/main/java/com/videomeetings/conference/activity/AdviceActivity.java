package com.videomeetings.conference.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.videomeetings.conference.AdHelper.AdUtils;
import com.videomeetings.conference.AdHelper.AppInterfaces;
import com.videomeetings.conference.R;
import com.videomeetings.conference.adapter.AdviceAdapter;
import com.videomeetings.conference.base.BaseActivity;
import com.videomeetings.conference.base.MyStaggeredGridLayoutManager;
import com.videomeetings.conference.utils.Constants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class AdviceActivity extends BaseActivity implements AdviceAdapter.OnClickListener {

    @BindView(R.id.mRecylerView)
    RecyclerView mRecylerView;
    ArrayList<String> mTxtData = new ArrayList<>();
    ArrayList<Drawable> mImgData = new ArrayList<>();
    AdviceAdapter adviceAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_advice;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        AdUtils.showNativeAd(AdviceActivity.this, Constants.adsJsonPOJO.getParameters().getNative_id().getDefaultValue().getValue(), findViewById(R.id.native_ads), false);

        mTxtData.add(null);
        mTxtData.add(getResources().getString(R.string.info1));
        mTxtData.add(getResources().getString(R.string.info2));
        mTxtData.add(getResources().getString(R.string.info3));
        mTxtData.add(getResources().getString(R.string.info4));
        mTxtData.add(null);
        mTxtData.add(getResources().getString(R.string.info5));
        mTxtData.add(getResources().getString(R.string.info6));
        mTxtData.add(getResources().getString(R.string.info7));
        mTxtData.add(getResources().getString(R.string.info8));

        mImgData.add(null);
        mImgData.add(ContextCompat.getDrawable(this, R.drawable.advice_bg1));
        mImgData.add(ContextCompat.getDrawable(this, R.drawable.advice_bg2));
        mImgData.add(ContextCompat.getDrawable(this, R.drawable.advice_bg3));
        mImgData.add(ContextCompat.getDrawable(this, R.drawable.advice_bg4));
        mImgData.add(null);
        mImgData.add(ContextCompat.getDrawable(this, R.drawable.advice_bg5));
        mImgData.add(ContextCompat.getDrawable(this, R.drawable.advice_bg6));
        mImgData.add(ContextCompat.getDrawable(this, R.drawable.advice_bg7));
        mImgData.add(ContextCompat.getDrawable(this, R.drawable.advice_bg8));

        adviceAdapter = new AdviceAdapter(this, mTxtData, mImgData, mRecylerView, this);
        MyStaggeredGridLayoutManager layoutManager = new MyStaggeredGridLayoutManager(this, 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 5 || position==0) {
                    return 2;
                }
                return 1;
            }
        });
        mRecylerView.setLayoutManager(layoutManager);
        mRecylerView.setAdapter(adviceAdapter);

    }

    @OnClick(R.id.mIVBack)
    public void onViewClicked() {
        finish();
    }

    @Override
    public void onclickAnim(int mPos) {
        AdUtils.showInterstitialAd(AdviceActivity.this, new AppInterfaces.InterStitialADInterface() {
            @Override
            public void adLoadState(boolean isLoaded) {
                Intent intent = new Intent(AdviceActivity.this, ShowAdviceActivity.class);
                intent.putExtra("POS", mPos);
                startActivity(intent);
            }
        });

    }
}