package com.example.android.snapevent;

import android.content.Context;
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

        imageView.setImageBitmap();
        return null;
    }
}
