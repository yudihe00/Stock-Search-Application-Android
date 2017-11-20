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



public class SubPage03 extends Fragment {
    private static final String TAG="Tab3Fragment";
    private Button btnTest;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sub_page03,container,false);
        btnTest = (Button) view.findViewById(R.id.button3);
        btnTest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Test button on page3",Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
