package com.yudihe.hw9;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;



public class SubPage01 extends Fragment {
    private static final String TAG="Tab1Fragment";
    private Button btnTest;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sub_page01,container,false);
        btnTest = (Button) view.findViewById(R.id.button);
        btnTest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Test button on page1",Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
