package com.example.android.snapevent;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

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
        return null;
    }
}
