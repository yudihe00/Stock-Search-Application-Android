package com.yudihe.hw9;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by heyudi on 11/19/17.
 */

public class Tab3FragMent extends android.support.v4.app.Fragment {
    private static final String TAG = "Tab3FragMent";
    private Button btnTEST3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab3_fragment,container,false);
        btnTEST3 = (Button) view.findViewById(R.id.btnTEST3);
        btnTEST3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getActivity(), "test for tab3", Toast.LENGTH_SHORT).show();
            }
        });
        return  view;

    }
}
