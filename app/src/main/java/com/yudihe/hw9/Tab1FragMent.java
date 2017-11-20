package com.yudihe.hw9;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by heyudi on 11/19/17.
 */

public class Tab1FragMent extends android.support.v4.app.Fragment {
    private static final String TAG = "Tab1FragMent";
    private Button btnTEST;
    private TextView textViewTest;
    private RequestQueue requestQueue;

    // Stock symbol name, get from StockActivity;
    private String symbol;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_fragment,container,false);
        //btnTEST = (Button) view.findViewById(R.id.btnTEST);
//        btnTEST.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Toast.makeText(getActivity(), "test for tab1", Toast.LENGTH_SHORT).show();
//            }
//        });

        textViewTest = (TextView) view.findViewById(R.id.textViewTest);
        symbol = ((StockActivity)getActivity()).getSymbol();
        textViewTest.setText("symbol: "+symbol);
        requestQueue = Volley.newRequestQueue(getActivity()); // 'this' is the Context


        String url = GlobalVariables.PHP_URL+"?name="+symbol;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        textViewTest.setText("Trimmed response: " + response.toString());
//                                    Toast.makeText(MainActivity.this, "change!", Toast.LENGTH_SHORT).show();
                        StringBuilder names = new StringBuilder();
                        names.append("Parsed names from the response: ");
//                        try {
//                            //adapter.clear();
////                            valueArray.clear();
////                            displayArray.clear();
//                            for(int i = 0; i < response.length(); i++){
//                                JSONObject jresponse = response.getJSONObject(i);
//                                String value = jresponse.getString("value");
//
//                                String display = jresponse.getString("display");
////                                valueArray.add(value);
////                                displayArray.add(display);
//
//                                //acTextView.setAdapter(adapter);
//
//                            }
//
////                                    responseName.setText(displayArray.toString());
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Nothing found!", Toast.LENGTH_SHORT).show();
                    }
                });
        //add request to queue
        requestQueue.add(jsonArrayRequest);


        return  view;

    }
}
