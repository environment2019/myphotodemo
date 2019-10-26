package com.example.myphoto.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myphoto.R;
import com.example.myphoto.persistence.PhotoRecord;

import java.util.ArrayList;
import java.util.List;

public class PhotoRecyclerViewAdapter extends RecyclerView.Adapter<PhotoRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private List<PhotoRecord> mListPhoto = new ArrayList<>();

    /**
     * Callback interface for item click actions in the list
     */
    public interface ItemClickListener {
        public void onItemClick(int pos);
    }

    private ItemClickListener mItemClickListener;

    public void setItemClickListener(ItemClickListener listener) {
        mItemClickListener = listener;
    }

    public PhotoRecyclerViewAdapter(Context context, List<PhotoRecord> listPhoto) {
        this.mContext = context;
        this.mListPhoto.clear();
        if (listPhoto != null) {
            this.mListPhoto.addAll(listPhoto);
        }
    }

    @Override
    public int getItemCount() {
        return mListPhoto.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        return new ViewHolder(layoutInflater.inflate(R.layout.photo_list_item, viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        String strCreationTime = mListPhoto.get(position).getCreateDatetime();
        viewHolder.tvCreationTime.setText(strCreationTime);
        String strCustomImageName = mListPhoto.get(position).getCustomImageName();
        viewHolder.tvName.setText(strCustomImageName);
        String thumbnailImagePath = mListPhoto.get(position).getThumbnailPathName();
        if (!TextUtils.isEmpty(thumbnailImagePath)) {
            int imageViewWidth = viewHolder.ivPhoto.getWidth();
            int imageViewHeight = viewHolder.ivPhoto.getHeight();
            // Optimize options for decoding to save memory occupation
            Bitmap bitmap = optimizeImageSize(thumbnailImagePath, imageViewWidth, imageViewHeight);
            viewHolder.ivPhoto.setImageBitmap(bitmap);
        }
        if (mItemClickListener != null) {
            viewHolder.llItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onItemClick(position);
                }
            });
        }
    }

    private Bitmap optimizeImageSize(String imagePath, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565; // Use low level config
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false;
        options.inSampleSize = calculateSampleSize(options,reqWidth, reqHeight);
        return BitmapFactory.decodeFile(imagePath, options);
    }

    private int calculateSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int sampleSize = 1;
        int picWidth = options.outWidth;
        int picHeight = options.outHeight;
        if (picWidth > reqWidth || picHeight > reqHeight) {
            int halfPicWidth = picWidth / 2;
            int halfPicHeight = picHeight / 2;
            while (halfPicWidth / sampleSize > reqWidth || halfPicHeight / sampleSize > reqHeight) {
                sampleSize *= 2;
            }
        }
        return sampleSize;
    }

    /**
     * Customized view holder for the list item
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout llItem;
        TextView tvCreationTime;
        TextView tvName;
        ImageView ivPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            llItem = itemView.findViewById(R.id.ll_item);
            tvCreationTime = itemView.findViewById(R.id.tv_creation_time);
            tvName = itemView.findViewById(R.id.tv_name);
            ivPhoto = itemView.findViewById(R.id.iv_photo);
        }
    }
}
