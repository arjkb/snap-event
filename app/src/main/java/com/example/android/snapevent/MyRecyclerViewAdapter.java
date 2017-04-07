package com.example.android.snapevent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

/**
 * Created by akbfedora on 4/6/17.
 */

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    String[] sampleDataSet;
    List<File> fileNames;

    final String LOG_TAG = "MyRecyclerViewAdapter";

//    public MyRecyclerViewAdapter(String[] dummyText) {
//        sampleDataSet = dummyText;
//    }

    public MyRecyclerViewAdapter(List<File> imageFileNames, String[] dummyText) {
        fileNames = imageFileNames;
        sampleDataSet = dummyText;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ImageView mImageView;
        public Button mButton1;
        public Button mButton2;

        public ViewHolder(View itemView) {
            super(itemView);

            mImageView = (ImageView) itemView.findViewById(R.id.image_view);
            mTextView = (TextView) itemView.findViewById(R.id.dummy_text_view);
            mButton1 = (Button) itemView.findViewById(R.id.button1);
            mButton2 = (Button) itemView.findViewById(R.id.button2);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View thisItemView = inflater.inflate(R.layout.my_cardlist_view, parent, false);

        return new ViewHolder(thisItemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        String thisItemText = sampleDataSet[position];
        holder.mTextView.setText(thisItemText);

        holder.mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, " Clicking button1 at position " + position);
            }
        });

        holder.mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, " Clicking button2 at position " + position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return sampleDataSet.length;
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

        Log.v(LOG_TAG, " >> " + targetH + " " + targetW + " " + photoH + " " + photoW);

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
