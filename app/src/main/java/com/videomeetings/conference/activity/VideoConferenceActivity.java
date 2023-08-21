package com.videomeetings.conference.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.amplitude.BuildConfig;
import com.videomeetings.conference.AdHelper.AdUtils;
import com.videomeetings.conference.R;
import com.videomeetings.conference.base.BaseActivity;
import com.videomeetings.conference.dialogs.CreateCallAlertDialog;
import com.videomeetings.conference.dialogs.JoinCallAlertDialog;
import com.videomeetings.conference.utils.Constants;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

import butterknife.OnClick;

public class VideoConferenceActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_conference;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        AdUtils.showNativeAd(VideoConferenceActivity.this, Constants.adsJsonPOJO.getParameters().getNative_id().getDefaultValue().getValue(), findViewById(R.id.native_ads), true);
        /*try {
            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL(""))
                    .setWelcomePageEnabled(false)
                    .setFeatureFlag("invite.enabled", false)
                    .setFeatureFlag("help.enabled", false)
                    .build();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }*/
        URL serverURL = null;
        try {
            serverURL = new URL("https://meet.jit.si");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        JitsiMeetConferenceOptions defaultOptions
                = new JitsiMeetConferenceOptions.Builder()
                .setServerURL(serverURL)
                .build();

        JitsiMeet.setDefaultConferenceOptions(defaultOptions);

    }

    @OnClick({R.id.mIvCreateNew, R.id.mIvJoin, R.id.mIVBack,})
    public void onViewClicked(View view) {
        switch (view.getId()) {
          /*  case R.id.share:
                break;
            case R.id.demoBtn:
                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(VideoConferenceActivity.this, LoginActivity.class));
                break;*/
            case R.id.mIVBack:
                finish();
                break;
            case R.id.mIvCreateNew:
                mOpenCreateCall();
                break;
            case R.id.mIvJoin:
                mOpenJoinCall();
                break;
        }
    }

    private void mOpenCreateCall() {
        CreateCallAlertDialog warnAlertDialog = new CreateCallAlertDialog(new CreateCallAlertDialog.OnClickListener() {

            @Override
            public void OnJoin(String str) {
                if (str.length() > 0) {
                    /*JitsiMeetConferenceOptions options
                            = new JitsiMeetConferenceOptions.Builder()
                            .setRoom(str)
                            .setFeatureFlag("invite.enabled", false)
                            .setFeatureFlag("help.enabled", false)
                            .build();
                    JitsiMeetActivity.launch(VideoConferenceActivity.this, options);*/
                    JitsiMeetConferenceOptions options
                            = new JitsiMeetConferenceOptions.Builder()
                            .setRoom(str)
                            .setFeatureFlag("welcomepage.enabled", false)
                            .setFeatureFlag("live-streaming.enabled",false)
                            .setFeatureFlag("invite.enabled",false)
                            .setFeatureFlag("video-share.enabled", false)
                            .setFeatureFlag("overflow-menu.enabled", true)
                            .setFeatureFlag("fullscreen.enabled", true)
                            .build();
                    JitsiMeetActivity.launch(VideoConferenceActivity.this, options);
                }
            }

            @Override
            public void OnShare(String str) {
                try {
                    String name="";
                    if(HomeActivity.getInstance()!=null){
                        name=HomeActivity.getInstance().user.getmUserName();
                    }
                    /*Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                    String shareMessage = "Invite Code = " + str;
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "Choose one"));*/

                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Conference call Invitation by " + name);
                    sendIntent.putExtra(Intent.EXTRA_TEXT,
                            name + " has invited you to join the upcoming meeting by entering the virtual conference room using the following access code: " + str +
                                    "\n\n Download now " + getString(R.string.app_name) + " to experience seamless calls!\n" +
                                    "\nJoin now with the link: \nhttps://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        warnAlertDialog.show(getSupportFragmentManager(), "");
    }

    private void mOpenJoinCall() {
        JoinCallAlertDialog warnAlertDialog = new JoinCallAlertDialog(new JoinCallAlertDialog.OnClickListener() {

            @Override
            public void OnJoin(String str) {
                if (str.length() > 0) {
                    JitsiMeetConferenceOptions options
                            = new JitsiMeetConferenceOptions.Builder()
                            .setRoom(str)
                            .setFeatureFlag("invite.enabled", false)
                            .setFeatureFlag("help.enabled", false)
                            .build();
                    JitsiMeetActivity.launch(VideoConferenceActivity.this, options);
                }
            }

        });
        warnAlertDialog.show(getSupportFragmentManager(), "");
    }


   /* public void onButtonClick(View v) {
        EditText editText = findViewById(R.id.codeBox);
        String text = editText.getText().toString();
        if (text.length() > 0) {
            JitsiMeetConferenceOptions options
                    = new JitsiMeetConferenceOptions.Builder()
                    .setRoom(text)
                    .setFeatureFlag("invite.enabled", false)
                    .setFeatureFlag("help.enabled", false)
                    .build();
            JitsiMeetActivity.launch(this, options);
        }

    }*/

}