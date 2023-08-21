package com.videomeetings.conference.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.videomeetings.conference.AdHelper.AdUtils;
import com.videomeetings.conference.R;
import com.videomeetings.conference.base.BaseActivity;
import com.videomeetings.conference.dialogs.CoinsAlertDialog;
import com.videomeetings.conference.utils.Constants;
import com.videomeetings.conference.utils.Global;

import butterknife.BindView;
import butterknife.OnClick;

public class PrivacyPolicyAcitivty extends BaseActivity {

    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.tv_diffaccept)
    TextView tvDiffaccept;
    @BindView(R.id.tv_policy1)
    TextView tvPolicy1;
    @BindView(R.id.tv_policy2)
    TextView tvPolicy2;
    @BindView(R.id.tv_policy3)
    TextView tvPolicy3;
    @BindView(R.id.iv_close)
    TextView ivClose;
    @BindView(R.id.tvText)
    TextView tvText;
    @BindView(R.id.relbtn)
    LinearLayout relbtn;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_privacy_policy;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        if (!prefManager.gettrueBoolean(Global.ISPRIVACYFIRST)) {
            mStartAct();
            finish();
        }
        AdUtils.showNativeAd(PrivacyPolicyAcitivty.this, Constants.adsJsonPOJO.getParameters().getNative_id().getDefaultValue().getValue(), findViewById(R.id.native_ads), false);

        tvPolicy1.setText(Html.fromHtml("Following link : <a href=\"https://clickmediainc.blogspot.com/2023/03/terms-conditions.html\"> <b> Terms and conditions of use </b> </a> "));
        this.tvPolicy1.setLinkTextColor(getResources().getColor(R.color.colorAccent));
        this.tvPolicy1.setMovementMethod(LinkMovementMethod.getInstance());
        this.tvPolicy2.setText(Html.fromHtml("Following Link : <a href=\"https://clickmediainc.blogspot.com/2023/03/privacy-policy.html\"> <b> Privacy policy </b> </a> "));
        this.tvPolicy2.setLinkTextColor(getResources().getColor(R.color.colorAccent));
        this.tvPolicy2.setMovementMethod(LinkMovementMethod.getInstance());
        this.tvPolicy3.setText(Html.fromHtml("Following Link : <a href=\"https://clickmediainc.blogspot.com/2023/03/app-community-guidelines.html\"> <b> App Community Guidelines </b> </a> "));
        this.tvPolicy3.setLinkTextColor(getResources().getColor(R.color.colorAccent));
        this.tvPolicy3.setMovementMethod(LinkMovementMethod.getInstance());
        this.tvDiffaccept.setText(Html.fromHtml("By pressing the <b> Accept </b> button, I declare I have read and accepted the following condition of use:"));
        this.tvDiffaccept.setLinkTextColor(getResources().getColor(R.color.colorAccent));
        this.tvDiffaccept.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @OnClick({R.id.tv_diffaccept, R.id.tv_policy1, R.id.tv_policy2, R.id.tv_policy3, R.id.iv_close, R.id.tvText})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                finish();
                break;
            case R.id.tvText:
                mStartAct();
                break;
        }
    }

    private void mStartAct() {
        if (Global.isNetworkAvailable(PrivacyPolicyAcitivty.this)) {
            startActivity(new Intent(this, IntroActivity.class));
            finish();
        } else {
            String str = getResources().getString(R.string.nointernet);
            CoinsAlertDialog coinsAlertDialog = new CoinsAlertDialog(str);
            coinsAlertDialog.show(getSupportFragmentManager(), "");
        }
    }

}