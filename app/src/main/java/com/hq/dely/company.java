package com.hq.dely;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
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

/**
 * Created by korir on 1/19/2018.
 */

public class company extends Fragment implements Toolbar.OnMenuItemClickListener{
    RecyclerView recyclerView;
    List<getcompdetails> complist;
    ProgressBar copbar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.company, container, false);
        copbar= (ProgressBar) rootView.findViewById(R.id.copbar);
        complist= new ArrayList<>();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rview);
        Toolbar toolbar= (Toolbar) getActivity().findViewById(R.id.toolbar);

        toolbar.setOnMenuItemClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        loadCompanies();

        return rootView;
    }

    //menu clicked
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.addcompany:
            addCompDialogue alert = new addCompDialogue();
            alert.setCancelable(false);
            alert.show(getFragmentManager(),"Add Company");
        }
        return false;
    }

    //Volley
    public void loadCompanies() {
        final String CO_ROOT_URL = "http://"+getResources().getString(R.string.url)+"/korirphp/delyco.php";
        copbar.setVisibility(View.VISIBLE);

        StringRequest sRequest = new StringRequest(Request.Method.GET, CO_ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        copbar.setVisibility(View.INVISIBLE);
                        try {
                            JSONArray json = new JSONArray(response);
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject jsonObject = json.getJSONObject(i);

                                int id = jsonObject.getInt("Id_Co");
                                String Compname = jsonObject.getString("Company");
                                String Address = jsonObject.getString("Address");
                                String co_logo = jsonObject.getString("co_logo");

                              getcompdetails getdetails = new getcompdetails(id,Compname, Address,co_logo);
                              complist.add(getdetails);
                            }
                            rvAdapter adapter = new rvAdapter(complist,getActivity());
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
    class rvAdapter extends RecyclerView.Adapter<rvAdapter.rvHolder> {
        private List<getcompdetails> complist;
        Context ctx;

        public rvAdapter(List<getcompdetails> complist, Context ctx) {
            this.complist = complist;
            this.ctx = ctx;
        }


        @Override
        public rvHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View itemview = inflater.inflate(R.layout.companyrv,parent,false);

            return new rvHolder(itemview);
        }

        @Override
        public void onBindViewHolder(rvAdapter.rvHolder holder, int position) {
            final getcompdetails getdetails = complist.get(position);

            holder.tvtitle.setText(getdetails.getCompname());
            holder.tvaddress.setText(getdetails.getAddress());

            Glide.with(getActivity())
                    .load(getdetails.getCo_logo())
                    .into(holder.ivlogo);

            holder.setclicker(new rvListener() {
                @Override
                public void onClick(View view, int position) {
                    final String CO_ROOT_URL = "http://"+getResources().getString(R.string.url)+"/korirphp/co_id.php";
                    final String Username=getActivity().getSharedPreferences("MySharedPrefs",Context.MODE_PRIVATE).getString("Username",null);
                    final int Id_Co = getdetails.getId();

                    copbar.setVisibility(View.VISIBLE);
                    StringRequest sRequest = new StringRequest(Request.Method.POST, CO_ROOT_URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    copbar.setVisibility(View.INVISIBLE);
                                    try {
                                        JSONObject json = new JSONObject(response);
                                       if(json.getString("messo") == "1"){
                                           AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                                           alert.setMessage("Company address set successfully!")
                                                   .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                       @Override
                                                       public void onClick(DialogInterface dialog, int which) {
                                                           ((SignUp)getActivity()).selectFragment(1);
                                                       }
                                                   })
                                                   .setCancelable(false)
                                                   .create()
                                                   .show();
                                       }else if(json.getString("messo") == "2"){
                                            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                                            alert.setMessage("Invalid Company Address")
                                                    .setPositiveButton("Retry", null)
                                                    .setCancelable(true)
                                                    .create()
                                                    .show();
                                        }
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
                                            .setPositiveButton("Retry set", null)
                                            .setCancelable(false)
                                            .create()
                                            .show();
                                }
                            })
                    {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("Id_Co", Id_Co+"");
                            params.put("Username",Username);
                            return params;
                        }
                    };

                    Singleton.getmInstance(getActivity()).addToRequestQueue(sRequest);
                }
            });
        }

        @Override
        public int getItemCount() {
            return complist.size();
        }

        //Holder
        class rvHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            TextView tvtitle, tvaddress;
            ImageView ivlogo;
            rvListener click;

            public rvHolder(View itemView) {
                super(itemView);

                tvtitle = (TextView) itemView.findViewById(R.id.tvtitle);
                tvaddress = (TextView) itemView.findViewById(R.id.tvaddress);
                ivlogo = (ImageView) itemView.findViewById(R.id.ivlogo);

                itemView.setOnClickListener(this);
            }
            public void setclicker(rvListener click){
                this.click =click;
            }

            @Override
            public void onClick(View v) {
                click.onClick(v,getAdapterPosition());
            }
        }
    }

    //getitemcless
    class getcompdetails {
        private int id;
        private String Compname,co_logo;
        private String address;


        public getcompdetails(int id,String Compname, String address, String co_logo) {
            this.id=id;
            this.Compname = Compname;
            this.address = address;
            this.co_logo= co_logo;
        }

        public int getId() {
            return id;
        }

        public String getCompname() {
            return Compname;
        }

        public String getAddress() {
            return address;
        }

        public String getCo_logo() {
            return co_logo;
        }
    }
}