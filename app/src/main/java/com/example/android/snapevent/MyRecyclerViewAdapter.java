package com.example.android.snapevent;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
        public Button mButton1;
        public Button mButton2;

        public ViewHolder(View itemView) {
            super(itemView);

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
}
