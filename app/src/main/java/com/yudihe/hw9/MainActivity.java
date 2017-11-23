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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AutoCompleteTextView acTextView;
    private RequestQueue requestQueue;
    private TextView textView;
    private  TextView responseName;

    //String[] country = {"abxxx","abxsse","assse","axsser","cbxxx","cbxsse","cssse","cxsser"};
    //save value and show for autocomplete array
    private ArrayList<String> valueArray = new ArrayList<>();
    private ArrayList<String> displayArray = new ArrayList<>();

    //Validation for autocompleteTextView
    private boolean mValidateResult = false;
    private String pattern = "^[a-zA-Z]{1,5}$";

    // progress bar
    private ProgressBar spinnerAutoComplete;

    // Fav ListView
    private ListView listViewFav;

    // Fav arraylist
    private ArrayList<FavoriteSymbol> favInfoList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        // Test for http request using volley
//        textView = (TextView) findViewById(R.id.volleyText);
//        responseName = (TextView) findViewById(R.id.responseName);
        requestQueue = Volley.newRequestQueue(this); // 'this' is the Context

        // Bind components
        acTextView = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView);
        spinnerAutoComplete = (ProgressBar)findViewById(R.id.autoCompleteProgressBar);
        spinnerAutoComplete.setVisibility(View.GONE);

        // Set arrayAdapter for autocomplete
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.select_dialog_item, valueArray);
        final Context context = this;
        acTextView.setThreshold(1);
        acTextView.setAdapter(adapter);

        // Validator for autocompletetext view
        acTextView.setValidator(new AutoCompleteTextView.Validator() {
            @Override
            public boolean isValid(CharSequence text) {
                if(text.toString().matches(pattern)) {
                    mValidateResult = true;
                } else {
                    mValidateResult = false;
                }
                return mValidateResult;
            }

            @Override
            public CharSequence fixText(CharSequence invalidText) {
                return "";
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
                mValidateResult = false;
                acTextView.performValidation();
                if (mValidateResult) {
                    spinnerAutoComplete.setVisibility(View.VISIBLE);
                    spinnerAutoComplete.bringToFront();

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

                                            //acTextView.setAdapter(adapter);

                                        }

                                        //adapter.notifyDataSetChanged();
                                        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.select_dialog_item, displayArray);
                                        acTextView.setAdapter(adapter);
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
                                    Toast.makeText(MainActivity.this, "Nothing found!", Toast.LENGTH_SHORT).show();
                                }
                            });
                    //add request to queue
                    requestQueue.add(jsonArrayRequest);

                } else {
                    Toast.makeText(MainActivity.this, "Please enter valid charactors and the number of charactors should less than 5!", Toast.LENGTH_LONG).show();
                }


            }
        });

        // Fav ListView
        listViewFav = (ListView) findViewById(R.id.ListViewFav);
        favInfoList = new ArrayList<FavoriteSymbol>();
        favInfoList.add(new FavoriteSymbol("AAPL","5","a","a","a"));
        favInfoList.add(new FavoriteSymbol("B","5","a","a","a"));
        favInfoList.add(new FavoriteSymbol("CL","5","a","a","a"));
        FavAdapter favAdapter = new FavAdapter(this, R.layout.fav_row,favInfoList);
        listViewFav.setAdapter(favAdapter);


    } // end of onCreate method

    class AutoCompleteAdapter extends ArrayAdapter {

        public AutoCompleteAdapter(@NonNull Context context, int resource, @NonNull List objects) {
            super(context, resource, objects);
        }

        @Override
        public int getCount() {
            return valueArray.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    } // end of AutoCompleteAdapter, not write



    // Perform get quote when click
    public void getQuote(View v){
//        Toast.makeText(MainActivity.this, "Get Quete!", Toast.LENGTH_SHORT).show();
        acTextView.performValidation();
        if (mValidateResult) {
//            Toast.makeText(MainActivity.this, "Correct Text", Toast.LENGTH_LONG).show();
            //  Initial intent for jump to StockMainACtivity
            Intent intent = new Intent(MainActivity.this, StockActivity.class);
            // Put symbol data in intent, transfer to StockMainActivity
            intent.putExtra("symbol", acTextView.getText().toString());
            // Jump
            MainActivity.this.startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, "Please enter a stock name or symbol!",
                    Toast.LENGTH_LONG).show();
        }
    } // end of getQuote

    // Clear when click
    public void clear(View v){
        Toast.makeText(MainActivity.this, "Clear!", Toast.LENGTH_SHORT).show();
    }// end of Clear
}
