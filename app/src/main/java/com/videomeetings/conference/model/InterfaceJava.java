package com.videomeetings.conference.model;

import android.webkit.JavascriptInterface;

import com.videomeetings.conference.activity.CallingVideoActivity;

public class InterfaceJava {

    CallingVideoActivity callActivity;

    public InterfaceJava(CallingVideoActivity callActivity) {
        this.callActivity = callActivity;
    }

    @JavascriptInterface
    public void onPeerConnected(){
        callActivity.onPeerConnected();
    }

}
