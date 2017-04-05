package com.example.android.snapevent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.util.List;

/**
 * Created by akbfedora on 4/4/17.
 */

public class GridViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<File> fileNames;

    String TAG = "GRID_VIEW";

    public GridViewAdapter(Context c, List<File> fileNames)    {
        Log.v(TAG, " Entering GridViewAdapter()");
        mContext = c;
        this.fileNames = fileNames;
    }

    @Override
    public int getCount() {
        Log.v(TAG, " Inside getCount() " + fileNames.size());
        return fileNames.size();
    }

    @Override
    public Object getItem(int position) {
        Log.v(TAG, " Inside getItem()");
        return null;
    }

    @Override
    public long getItemId(int position) {
        Log.v(TAG, " Inside getItemId()");
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.v(TAG, " Inside getView()");

        ImageView imageView;
        if (convertView == null)    {
            // if it's not recycled, recycle some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(2, 2, 2, 2);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageBitmap(getImageBitmap(imageView, position));
        return imageView;
    }

    private Bitmap getImageBitmap(ImageView imageView, int position)   {
        imageView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int targetW = imageView.getMeasuredWidth();
        int targetH = imageView.getMeasuredHeight();

        // get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileNames.get(position).toString(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        Log.v(TAG, " >> " + targetH + " " + targetW + " " + photoH + " " + photoW);

        // determine how much to scale down the image
        int scaleFactor = Math.min(photoW/100, photoH/100);

        // decode the image file into a bitmap sized to fill the view
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable =true;

        Bitmap bitmap = BitmapFactory.decodeFile(fileNames.get(position).toString(), bmOptions);

        return bitmap;
    }
}
