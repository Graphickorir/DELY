package com.hq.dely;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Toast;

public class Home extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    static boolean choice,changeicon;
    static String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (SharedPrefs.getmInstance(this).UserIsLoged())
            changeicon = true;
        else
            changeicon = false;

        name = this.getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE).getString("Username",null);
        Intent i = getIntent();
        choice = i.getBooleanExtra("choice", true);

        if(!choice){
            SharedPrefs.getmInstance(this).userlogout();}


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if(position == 2)
                    mViewPager.getAdapter().notifyDataSetChanged();
            }
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    //Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_homefrag, menu);

        if(changeicon){
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setIcon(R.drawable.male);
            menu.getItem(1).setEnabled(false);}
        return true;
    }

    //Adapter
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    Home_Frag t1=new Home_Frag();
                    return t1;
                case 1:
                    List_Frag t2=new List_Frag();
                    return t2;
                case 2:
                    Fav_Frag t4=new Fav_Frag();
                    return t4;
                case 3:
                    Profile_Frag t3=new Profile_Frag();
                    return t3;
                default:
                    return null;
            }
        }

        @Override
        public int getItemPosition(Object object) {
            return super.POSITION_NONE;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "HOME";
                case 1:
                    return "LIST";
                case 2:
                    return "FAVOURITES";
                case 3:
                    return "PROFILE";
            }
            return null;
        }
    }
}
