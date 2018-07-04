package com.hq.dely;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Korir on 2/16/2018.
 */

public class Fav_Frag extends Fragment {
    RecyclerView rvfav;
    favRvAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fav, container, false);
        rvfav = (RecyclerView) rootView.findViewById(R.id.rvfav);

        rvfav.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvfav.setHasFixedSize(true);
        adapter = new favRvAdapter(getAllFav(),getActivity());
        rvfav.setAdapter(adapter);

        return rootView;
    }
    public List<getFavItems> getAllFav() {
        myDbHelper helper = new myDbHelper(getActivity());

        String query = "SELECT * FROM " + myDbHelper.TABLE_FAV;
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        List<getFavItems> favList = new ArrayList<>();

        getFavItems fact;
        if (cursor.moveToFirst()) {
            do {
                fact = new getFavItems();
                fact.setFavid(cursor.getInt(1));
                fact.setFavitem(cursor.getString(2));
                fact.setFavprice(cursor.getInt(3));
                fact.setFavpart(cursor.getString(4));

                favList.add(fact);
            } while (cursor.moveToNext());
        }
        return favList;
    }

    //Adapter
    class favRvAdapter extends RecyclerView.Adapter<favRvAdapter.rvHolder> {
        private List<getFavItems> favlist;
        Context ctx;

        private favRvAdapter(List<getFavItems> favlist, Context ctx) {
            this.favlist = favlist;
            this.ctx = ctx;
        }


        @Override
        public rvHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View itemview = inflater.inflate(R.layout.fav_items,parent,false);

            return new rvHolder(itemview);
        }

        @Override
        public void onBindViewHolder(final favRvAdapter.rvHolder holder, int position) {
            final getFavItems getdetails = favlist.get(position);
            final String part = getdetails.getFavpart();
            final String favitem = getdetails.getFavitem();
            final int price = getdetails.getFavprice();
            final int id = getdetails.getFavid();

            holder.tvfavpart.setText(part);
            holder.tvfavitem.setText(favitem);
            holder.tvfavprice.setText(price+"");
            holder.ivfavitem.setImageResource(R.drawable.favyes);
            holder.ivfavcart.setImageResource(R.drawable.cartyes);

            myDbHelper helper = new myDbHelper(ctx);
            dbOperations operate = new dbOperations(helper);
            boolean checkcartbefore = operate.checkCart(id);

            if (checkcartbefore)
                holder.ivfavcart.setImageResource(R.drawable.cartyes);
            else
                holder.ivfavcart.setImageResource(R.drawable.cartno);

            holder.ivfavcart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myDbHelper helper = new myDbHelper(ctx);
                    dbOperations operate = new dbOperations(helper);
                    boolean checkcart = operate.checkCart(id);

                    if(checkcart){
                        holder.ivfavcart.setImageResource(R.drawable.cartno);
                        operate.removeFromCart(id);}
                    else{
                        holder.ivfavcart.setImageResource(R.drawable.cartyes);
                        operate.addCartItem(id,favitem,price, part); }
                }
            });
            holder.ivfavitem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myDbHelper helper = new myDbHelper(ctx);
                    dbOperations operate = new dbOperations(helper);
                    operate.removeFromFav(id);
                    favlist.remove(holder.getAdapterPosition());
                    adapter.notifyItemRemoved(holder.getAdapterPosition());
                }
            });
        }

        @Override
        public int getItemCount() {
            return favlist.size();
        }

        //Holder
        class rvHolder extends RecyclerView.ViewHolder {
            TextView tvfavpart, tvfavitem, tvfavprice;
            ImageView ivfavitem,ivfavcart;
            rvListener click;

            private rvHolder(View itemView) {
                super(itemView);

                tvfavpart = (TextView) itemView.findViewById(R.id.tvfavpart);
                tvfavitem = (TextView) itemView.findViewById(R.id.tvfavitem);
                tvfavprice = (TextView) itemView.findViewById(R.id.tvfavprice);
                ivfavitem = (ImageView) itemView.findViewById(R.id.ivfavitem);
                ivfavcart = (ImageView) itemView.findViewById(R.id.ivfavcart);
            }
        }
    }
    public static class getFavItems{
        private String favpart,favitem;
        private int favprice,favid;

        public String getFavpart() {
            return favpart;
        }

        private void setFavpart(String favpart) {
            this.favpart = favpart;
        }

        public String getFavitem() {
            return favitem;
        }

        private void setFavitem(String favitem) {
            this.favitem = favitem;
        }

        public int getFavprice() {
            return favprice;
        }

        private void setFavprice(int favprice) {
            this.favprice = favprice;
        }

        public int getFavid() {
            return favid;
        }

        private void setFavid(int favid) {
            this.favid = favid;
        }
    }
}
