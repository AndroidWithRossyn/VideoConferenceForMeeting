package com.videomeetings.conference.model;

import android.webkit.JavascriptInterface;

import com.videomeetings.conference.activity.CallingAudioActivity;

public class InterfaceAudioJava {

    CallingAudioActivity callActivity;

    public InterfaceAudioJava(CallingAudioActivity callActivity) {
        this.callActivity = callActivity;
    }

    @JavascriptInterface
    public void onPeerConnected() {
        callActivity.onPeerConnected();
    }

}
