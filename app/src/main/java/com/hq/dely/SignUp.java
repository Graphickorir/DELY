package com.hq.dely;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;

public class SignUp extends AppCompatActivity {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private static boolean checked;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0){
                    toolbar.getMenu().clear();
                    toolbar.inflateMenu(R.menu.menu_company);
                }else if (position == 1){
                    toolbar.getMenu().clear();
                    toolbar.inflateMenu(R.menu.menu_que);
                }else if (position == 2){
                    toolbar.getMenu().clear();
                    toolbar.inflateMenu(R.menu.menu_que);
                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        Intent i = getIntent();
        int tabis = i.getIntExtra("Tab", 0);
        checked = i.getBooleanExtra("cbdata", true);

        if (tabis==0){
            TabLayout.Tab tab = tabLayout.getTabAt(0);
            tab.select();
        }else if (tabis==1) {
            TabLayout.Tab tab = tabLayout.getTabAt(1);
            tab.select();
        }else if (tabis==2) {
            TabLayout.Tab tab = tabLayout.getTabAt(2);
            tab.select();}


    }

    //Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_company, menu);
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
                    company t1=new company();
                    return t1;
                case 1:
                    SecQue t2=new SecQue();
                    return t2;
                case 2:
                    PayMethod t3=new PayMethod();
                    return t3;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Company Address";
                case 1:
                    return "Security Question";
                case 2:
                    return "Payment Methods";
            }
            return null;
        }
    }

    //custom
    public static void GotoHome(Context context){
        Intent intent =new Intent(context, Home.class);
        intent.putExtra("choice",checked);
        context.startActivity(intent);
    }

    //custom
    public void selectFragment(int position){
        mViewPager.setCurrentItem(position, true);
    }
}
