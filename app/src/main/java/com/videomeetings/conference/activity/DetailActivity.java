package com.videomeetings.conference.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.videomeetings.conference.AdHelper.AdUtils;
import com.videomeetings.conference.AdHelper.AppInterfaces;
import com.videomeetings.conference.R;
import com.videomeetings.conference.base.BaseActivity;
import com.videomeetings.conference.model.User;
import com.videomeetings.conference.utils.Constants;
import com.videomeetings.conference.utils.Global;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.OnClick;

import static com.videomeetings.conference.utils.Global.getCacheFile;
import static com.videomeetings.conference.utils.Global.getImageData;

public class DetailActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    Context mContext;
    @BindView(R.id.mETFullName)
    EditText mETFullName;
    @BindView(R.id.mETUserName)
    EditText mETUserName;
    @BindView(R.id.mETAge)
    EditText mETAge;
    @BindView(R.id.mRbtnMale)
    RadioButton mRbtnMale;
    @BindView(R.id.mRbtnFemale)
    RadioButton mRbtnFemale;
    FirebaseDatabase database;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_detail;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mContext = this;

        AdUtils.showNativeAd(DetailActivity.this, Constants.adsJsonPOJO.getParameters().getNative_id().getDefaultValue().getValue(), findViewById(R.id.native_ads), false);

        mRbtnMale.setChecked(true);
        mRbtnFemale.setOnCheckedChangeListener(this);
        mRbtnMale.setOnCheckedChangeListener(this);

        database = FirebaseDatabase.getInstance();
        mETFullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    mETFullName.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mETUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    mETUserName.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mETAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    mETAge.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @OnClick({R.id.mTxtNext})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.mTxtNext:
                mSetInfoLayout();
                break;
        }
    }

    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            // handle exception
        }

        return bitmap;
    }

    private void mSetDatabseData() {
        String saveUriToFile = "Anims/anim1.png";
        Global.displayProgress(DetailActivity.this);
        if (AnimSelectionActivity.getInstance() != null) {
            if (AnimSelectionActivity.getInstance().animSelectionAdapter.getPos() == -1) {
                File image = new File(Global.ProfilePath);
                saveUriToFile = getImageData(image);//saveUriToFile(Global.getUri(new File(Global.ProfilePath), mContext), mContext);
            } else {
                Bitmap bitmap = getBitmapFromAsset(DetailActivity.this, Global.ProfilePath);
                saveUriToFile = getImageData(bitmap);//Global.ProfilePath;
            }
        }
        String fullname = mETFullName.getText().toString();
        String username = mETUserName.getText().toString();
        String age = mETAge.getText().toString();
        String gender = "M";
        if (mRbtnFemale.isChecked()) {
            gender = "F";
        }
        String Uid = Global.getRandomString(10) + username;
        User firebaseUser = new User(fullname, username, age, gender, saveUriToFile, 0);
        database.getReference()
                .child("Profiles")
                .child(Uid)
                .setValue(firebaseUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                Global.dismissProgress();
                if (task.isSuccessful()) {
                    AdUtils.showInterstitialAd(DetailActivity.this, new AppInterfaces.InterStitialADInterface() {
                        @Override
                        public void adLoadState(boolean isLoaded) {
                            prefManager.setFirstTimeDetail(false);
                            prefManager.setUniqId(Uid);
                            prefManager.setBoolean(Global.ISOUND, true);
                            prefManager.setBoolean(Global.ISVIBRATE, true);
                            prefManager.setBoolean(Global.ISNOTIFY, true);
                            prefManager.setBoolean(Global.DND, false);
                            startActivity(new Intent(DetailActivity.this, HomeActivity.class));
                            finishAffinity();
                        }
                    });

                } else {
                    Toast.makeText(DetailActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void mSetInfoLayout() {
        mCheckTextData();

    }

    private void mCheckTextData() {
        if (Global.isEmptyStr(mETFullName.getText().toString())) {
            mETFullName.setError("Enter Full Name");
        } else {
            mETFullName.setError(null);
        }
        if (Global.isEmptyStr(mETUserName.getText().toString())) {
            mETUserName.setError("Enter User Name");
        } else {
            mETUserName.setError(null);
        }
        if (!Global.isEmptyStr(mETFullName.getText().toString()) && !Global.isEmptyStr(mETUserName.getText().toString())) {
            mCheckAgeTextData();
        }
    }

    private void mCheckAgeTextData() {
        if (Global.isEmptyStr(mETAge.getText().toString())) {
            mETAge.setError("Enter Age");
        } else {
            mETAge.setError(null);
        }
        if (!Global.isEmptyStr(mETAge.getText().toString())) {
            mSetDatabseData();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            if (buttonView.getId() == R.id.mRbtnMale) {
                mRbtnFemale.setChecked(false);
            }
            if (buttonView.getId() == R.id.mRbtnFemale) {
                mRbtnMale.setChecked(false);
            }
        }
    }

    public String saveUriToFile(Uri uri, Context context) {
        InputStream inputStream;
        BufferedInputStream bufferedInputStream;
        BufferedOutputStream bufferedOutputStream;
        Exception e;
        BufferedInputStream bufferedInputStream2 = null;
        BufferedOutputStream bufferedOutputStream2 = null;
        BufferedInputStream bufferedInputStream3 = null;
        Context applicationContext = getApplicationContext();
        File cacheFile = getCacheFile("ProfilePic.png", context);
        BufferedOutputStream bufferedOutputStream3 = null;
        try {
            inputStream = applicationContext.getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                try {
                    bufferedInputStream3 = new BufferedInputStream(inputStream);
                } catch (Exception e2) {
                    e = e2;
                    bufferedOutputStream2 = null;
                } catch (Throwable th) {
                    th = th;
                    bufferedInputStream = null;
                    if (bufferedInputStream != null) {
                    }
                    if (bufferedOutputStream3 != null) {
                    }
                    if (inputStream != null) {
                    }
                    throw th;
                }
            } else {
                bufferedInputStream3 = null;
            }
            try {
                bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(cacheFile, false));
            } catch (Exception e3) {
                e = e3;
                bufferedOutputStream = null;
                bufferedInputStream2 = bufferedInputStream3;
                try {
                    e.printStackTrace();
                    if (bufferedInputStream2 != null) {
                    }
                    if (bufferedOutputStream != null) {
                    }
                    if (inputStream != null) {
                    }
                    cacheFile = null;
                    if (cacheFile != null) {
                    }
                } catch (Throwable th2) {
                    bufferedOutputStream3 = bufferedOutputStream;
                    bufferedInputStream = bufferedInputStream2;
                    if (bufferedInputStream != null) {
                    }
                    if (bufferedOutputStream3 != null) {
                    }
                    if (inputStream != null) {
                    }
                    throw th2;
                }
            } catch (Throwable th3) {
                bufferedInputStream = bufferedInputStream3;
                if (bufferedInputStream != null) {
                }
                if (bufferedOutputStream3 != null) {
                }
                if (inputStream != null) {
                }
                throw th3;
            }
            try {
                byte[] bArr = new byte[1024];
                if (bufferedInputStream3 != null) {
                    bufferedInputStream3.read(bArr);
                    do {
                        bufferedOutputStream.write(bArr);
                    } while (bufferedInputStream3.read(bArr) != -1);
                } else {
                    cacheFile = null;
                }
                if (bufferedInputStream3 != null) {
                    try {
                        bufferedInputStream3.close();
                    } catch (IOException e4) {
                        e4.printStackTrace();
                        cacheFile = null;
                        if (cacheFile != null) {
                        }
                    }
                }
                bufferedOutputStream.close();
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e5) {
                e = e5;
                bufferedInputStream2 = bufferedInputStream3;
                e.printStackTrace();
                if (bufferedInputStream2 != null) {
                }
                if (bufferedOutputStream != null) {
                }
                if (inputStream != null) {
                }
                cacheFile = null;
                if (cacheFile != null) {
                }
            }
        } catch (IOException e6) {
            e = e6;
            inputStream = null;
            bufferedOutputStream2 = null;
            bufferedOutputStream = bufferedOutputStream2;
            e.printStackTrace();
            if (bufferedInputStream2 != null) {
                try {
                    bufferedInputStream2.close();
                    if (bufferedOutputStream != null) {
                        bufferedOutputStream.close();
                    }
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            cacheFile = null;
            if (cacheFile != null) {
            }
        } catch (Throwable th4) {
            inputStream = null;
            bufferedInputStream = null;
            if (bufferedInputStream != null) {
                try {
                    bufferedInputStream.close();
                } catch (IOException e7) {
                    e7.printStackTrace();
                    try {
                        throw th4;
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            }
            if (bufferedOutputStream3 != null) {
                try {
                    bufferedOutputStream3.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            try {
                throw th4;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        if (cacheFile != null) {
            return cacheFile.getPath();
        }
        return null;
    }

}