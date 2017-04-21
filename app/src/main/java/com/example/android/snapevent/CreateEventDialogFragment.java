package com.example.android.snapevent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;

/**
 * Created by akbfedora on 4/21/17.
 */

public class CreateEventDialogFragment extends DialogFragment {
    final String TAG = "CEDF";
    String dialogMessage = null;

    public interface CreateEventDialogListener  {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }
    CreateEventDialogListener mListener;

    public CreateEventDialogFragment(String message)  {
        this.dialogMessage = message;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (CreateEventDialogListener) activity;
        } catch (ClassCastException e)  {
            throw new ClassCastException(activity.toString()
                            + " must implement CreateEventDialogListener");
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(dialogMessage)
                .setTitle("Title")
                .setPositiveButton("Positive Button", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v(TAG, "Positive dialog click!");
                        mListener.onDialogPositiveClick(CreateEventDialogFragment.this);
                    }
                })
                .setNegativeButton("Negative Button", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v(TAG, "Negative dialog click!");
                        mListener.onDialogNegativeClick(CreateEventDialogFragment.this);
                    }
                });
        return builder.create();
    }
}
