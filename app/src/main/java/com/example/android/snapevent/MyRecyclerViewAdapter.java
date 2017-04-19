package com.example.android.snapevent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

/**
 * Created by akbfedora on 4/6/17.
 */

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

//    String[] sampleDataSet;
    List<File> fileNames;

    final String LOG_TAG = "MyRecyclerViewAdapter";
    Context context;

//    public MyRecyclerViewAdapter(String[] dummyText) {
//        sampleDataSet = dummyText;
//    }

    public MyRecyclerViewAdapter(List<File> imageFileNames) {
        fileNames = imageFileNames;
    }

//    public MyRecyclerViewAdapter(List<File> imageFileNames, String[] dummyText) {
//        fileNames = imageFileNames;
//        sampleDataSet = dummyText;
//    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;
        private ImageView mImageView;
        private Button mButton1;
        private Button mButton2;

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
        context = parent.getContext();
        return new ViewHolder(thisItemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        String thisItemFileName = fileNames.get(position).toString();
//        holder.mTextView.setText(thisItemText);
        holder.mTextView.setText(thisItemFileName);
//        holder.mImageView.setImageBitmap(getImageBitmap(holder.mImageView, position));

        Glide.with(context)
                .load(fileNames.get(position))
                .centerCrop()
                .into(holder.mImageView);
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
        return fileNames.size();
    }

    private Bitmap getImageBitmap(ImageView imageView, int position)   {
        imageView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int targetW = imageView.getMeasuredWidth();
        int targetH = imageView.getMeasuredHeight();

        // get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = 2;
        bmOptions.inPurgeable = true;

        Matrix matrix = new Matrix();
        matrix.postScale(0.5f, 0.5f);

        Bitmap croppedBitmap = null;

        try {
            Bitmap bitmap = BitmapFactory.decodeFile(fileNames.get(position).toString(), bmOptions);
            Log.v(LOG_TAG, " Bitmap: " + position);
            final int x = 0;
            final int y = bitmap.getHeight()/3;
            final int height = bitmap.getHeight()/3;
            final int width = bitmap.getWidth();

            croppedBitmap = Bitmap.createBitmap(bitmap, x, y, width, height, matrix, true);
        } catch (NullPointerException E)    {
            Log.e(LOG_TAG, "getImageBitmap(): NULL POINTER EXCEPTION " + E);
        }

//        return bitmap;
        return croppedBitmap;
    }
}
