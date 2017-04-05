package com.example.android.snapevent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    public GridViewAdapter(Context c, List<File> fileNames)    {
        mContext = c;
        this.fileNames = fileNames;
    }

    @Override
    public int getCount() {
        return fileNames.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null)    {
            // if it's not recycled, recycle some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageBitmap(getImageBitmap(imageView, position));
        return null;
    }

    private Bitmap getImageBitmap(ImageView imageView, int position)   {
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileNames.get(position).toString(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // decode the image file into a bitmap sized to fill the view
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable =true;

        Bitmap bitmap = BitmapFactory.decodeFile(fileNames.get(position).toString(), bmOptions);
//        imageView.setImageBitmap(bitmap);

        return bitmap;
    }
}
