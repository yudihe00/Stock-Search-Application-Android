package com.yudihe.hw9;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
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

// for fb sdk
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

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
    private String currDrawIndicator; // change by click change
    private String currDrawUrl;
    private String currSelectIndicator; // change by spinner select

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

    // TextView for error
    private  TextView textViewError;

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
    private Context context;

    // Stock symbol name, get from StockActivity;
    private String symbol;

    // ImageView for fbshare and favorite, work as buttons
    private ImageView imageViewFbShare;
    private ImageView imageViewFav;

    // FavList, store the symbol of favorite
    private ArrayList<String> favList;


    // share dialogue
    ShareDialog shareDialog;
    CallbackManager callbackManager;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_fragment,container,false);

        textViewTest = (TextView) view.findViewById(R.id.textViewTest);
        symbol = ((StockActivity)getActivity()).getSymbol();
        textViewTest.setText("symbol: "+symbol);
        requestQueue = Volley.newRequestQueue(getActivity()); // 'this' is the Context

        // context
        context = getActivity();

        // TableLayout
        detailTableLayout = (TableLayout) view.findViewById(R.id.detailTable);

        // Hide Table
        detailTableLayout.setVisibility(View.INVISIBLE);

        // TextView for error
        textViewError = (TextView)view.findViewById(R.id.textViewError);

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


        // ImageView for fbshare and favorite
        imageViewFbShare = (ImageView) view.findViewById(R.id.fbButton);
        imageViewFav = (ImageView) view.findViewById(R.id.favButton);

        // OnCreate view, if the symbol is in FavList, the FAvImage should change to full star
        favList = FavSingleton.getInstance().getFavList();
        if(favList.contains(symbol)) {
            imageViewFav.setImageResource(R.drawable.filled);
        }

        // Favorite click listener
        imageViewFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FavSingleton.getInstance().getFavList().contains(symbol)) {
                    FavSingleton.getInstance().deleteFromFav(symbol);
                    imageViewFav.setImageResource(R.drawable.empty);
                } else {
                    FavSingleton.getInstance().addToFav(symbol);
                    imageViewFav.setImageResource(R.drawable.filled);
                }

            }
        });


        // FB share
        shareDialog = new ShareDialog(this);
        callbackManager = CallbackManager.Factory.create();

        imageViewFbShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currDrawUrl!="") {
                    ShareLinkContent content = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse(currDrawUrl))
                            .build();
                    shareDialog.show(content);
                } else {
                    Toast.makeText(getActivity(),"Highchart Export Failed, cannot share on Facebook!",
                            Toast.LENGTH_SHORT).show();
                }

            }

        });



        // TextView for change, work as a button
        changeTextView = (TextView) view.findViewById(R.id.change);

        changeTextView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeTextView.setTextColor(Color.parseColor("#aca8a8"));
                changeTextView.setClickable(false);
                selectIndicator = spinnerIndicators.getSelectedItem().toString();
                currDrawIndicator = selectIndicator;
                currDrawUrl = "";
                Toast.makeText(getActivity(),"begin to draw "+selectIndicator+" chart",Toast.LENGTH_SHORT).show();
                drawCharts(symbol,selectIndicator);
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
                currSelectIndicator = spinnerIndicators.getSelectedItem().toString();
                if (!currSelectIndicator.equals(currDrawIndicator)){
                    changeTextView.setClickable(true);
                } else {
                    changeTextView.setClickable(false);
                    changeTextView.setTextColor(Color.parseColor("#aca8a8"));
                }
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


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.INVISIBLE);
                        //Toast.makeText(getActivity(), "Http request failed!", Toast.LENGTH_SHORT).show();
                        textViewError.setVisibility(View.VISIBLE);
                    }
                });
        //add request to queue
        requestQueue.add(jsonArrayRequest);


        return  view;

    } // End of onCreateView

    public void changeChart(View v){
        changeTextView.setTextColor(Color.parseColor("#aca8a8"));

    }


    public class WebAppInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /** Show a toast from the web page */
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }


        @JavascriptInterface
        public void saveChartUrl(String url) {
            currDrawUrl = url;
        }

    }

    private void drawCharts(final String symbol, final String indicator) {
        WebSettings webSettings = webViewCharts.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webViewCharts.addJavascriptInterface(new WebAppInterface(context), "AndroidInterface");


        webViewCharts.setWebViewClient(new WebViewClient());
        webViewCharts.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url){
                webViewCharts.loadUrl("javascript:drawChartOf('" + symbol+"&"+indicator + "')");
            }
        });
        webViewCharts.loadUrl(GlobalVariables.LOCALBASE_URL+ "/hw9/hw9-1.html");
    }
}
