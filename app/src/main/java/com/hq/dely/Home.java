package com.hq.dely;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.hq.dely.myDbHelper.TABLE_CART;

public class Home extends AppCompatActivity implements addOrRemove, Toolbar.OnMenuItemClickListener
        ,NavigationView.OnNavigationItemSelectedListener{

    private ViewPager mViewPager;
    static boolean choice,changeicon;
    public MenuItem menuItem,subname,subbalance;
    long count;
    String name,email,gender;
    TabLayout tabLayout;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    NavigationView navview;
    TextView navdely;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        name = this.getSharedPreferences(
                "MySharedPrefs", Context.MODE_PRIVATE).getString("Username",null);
        email = this.getSharedPreferences(
                "MySharedPrefs", Context.MODE_PRIVATE).getString("Email",null);
        gender = this.getSharedPreferences(
                "MySharedPrefs", Context.MODE_PRIVATE).getString("Gender",null);
        choice = this.getSharedPreferences(
                "MySharedPrefs", Context.MODE_PRIVATE).getBoolean("Check",true);

        if(!choice){
            SharedPrefs.getmInstance(this).userlogout();}

        myDbHelper helper = new myDbHelper(this);
        dbOperations operate = new dbOperations(helper);
        count = operate.getProfilesCount();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(this);

        drawer = (DrawerLayout) findViewById(R.id.navdrawer);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navview = (NavigationView) findViewById(R.id.navview);
        navview.setNavigationItemSelectedListener(this);
        View headerView = navview.getHeaderView(0);
        navdely = (TextView) headerView.findViewById(R.id.navdely);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Lobster.otf");
        navdely.setTypeface(custom_font);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        setIcons();

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    onAddProduct();
                    setIcons();}
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
        myDbHelper helper = new myDbHelper(this);
        dbOperations operate = new dbOperations(helper);
        count = operate.getProfilesCount();
        invalidateOptionsMenu();
    }

    @Override
    public void onRemoveProduct() {
        myDbHelper helper = new myDbHelper(this);
        dbOperations operate = new dbOperations(helper);
        count = operate.getProfilesCount();
        invalidateOptionsMenu();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (SharedPrefs.getmInstance(this).UserIsLoged())
            changeicon = true;
        else
            changeicon = false;

        if(changeicon){
            menu.getItem(1).setIcon(R.drawable.male);
            subname = menu.getItem(1).getSubMenu().getItem(0);
            subbalance = menu.getItem(1).getSubMenu().getItem(1);
        }else {
            menu.getItem(1).setIcon(R.drawable.loginicon);
            menu.getItem(1).getSubMenu().setGroupVisible(0,false);
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
                if(changeicon) {
                    checkBal();
                }else {
                    Intent i = new Intent(this,Login.class);
                    this.startActivity(i);
                }
                break;
            case R.id.cart_action:
                invalidateOptionsMenu();
                TabLayout.Tab tab = tabLayout.getTabAt(3);
                tab.select();
                break;
        }
        return false;
    }

    //volley check balance
    private void checkBal() {
        final int id = this.getSharedPreferences(
                "MySharedPrefs", Context.MODE_PRIVATE).getInt("Id",0);
            final String CO_ROOT_URL = "http://"+getResources().getString(R.string.url)+"/korirphp/getbal.php";
            StringRequest sRequest = new StringRequest(Request.Method.POST, CO_ROOT_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject json = new JSONObject(response);
                                if(json.getString("messo").equals("1")){
                                    subname.setTitle("User: "+json.getString("user"));
                                    subbalance.setTitle("Bal: KSH "+json.getInt("bal"));
                                }else if(json.getString("messo").equals("2")){
                                    subname.setTitle("User: "+name);
                                    subbalance.setTitle("Bal: KSH 0");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(Home.this, "Failed to get balance", Toast.LENGTH_SHORT).show();
                        }
                    })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("id",id+"");
                    return params;
                }
            };
            Singleton.getmInstance(Home.this).addToRequestQueue(sRequest);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.navhome) {
            TabLayout.Tab tab = tabLayout.getTabAt(0);
            tab.select();
        } else if (id == R.id.navlist) {
            TabLayout.Tab tab = tabLayout.getTabAt(1);
            tab.select();
        } else if (id == R.id.navfav) {
            TabLayout.Tab tab = tabLayout.getTabAt(2);
            tab.select();
        } else if (id == R.id.navcart) {
            TabLayout.Tab tab = tabLayout.getTabAt(3);
            tab.select();
        } else if (id == R.id.navpro) {
            TabLayout.Tab tab = tabLayout.getTabAt(4);
            tab.select();
        } else if (id == R.id.navtrans) {
            if(SharedPrefs.getmInstance(this).UserIsLoged()){
                Intent i = new Intent(this,Trans.class);
                startActivity(i);
            }else
                Toast.makeText(this, "Please Login First", Toast.LENGTH_SHORT).show();
        }else if (id == R.id.navreport) {
            String[] to = {"delycustomercare.co.ke"};
            Intent emailintent = new Intent(Intent.ACTION_SEND);
            emailintent.setType("text/plain");
            emailintent.putExtra(Intent.EXTRA_EMAIL, to);
            emailintent.putExtra(Intent.EXTRA_SUBJECT, "Report Bug");
            startActivity(emailintent);
        }else if (id == R.id.navlog) {
            SharedPrefs.getmInstance(this).userlogout();
        }

        invalidateOptionsMenu();
        drawer.invalidate();
        drawer.closeDrawer(GravityCompat.START);
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
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        onAddProduct();
        super.onResume();
    }
}

