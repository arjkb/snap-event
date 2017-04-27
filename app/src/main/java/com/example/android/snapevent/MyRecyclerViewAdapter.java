package com.example.android.snapevent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.util.List;

/**
 * Created by akbfedora on 4/6/17.
 */

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<File> fileNames;

    final String LOG_TAG = "MyRecyclerViewAdapter";
    private Context context;

    public MyRecyclerViewAdapter(List<File> imageFileNames) {
        fileNames = imageFileNames;
    }

    public interface RecyclerViewButtonClickListener {
        public void onButton1Click(int position);
        public void onButton2Click(String detectedText, int position);
    }

    private RecyclerViewButtonClickListener recyclerViewButtonClickListener;

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
        try {
            recyclerViewButtonClickListener = (RecyclerViewButtonClickListener) context;
        } catch (Exception E)   {
            Log.w(LOG_TAG, " RVBCL ERROR " + E.toString());
        }
        return new ViewHolder(thisItemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        String thisItemFileName = fileNames.get(position).toString();
        holder.mTextView.setText(thisItemFileName);

        Glide.with(context)
                .load(fileNames.get(position))
                .centerCrop()
                .into(holder.mImageView);

        holder.mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buttonClickMessage = "Clicked button 1 at position " + position;
                Log.v(LOG_TAG, buttonClickMessage);
                recyclerViewButtonClickListener.onButton1Click(position);
            }
        });

        holder.mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buttonClickMessage = "Clicked button 2 at position " + position;
                Log.v(LOG_TAG, buttonClickMessage);

                String detectedText = detectText(holder.mImageView);
                Log.v(LOG_TAG, " Yippy! DT: " + detectedText);

                recyclerViewButtonClickListener.onButton2Click(detectedText, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileNames.size();
    }

    public SparseArray<TextBlock> detectText(ImageView mImageView)  {
//        Log.v(LOG_TAG, " Entering detectText " + imageViewID);
        Log.v(LOG_TAG, " Entering detectText ");
        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();

        if (!textRecognizer.isOperational())    {
            Log.v(LOG_TAG, " Detector dependencies are not yet available!");

            IntentFilter lowStorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = context.registerReceiver(null, lowStorageFilter) != null;
            if (hasLowStorage)  {
                Toast.makeText(context, "LOW STORAGE!", Toast.LENGTH_SHORT).show();
                Log.w(LOG_TAG, "Low storage!");
            }
        }

//        ImageView mImageView = (ImageView) context.findViewById(imageViewID);

        Bitmap bitmap = ((GlideBitmapDrawable)mImageView.getDrawable().getCurrent()).getBitmap();
        Bitmap convertedBitmap = convert(bitmap, Bitmap.Config.ARGB_8888);
        Frame frame = new Frame.Builder().setBitmap(convertedBitmap).build();

        SparseArray<TextBlock> textBlockSparseArray = textRecognizer.detect(frame);
//        String detectedText = "";
//
//        Log.v(LOG_TAG, " textBoxSparseArray size: " + textBlockSparseArray.size());
//        for(int i = 0; i < textBlockSparseArray.size(); i++)    {
//            TextBlock textBlock = textBlockSparseArray.valueAt(i);
//            detectedText += textBlock.getValue();
//            Log.v(LOG_TAG, " Text! " + textBlock.getValue());
//        }
//
//        Log.v(LOG_TAG, " Exiting detectText " + detectedText);
//
//        return detectedText;
        return textBlockSparseArray;
    }

    private Bitmap convert(Bitmap bitmap, Bitmap.Config config) {
        Bitmap convertedBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), config);
        Canvas canvas = new Canvas(convertedBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return convertedBitmap;
    }
}
