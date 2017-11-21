package com.yudihe.hw9;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
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

    // String for the select indicator
    private String selectIndicator;

    // Table layout
    private TableLayout detailTableLayout;

    // TextView in detail table
    private TextView tableTextViewSymbol;
    private TextView tableTextViewLastPrice;
    private TextView tableTextViewChange;
    private TextView tableTextViewTimeStamp;
    private TextView tableTextViewOpen;
    private TextView tableTextViewClose;
    private TextView tableTextViewRange;
    private TextView tableTextViewVolume;

    // ImageView in detail table
    private ImageView changeImageView;

    // Progress bar
    private ProgressBar progressBar;

    // Spinner for indicators
    private Spinner spinnerIndicators;

    // TextView for change, work as a button
    private TextView changeTextView;

    // WebView for charts
    private WebView webViewCharts;

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

        // TableLayout
        detailTableLayout = (TableLayout) view.findViewById(R.id.detailTable);

        // Hide Table
        detailTableLayout.setVisibility(View.INVISIBLE);

        // TextView in Table
        tableTextViewSymbol = (TextView) view.findViewById(R.id.textViewStockSymbol);
        tableTextViewLastPrice = (TextView) view.findViewById(R.id.textViewLastPrice);
        tableTextViewChange = (TextView) view.findViewById(R.id.textViewChange);
        tableTextViewTimeStamp = (TextView) view.findViewById(R.id.textViewTimeStamp);
        tableTextViewOpen = (TextView) view.findViewById(R.id.textViewOpen);
        tableTextViewClose = (TextView) view.findViewById(R.id.textViewClose);
        tableTextViewRange = (TextView) view.findViewById(R.id.textViewDayRange);
        tableTextViewVolume = (TextView) view.findViewById(R.id.textViewVolume);

        // ImageView in Table
        changeImageView = (ImageView) view.findViewById(R.id.tableChangeImageView);

        // Progress bar
        progressBar = (ProgressBar) view.findViewById(R.id.detailProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        // WebView for charts
        webViewCharts = (WebView) view.findViewById(R.id.webViewChart);
        WebSettings webSettings = webViewCharts.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webViewCharts.setWebViewClient(new WebViewClient());

        webViewCharts.loadUrl("http://www.superman.com");

        // TextView for change, work as a button
        changeTextView = (TextView) view.findViewById(R.id.change);

        changeTextView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeTextView.setTextColor(Color.parseColor("#aca8a8"));
                changeTextView.setClickable(false);
                selectIndicator = spinnerIndicators.getSelectedItem().toString();
                Toast.makeText(getActivity(),"begin to draw "+selectIndicator+" chart",Toast.LENGTH_SHORT).show();
            }
        });

        // Spinner for Indicators
        spinnerIndicators = (Spinner) view.findViewById(R.id.spinnerIndicators);
        ArrayAdapter<String> indicatorAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.indicators));
        indicatorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIndicators.setAdapter(indicatorAdapter);

        // Spinner select event
        spinnerIndicators.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // When item selected change to black color and the change button is clickable
                changeTextView.setTextColor(Color.parseColor("#000000"));
                changeTextView.setClickable(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
                changeTextView.setClickable(false);
            }

        });

        String url = GlobalVariables.PHP_URL+"?symbol="+symbol+"&action=getStockData";

        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            progressBar.setVisibility(View.INVISIBLE);
                            //Set detail table
                            tableTextViewSymbol.setText(response.getString("symbol"));
                            tableTextViewLastPrice.setText(response.getString("last price"));
                            tableTextViewTimeStamp.setText(response.getString("time stamp"));
                            tableTextViewOpen.setText(response.getString("open"));
                            tableTextViewClose.setText(response.getString("close"));
                            tableTextViewVolume.setText(response.getString("volume"));
                            tableTextViewRange.setText(response.getString("day's range"));

                            // Set change cell in table
                            String change = response.getString("change");
                            String changePercent = response.getString("change percent");
                            tableTextViewChange.setText(change+"("+changePercent+")");
                            double changeDouble = Double.parseDouble(change);
                            if(changeDouble >= 0) {
                                changeImageView.setImageResource(R.drawable.up);
                            } else {
                                changeImageView.setImageResource(R.drawable.down);
                            }
                            // Set table visible
                            detailTableLayout.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getActivity(), "Http request failed!", Toast.LENGTH_SHORT).show();
                    }
                });
        //add request to queue
        requestQueue.add(jsonArrayRequest);


        return  view;

    } // End of onCreateView

    public void changeChart(View v){
        changeTextView.setTextColor(Color.parseColor("#aca8a8"));

    }
}
