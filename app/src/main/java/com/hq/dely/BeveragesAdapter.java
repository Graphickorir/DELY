package com.hq.dely;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class BeveragesAdapter extends BaseAdapter{
        private ArrayList<Menulist.getBevList> bevitems;
        private Context ctx;
        private String title;
        private int partner;
        LayoutInflater inflater;

        public BeveragesAdapter(Context ctx,ArrayList<Menulist.getBevList> bevitems,String title,int partner) {
            this.ctx = ctx;
            this.bevitems = bevitems;
            this.title = title;
            this.partner = partner;
            inflater = (LayoutInflater) ctx.getSystemService(ctx.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return bevitems.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }



        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflater.inflate(R.layout.menulist_items,null);

            final Menulist.getBevList getitem = bevitems.get(i);
            final ImageView ivitemfav = (ImageView) view.findViewById(R.id.ivitemfav);
            TextView tvitemname = (TextView) view.findViewById(R.id.tvitemname);
            TextView tvitemprice = (TextView) view.findViewById(R.id.tvitemprice);
            final ImageView ivitemcart = (ImageView) view.findViewById(R.id.ivitemcart);
            tvitemname.setText(getitem.getItemname());
            tvitemprice.setText(getitem.getItemprice()+"");

            myDbHelper helper = new myDbHelper(ctx);
            dbOperations operate = new dbOperations(helper);
            boolean checkcartbefore = operate.checkCart(getitem.getItemid());
            boolean checkfavbefore = operate.checkFav(getitem.getItemid());

            if (checkcartbefore)
                ivitemcart.setImageResource(R.drawable.cartyes);
            else
                ivitemcart.setImageResource(R.drawable.cartno);

            if (checkfavbefore)
                ivitemfav.setImageResource(R.drawable.favyes);
            else
                ivitemfav.setImageResource(R.drawable.favno);

            ivitemcart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myDbHelper helper = new myDbHelper(ctx);
                    dbOperations operate = new dbOperations(helper);
                    boolean checkcart = operate.checkCart(getitem.getItemid());

                    if(checkcart){
                        ivitemcart.setImageResource(R.drawable.cartno);
                        operate.removeFromCart(getitem.getItemid());
                        ((addOrRemove)ctx).onRemoveProduct();}
                    else{
                        ivitemcart.setImageResource(R.drawable.cartyes);
                        operate.addCartItem(getitem.getItemid(),getitem.getItemname(),getitem.getItemprice(),title);
                        ((addOrRemove)ctx).onAddProduct();}
                }
            });

            ivitemfav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myDbHelper helper = new myDbHelper(ctx);
                    dbOperations operate = new dbOperations(helper);
                    boolean checkfav = operate.checkFav(getitem.getItemid());

                    if(checkfav) {
                        ivitemfav.setImageResource(R.drawable.favno);
                        operate.removeFromFav(getitem.getItemid()); }
                    else{
                        ivitemfav.setImageResource(R.drawable.favyes);
                        operate.addFavItem(getitem.getItemid(),getitem.getItemname(),getitem.getItemprice(),title); }
                }
            });
            return view;
        }
}
