package com.yudihe.hw9;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
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

import java.util.ArrayList;

/**
 * Created by heyudi on 11/19/17.
 */

public class Tab3FragMent extends android.support.v4.app.Fragment {
    private static final String TAG = "Tab3FragMent";
    private Button btnTEST3;
    private RequestQueue requestQueue;
    private ArrayList<News> newsList;
    private ArrayList<String> linkUrls;

    // Textview for test
    //TextView textViewTest;

    // ListView for news
    ListView listViewNews;

    // Stock symbol name, get from StockActivity;
    private String symbol;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab3_fragment,container,false);

        // Get symbol name
        symbol = ((StockActivity)getActivity()).getSymbol();

        // TextView for test
        //textViewTest = (TextView) view.findViewById(R.id.textViewTest);

        // ListView for news
        listViewNews = (ListView) view.findViewById(R.id.listViewNews);

        // newsList
        newsList = new ArrayList<News>();

        // link urls
        linkUrls = new ArrayList<String>();

        // Request queue
        requestQueue = Volley.newRequestQueue(getActivity()); // 'this' is the Context

        String url = GlobalVariables.PHP_URL+"?newssymbol="+symbol;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        newsList.clear();
                        linkUrls.clear();
                        //textViewTest.setText(response.toString());
                        try {
                            for(int i = 0; i < response.length(); i++){
                                JSONObject jresponse = response.getJSONObject(i);
                                String title = jresponse.getString("title");
                                String link = jresponse.getString("link");
                                String pubDate = jresponse.getString("pubDate");
                                String authorName = jresponse.getString("author_name");
                                newsList.add(new News(title, link, pubDate, authorName));
                                linkUrls.add(link);
                            }
                            NewsAdapter adapter = new NewsAdapter(getActivity(),R.layout.news_row,newsList);
                            listViewNews.setAdapter(adapter);

                            listViewNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                    Uri uri = Uri.parse(linkUrls.get(position));
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(intent);

                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "News request failed!", Toast.LENGTH_SHORT).show();
                    }
                });
        //add request to queue
        requestQueue.add(jsonArrayRequest);


//        btnTEST3 = (Button) view.findViewById(R.id.btnTEST3);
//        btnTEST3.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Toast.makeText(getActivity(), "symbol is "+symbol, Toast.LENGTH_SHORT).show();
//            }
//        });
        return  view;

    }
}
