package com.yudihe.hw9;

import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

public class StockActivity extends AppCompatActivity {


    public static final String TAG="StockActivity";
    private SectionPageAdapter mSectionPageAdapter;

    private ViewPager mViewPager;

    // Stock Symbol tranfered from previous page
    private String symbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        Log.d(TAG,"onCreate: starting");
        mSectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPageer(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        setupViewPageer(mViewPager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void setupViewPageer(ViewPager viewPager) {
        SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new SubPage01(),"TAB1");
        adapter.addFragment(new SubPage02(),"TAB2");
        adapter.addFragment(new SubPage03(),"TAB3");
        viewPager.setAdapter(adapter);
    }



}
