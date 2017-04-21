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
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

/**
 * Created by akbfedora on 4/6/17.
 */

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    List<File> fileNames;

    final String LOG_TAG = "MyRecyclerViewAdapter";
    Context context;

    public MyRecyclerViewAdapter(List<File> imageFileNames) {
        fileNames = imageFileNames;
    }

    public interface RecyclerViewButtonClickListener {
        public void onButton1Click(RecyclerView.ViewHolder vh);
        public void onButton2Click(RecyclerView.ViewHolder vh);
    }

    RecyclerViewButtonClickListener recyclerViewButtonClickListener;

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
                Toast.makeText(context, buttonClickMessage, Toast.LENGTH_SHORT).show();
                recyclerViewButtonClickListener.onButton1Click(holder);
            }
        });

        holder.mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buttonClickMessage = "Clicked button 2 at position " + position;
                Log.v(LOG_TAG, buttonClickMessage);
                Toast.makeText(context, buttonClickMessage, Toast.LENGTH_SHORT).show();
                recyclerViewButtonClickListener.onButton2Click(holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileNames.size();
    }
}
