package com.hq.dely;


import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by Korir on 2/16/2018.
 */

public class Home_Frag extends Fragment implements View.OnClickListener{
    Button bt,btl;
    ViewPager vphome;
    List<getimages> imagelist;
    CircleIndicator indicator;
    ProgressBar vppbar;
    TextView tvvphome;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        bt = (Button) rootView.findViewById(R.id.bthome);
        btl = (Button) rootView.findViewById(R.id.btloghome);
        tvvphome = (TextView) rootView.findViewById(R.id.tvvphome);
        vphome = (ViewPager) rootView.findViewById(R.id.vphome);
        indicator = (CircleIndicator) rootView.findViewById(R.id.indicator);
        vppbar = (ProgressBar) rootView.findViewById(R.id.vppbar);
        imagelist = new ArrayList<>();

        loadImages();

        tvvphome.setOnClickListener(this);
        bt.setOnClickListener(this);
        btl.setOnClickListener(this);

        return rootView;
    }

    private void loadImages() {
        final String CO_ROOT_URL = "http://192.168.56.1/korirphp/slider.php";
        vppbar.setVisibility(View.VISIBLE);
        StringRequest sRequest = new StringRequest(Request.Method.GET, CO_ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        vppbar.setVisibility(View.INVISIBLE);
                        tvvphome.setVisibility(View.INVISIBLE);
                        try {
                            JSONArray json = new JSONArray(response);
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject jsonObject = json.getJSONObject(i);

                                String imagename = jsonObject.getString("imagename");
                                String image = jsonObject.getString("images");

                                getimages get = new getimages(imagename,image);
                                imagelist.add(get);
                            }
                            VpAdapter vpAdapter = new VpAdapter(imagelist);
                            vphome.setAdapter(vpAdapter);
                            indicator.setViewPager(vphome);

                            Timer timer = new Timer();
                            timer.scheduleAtFixedRate(new vpTimer(),2000,5000);

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
                        tvvphome.setVisibility(View.VISIBLE);
                    }
                });
        Singleton.getmInstance(getActivity()).addToRequestQueue(sRequest);
    }



    //Timer
    public class vpTimer extends TimerTask{
        @Override
        public void run() {
            vphome.post(new Runnable() {
                @Override
                public void run() {
                    vphome.setCurrentItem((vphome.getCurrentItem() + 1) % imagelist.size(), false);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        if(v == bt){
            SharedPrefs.getmInstance(getActivity()).userlogout();
        }else if(v == btl){
            Intent i = new Intent(getActivity(),Login.class);
            getActivity().startActivity(i);
        }else if(v == tvvphome){
            loadImages();
        }
    }

    //Adapter
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
                        Toast.makeText(getActivity(),"clicked \t"+ get.getImagename(),Toast.LENGTH_LONG).show();
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

    //Getter
    class getimages{
        String imagename,image;

        public getimages(String imagename,String image) {
            this.imagename = imagename;
            this.image = image;
        }

        public String getImagename() {
            return imagename;
        }

        public String getImage() {
            return image;
        }
    }
}
