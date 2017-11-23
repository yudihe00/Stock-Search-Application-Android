package com.yudihe.hw9;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Autocomplete
    private AutoCompleteTextView acTextView;
    private ArrayAdapter adapter;

    // context
    private Context context;

    private RequestQueue requestQueue;
    private TextView textView;
    private  TextView responseName;

    //String[] country = {"abxxx","abxsse","assse","axsser","cbxxx","cbxsse","cssse","cxsser"};
    //save value and show for autocomplete array
    private ArrayList<String> valueArray = new ArrayList<>();
    private ArrayList<String> displayArray = new ArrayList<>();

    //Validation for autocompleteTextView
    private boolean mValidateResult = false;
    private String pattern = "(^\\s*)";

    // progress bar
    private ProgressBar spinnerAutoComplete;
    private ProgressBar spinnerFavLoading;

    // Fav ListView
    private ListView listViewFav;

    // Fav arraylist
    private ArrayList<FavoriteSymbol> favInfoList;

    // Fav symble name list, get from singlton
    private ArrayList<String> favSymbolList;

    // Number of Fav request done
    private int numFavReqDone;

    // Refresh button
    private ImageView imageViewRefresh;

    // AutoRefresh switch
    private Switch switchAutoRefresh;

    // AutoRefresh thread
    private Thread threadAutoRefresh;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        // Test for http request using volley
//        textView = (TextView) findViewById(R.id.volleyText);
//        responseName = (TextView) findViewById(R.id.responseName);
        requestQueue = Volley.newRequestQueue(this); // 'this' is the Context

        // Bind components
        acTextView = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView);
        spinnerAutoComplete = (ProgressBar)findViewById(R.id.autoCompleteProgressBar);
        spinnerAutoComplete.setVisibility(View.INVISIBLE);

        // Set arrayAdapter for autocomplete
        //adapter = new ArrayAdapter(this, android.R.layout.select_dialog_item, valueArray);
        final Context context = this;
        acTextView.setThreshold(1);
        //acTextView.setAdapter(adapter);

        // Validator for autocompletetext view
        acTextView.setValidator(new AutoCompleteTextView.Validator() {
            @Override
            public boolean isValid(CharSequence text) {
                if(text.toString().matches(pattern)|| text.toString().length()==0) {
                    mValidateResult = false;
                } else {
                    mValidateResult = true;
                }
                return mValidateResult;
            }

            @Override
            public CharSequence fixText(CharSequence invalidText) {
                return invalidText.toString();
            }
        });


        acTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
//                mValidateResult = false;
//                acTextView.performValidation();
//
//                if (mValidateResult) {
                    spinnerAutoComplete.setVisibility(View.VISIBLE);
                    spinnerAutoComplete.bringToFront();

                    // Clear adapter
                    // acTextView.setAdapter((ArrayAdapter<String>)null);

                    // Test for http request using volley

                    String symbol = acTextView.getText().toString();
                    String url = GlobalVariables.PHP_URL+"?name="+symbol;

                    JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
//                                textView.setText("Trimmed response: " + response.toString());
//                                    Toast.makeText(MainActivity.this, "change!", Toast.LENGTH_SHORT).show();
                                StringBuilder names = new StringBuilder();
                                names.append("Parsed names from the response: ");
                                try {
                                    //adapter.clear();
                                    valueArray.clear();
                                    displayArray.clear();
                                    for(int i = 0; i < response.length(); i++){
                                        JSONObject jresponse = response.getJSONObject(i);
                                        String value = jresponse.getString("value");

                                        String display = jresponse.getString("display");
                                        valueArray.add(value);
                                        displayArray.add(display);

                                        // acTextView.setAdapter(adapter);

                                    }

                                    //adapter.notifyDataSetChanged();
                                    adapter = new ArrayAdapter(context, android.R.layout.select_dialog_item, displayArray);
                                    acTextView.setAdapter(adapter);

                                    // The first autocomplete always not shown, need to set dropdown view
//                                    if(acTextView.getText().toString().length()==1) {
                                        acTextView.showDropDown();
//                                    }

                                    spinnerAutoComplete.setVisibility(View.GONE);

//                                    responseName.setText(displayArray.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //Toast.makeText(MainActivity.this, "Auto-Complete request failed.", Toast.LENGTH_SHORT).show();
                                spinnerAutoComplete.setVisibility(View.GONE);
                            }
                        });
                    //add request to queue
                    requestQueue.add(jsonArrayRequest);

