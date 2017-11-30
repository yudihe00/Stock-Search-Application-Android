package com.yudihe.hw9;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static com.yudihe.hw9.Tab1FragMent.fromJson;
import static com.yudihe.hw9.Tab1FragMent.toJson;

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

    // Spinner for sort
    private Spinner spinnerSortBy;
    private Spinner spinnerOrder;
    ArrayAdapter<String> arrayAdapterSortBy;
    ArrayAdapter<String> arrayAdapterOrder;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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
        // Fav ListView
        listViewFav = (ListView) findViewById(R.id.ListViewFav);
        registerForContextMenu(listViewFav);

//        // Long press to delete Favsymbol
//        listViewFav.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                TextView symbolView=(TextView)view.findViewById(R.id.favSymbol);
//                String symbol = symbolView.getText().toString();
//                favSymbolList=getCurrFavList(context);
//                return true;
//            }
//        });
        listViewFav.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // interrupt autorefresh thread
                threadAutoRefresh.interrupt();

                //threadAutoRefresh.setThread(null);
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
                                            //Toast.makeText(context,"refresh",Toast.LENGTH_SHORT).show();
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


                TextView symbolView=(TextView)view.findViewById(R.id.favSymbol);
                String symbol = symbolView.getText().toString();
                Intent intent = new Intent(MainActivity.this, StockActivity.class);

                // Put symbol data in intent, transfer to StockMainActivity
                intent.putExtra("symbol", symbol);
                // Jump
                MainActivity.this.startActivity(intent);
            }
        });

        // Set initial FavTable
        refreshFavTable();

        // Refresh Fav table
        imageViewRefresh = (ImageView) findViewById(R.id.imageViewRefresh);
        imageViewRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshFavTable();
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
                                    //Toast.makeText(context,"refresh",Toast.LENGTH_SHORT).show();
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
                //Toast.makeText(context,"check state "+Boolean.toString(isChecked),Toast.LENGTH_SHORT).show();
                if(isChecked) {
                    threadAutoRefresh.start();
                }
                else {
                    threadAutoRefresh.interrupt();
                    
                    //threadAutoRefresh.setThread(null);
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
                                                //Toast.makeText(context,"refresh",Toast.LENGTH_SHORT).show();
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
                }
            }
        });


        // Setting for Sort By
        spinnerSortBy = (Spinner) findViewById(R.id.spinnerSortBy);
        arrayAdapterSortBy = new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.SortByArray)) {
            @Override
            public boolean isEnabled(int position) {
                if (position==0){
                    return false;
                }
                return true;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                // TODO Auto-generated method stub
                View mView = super.getDropDownView(position, convertView, parent);
                TextView mTextView = (TextView) mView;
                if (position == 0) {
                    mTextView.setTextColor(Color.GRAY);
                } else {
                    mTextView.setTextColor(Color.BLACK);
                }
                return mView;
            }
        };
        arrayAdapterSortBy.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSortBy.setAdapter(arrayAdapterSortBy);

        spinnerSortBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sortBySelect = spinnerSortBy.getSelectedItem().toString();
                String orderSelect = spinnerOrder.getSelectedItem().toString();
                if (!orderSelect.equals("Order") ){
                    favInfoList=sortFavInfoList(favInfoList,sortBySelect,orderSelect);
                    FavAdapter favAdapter = new FavAdapter(context, R.layout.fav_row,favInfoList);
                    listViewFav.setAdapter(favAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Setting for order
        spinnerOrder = (Spinner) findViewById(R.id.spinnerOrder);
        arrayAdapterOrder = new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.OrderArray)) {
            @Override
            public boolean isEnabled(int position) {
                if (position==0){
                    return false;
                }
                return true;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                // TODO Auto-generated method stub
                View mView = super.getDropDownView(position, convertView, parent);
                TextView mTextView = (TextView) mView;
                if (position == 0) {
                    mTextView.setTextColor(Color.GRAY);
                } else {
                    mTextView.setTextColor(Color.BLACK);
                }
                return mView;
            }
        };
        arrayAdapterOrder.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrder.setAdapter(arrayAdapterOrder);
        spinnerOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sortBySelect = spinnerSortBy.getSelectedItem().toString();
                String orderSelect = spinnerOrder.getSelectedItem().toString();
                if (!sortBySelect.equals("Sort By") ){
                    favInfoList=sortFavInfoList(favInfoList,sortBySelect,orderSelect);
                    FavAdapter favAdapter = new FavAdapter(context, R.layout.fav_row,favInfoList);
                    listViewFav.setAdapter(favAdapter);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    } // end of onCreate method

    // Create context manu for long press listview of favorite, pop up automatically
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if(v.getId()==R.id.ListViewFav ){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle("Remove from Favorites?");
            String[] menuItems={"Yes","No"};
            for(int i=0;i<menuItems.length;i++) {
                menu.add(Menu.NONE,i,i,menuItems[i]);
            }

        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems={"Yes","No"};
        String menuItemName = menuItems[menuItemIndex];
        favSymbolList = getCurrFavList(context);
        String listItemName = favInfoList.get(info.position).getSymbolName();

        if(menuItemName.equals("Yes")){
            favSymbolList = getCurrFavList(context);
            favSymbolList.remove(listItemName);
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("FAVSYMBOL", toJson(favSymbolList)).apply();
            refreshFavTable();

            // if no symbol in favSymbolList
        }

        //Toast.makeText(context,"Select "+listItemName+" to "+menuItemName,Toast.LENGTH_SHORT).show();
        //text.setText(String.format("Selected %s for item %s", menuItemName, listItemName));
        return true;
    }

    // RefreshFavTable
    private void refreshFavTable() {
        // Fav refresh and load when view is create
        // Fav load progressBar
        //spinnerFavLoading = (ProgressBar) findViewById(R.id.progressLoadFav);
        spinnerFavLoading.setVisibility(View.VISIBLE);
        // Fav ListView
//        listViewFav = (ListView) findViewById(R.id.ListViewFav);



        favInfoList = new ArrayList<FavoriteSymbol>();
        // Get current all fav symbol name
//        favSymbolList = FavSingleton.getInstance().getFavList();
        favSymbolList = getCurrFavList(context);
        numFavReqDone=0;
        // Get detail data, store in favInfoList
        for (int i=0; i<favSymbolList.size(); i++) {
            upDateSymbolInfoList(favSymbolList.get(i),context);
        }
        //        favInfoList.add(new FavoriteSymbol("AAPL","5","a","a","a"));
        //        favInfoList.add(new FavoriteSymbol("B","5","a","a","a"));
        //        favInfoList.add(new FavoriteSymbol("CL","5","a","a","a"));
        if(favSymbolList.size() == 0){
            spinnerFavLoading.setVisibility(View.INVISIBLE);
            FavAdapter favAdapter = new FavAdapter(context, R.layout.fav_row,favInfoList);
            listViewFav.setAdapter(favAdapter);
            
        }
        else if(numFavReqDone == favSymbolList.size()) {
            String sortBySelect = spinnerSortBy.getSelectedItem().toString();
            String orderSelect = spinnerOrder.getSelectedItem().toString();
            if(!sortBySelect.equals("Sort By") && !orderSelect.equals("Order")) {
                favInfoList=sortFavInfoList(favInfoList,sortBySelect,orderSelect);
            } else {
                favInfoList=sortFavInfoList(favInfoList,"Default","Ascending");
            }

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
//                            String timestamp = Long.toString(new Date().getTime());
                            int timestamp = favSymbolList.indexOf(symbol);
                            // Set change cell in table
                            String change = response.getString("change");
                            String changePercent = response.getString("change percent");
                            favInfoList.add(new FavoriteSymbol(symbol,price,timestamp,change,changePercent));
                            numFavReqDone++;

                            // End of loading
                            if(numFavReqDone == favSymbolList.size()) {

                                String sortBySelect = spinnerSortBy.getSelectedItem().toString();
                                String orderSelect = spinnerOrder.getSelectedItem().toString();
                                if(!sortBySelect.equals("Sort By") && !orderSelect.equals("Order")) {
                                    favInfoList=sortFavInfoList(favInfoList,sortBySelect,orderSelect);
                                } else {
                                    favInfoList=sortFavInfoList(favInfoList,"Default","Ascending");
                                }
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
                        Toast.makeText(context, "Failed to refresh "+symbol+" info.",
                                Toast.LENGTH_SHORT).show();
                        //textViewErrorMain.setVisibility(View.VISIBLE);

                        // End of loading
                        if(numFavReqDone == favSymbolList.size()) {
                            String sortBySelect = spinnerSortBy.getSelectedItem().toString();
                            String orderSelect = spinnerOrder.getSelectedItem().toString();
                            if(!sortBySelect.equals("Sort By") && !orderSelect.equals("Order")) {
                                favInfoList=sortFavInfoList(favInfoList,sortBySelect,orderSelect);
                            } else {
                                favInfoList=sortFavInfoList(favInfoList,"Default","Ascending");
                            }
                            FavAdapter favAdapter = new FavAdapter(context, R.layout.fav_row,favInfoList);
                            listViewFav.setAdapter(favAdapter);
                            spinnerFavLoading.setVisibility(View.INVISIBLE);
                        }
                    }
                });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                0,
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

            // interrupt autorefresh thread
            threadAutoRefresh.interrupt();

            //threadAutoRefresh.setThread(null);
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
                                        //Toast.makeText(context,"refresh",Toast.LENGTH_SHORT).show();
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

    public static ArrayList<String> getCurrFavList(Context context) {
        String favString = PreferenceManager.getDefaultSharedPreferences(context).getString("FAVSYMBOL", "");
        ArrayList<String> favList=new ArrayList<>();
        if (favString!=""){

            favList = (ArrayList<String>) fromJson(favString,
                    new TypeToken<ArrayList<String>>() {
                    }.getType());
        }

        return favList;
    }

    private ArrayList<FavoriteSymbol> sortFavInfoList(ArrayList<FavoriteSymbol> favInfoList,
                                                      String sortBy, String order) {

        if (sortBy.equals("Price")){
            Collections.sort(favInfoList,new Comparator<FavoriteSymbol>() {
                @Override
                public int compare(FavoriteSymbol s1,FavoriteSymbol s2) {
                    if(s1.getPriceFloat()-s2.getPriceFloat()>=0) {
                        return 1;
                    }
                    else {
                        return -1;
                    }
                }
            });
            if(order.equals("Descending")){
                Collections.reverse(favInfoList);
            }
        }

        if (sortBy.equals("Change(%)")){
            Collections.sort(favInfoList,new Comparator<FavoriteSymbol>() {
                @Override
                public int compare(FavoriteSymbol s1,FavoriteSymbol s2) {
                    if(s1.getChangePercentFloat()-s2.getChangePercentFloat()>=0) {
                        return 1;
                    }
                    else {
                        return -1;
                    }
                }
            });
            if(order.equals("Descending")){
                Collections.reverse(favInfoList);
            }
        }

        if (sortBy.equals("Change")){
            Collections.sort(favInfoList,new Comparator<FavoriteSymbol>() {
                @Override
                public int compare(FavoriteSymbol s1,FavoriteSymbol s2) {
                    if(s1.getChangeFloat()-s2.getChangeFloat()>=0) {
                        return 1;
                    }
                    else {
                        return -1;
                    }
                }
            });
            if(order.equals("Descending")){
                Collections.reverse(favInfoList);
            }
        }

        if (sortBy.equals("Symbol")){
            Collections.sort(favInfoList,new Comparator<FavoriteSymbol>() {
                @Override
                public int compare(FavoriteSymbol s1,FavoriteSymbol s2) {
                    return s1.getSymbolName().compareTo(s2.getSymbolName());
                }
            });
            if(order.equals("Descending")){
                Collections.reverse(favInfoList);
            }
        }

        if (sortBy.equals("Default")){
            Collections.sort(favInfoList,new Comparator<FavoriteSymbol>() {
                @Override
                public int compare(FavoriteSymbol s1,FavoriteSymbol s2) {
                    if(s1.getTimeStampt()-s2.getTimeStampt()>=0) {
                        return 1;
                    }
                    else {
                        return -1;
                    }
                }
            });
            if(order.equals("Descending")){
                Collections.reverse(favInfoList);
            }
        }
        return favInfoList;
    }
}
