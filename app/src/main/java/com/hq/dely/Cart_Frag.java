package com.hq.dely;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Korir on 2/16/2018.
 */

public class Cart_Frag extends Fragment {
    RecyclerView rvcart;
    cartRvAdapter adapter;
    TextView tvcartcomp,tvcartaddress,tvcartpay,tvtotal,tvchangeaddress,tvchangepay;



    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getAllCart().size() >= 1){
            View rootView = inflater.inflate(R.layout.fragment_cart, container, false);
            boolean isloged;
            isloged = SharedPrefs.getmInstance(getActivity()).UserIsLoged();
            LinearLayout lvnotloged = (LinearLayout) rootView.findViewById(R.id.lvnotloged);

            if(isloged)
                loaduserdetails();
            else
                lvnotloged.setVisibility(View.VISIBLE);

            tvcartcomp = (TextView) rootView.findViewById(R.id.tvcartcomp);
            tvcartaddress = (TextView) rootView.findViewById(R.id.tvcartaddress);
            tvcartpay = (TextView) rootView.findViewById(R.id.tvcartpay);
            tvtotal = (TextView) rootView.findViewById(R.id.tvtotal);
            tvchangeaddress = (TextView) rootView.findViewById(R.id.tvchangeaddress);
            tvchangepay = (TextView) rootView.findViewById(R.id.tvchangepay);

            rvcart = (RecyclerView) rootView.findViewById(R.id.rvcart);
            rvcart.setLayoutManager(new LinearLayoutManager(getActivity()));
            rvcart.setHasFixedSize(true);
            adapter = new cartRvAdapter(getAllCart(),getActivity());
            rvcart.setAdapter(adapter);

            myDbHelper helper = new myDbHelper(getActivity());
            dbOperations operations = new dbOperations(helper);
            tvtotal.setText("KSH "+operations.getcarttotal());

            TextView tvnoitems = (TextView) rootView.findViewById(R.id.tvnoitems);
            tvnoitems.setText("" + getAllCart().size());
        return rootView;}
        else{
            View rootView = inflater.inflate(R.layout.addcartitems, container, false);
            return rootView;
        }
    }

    //Combo Volley
    public void loaduserdetails() {
        final String CO_ROOT_URL = "http://"+getResources().getString(R.string.url)+"/korirphp/cartdetails.php";
        final String User = getActivity().getSharedPreferences("MySharedPrefs",Context.MODE_PRIVATE).getString("Username",null);

        StringRequest sRequest = new StringRequest(Request.Method.POST, CO_ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            tvcartcomp.setText(""+jsonObject.getString("Company"));
                            tvcartaddress.setText(""+jsonObject.getString("Address"));
                            tvcartpay.setText(""+jsonObject.getString("Method"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                        alert.setMessage("Failed To Get User Info!")
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        loaduserdetails();
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
                params.put("User",User);
                return params;
            }
        };
        Singleton.getmInstance(getActivity()).addToRequestQueue(sRequest);
    }

    public List<getCartItems> getAllCart() {
        myDbHelper helper = new myDbHelper(getActivity());

        String query = "SELECT * FROM " + myDbHelper.TABLE_CART;
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        List<getCartItems> cartList = new ArrayList<>();

        getCartItems fact;
        if (cursor.moveToFirst()) {
            do {
                fact = new getCartItems();
                fact.setCartid(cursor.getInt(1));
                fact.setCartitem(cursor.getString(2));
                fact.setCartprice(cursor.getInt(3));
                fact.setCartpart(cursor.getString(4));

                cartList.add(fact);
            } while (cursor.moveToNext());
        }
        return cartList;
    }

    //Adapter
    class cartRvAdapter extends RecyclerView.Adapter<cartRvAdapter.rvHolder> {
        private List<getCartItems> cartlist;
        Context ctx;

        private cartRvAdapter(List<getCartItems> cartlist, Context ctx) {
            this.cartlist = cartlist;
            this.ctx = ctx;
        }


        @Override
        public rvHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View itemview = inflater.inflate(R.layout.fav_items,parent,false);

            return new rvHolder(itemview);
        }

        @Override
        public void onBindViewHolder(final cartRvAdapter.rvHolder holder, int position) {
            final getCartItems getdetails = cartlist.get(position);
            final String part = getdetails.getCartpart();
            final String cartitem = getdetails.getCartitem();
            final int price = getdetails.getCartprice();
            final int id = getdetails.getCartid();

            holder.tvcartpart.setText(part);
            holder.tvcartitem.setText(cartitem);
            holder.tvcartprice.setText(price+"");
            holder.ivcartitem.setImageResource(R.drawable.favyes);
            holder.ivcartcart.setImageResource(R.drawable.cartyes);

            myDbHelper helper = new myDbHelper(ctx);
            dbOperations operate = new dbOperations(helper);
            boolean checkfavbefore = operate.checkFav(id);

            if (checkfavbefore)
                holder.ivcartitem.setImageResource(R.drawable.favyes);
            else
                holder.ivcartitem.setImageResource(R.drawable.favno);

            holder.ivcartitem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myDbHelper helper = new myDbHelper(ctx);
                    dbOperations operate = new dbOperations(helper);
                    boolean checkfav = operate.checkFav(id);

                    if(checkfav){
                        holder.ivcartitem.setImageResource(R.drawable.favno);
                        operate.removeFromFav(id);
                    }
                    else{
                        holder.ivcartitem.setImageResource(R.drawable.favyes);
                        operate.addFavItem(id,cartitem,price, part);
                    }
                }
            });
            holder.ivcartcart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myDbHelper helper = new myDbHelper(ctx);
                    dbOperations operate = new dbOperations(helper);
                    operate.removeFromCart(id);
                    cartlist.remove(holder.getAdapterPosition());
                    adapter.notifyItemRemoved(holder.getAdapterPosition());
                    tvtotal.setText("KSH "+operate.getcarttotal());
                    ((addOrRemove)ctx).onRemoveProduct();

                }
            });
        }

        @Override
        public int getItemCount() {
            return cartlist.size();
        }

        //Holder
        class rvHolder extends RecyclerView.ViewHolder {
            TextView tvcartpart, tvcartitem, tvcartprice;
            ImageView ivcartitem,ivcartcart;

            private rvHolder(View itemView) {
                super(itemView);

                tvcartpart = (TextView) itemView.findViewById(R.id.tvfavpart);
                tvcartitem = (TextView) itemView.findViewById(R.id.tvfavitem);
                tvcartprice = (TextView) itemView.findViewById(R.id.tvfavprice);
                ivcartitem = (ImageView) itemView.findViewById(R.id.ivfavitem);
                ivcartcart = (ImageView) itemView.findViewById(R.id.ivfavcart);
            }
        }
    }

    public static class getCartItems{
        private String cartpart,cartitem;
        private int cartprice,cartid;

        public String getCartpart() {
            return cartpart;
        }

        private void setCartpart(String cartpart) {
            this.cartpart = cartpart;
        }

        public String getCartitem() {
            return cartitem;
        }

        private void setCartitem(String cartitem) {
            this.cartitem = cartitem;
        }

        public int getCartprice() {
            return cartprice;
        }

        private void setCartprice(int cartprice) {
            this.cartprice = cartprice;
        }

        public int getCartid() {
            return cartid;
        }

        private void setCartid(int cartid) {
            this.cartid = cartid;
        }
    }
}
