package com.videomeetings.conference.activity;

import static com.videomeetings.conference.utils.Global.getBitmapData;
import static com.videomeetings.conference.utils.Global.showToast;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.videomeetings.conference.AdHelper.AdUtils;
import com.videomeetings.conference.AdHelper.AppInterfaces;
import com.videomeetings.conference.BuildConfig;
import com.videomeetings.conference.R;
import com.videomeetings.conference.base.BaseActivity;
import com.videomeetings.conference.dialogs.CoinsAlertDialog;
import com.videomeetings.conference.model.User;
import com.videomeetings.conference.utils.Constants;
import com.videomeetings.conference.utils.Global;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends BaseActivity implements AppInterfaces.InterStitialADInterface {

    @BindView(R.id.mDrawerlayout)
    DrawerLayout mDrawerlayout;
    @BindView(R.id.mNavigationView)
    NavigationView mNavigationView;
    @BindView(R.id.mIVMenu)
    CircleImageView mIVMenu;
    private ActionBarDrawerToggle drawerToggle;
    boolean ISDone = false;
    boolean ISOpen = false;
    int boycount = 0;
    int girlcount = 0;
    String isVideo = "IsVideo", isAudio = "IsAudio", isAdvice = "ISAdvice", isProfile = "IsProfile", isSetting = "IsSetting", mIvConference = "mIvConference";
    String mTemp = null;
    FirebaseDatabase database;
    User user;
    public static HomeActivity homeActivity;

    public static HomeActivity getInstance() {
        return homeActivity;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        homeActivity = this;
//        Global.displayProgress(HomeActivity.this);
        drawerToggle = new ActionBarDrawerToggle(this, mDrawerlayout, R.string.draweropen, R.string.drawerclose);

        mDrawerlayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        mCheckPermission();

        AdUtils.showNativeAd(HomeActivity.this, Constants.adsJsonPOJO.getParameters().getNative_id().getDefaultValue().getValue(), findViewById(R.id.native_ads), false);

        FirebaseDatabase.getInstance().getReference().child("Profiles").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        int total = mGetUsersCount((Map<String, Object>) dataSnapshot.getValue());//collectPhoneNumbers((Map<String, Object>) dataSnapshot.getValue());
                        Global.mTotalUsers = total;
                        Global.mTotalBoys = boycount;
                        Global.mTotalGirls = girlcount;
                        ISDone = true;
                        if (ISOpen) {
                            ISOpen = false;
                            Global.dismissProgress();
                            if (Global.mCallId == 1) {
                                startActivity(new Intent(HomeActivity.this, ShowVideoCallActivity.class));
                            } else {
                                startActivity(new Intent(HomeActivity.this, ShowAudioCallActivity.class));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        View nav_header = mNavigationView.getHeaderView(0);
        LinearLayout mRlHeader = nav_header.findViewById(R.id.mRlHeader);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        mRlHeader.setLayoutParams(layoutParams);
        database = FirebaseDatabase.getInstance();
        database.getReference().child("Profiles")
                .child(prefManager.getUniqId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        Global.dismissProgress();
                        user = snapshot.getValue(User.class);
                        Bitmap bitmap = getBitmapData(user.getmProfilePath());

                        mIVMenu.setImageBitmap(bitmap);
                        ((CircleImageView) nav_header.findViewById(R.id.mProfilePic)).setImageBitmap(bitmap);
                        ((TextView) nav_header.findViewById(R.id.mTxtName)).setText(user.getmUserName());
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    private int mGetUsersCount(Map<String, Object> users) {
        int count = 0;
        boycount = 0;
        girlcount = 0;
        if (users != null) {
            for (Map.Entry<String, Object> entry : users.entrySet()) {
                Map singleUserDesk = (Map) entry.getValue();
                String gender = (String) singleUserDesk.get("mGender");
                if (gender.equalsIgnoreCase("M")) {
                    boycount++;
                } else {
                    girlcount++;
                }
                count++;
            }
            return count;
        }
        return count;

    }

    private void mGetBoysUsersData() {
        FirebaseDatabase.getInstance().getReference().child("Profiles").orderByChild("mGender")
                .equalTo("M").addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //Get map of users in datasnapshot
                                int total = mGetUsersCount((Map<String, Object>) dataSnapshot.getValue());//collectPhoneNumbers((Map<String, Object>) dataSnapshot.getValue());
                                Global.mTotalBoys = total;
                                mGetGirlsUsersData();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                //handle databaseError
                            }
                        });
    }

    private void mGetGirlsUsersData() {
        FirebaseDatabase.getInstance().getReference().child("Profiles").orderByChild("mGender")
                .equalTo("F").addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //Get map of users in datasnapshot
                                int total = mGetUsersCount((Map<String, Object>) dataSnapshot.getValue());//collectPhoneNumbers((Map<String, Object>) dataSnapshot.getValue());
                                Global.mTotalGirls = total;
                                ISDone = true;
                                if (ISOpen) {
                                    ISOpen = false;
                                    Global.dismissProgress();
                                    if (Global.mCallId == 1) {
                                        startActivity(new Intent(HomeActivity.this, ShowVideoCallActivity.class));
                                    } else {
                                        startActivity(new Intent(HomeActivity.this, ShowAudioCallActivity.class));
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                //handle databaseError
                            }
                        });
    }


    private void mCheckPermission() {
        String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};

        if (!isPermissionsGranted(HomeActivity.this, permissions)) {
            askCompactPermissions(permissions, new PermissionResult() {
                @Override
                public void permissionGranted() {
                }

                @Override
                public void permissionDenied() {
                    showToast(HomeActivity.this, "Permission Denied..!");
                    finish();
                }

                @Override
                public void permissionForeverDenied() {
                    showToast(HomeActivity.this, "Permission Forever Denied..!");
                    finish();
                }
            });
        }
    }

    @OnClick({R.id.mIVMenu, R.id.mIvVideoCall, R.id.mIvAudioCall, R.id.mIvCoins, R.id.mIvAdvice})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.mIVMenu:
                mDrawerlayout.openDrawer(GravityCompat.START);
                break;
            case R.id.mIvVideoCall:
                Global.mCallId = 1;
                if (ISDone) {
                    mTemp = isVideo;
                    AdUtils.showInterstitialAd(HomeActivity.this, this);
                } else {
                    ISOpen = true;
                    Global.displayProgress(HomeActivity.this);
                }
                break;
            case R.id.mIvAudioCall:
                Global.mCallId = 2;
                if (ISDone) {
                    mTemp = isAudio;
                    AdUtils.showInterstitialAd(HomeActivity.this, this);
                } else {
                    ISOpen = true;
                    Global.displayProgress(HomeActivity.this);
                }
                break;
            case R.id.mIvCoins:
                mTemp = mIvConference;
                AdUtils.showInterstitialAd(HomeActivity.this, this);
                break;
            case R.id.mIvAdvice:
                mTemp = isAdvice;
                AdUtils.showInterstitialAd(HomeActivity.this, this);
                break;

        }
    }

    private void mShowCoinDialog() {
        CoinsAlertDialog coinsAlertDialog = new CoinsAlertDialog();
        coinsAlertDialog.show(getSupportFragmentManager(), "");
    }

    private void mShowProDialog() {
        String str = getResources().getString(R.string.proTxt);
        CoinsAlertDialog coinsAlertDialog = new CoinsAlertDialog(str);
        coinsAlertDialog.show(getSupportFragmentManager(), "");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerlayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerlayout.closeDrawer((GravityCompat.START));
        } else {
            finish();
        }

    }


    public void mOnNavigationClick(View view) {
        switch (view.getId()) {
            case R.id.mIvNavBack:
                break;
            case R.id.mTxtProfile:
                mTemp = isProfile;
                AdUtils.showInterstitialAd(HomeActivity.this, this);
                break;
            case R.id.mTxtSetting:
                mTemp = isSetting;
                AdUtils.showInterstitialAd(HomeActivity.this, this);
                break;
            case R.id.mTxtCoin:
                mShowCoinDialog();
                break;
            case R.id.mTxtPro:
                mShowProDialog();
                break;
            case R.id.mTxtShare:
                mShareApp();
                break;
            case R.id.mTxtMore:
                break;
            case R.id.mTxtPrivacy:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://clickmediainc.blogspot.com/2023/03/privacy-policy.html"));
                startActivity(browserIntent);
                break;
            case R.id.mTxtTC:
                Intent tcIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://clickmediainc.blogspot.com/2023/03/terms-conditions.html"));
                startActivity(tcIntent);
                break;
            case R.id.mTxtRate:
                mRateApp();
                break;
            case R.id.mTxtLogout:
                mLogoutApp();
                break;

        }
        mDrawerlayout.closeDrawer(GravityCompat.START);
    }

    private void mLogoutApp() {
        prefManager.setFirstTimeDetail(true);
        startActivity(new Intent(HomeActivity.this, AnimSelectionActivity.class));
        finishAffinity();
    }

    private void mRateApp() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("market://details?id=");
            sb.append(getPackageName());
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse(sb.toString())));
        } catch (ActivityNotFoundException e) {
            Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details");
            startActivity(rateIntent);
        }
    }

    private Intent rateIntentForUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21) {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        } else {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }

    private void mShareApp() {
        try {
         /*   Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            String shareMessage = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));*/

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name) + " App Invitation");
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Experience seamless and crystal-clear video conference calls by downloading " + getString(R.string.app_name) +
                    " app today! Join meetings from anywhere, at any time, with just a few taps on your device. " +
                    "\n\nDon't miss out on the opportunity to connect with colleagues and clients like never before. \n\n" +
                    "\nJoin now with the link: \nhttps://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void adLoadState(boolean isLoaded) {
        if (mTemp.equalsIgnoreCase(isVideo)) {
            startActivity(new Intent(HomeActivity.this, ShowVideoCallActivity.class));
        } else if (mTemp.equalsIgnoreCase(isAudio)) {
            startActivity(new Intent(HomeActivity.this, ShowAudioCallActivity.class));
        } else if (mTemp.equalsIgnoreCase(isAdvice)) {
            startActivity(new Intent(HomeActivity.this, AdviceActivity.class));
        } else if (mTemp.equalsIgnoreCase(isProfile)) {
            startActivity(new Intent(HomeActivity.this, ProfileEditActivity.class));
        } else if (mTemp.equalsIgnoreCase(isSetting)) {
            startActivity(new Intent(HomeActivity.this, SettingActivity.class));
        } else if (mTemp.equalsIgnoreCase(mIvConference)) {
            startActivity(new Intent(HomeActivity.this, VideoConferenceActivity.class));
        }

    }
}