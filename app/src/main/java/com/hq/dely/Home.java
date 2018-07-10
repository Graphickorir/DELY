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
import android.view.MenuItem;
import android.widget.Toast;

import static com.hq.dely.myDbHelper.TABLE_CART;

public class Home extends AppCompatActivity implements addOrRemove, Toolbar.OnMenuItemClickListener{

    private ViewPager mViewPager;
    static boolean choice,changeicon;
    public MenuItem menuItem;
    long count;
    static String name;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        name = this.getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE).getString("Username",null);
        choice = this.getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE).getBoolean("Check",true);

        if(!choice){
            SharedPrefs.getmInstance(this).userlogout();}

        myDbHelper helper = new myDbHelper(this);
        dbOperations operate = new dbOperations(helper);
        count = operate.getProfilesCount();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(this);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        setIcons();


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 2) {
                    mViewPager.getAdapter().notifyDataSetChanged();
                    setIcons();}
                else if (position == 3){
                    mViewPager.getAdapter().notifyDataSetChanged();
                    setIcons();
                }
            }
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    private void setIcons(){
        tabLayout.getTabAt(0).setIcon(R.drawable.hometab);
        tabLayout.getTabAt(1).setIcon(R.drawable.listtab);
        tabLayout.getTabAt(2).setIcon(R.drawable.favicontab);
        tabLayout.getTabAt(3).setIcon(R.drawable.carttab);
        tabLayout.getTabAt(4).setIcon(R.drawable.usertab);
    }

    //Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_homefrag, menu);

        return true;
    }

    @Override
    public void onAddProduct() {
        ++count;
        invalidateOptionsMenu();
    }

    @Override
    public void onRemoveProduct() {
        --count;
        invalidateOptionsMenu();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (SharedPrefs.getmInstance(this).UserIsLoged())
            changeicon = true;
        else
            changeicon = false;

        if(changeicon){
            menu.getItem(0).setIcon(R.drawable.male);
            menu.getItem(0).setEnabled(false);}
        else {
            menu.getItem(0).setIcon(R.drawable.loginicon);
        }

        menuItem = menu.findItem(R.id.cart_action);
        menuItem.setIcon(Converter.convertLayoutToImage(this,count,R.drawable.carticon));

        return super.onPrepareOptionsMenu(menu);
    }

    //menu clicked
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.usericon:
                Intent i = new Intent(this,Login.class);
                this.startActivity(i);
                break;
            case R.id.cart_action:
                break;
            case R.id.logouticon:
                SharedPrefs.getmInstance(this).userlogout();
                invalidateOptionsMenu();
                break;
            case R.id.settingsicon:
                Toast.makeText(this, "settings", Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
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
                    Fav_Frag t3=new Fav_Frag();
                    return t3;
                case 3:
                    Cart_Frag t4=new Cart_Frag();
                    return t4;
                case 4:
                    Profile_Frag t5=new Profile_Frag();
                    return t5;
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
            return 5;
        }

    }

    @Override
    protected void onPostResume() {
        invalidateOptionsMenu();
        super.onPostResume();
    }
}

