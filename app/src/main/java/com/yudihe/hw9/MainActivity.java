package com.yudihe.hw9;

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

public class MainActivity extends AppCompatActivity {

    AutoCompleteTextView acTextView;
    private RequestQueue requestQueue;
    private TextView textView;
    private  TextView responseName;

    String[] country = {"abxxx","abxsse","assse","axsser","cbxxx","cbxsse","cssse","cxsser"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        // Test for http request using volley
        textView = (TextView) findViewById(R.id.volleyText);
        responseName = (TextView) findViewById(R.id.responseName);
        requestQueue = Volley.newRequestQueue(this); // 'this' is the Context

        acTextView = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.select_dialog_item, country);
        acTextView.setThreshold(1);
        acTextView.setAdapter(adapter);
        acTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // Test for http request using volley

                String symbol = acTextView.getText().toString();
                String url = GlobalVariables.PHP_URL+"?name="+symbol;

                //save value and show for autocomplete array
                final ArrayList<String> valueArray = new ArrayList<>();
                final ArrayList<String> displayArray = new ArrayList<>();

                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                textView.setText("Trimmed response: " + response.toString());
                                Toast.makeText(getApplicationContext(), "change!", Toast.LENGTH_SHORT).show();
                                StringBuilder names = new StringBuilder();
                                names.append("Parsed names from the response: ");
                                try {
                                    for(int i = 0; i < response.length(); i++){
                                        JSONObject jresponse = response.getJSONObject(i);
                                        String value = jresponse.getString("value");
                                        valueArray.add(value);
                                        String display = jresponse.getString("display");
                                        displayArray.add(display);

                                        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.select_dialog_item, country);
                                        acTextView.setThreshold(1);
                                        acTextView.setAdapter(adapter);

                                    }

                                    responseName.setText(displayArray.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "Nothing found!", Toast.LENGTH_SHORT).show();
                            }
                        });
                //add request to queue
                requestQueue.add(jsonArrayRequest);




            }
        });
    } // end of onCreate method

    class AutoCompleteAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 0;
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
    }
}
