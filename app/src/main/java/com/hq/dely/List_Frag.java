package com.hq.dely;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Korir on 2/16/2018.
 */

public class List_Frag extends Fragment {
    RecyclerView recyclerView;
    List<getlistdetails> listrv;

    AlertDialog.Builder custombuilder ;
    AlertDialog customalert ;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        listrv = new ArrayList<>();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.listrv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        loadPartners();

        return rootView;
    }

    //Volley
    public void loadPartners() {
        final String CO_ROOT_URL = "http://"+getResources().getString(R.string.url)+"/dely/delyapp/listrv.php";
        StringRequest sRequest = new StringRequest(Request.Method.POST, CO_ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray json = new JSONArray(response);
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject jsonObject = json.getJSONObject(i);

                                int id = jsonObject.getInt("Id_part");
                                String restname = jsonObject.getString("Partner");
                                String Address = jsonObject.getString("Address");
                                String rating = jsonObject.getString("Rating");
                                String restlogo = jsonObject.getString("Logo");

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
                                        loadPartners();
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
        public void onBindViewHolder(listRvAdapter.rvHolder holder, final int position) {
            final getlistdetails getdetails = listrv.get(position);
            final int partner = getdetails.getId();
            final String title = getdetails.getRestname();

            holder.tv1listrv.setText(getdetails.getRestname());
            holder.tv2listrv.setText(getdetails.getAddress());
            holder.rblistrv.setRating(getdetails.getRating());

            Glide.with(getActivity())
                    .load(getdetails.getRestlogo())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.ivlistrv);

            holder.ivlistrv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    custombuilder = new AlertDialog.Builder(getActivity());
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.custom_alertlist_frag, null);
                    custombuilder.setTitle("Rate: "+title)
                            .setView(dialogView)
                            .setCancelable(true);
                    ImageView alertiv = (ImageView) dialogView.findViewById(R.id.alertiv);
                    final RatingBar alertrb = (RatingBar) dialogView.findViewById(R.id.alertrb);
                    final TextView alerttv = (TextView) dialogView.findViewById(R.id.alerttv);
                    Glide.with(getActivity())
                            .load(getdetails.getRestlogo())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(alertiv);
                    alerttv.setText("Rate");
                    custombuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            boolean check = SharedPrefs.getmInstance(getActivity()).UserIsLoged();
                            Float rating = alertrb.getRating();
                            if(check){
                                if (rating < 0.5)
                                    Toast.makeText(getActivity(), "please set a rating ", Toast.LENGTH_SHORT).show();
                                else
                                    setRating(rating,partner);
                            }else {
                                Toast.makeText(getActivity(), "You need to be logged in ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    customalert = custombuilder.create();
                    customalert.show();
                }
            });
            holder.setclicker(new rvListener() {
                @Override
                public void onClick(View view, int position) {
                    Intent intent =new Intent(getActivity(), Menulist.class);
                    intent.putExtra("partner",partner);
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
//                ivlistrv.setScaleType(ImageView.ScaleType.FIT_XY);
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

    public void setRating(final Float rating,final int partid){
        final String CO_ROOT_URL = "http://"+getResources().getString(R.string.url)+"/dely/delyapp/rating.php";
        final int userid = getActivity().getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE).getInt("Id",0);
        StringRequest sRequest = new StringRequest(Request.Method.POST, CO_ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jobject = new JSONObject(response);
                            if(jobject.getString("messo").equals("1")){
                                Toast.makeText(getActivity(), "Rating set successfully", Toast.LENGTH_LONG).show();
                                customalert.cancel();
                            }
                            if(jobject.getString("messo").equals("2")){
                                Toast.makeText(getActivity(), "Rating not set try later", Toast.LENGTH_LONG).show();
                                customalert.cancel();
                            }if(jobject.getString("messo").equals("3")){
                                Toast.makeText(getActivity(), "Please order from the restaurant to rate it", Toast.LENGTH_LONG).show();
                                customalert.cancel();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Rating not set check your connection", Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("rating", rating+"");
                params.put("partid", partid+"");
                params.put("userid", userid+"");
                return params;
            }
        };
        Singleton.getmInstance(getActivity()).addToRequestQueue(sRequest);
    }
}
