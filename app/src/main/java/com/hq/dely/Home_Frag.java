package com.hq.dely;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by Korir on 2/16/2018.
 */

public class Home_Frag extends Fragment implements View.OnClickListener{
    ViewPager vphome;
    List<getimages> imagelist;

    CircleIndicator indicator;
    ProgressBar vppbar;
    TextView tvvphone;

    RecyclerView bestrv;
    List<getBestDetails> bestlist;

    GridView homgrid;
    List<getSpecials> speclialslist;

    private static int currentPage = 0;
    boolean net = true;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (net){
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        tvvphone = (TextView) rootView.findViewById(R.id.tvvphone);
        vphome = (ViewPager) rootView.findViewById(R.id.vphome);
        indicator = (CircleIndicator) rootView.findViewById(R.id.indicator);

        //vphome
        imagelist = new ArrayList<>();
        vppbar = (ProgressBar) rootView.findViewById(R.id.vppbar);
        //specials
        speclialslist = new ArrayList<>();
        homgrid = (GridView) rootView.findViewById(R.id.homgrid);
        homgrid.setFocusable(false);
        //bestsales
        bestlist= new ArrayList<>();
        bestrv = (RecyclerView) rootView.findViewById(R.id.rvbest);


        tvvphone.setOnClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        bestrv.setLayoutManager(layoutManager);
        bestrv.setHasFixedSize(true);
        bestrv.setFocusable(false);
        loadBest();
        getSpecialsItems();
        loadImages();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new vpTimer(),2000,5000);

