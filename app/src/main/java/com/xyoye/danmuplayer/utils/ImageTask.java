package com.xyoye.danmuplayer.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by xyy on 2018-03-03 下午 6:05
 */

public class ImageTask extends AsyncTask<String, Void, Bitmap> {
    @SuppressLint("StaticFieldLeak")
    private View view;
    private String imageUrl;
    private LruCache<String, Bitmap> mImageCache;

    public ImageTask(View view){
        this.view = view;
        int maxCache = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxCache / 8;
        mImageCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        imageUrl = params[0];
        Bitmap bitmap;
        if (mImageCache.get(imageUrl) == null) {
            bitmap = downloadImage();
            if (bitmap != null && imageUrl != null)
                mImageCache.put(imageUrl, bitmap);
        }else {
            bitmap = downloadImage();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        ImageView iv = (ImageView) view.findViewWithTag(imageUrl);
        if (iv != null && result != null) {
            iv.setImageBitmap(result);
        }
    }

    /**
     * 根据路径获取视频帧
     */
    private Bitmap downloadImage() {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(imageUrl);
        return retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
    }
}
