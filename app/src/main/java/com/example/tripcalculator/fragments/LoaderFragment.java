package com.example.tripcalculator.fragments;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.tripcalculator.R;

public class LoaderFragment extends DialogFragment {

    private ViewGroup root;

    public LoaderFragment(ViewGroup root){
        this.root = root;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View rootView = getLayoutInflater().inflate(R.layout.loader_view, root, false);
        Dialog dialog = new Dialog(requireContext());
        Drawable drawable;
        ProgressBar progressBar = rootView.findViewById(R.id.progress_bar);
        progressBar.setIndeterminate(true);
        drawable = progressBar.getIndeterminateDrawable();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(drawable);
            dialog.getWindow().setContentView(rootView);
        }
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        return dialog;
    }
}