//                } else {
//                    Toast.makeText(MainActivity.this, "Please enter valid charactors and the number of charactors should less than 5!", Toast.LENGTH_LONG).show();
//                    acTextView.dismissDropDown();
//                }


            }
        });


        // Fav refresh and load when view is create
        // Fav load progressBar
        spinnerFavLoading = (ProgressBar) findViewById(R.id.progressLoadFav);
        spinnerFavLoading.setVisibility(View.VISIBLE);
        // Fav ListView
        listViewFav = (ListView) findViewById(R.id.ListViewFav);
        favInfoList = new ArrayList<FavoriteSymbol>();
        // Get current all fav symbol name
        favSymbolList = FavSingleton.getInstance().getFavList();
        numFavReqDone=0;
        // Get detail data, store in favInfoList
        for (int i=0; i<favSymbolList.size(); i++) {
            upDateSymbolInfoList(favSymbolList.get(i),this);
        }
//        favInfoList.add(new FavoriteSymbol("AAPL","5","a","a","a"));
//        favInfoList.add(new FavoriteSymbol("B","5","a","a","a"));
//        favInfoList.add(new FavoriteSymbol("CL","5","a","a","a"));
        if(numFavReqDone == favSymbolList.size()) {
            FavAdapter favAdapter = new FavAdapter(this, R.layout.fav_row,favInfoList);
            listViewFav.setAdapter(favAdapter);
            spinnerFavLoading.setVisibility(View.INVISIBLE);
        }

        // Refresh Fav table
        imageViewRefresh = (ImageView) findViewById(R.id.imageViewRefresh);
        imageViewRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fav refresh and load when view is create
                // Fav load progressBar
                //spinnerFavLoading = (ProgressBar) findViewById(R.id.progressLoadFav);
                spinnerFavLoading.setVisibility(View.VISIBLE);
                // Fav ListView
                listViewFav = (ListView) findViewById(R.id.ListViewFav);
                favInfoList = new ArrayList<FavoriteSymbol>();
                // Get current all fav symbol name
                favSymbolList = FavSingleton.getInstance().getFavList();
                numFavReqDone=0;
                // Get detail data, store in favInfoList
                for (int i=0; i<favSymbolList.size(); i++) {
                    upDateSymbolInfoList(favSymbolList.get(i),context);
                }
        //        favInfoList.add(new FavoriteSymbol("AAPL","5","a","a","a"));
        //        favInfoList.add(new FavoriteSymbol("B","5","a","a","a"));
        //        favInfoList.add(new FavoriteSymbol("CL","5","a","a","a"));
                if(numFavReqDone == favSymbolList.size()) {
                    FavAdapter favAdapter = new FavAdapter(context, R.layout.fav_row,favInfoList);
                    listViewFav.setAdapter(favAdapter);
                    spinnerFavLoading.setVisibility(View.INVISIBLE);
                }
            }
        });

        // AutoRefresh Fav table
        threadAutoRefresh = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(numFavReqDone == favSymbolList.size()) {
                                    refreshFavTable();
                                    Toast.makeText(context,"refresh",Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context,"last refresh request not done.Cannot make new refresh request.",Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                        Thread.sleep(10000);
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        switchAutoRefresh=(Switch)findViewById(R.id.switchAutoRefresh);
        switchAutoRefresh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(context,"check state "+Boolean.toString(isChecked),Toast.LENGTH_SHORT).show();
                if(isChecked) {
                    threadAutoRefresh.start();
                }
                else {
                    threadAutoRefresh.interrupt();
                }
            }
        });



    } // end of onCreate method

    // RefreshFavTable
    private void refreshFavTable() {
        // Fav refresh and load when view is create
        // Fav load progressBar
        //spinnerFavLoading = (ProgressBar) findViewById(R.id.progressLoadFav);
        spinnerFavLoading.setVisibility(View.VISIBLE);
        // Fav ListView
        listViewFav = (ListView) findViewById(R.id.ListViewFav);
        favInfoList = new ArrayList<FavoriteSymbol>();
        // Get current all fav symbol name
        favSymbolList = FavSingleton.getInstance().getFavList();
        numFavReqDone=0;
        // Get detail data, store in favInfoList
        for (int i=0; i<favSymbolList.size(); i++) {
            upDateSymbolInfoList(favSymbolList.get(i),context);
        }
        //        favInfoList.add(new FavoriteSymbol("AAPL","5","a","a","a"));
        //        favInfoList.add(new FavoriteSymbol("B","5","a","a","a"));
        //        favInfoList.add(new FavoriteSymbol("CL","5","a","a","a"));
        if(numFavReqDone == favSymbolList.size()) {
            FavAdapter favAdapter = new FavAdapter(context, R.layout.fav_row,favInfoList);
            listViewFav.setAdapter(favAdapter);
            spinnerFavLoading.setVisibility(View.INVISIBLE);
        }

    }


    // Up date FavSymbolInfoList
    private void upDateSymbolInfoList(final String symbol, final Context context) {
        //spinnerAutoComplete.setVisibility(View.VISIBLE);
        String url = GlobalVariables.PHP_URL+"?symbol="+symbol+"&action=getStockData";

        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            //spinnerAutoComplete.setVisibility(View.INVISIBLE);
                            //spinnerFavLoading.setVisibility(View.VISIBLE);
                            //Set detail table
                            String symbol=response.getString("symbol");
                            String price = response.getString("last price");
                            String timestamp = Long.toString(new Date().getTime());
                            // Set change cell in table
                            String change = response.getString("change");
                            String changePercent = response.getString("change percent");
                            favInfoList.add(new FavoriteSymbol(symbol,price,timestamp,change,changePercent));
                            numFavReqDone++;

                            // End of loading
                            if(numFavReqDone == favSymbolList.size()) {
                                FavAdapter favAdapter = new FavAdapter(context, R.layout.fav_row,favInfoList);
                                listViewFav.setAdapter(favAdapter);
                                spinnerFavLoading.setVisibility(View.INVISIBLE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //spinnerFavLoading.setVisibility(View.INVISIBLE);
                        numFavReqDone++;
                        Toast.makeText(context, "Failed to refresh "+symbol+" info.", Toast.LENGTH_SHORT).show();
                        //textViewErrorMain.setVisibility(View.VISIBLE);

                        // End of loading
                        if(numFavReqDone == favSymbolList.size()) {
                            FavAdapter favAdapter = new FavAdapter(context, R.layout.fav_row,favInfoList);
                            listViewFav.setAdapter(favAdapter);
                            spinnerFavLoading.setVisibility(View.INVISIBLE);
                        }
                    }
                });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //add request to queue
        requestQueue.add(jsonArrayRequest);
    }

    // Perform get quote when click
    public void getQuote(View v){
//        Toast.makeText(MainActivity.this, "Get Quete!", Toast.LENGTH_SHORT).show();
        mValidateResult = false;
        acTextView.performValidation();

        if (mValidateResult) {
//            Toast.makeText(MainActivity.this, "Correct Text", Toast.LENGTH_LONG).show();
            //  Initial intent for jump to StockMainACtivity
            Intent intent = new Intent(MainActivity.this, StockActivity.class);
            String symbolName = acTextView.getText().toString().toUpperCase();
            String[] tmp = symbolName.split(" ");
            symbolName=tmp[0];
            // Put symbol data in intent, transfer to StockMainActivity
            intent.putExtra("symbol", symbolName);
            // Jump
            MainActivity.this.startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, "Please enter a stock name or symbol!",
                    Toast.LENGTH_LONG).show();
        }
    } // end of getQuote

    // Clear when click
    public void clear(View v){
        //Toast.makeText(MainActivity.this, "Clear!", Toast.LENGTH_SHORT).show();
        acTextView.setText("");
    }// end of Clear
}
