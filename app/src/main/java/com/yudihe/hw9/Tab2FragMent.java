package com.yudihe.hw9;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * Created by heyudi on 11/19/17.
 */

public class Tab2FragMent extends android.support.v4.app.Fragment {
    private static final String TAG = "Tab2FragMent";
    private Button btnTEST2;

    // webSettings
    WebSettings webSettings;

    // Stock symbol name, get from StockActivity;
    private String symbol;

    // WebView for charts
    private WebView webViewCharts;

    // Progress bar
    private ProgressBar progressBar;

    // Activity
    private Context activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2_fragment,container,false);

        // Get symbol name
        symbol = ((StockActivity)getActivity()).getSymbol();

        // Progress bar
        progressBar = (ProgressBar) view.findViewById(R.id.progressBarHis);
        progressBar.setVisibility(View.VISIBLE);

        //
        activity = getActivity();


        // WebView for charts
        webViewCharts = (WebView) view.findViewById(R.id.webViewChartHis);
        drawHisCharts(symbol);


        return  view;

    }

    private void drawHisCharts(final String symbol){
        webSettings = webViewCharts.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //webViewCharts.addJavascriptInterface(new WebAppInterface(getContext()), "Android");

        // ProgressBar interface
        webViewCharts.addJavascriptInterface(new ShowProgressInterface(progressBar), "ProgressInterface");

        webViewCharts.setWebViewClient(new WebViewClient());



        webViewCharts.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            public void onPageFinished(WebView view, String url){
                webViewCharts.loadUrl("javascript:drawHisCharts('" + symbol+ "')");
                //progressBar.setVisibility(View.GONE);
            }
        });
        webViewCharts.loadUrl(GlobalVariables.LOCALBASE_URL+ "/hw9/hw9-his.html");
        //progressBar.setVisibility(View.INVISIBLE);
    }

    private class ShowProgressInterface {
        ProgressBar progressBar;


        ShowProgressInterface(ProgressBar progressBar) {
            this.progressBar = progressBar;
        }

        @JavascriptInterface
        public void showProgress() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @JavascriptInterface
        public void hideProgress() {
            // change UI on ui thread
            ((Activity)activity).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }
}
