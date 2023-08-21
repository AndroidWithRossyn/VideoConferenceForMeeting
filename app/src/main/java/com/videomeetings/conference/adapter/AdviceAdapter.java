package com.videomeetings.conference.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.videomeetings.conference.AdHelper.AdUtils;
import com.videomeetings.conference.R;
import com.videomeetings.conference.utils.Constants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.videomeetings.conference.utils.Global.isEmptyStr;

public class AdviceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    OnClickListener onClickListener;
    RecyclerView mRecyclerView;
    ArrayList<String> mtxtData = new ArrayList<String>();
    ArrayList<Drawable> mimgData = new ArrayList<Drawable>();
    ArrayList<String> mColor = new ArrayList<String>() {
        {
            add(null);
            add("#e3f5e9");
            add("#fceff8");
            add("#edf5f8");
            add("#e5ecff");
            add(null);
            add("#fdeff3");
            add("#eef2fd");
            add("#f7f2ff");
            add("#f9f2ed");
        }
    };
    int mPos = 0;

    public AdviceAdapter(Context adviceActivity, ArrayList<String> mTxtData, ArrayList<Drawable> mImgData,RecyclerView recyclerView, OnClickListener listener) {
        context = adviceActivity;
        mtxtData.addAll(mTxtData);
        mimgData.addAll(mImgData);
        onClickListener = listener;
        mRecyclerView=recyclerView;
    }

    public void clearSelection() {
        mPos = -1;
        notifyDataSetChanged();
    }

    public int getPos() {
        return mPos;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.mIVBg)
        ImageView mIVBg;
        @BindView(R.id.mIvIcon)
        ImageView mIvIcon;
        @BindView(R.id.mTxtInfo)
        TextView mTxtInfo;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
    public class MyAdsHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.native_ads)
        LinearLayout native_ads;

        public MyAdsHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == 0) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_ads, parent, false);
            return new MyAdsHolder(itemView);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_advice, parent, false);
            return new MyViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder myholder, final int position) {
        if (mtxtData.get(position) == null) {

            MyAdsHolder holder=(MyAdsHolder)myholder;
            AdUtils.showNativeAd((Activity) context, Constants.adsJsonPOJO.getParameters().getNative_id().getDefaultValue().getValue(),holder.native_ads, true);
        } else {

            MyViewHolder holder=(MyViewHolder)myholder;
            String path = mtxtData.get(position);
            String mColors = mColor.get(position);
            Drawable image = mimgData.get(position);

            holder.mIvIcon.setImageDrawable(image);
            holder.mIVBg.setImageTintList(ColorStateList.valueOf(Color.parseColor(mColors)));
            holder.mTxtInfo.setText("" + path);

            if (!isEmptyStr(path)) {

                holder.mIVBg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPos = position;
                        notifyDataSetChanged();
                        onClickListener.onclickAnim(position);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mtxtData.size();
    }

    public interface OnClickListener {
        void onclickAnim(int string);
    }

    @Override
    public int getItemViewType(int position) {
        if (mtxtData.get(position) == null) {
            return 0;
        } else {
            return 1;
        }
    }
}