        return rootView;}
        else {
            View rootView = inflater.inflate(R.layout.fraghomenet, container, false);
            Button btnonet = (Button) rootView.findViewById(R.id.btnonet);

            btnonet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().recreate();
                }
            });
            return rootView;
        }
    }


    //implementing onclick
    @Override
    public void onClick(View v) {
        if(v == tvvphone){
            loadImages();
        }
    }

    //Home automatic viewpager
    //vpvolley
    private void loadImages() {
        final String CO_ROOT_URL = "https://"+getResources().getString(R.string.url)+"/slider.php";
        vppbar.setVisibility(View.VISIBLE);
        StringRequest sRequest = new StringRequest(Request.Method.GET, CO_ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        vppbar.setVisibility(View.INVISIBLE);
                        tvvphone.setVisibility(View.INVISIBLE);
                        try {
                            JSONArray json = new JSONArray(response);
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject jsonObject = json.getJSONObject(i);

                                int partid = jsonObject.getInt("partid");
                                String part = jsonObject.getString("part");
                                String image = jsonObject.getString("images");

                                getimages get = new getimages(partid,part,image);
                                imagelist.add(get);
                            }


                            VpAdapter vpAdapter = new VpAdapter(imagelist);
                            vphome.setAdapter(vpAdapter);
                            indicator.setViewPager(vphome);

                            vpAdapter.registerDataSetObserver(indicator.getDataSetObserver());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        vppbar.setVisibility(View.INVISIBLE);
                        tvvphone.setVisibility(View.VISIBLE);
                    }
                });
        Singleton.getmInstance(getActivity()).addToRequestQueue(sRequest);
    }

    //vpTimer
    public class vpTimer extends TimerTask{
        @Override
        public void run() {
            vphome.post(new Runnable() {
                @Override
                public void run() {
                    vphome.setCurrentItem((vphome.getCurrentItem() + 1) % 6, false);
                }
            });
        }
    }

    //vpAdapter
    class VpAdapter extends PagerAdapter{

        private LayoutInflater layoutinflate;
        private List<getimages> imagelist;


        public VpAdapter(List<getimages> imagelist) {
            this.imagelist = imagelist;
        }

        @Override
        public int getCount() {
            return imagelist.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            final getimages get = imagelist.get(position);

            layoutinflate = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
            View view = layoutinflate.inflate(R.layout.homevpimage,null);
            ImageView ivhomevp = (ImageView) view.findViewById(R.id.ivhomevp);
            ivhomevp.setScaleType(ImageView.ScaleType.FIT_XY);

            Glide.with(getActivity())
                    .load(get.getImage())
                    .into(ivhomevp);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(position != -1){
                        Intent i = new Intent(getActivity(),Menulist.class);
                        i.putExtra("partner",get.getPartid());
                        i.putExtra("title",get.getImagename());
                        startActivity(i);
                }
            }});

            container.addView(view, 0);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }
    }

    //vpGetter
    class getimages{
        int partid;
        String part,image;

        public getimages(int partid,String part,String image) {
            this.partid = partid;
            this.part = part;
            this.image = image;
        }
        public int getPartid() {
            return partid;
        }

        public String getImagename() {
            return part;
        }

        public String getImage() {
            return image;
        }
    }

    //Home specials gridview
    //volley
    public void getSpecialsItems(){
        final String CO_ROOT_URL = "https://"+getResources().getString(R.string.url)+"/specials.php";

        StringRequest sRequest = new StringRequest(Request.Method.GET, CO_ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray json = new JSONArray(response);
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject jsonObject = json.getJSONObject(i);

                                int itemId = jsonObject.getInt("Item_id");
                                String itemName = jsonObject.getString("Item_name");
                                int itemPrice = jsonObject.getInt("Item_price");
                                String Sdesc = jsonObject.getString("part");
                                String Simage = jsonObject.getString("specials");

                                getSpecials spec = new getSpecials(itemId,itemName,itemPrice,Sdesc,Simage);
                                speclialslist.add(spec);
                            }
                            gvAdapter gv = new gvAdapter(speclialslist);
                            homgrid.setAdapter(gv);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "No Gridview images ", Toast.LENGTH_SHORT).show();
                    }
                });
        Singleton.getmInstance(getActivity()).addToRequestQueue(sRequest);
    }

    //Grid view Adapter
    class gvAdapter extends BaseAdapter{
        LayoutInflater gridinflater;
        private List<getSpecials> speclialslist;

        public gvAdapter(List<getSpecials> speclialslist) {
            this.speclialslist = speclialslist;
        }

        @Override
        public int getCount() {
            return speclialslist.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            final getSpecials get = speclialslist.get(position);

            gridinflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
            View view = gridinflater.inflate(R.layout.homegridview,null);
            ImageView ivgridview = (ImageView) view.findViewById(R.id.ivgridview);
            TextView tvgridview = (TextView) view.findViewById(R.id.tvgridview);
            ivgridview.setScaleType(ImageView.ScaleType.FIT_XY);

            tvgridview.setText("KSH "+get.getItemPrice());
            Glide.with(getActivity())
                    .load(get.geSpecialsimages())
                    .into(ivgridview);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    myDbHelper helper = new myDbHelper(getActivity());
                    dbOperations operate = new dbOperations(helper);
                    boolean checkcart = operate.checkCart(get.getItemId());

                    if(checkcart){
                        operate.removeFromCart(get.getItemId());
                        ((addOrRemove)getActivity()).onRemoveProduct();
                        Toast.makeText(getActivity(), get.getItemName()+" removed from cart", Toast.LENGTH_SHORT).show();}
                    else{
                        operate.addCartItem(get.getItemId(),get.getItemName(),get.getItemPrice(),get.getSpecialSdesc());
                        ((addOrRemove)getActivity()).onAddProduct();
                        Toast.makeText(getActivity(), get.getItemName()+" added to cart", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            return view;
        }
    }

    //gridGetter
    class getSpecials{
        String Sdesc,Simage,itemName;
        int itemId,itemPrice;

        public getSpecials(int itemId,String itemName,int itemPrice,String Sdesc,String Simage) {
            this.itemId= itemId;
            this.itemName = itemName;
            this.itemPrice = itemPrice;
            this.Sdesc = Sdesc;
            this.Simage = Simage;
        }

        public String getItemName() {
            return itemName;
        }

        public int getItemId() {
            return itemId;
        }

        public int getItemPrice() {
            return itemPrice;
        }

        public String getSpecialSdesc() {
            return Sdesc;
        }

        public String geSpecialsimages() {
            return Simage;
        }
    }


    //Home bestsales
    //Volley
    public void loadBest() {
        final String CO_ROOT_URL = "https://"+getResources().getString(R.string.url)+"/bestsales.php";

        StringRequest sRequest = new StringRequest(Request.Method.GET, CO_ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray json = new JSONArray(response);
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject jsonObject = json.getJSONObject(i);

                                int itemId = jsonObject.getInt("Item_id");
                                String itemName = jsonObject.getString("Item_name");
                                int itemPrice = jsonObject.getInt("Item_price");
                                String bestpatner = jsonObject.getString("part");
                                String bestimages = jsonObject.getString("best_sales");

                                getBestDetails getbest = new getBestDetails(itemId,itemName,itemPrice,bestpatner, bestimages);
                                bestlist.add(getbest);
                            }
                            homeRvAdapter adapter = new homeRvAdapter(bestlist,getActivity());
                            bestrv.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        Singleton.getmInstance(getActivity()).addToRequestQueue(sRequest);
    }

    //Adapter
    class homeRvAdapter extends RecyclerView.Adapter<homeRvAdapter.rvHolder>{
        private List<getBestDetails> bestlist;
        Context ctx;

        public homeRvAdapter(List<getBestDetails> bestlist, Context ctx) {
            this.bestlist = bestlist;
            this.ctx = ctx;
        }


        @Override
        public rvHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View itemview = inflater.inflate(R.layout.bestsalecardview,parent,false);

            return new rvHolder(itemview);
        }

        @Override
        public void onBindViewHolder(homeRvAdapter.rvHolder holder, int position) {
            final getBestDetails getbest = bestlist.get(position);
            holder.besttv.setText("KSH "+getbest.getItemPrice());
            Glide.with(getActivity())
                    .load(getbest.getBestimages())
                    .into(holder.ivbest);
            holder.setclicker(new rvListener() {
                @Override
                public void onClick(View view, int position) {
                    myDbHelper helper = new myDbHelper(getActivity());
                    dbOperations operate = new dbOperations(helper);
                    boolean checkcart = operate.checkCart(getbest.getItemId());

                    if(checkcart){
//                        ivitemcart.setImageResource(R.drawable.cartno);
                        operate.removeFromCart(getbest.getItemId());
                        ((addOrRemove)getActivity()).onRemoveProduct();
                        Toast.makeText(getActivity(), getbest.getItemName()+" removed from cart", Toast.LENGTH_SHORT).show();}
                    else{
//                        ivitemcart.setImageResource(R.drawable.cartyes);
                        operate.addCartItem(getbest.getItemId(),getbest.getItemName(),getbest.getItemPrice(),getbest.getPartnername());
                        ((addOrRemove)getActivity()).onAddProduct();
                        Toast.makeText(getActivity(), getbest.getItemName()+" added to cart", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return bestlist.size();
        }



        //Holder
        class rvHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            ImageView ivbest;
            TextView besttv;
            rvListener click;

            rvHolder(View itemView) {
                super(itemView);
                ivbest = (ImageView) itemView.findViewById(R.id.ivbest);
                besttv = (TextView) itemView.findViewById(R.id.besttv);
                ivbest.setScaleType(ImageView.ScaleType.FIT_XY);

                itemView.setOnClickListener(this);
            }

            void setclicker(rvListener click){
                this.click =click;
            }

            @Override
            public void onClick(View v) {
                click.onClick(v,getAdapterPosition());
            }
        }
    }

    //getitemcless
    class getBestDetails {
        private String bestpatner,bestimages,itemName;
        int itemId,itemPrice;

        public getBestDetails(int itemId,String itemName,int itemPrice,String bestpatner, String bestimages) {
            this.itemId= itemId;
            this.itemName = itemName;
            this.itemPrice = itemPrice;
            this.bestpatner = bestpatner;
            this.bestimages= bestimages;
        }

        public String getItemName() {
            return itemName;
        }

        public int getItemId() {
            return itemId;
        }

        public int getItemPrice() {
            return itemPrice;
        }

        public String getPartnername() {
            return bestpatner;
        }

        public String getBestimages() {
            return bestimages;
        }
    }
}

