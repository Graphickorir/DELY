package com.hq.dely;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

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

/**
 * Created by Korir on 2/16/2018.
 */

public class List_Frag extends Fragment {
    RecyclerView recyclerView;
    List<getlistdetails> listrv;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.listrv);
        listrv = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        loadCompanies();

        return rootView;
    }

    //Volley
    public void loadCompanies() {
        final String CO_ROOT_URL = "http://"+getResources().getString(R.string.url)+"/korirphp/listrv.php";
        StringRequest sRequest = new StringRequest(Request.Method.POST, CO_ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray json = new JSONArray(response);
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject jsonObject = json.getJSONObject(i);

                                int id = jsonObject.getInt("Id_partner");
                                String restname = jsonObject.getString("partner");
                                String Address = jsonObject.getString("Address");
                                String rating = jsonObject.getString("Rating");
                                String restlogo = jsonObject.getString("logo");

                                getlistdetails getdetails = new getlistdetails(id,restname,Address,rating,restlogo);
                                listrv.add(getdetails);
                            }
                            listRvAdapter adapter = new listRvAdapter(listrv,getActivity());
                            recyclerView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                        alert.setMessage("Failed To Connect!")
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        loadCompanies();
                                    }
                                })
                                .setCancelable(false)
                                .create()
                                .show();
                    }
                });
        Singleton.getmInstance(getActivity()).addToRequestQueue(sRequest);
    }

    //Adapter
    class listRvAdapter extends RecyclerView.Adapter<listRvAdapter.rvHolder> {
        private List<getlistdetails> listrv;
        Context ctx;

        private listRvAdapter(List<getlistdetails> listrv, Context ctx) {
            this.listrv = listrv;
            this.ctx = ctx;
        }


        @Override
        public rvHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View itemview = inflater.inflate(R.layout.customlistrv,parent,false);

            return new rvHolder(itemview);
        }

        @Override
        public void onBindViewHolder(listRvAdapter.rvHolder holder, int position) {
            final getlistdetails getdetails = listrv.get(position);
            final int partner = getdetails.getId();
            final String title = getdetails.getRestname();

            holder.tv1listrv.setText(getdetails.getRestname());
            holder.tv2listrv.setText(getdetails.getAddress());
            holder.rblistrv.setRating(getdetails.getRating());

            Glide.with(getActivity())
                    .load(getdetails.getRestlogo())
                    .into(holder.ivlistrv);

            holder.setclicker(new rvListener() {
                @Override
                public void onClick(View view, int position) {
                    Intent intent =new Intent(getActivity(), Menulist.class);
                    intent.putExtra("partner",partner+"");
                    intent.putExtra("title", title);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return listrv.size();
        }

        //Holder
        class rvHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            TextView tv1listrv, tv2listrv;
            ImageView ivlistrv;
            rvListener click;
            RatingBar rblistrv;

            private rvHolder(View itemView) {
                super(itemView);

                tv1listrv = (TextView) itemView.findViewById(R.id.tv1listrv);
                tv2listrv = (TextView) itemView.findViewById(R.id.tv2listrv);
                ivlistrv = (ImageView) itemView.findViewById(R.id.ivlistrv);
                ivlistrv.setScaleType(ImageView.ScaleType.FIT_XY);
                rblistrv = (RatingBar)  itemView.findViewById(R.id.rblistrv);

                itemView.setOnClickListener(this);
            }
            private void setclicker(rvListener click){
                this.click =click;
            }

            @Override
            public void onClick(View v) {
                click.onClick(v,getAdapterPosition());
            }
        }
    }

    //getitemcless
    class getlistdetails {
        private int id;
        private String restname,restlogo,address;
        private float rating;


        private getlistdetails(int id,String restname, String address,String rating, String restlogo) {
            this.id=id;
            this.restname = restname;
            this.address = address;
            this.rating = Float.parseFloat(rating);
            this.restlogo= restlogo;
        }

        public int getId() {
            return id;
        }

        public String getRestname() {
            return restname;
        }

        public String getAddress() {
            return address;
        }

        public float getRating() {
            return rating;
        }

        public String getRestlogo() {
            return restlogo;
        }
    }
}
