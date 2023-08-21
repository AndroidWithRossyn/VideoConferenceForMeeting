package com.videomeetings.conference.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.videomeetings.conference.AdHelper.AdUtils;
import com.videomeetings.conference.R;
import com.videomeetings.conference.adapter.AnimSelectionAdapter;
import com.videomeetings.conference.base.BaseActivity;
import com.videomeetings.conference.base.MyStaggeredGridLayoutManager;
import com.videomeetings.conference.utils.Constants;
import com.videomeetings.conference.utils.Global;
import com.videomeetings.conference.utils.PermitConstant;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.videomeetings.conference.utils.Global.getContentMediaUri;
import static com.videomeetings.conference.utils.Global.getPath;
import static com.videomeetings.conference.utils.Global.isLatestVersion;
import static com.videomeetings.conference.utils.Global.showToast;

public class AnimSelectionActivity extends BaseActivity implements AnimSelectionAdapter.OnClickListener {

    @BindView(R.id.mProfilePic)
    CircleImageView mProfilePic;
    @BindView(R.id.mRecylerView)
    RecyclerView mRecylerView;
    AnimSelectionAdapter animSelectionAdapter;
    String[] images = new String[0];
    ArrayList<String> listImages;

    public static AnimSelectionActivity animSelectionActivity;

    public static AnimSelectionActivity getInstance() {
        return animSelectionActivity;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_anim_selection;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        if (!prefManager.isFirstTimeDetail()) {
            goToNextActivity();
        }
        animSelectionActivity = this;

        AdUtils.showNativeAd(animSelectionActivity, Constants.adsJsonPOJO.getParameters().getNative_id().getDefaultValue().getValue(), findViewById(R.id.native_ads), false);

        getAnimData();

    }

    private void goToNextActivity() {
        startActivity(new Intent(AnimSelectionActivity.this, HomeActivity.class));
        finish();
    }

    private void getAnimData() {
        try {
            images = getAssets().list("Anims");
            listImages = new ArrayList<String>(Arrays.asList(images));
            animSelectionAdapter = new AnimSelectionAdapter(AnimSelectionActivity.this, listImages, this);
            mRecylerView.setLayoutManager(new MyStaggeredGridLayoutManager(AnimSelectionActivity.this, 3));
            mRecylerView.setAdapter(animSelectionAdapter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.mTxtChoosePic, R.id.mTxtNext})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.mTxtChoosePic:
                mCheckPermission();
                break;
            case R.id.mTxtNext:
                startActivity(new Intent(this, DetailActivity.class));
                break;
        }
    }

    @Override
    public void onclickAnim(String string) {
        InputStream inputstream = null;
        try {
            Global.ProfilePath = "Anims/" + string;
            inputstream = getAssets().open("Anims/" + string);
            Drawable drawable = Drawable.createFromStream(inputstream, null);
            mProfilePic.setImageBitmap(null);
            mProfilePic.setImageDrawable(drawable);
        } catch (IOException e) {
            e.printStackTrace();
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

        if (isPermissionsGranted(AnimSelectionActivity.this, string)) {
            OpenImageChooser();
        } else {
            askCompactPermissions(string, new PermissionResult() {
                @Override
                public void permissionGranted() {
                    OpenImageChooser();
                }

                @Override
                public void permissionDenied() {
                    showToast(AnimSelectionActivity.this, "Permission Denied..!");
                    finish();
                }

                @Override
                public void permissionForeverDenied() {
                    showToast(AnimSelectionActivity.this, "Permission Forever Denied..!");
                    finish();
                }
            });
        }
    }

    public void OpenImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, getContentMediaUri());
        intent.setType("image/*");
        startActivityForResult(intent, 22);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 22) {
                String actualFilepath = getPath(this, data.getData());
                mProfilePic.setImageDrawable(null);
                Glide.with(AnimSelectionActivity.this).load(actualFilepath).into(mProfilePic);
                Global.ProfilePath = actualFilepath;
                if (animSelectionAdapter != null) {
                    animSelectionAdapter.clearSelection();
                }
            } else if (requestCode == 3) {
                Toast.makeText(this, "Trim video success", Toast.LENGTH_SHORT).show();
            }
        }
    }
}