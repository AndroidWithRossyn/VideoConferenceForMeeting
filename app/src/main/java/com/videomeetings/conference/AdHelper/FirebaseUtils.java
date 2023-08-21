package com.videomeetings.conference.AdHelper;

import android.content.Context;

import androidx.annotation.NonNull;

import com.videomeetings.conference.R;
import com.videomeetings.conference.utils.Global;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
//import com.news.crypto.bitcoinnews.Interfaces.AppInterfaces;
//import com.news.crypto.bitcoinnews.R;
//import com.news.crypto.bitcoinnews.Utils.Global;

public class FirebaseUtils {
    static String jsonData = "";

    public static void initiateAndStoreFirebaseRemoteConfig(Context context, AppInterfaces.AdDataInterface adDataInterface) {
        FirebaseApp.initializeApp(context);
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(1).build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);

        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {

            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if (task.isSuccessful()) {
                    jsonData = mFirebaseRemoteConfig.getAll().get("mData").asString();
                    Global.printLog("==mdata==",jsonData);
                    adDataInterface.getAdData(Global.getAdsData(jsonData));
                }
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Global.printLog("ad failed", e.getLocalizedMessage());
            }
        });
    }
}
