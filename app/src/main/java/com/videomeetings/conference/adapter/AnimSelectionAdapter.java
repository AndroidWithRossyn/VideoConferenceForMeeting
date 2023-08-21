package com.videomeetings.conference.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.videomeetings.conference.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.videomeetings.conference.utils.Global.isEmptyStr;

public class AnimSelectionAdapter extends RecyclerView.Adapter<AnimSelectionAdapter.MyViewHolder> {
    Context context;
    OnClickListener onClickListener;
    ArrayList<String> mData = new ArrayList<String>();
    int mPos = 0;

    public void clearSelection() {
        mPos = -1;
        notifyDataSetChanged();
    }

    public int getPos() {
        return mPos;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout mRLSelect;
        CircleImageView mIvAnim;

        public MyViewHolder(View view) {
            super(view);
            mRLSelect = view.findViewById(R.id.mRLSelect);
            mIvAnim = view.findViewById(R.id.mIvAnim);
        }
    }

    public AnimSelectionAdapter(Context con, ArrayList<String> stringArray, OnClickListener mListener) {
        context = con;
        mData.addAll(stringArray);
        onClickListener = mListener;
    }

    @NonNull
    @Override
    public AnimSelectionAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_anim, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final AnimSelectionAdapter.MyViewHolder holder, final int position) {
        String path = mData.get(position);
        if (!isEmptyStr(path)) {

            InputStream inputstream = null;
            try {
                inputstream = context.getAssets().open("Anims/" + path);
                Drawable drawable = Drawable.createFromStream(inputstream, null);
                holder.mIvAnim.setImageDrawable(drawable);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (mPos == position) {
                holder.mRLSelect.setVisibility(View.VISIBLE);
            } else {
                holder.mRLSelect.setVisibility(View.GONE);
            }
            holder.mIvAnim.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPos = position;
                    notifyDataSetChanged();
                    onClickListener.onclickAnim(path);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public interface OnClickListener {
        void onclickAnim(String string);
    }

}
