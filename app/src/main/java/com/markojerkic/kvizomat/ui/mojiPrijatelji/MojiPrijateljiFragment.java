package com.markojerkic.kvizomat.ui.mojiPrijatelji;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.markojerkic.kvizomat.R;

public class MojiPrijateljiFragment extends Fragment {

    private MojiPrijateljiViewModel mojiPrijateljiViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mojiPrijateljiViewModel =
                ViewModelProviders.of(this).get(MojiPrijateljiViewModel.class);
        View root = inflater.inflate(R.layout.moji_prijatelji, container, false);
        final TextView textView = root.findViewById(R.id.text_slideshow);
        mojiPrijateljiViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}
