package com.hq.dely;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;
import java.util.Map;

public class Trans extends AppCompatActivity {
    TextView transbal;
    RecyclerView rvtrans;
    List<getTransdetails> translist;

    ProgressBar tpbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans);
        translist = new ArrayList<>();
        tpbar = (ProgressBar) findViewById(R.id.tpbar);
        ImageView ivdepo = (ImageView) findViewById(R.id.ivdepo);

        ivdepo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Trans.this,Deposit.class);
                startActivity(i);
            }
        });
        transbal = (TextView) findViewById(R.id.transbal);
        rvtrans = (RecyclerView) findViewById(R.id.rvtrans);
        rvtrans.setLayoutManager(new LinearLayoutManager(this));
        rvtrans.setHasFixedSize(true);
        loadTrans();
    }

    //Volley LOAD TRANS
    public void loadTrans() {
        tpbar.setVisibility(View.VISIBLE);
        final String CO_ROOT_URL = "http://"+getResources().getString(R.string.url)+"/dely/delyapp/translist.php";
        final int id = this.getSharedPreferences("MySharedPrefs",Context.MODE_PRIVATE).getInt("Id",0);
        StringRequest sRequest = new StringRequest(Request.Method.POST, CO_ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        tpbar.setVisibility(View.INVISIBLE);
                        try {
                            JSONArray json = new JSONArray(response);
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject jsonObject = json.getJSONObject(i);
                                if (jsonObject.getString("messo").equals("1")) {
                                    int tid = jsonObject.getInt("Trans_id");
                                    String order = jsonObject.getString("Order_num");
                                    int trans = Math.abs(jsonObject.getInt("Trans"));
                                    String date = jsonObject.getString("Trans_date");
                                    transbal.setText(jsonObject.getInt("Bal")+"");

                                    getTransdetails getdetails = new getTransdetails(tid,order,trans,date);
                                    translist.add(getdetails);
                                }else if (jsonObject.getString("messo").equals("2")) {
                                    AlertDialog.Builder alert = new AlertDialog.Builder(Trans.this);
                                    alert.setTitle("You have no Transactions")
                                            .setMessage("Please deposit or purchase items to view transactions")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    finish();
                                                }
                                            })
                                            .setCancelable(false)
                                            .create()
                                            .show();
                                }
                            }
                            translistAdapter adapter = new translistAdapter(translist,Trans.this);
                            rvtrans.setAdapter(adapter);
                        } catch (JSONException e) {
                            Toast.makeText(Trans.this, "bigger problems", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(Trans.this);
                        alert.setMessage("Failed To Connect!")
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        loadTrans();
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
                params.put("userid",id+"");
                return params;
            }
        };
        Singleton.getmInstance(Trans.this).addToRequestQueue(sRequest);
    }

    //Volley load spinner
    public void loadSpinner(final String num,final List<getSpinItems> spindata) {
        final String CO_ROOT_URL = "http://"+getResources().getString(R.string.url)+"/dely/delyapp/spinner.php";
        StringRequest sRequest = new StringRequest(Request.Method.POST, CO_ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray json = new JSONArray(response);
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject jsonObject = json.getJSONObject(i);
                                String part = jsonObject.getString("part");
                                String item = jsonObject.getString("item");
                                int dely = jsonObject.getInt("dely");

                                getSpinItems getspin = new getSpinItems(part,item,dely);
                                spindata.add(getspin);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error", "onErrorResponse: "+error);
                        AlertDialog.Builder alert = new AlertDialog.Builder(Trans.this);
                        alert.setMessage("Failed To Connect!")
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        loadSpinner(num,spindata);
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
                params.put("order",num);
                return params;
            }
        };
        Singleton.getmInstance(Trans.this).addToRequestQueue(sRequest);
    }

    //Adapter
    class translistAdapter extends RecyclerView.Adapter<translistAdapter.rvHolder> {
        private List<getTransdetails> translist;
        Context ctx;

        private translistAdapter(List<getTransdetails> translist, Context ctx) {
            this.translist = translist;
            this.ctx = ctx;
        }


        @Override
        public rvHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(Trans.this);
            View itemview = inflater.inflate(R.layout.custom_transrv,parent,false);

            return new rvHolder(itemview);
        }

        @Override
        public void onBindViewHolder(translistAdapter.rvHolder holder, final int position) {
            final getTransdetails getdetails = translist.get(position);

            holder.transdate.setText(getdetails.getDate());
            holder.transid.setText(getdetails.getId()+"");
            holder.tvtrans.setText("KSH "+getdetails.getTrans()+"");

            if (getdetails.getOrder().equals("null")){
                holder.transtype.setText("Deposit");
            }else {
                holder.transtype.setText("Order");
                List<getSpinItems> spindata =new ArrayList<>();
                String part ="Order number";
                String item =getdetails.getOrder();
                int dely = -1;
                getSpinItems getspin = new getSpinItems(part,item,dely);
                spindata.add(getspin);
                loadSpinner(getdetails.getOrder(),spindata);
                spinAdapter sadapt = new spinAdapter(spindata,ctx);
                holder.transspin.setVisibility(View.VISIBLE);
                holder.transspin.setAdapter(sadapt);
                holder.transspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return translist.size();
        }

        //Holder
        class rvHolder extends RecyclerView.ViewHolder{
            TextView transdate, transid,tvtrans,transtype;
            Spinner transspin;

            private rvHolder(View itemView) {
                super(itemView);

                transdate = (TextView) itemView.findViewById(R.id.transdate);
                transid = (TextView) itemView.findViewById(R.id.transid);
                tvtrans = (TextView) itemView.findViewById(R.id.tvtrans);
                transtype = (TextView) itemView.findViewById(R.id.transtype);
                transspin = (Spinner) itemView.findViewById(R.id.transspinner);
            }
        }
    }

    //getitemcless
    class getTransdetails {
        private int tid,trans;
        private String date,order;

        public getTransdetails(int tid, String order, int trans, String date) {
            this.tid = tid;
            this.order = order;
            this.trans = trans;
            this.date = date;
        }

        public int getId() {
            return tid;
        }

        public String getOrder() {
            return order;
        }

        public int getTrans() {
            return trans;
        }

        public String getDate() {
            return date;
        }
    }

    //SpinnerAdapter
    private class spinAdapter extends BaseAdapter{
        private List<getSpinItems> spindata;
        Context ctx;

        public spinAdapter(List<getSpinItems> spindata, Context ctx) {
            this.spindata = spindata;
            this.ctx = ctx;
        }

        @Override
        public int getCount() {
            return spindata.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = (LayoutInflater.from(ctx));
            getSpinItems items = spindata.get(i);

            view = inflater.inflate(R.layout.trans_spinner,null);
            TextView transpart = (TextView) view.findViewById(R.id.transpart);
            TextView transitem = (TextView) view.findViewById(R.id.transitem);
            TextView transdely = (TextView) view.findViewById(R.id.transdely);
            ImageView ivtrans = (ImageView) view.findViewById(R.id.ivtrans);

            transpart.setText(items.getPart());
            transitem.setText(items.getItem());
            if (items.isDely() == -1){
                transdely.setVisibility(View.INVISIBLE);
                ivtrans.setVisibility(View.INVISIBLE);
            }else if (items.isDely() == 0){
                ivtrans.setImageResource(R.drawable.redcircle);
            }else if(items.isDely() == 1){
                ivtrans.setImageResource(R.drawable.greencircle);
            }
            return view;
        }
    }

    //getSpinneritems
    class getSpinItems{
        String part,item;
        int dely;

        private getSpinItems(String part, String item, int dely) {
            this.part = part;
            this.item = item;
            this.dely = dely;
        }

        public String getPart() {
            return part;
        }

        public String getItem() {
            return item;
        }

        public int isDely() {
            return dely;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
