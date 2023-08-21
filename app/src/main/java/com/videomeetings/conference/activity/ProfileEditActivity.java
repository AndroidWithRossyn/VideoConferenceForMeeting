package com.videomeetings.conference.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.videomeetings.conference.AdHelper.AdUtils;
import com.videomeetings.conference.R;
import com.videomeetings.conference.base.BaseActivity;
import com.videomeetings.conference.model.User;
import com.videomeetings.conference.utils.Constants;
import com.videomeetings.conference.utils.Global;
import com.videomeetings.conference.utils.PermitConstant;
import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.videomeetings.conference.utils.Global.getBitmapData;
import static com.videomeetings.conference.utils.Global.getCacheFile;
import static com.videomeetings.conference.utils.Global.getImageData;
import static com.videomeetings.conference.utils.Global.isLatestVersion;
import static com.videomeetings.conference.utils.Global.showToast;

public class ProfileEditActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {
    Context mContext;
    @BindView(R.id.mProfilePic)
    CircleImageView mProfilePic;
    @BindView(R.id.mETUserName)
    EditText mETUserName;
    @BindView(R.id.mETFullName)
    EditText mETFullName;
    @BindView(R.id.mETAge)
    EditText mETAge;
    @BindView(R.id.mRbtnMale)
    RadioButton mRbtnMale;
    @BindView(R.id.mRbtnFemale)
    RadioButton mRbtnFemale;
    FirebaseDatabase database;
    User user;
    String mPath = null;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_profile_edit;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mContext = this;
        Global.displayProgress(ProfileEditActivity.this);
        database = FirebaseDatabase.getInstance();
        database.getReference().child("Profiles")
                .child(prefManager.getUniqId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        AdUtils.showNativeAd(ProfileEditActivity.this, Constants.adsJsonPOJO.getParameters().getNative_id().getDefaultValue().getValue(), findViewById(R.id.native_ads), false);

                        Global.dismissProgress();
                        user = snapshot.getValue(User.class);
                        mSetDataProfile();
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
        mRbtnFemale.setOnCheckedChangeListener(this);
        mRbtnMale.setOnCheckedChangeListener(this);
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
    private void mSetDataProfile() {
        mETFullName.setText(user.getmFullName());
        mETUserName.setText(user.getmUserName());
        mETAge.setText(user.getmAge());
        if (user.getmGender().equalsIgnoreCase("F")) {
            mRbtnFemale.setChecked(true);
            mRbtnMale.setChecked(false);
        } else {
            mRbtnMale.setChecked(true);
            mRbtnFemale.setChecked(false);
        }
        Bitmap bitmap = getBitmapData(user.getmProfilePath());
        mProfilePic.setImageBitmap(bitmap);
//        Glide.with(this)
//                .load(getBitmapData(user.getmProfilePath()))
//                .placeholder(R.drawable.demo_img)
//                .into(mProfilePic);
        mPath = user.getmProfilePath();
    }

    @OnClick({R.id.mIVBack, R.id.mIvCamera, R.id.mTxtSave})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.mIVBack:
                finish();
                break;
            case R.id.mIvCamera:
                mCheckPermission();
                break;
            case R.id.mTxtSave:
                mSetDatabseData();
                break;
        }
    }

    private void mCheckPermission() {
        String[] string;

        if (isLatestVersion()) {
            string = new String[]{PermitConstant.Manifest_READ_EXTERNAL_STORAGE};
        } else {
            string = new String[]{PermitConstant.Manifest_READ_EXTERNAL_STORAGE,
                    PermitConstant.Manifest_WRITE_EXTERNAL_STORAGE};
        }

        if (isPermissionsGranted(ProfileEditActivity.this, string)) {
            mOpenGalleryPic();
        } else {
            askCompactPermissions(string, new PermissionResult() {
                @Override
                public void permissionGranted() {
                    mOpenGalleryPic();
                }

                @Override
                public void permissionDenied() {
                    showToast(ProfileEditActivity.this, "Permission Denied..!");
                    finish();
                }

                @Override
                public void permissionForeverDenied() {
                    showToast(ProfileEditActivity.this, "Permission Forever Denied..!");
                    finish();
                }
            });
        }
    }

    private void mOpenGalleryPic() {
        ImagePicker.Companion.with((Activity) this).crop().galleryOnly().galleryMimeTypes(new String[]{"image/png", "image/jpg", "image/jpeg"}).maxResultSize(1080, 1920).start(102);
    }

    private void mSetDatabseData() {
        String saveUriToFile = user.getmProfilePath();
        Global.displayProgress(ProfileEditActivity.this);
        if (!mPath.equalsIgnoreCase(user.getmProfilePath())) {
            File image = new File(mPath);

            saveUriToFile = getImageData(image);//saveUriToFile(Global.getUri(new File(mPath), mContext), mContext);
        }
        String fullname = mETFullName.getText().toString();
        String username = mETUserName.getText().toString();
        String age = mETAge.getText().toString();
        String gender = user.getmGender();
        if (mRbtnFemale.isChecked()) {
            gender = "F";
        }else if (mRbtnMale.isChecked()) {
            gender = "M";
        }
        String Uid = prefManager.getUniqId();
        User firebaseUser = new User(fullname, username, age, gender, saveUriToFile, 0);
        database.getReference()
                .child("Profiles")
                .child(Uid)
                .setValue(firebaseUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                Global.dismissProgress();
                if (task.isSuccessful()) {
                    Toast.makeText(mContext, "Profile Edit Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ProfileEditActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            Uri uri = data != null ? data.getData() : null;
            mPath = uri.getPath();
            Glide.with(this)
                    .load(mPath)
                    .placeholder(R.drawable.demo_img)
                    .into(mProfilePic);
        }
    }
}