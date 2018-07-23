package com.hq.dely;


import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Menulist extends AppCompatActivity implements addOrRemove,Toolbar.OnMenuItemClickListener{
    ListView bevitemlv;
    ArrayList<getBevList> bevitems;


    ListView mealsitemlv;
    ArrayList<getMealsList> mealsitems;

    ListView comboitemlv;
    ArrayList<getComboList> comboitems;

    public int partner;
    String title;

    long count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menulist);
        partner= getIntent().getIntExtra("partner",0);
        title = getIntent().getStringExtra("title");
        setTitle(title+" "+"Menu");

        myDbHelper helper = new myDbHelper(this);
        dbOperations operate = new dbOperations(helper);
        count = operate.getProfilesCount();

        bevitemlv = (ListView) findViewById(R.id.bevitemlv);
        bevitems = new ArrayList<>();
        loadbevitems();

        mealsitemlv = (ListView) findViewById(R.id.mealsitemlv);
        mealsitems = new ArrayList<>();
        loadmealsitems();

        comboitemlv = (ListView) findViewById(R.id.comboitemlv);
        comboitems = new ArrayList<>();
        loadcomboitems();

    }

    //Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_menulist, menu);
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

        MenuItem menuItem = menu.findItem(R.id.menulistcart);
        menuItem.setIcon(Converter.convertLayoutToImage(this,count,R.drawable.carticon));

        return super.onPrepareOptionsMenu(menu);
    }

    //menu clicked
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menulistcart:
                break;
        }
        return false;
    }




    public static class Utility {

        public static void setListViewHeightBasedOnChildren(ListView listView) {
            ListAdapter listAdapter = listView.getAdapter();
            if (listAdapter == null) {
                return;
            }

            int totalHeight = 0;
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
            for (int i = 0; i < listAdapter.getCount(); i++) {
                View listItem = listAdapter.getView(i, null, listView);
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                totalHeight += listItem.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
            listView.setLayoutParams(params);
            listView.requestLayout();
        }
    }

    //Bevarages Volley 192.168.56.1
    public void loadbevitems() {
        final String CO_ROOT_URL = "http://"+getResources().getString(R.string.url)+"/korirphp/listitems.php";
        final int cat = 1;
        StringRequest sRequest = new StringRequest(Request.Method.POST, CO_ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray json = new JSONArray(response);
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject jsonObject = json.getJSONObject(i);

                                int Item_id = jsonObject.getInt("Item_id");
                                String Item_name = jsonObject.getString("Item_name");
                                int Item_price = jsonObject.getInt("Item_price");

                                getBevList getitems = new getBevList(Item_id,Item_name,Item_price);
                                bevitems.add(getitems);
                            }

                            BeveragesAdapter adapter = new BeveragesAdapter(Menulist.this,bevitems,title,partner);
                            bevitemlv.setAdapter(adapter);
                            Utility.setListViewHeightBasedOnChildren(bevitemlv);
                            ((BaseAdapter) bevitemlv.getAdapter()).notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(Menulist.this);
                        alert.setMessage("Failed To Connect!")
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        loadbevitems();
                                    }
                                })
                                .setCancelable(false)
                                .create()
                                .show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Partner", partner+"");
                params.put("Item_cat",cat+"");
                return params;
            }
        };
        Singleton.getmInstance(this).addToRequestQueue(sRequest);
    }


    //Beverages getitems
    class getBevList{
        private int itemid,itemprice;
        private String itemname;

        public getBevList(int itemid,String itemname,int itemprice){
            this.itemid = itemid;
            this.itemname = itemname;
            this.itemprice = itemprice;
        }

        public int getItemid() {
            return itemid;
        }
        public String getItemname() {
            return itemname;
        }
        public int getItemprice() {
            return itemprice;
        }
    }

    //Meals Volley
    public void loadmealsitems() {
        final String CO_ROOT_URL = "http://"+getResources().getString(R.string.url)+"/korirphp/listitems.php";
        final int cat = 2;

        StringRequest sRequest = new StringRequest(Request.Method.POST, CO_ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray json = new JSONArray(response);
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject jsonObject = json.getJSONObject(i);

                                int Item_id = jsonObject.getInt("Item_id");
                                String Item_name = jsonObject.getString("Item_name");
                                int Item_price = jsonObject.getInt("Item_price");

                                getMealsList getitems = new getMealsList(Item_id,Item_name,Item_price);
                                mealsitems.add(getitems);
                            }

                            MealsAdapter adapter = new MealsAdapter(Menulist.this,mealsitems,title,partner);
                            mealsitemlv.setAdapter(adapter);
                            Utility.setListViewHeightBasedOnChildren(mealsitemlv);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(Menulist.this);
                        alert.setMessage("Failed To Connect!")
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        loadmealsitems();
                                    }
                                })
                                .setCancelable(false)
                                .create()
                                .show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Partner", partner+"");
                params.put("Item_cat",cat+"");
                return params;
            }
        };
        Singleton.getmInstance(this).addToRequestQueue(sRequest);
    }


    //meals getitems
    class getMealsList{
        private int itemid,itemprice;
        private String itemname;

        public getMealsList(int itemid, String itemname, int itemprice){
            this.itemid = itemid;
            this.itemname = itemname;
            this.itemprice = itemprice;
        }

        public int getItemid() {
            return itemid;
        }
        public String getItemname() {
            return itemname;
        }
        public int getItemprice() {
            return itemprice;
        }
    }

    //Combo Volley
    public void loadcomboitems() {
        final String CO_ROOT_URL = "http://"+getResources().getString(R.string.url)+"/korirphp/listitems.php";
        final int cat = 3;

        StringRequest sRequest = new StringRequest(Request.Method.POST, CO_ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray json = new JSONArray(response);
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject jsonObject = json.getJSONObject(i);

                                int Item_id = jsonObject.getInt("Item_id");
                                String Item_name = jsonObject.getString("Item_name");
                                int Item_price = jsonObject.getInt("Item_price");

                                getComboList getitems = new getComboList(Item_id,Item_name,Item_price);
                                comboitems.add(getitems);
                            }

                            ComboAdapter adapter = new ComboAdapter(Menulist.this,comboitems,title,partner);
                            comboitemlv.setAdapter(adapter);
                            Utility.setListViewHeightBasedOnChildren(comboitemlv);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(Menulist.this);
                        alert.setMessage("Failed To Connect!")
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        loadcomboitems();
                                    }
                                })
                                .setCancelable(false)
                                .create()
                                .show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Partner", partner+"");
                params.put("Item_cat",cat+"");
                return params;
            }
        };
        Singleton.getmInstance(this).addToRequestQueue(sRequest);
    }


    //meals getitems
    class getComboList{
        private int itemid,itemprice;
        private String itemname;

        public getComboList(int itemid, String itemname, int itemprice){
            this.itemid = itemid;
            this.itemname = itemname;
            this.itemprice = itemprice;
        }

        public int getItemid() {
            return itemid;
        }
        public String getItemname() {
            return itemname;
        }
        public int getItemprice() {
            return itemprice;
        }
    }
}
