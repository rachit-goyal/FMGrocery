package in.aaaos.fmgrocery;

import android.content.Context;

import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;

public class OrderSummaryAdapter extends RecyclerView.Adapter<OrderSummaryAdapter.ViewHolder> {

    Context context;
    ArrayList<MyOrderBean> singleUser;

    public OrderSummaryAdapter(Context context, ArrayList<MyOrderBean> singleUser) {

        this.context = context;
        this.singleUser = singleUser;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name,qty,price,total;



        public ViewHolder(View v) {

            super(v);

            name = (TextView) v.findViewById(R.id.name);
            qty = (TextView) v.findViewById(R.id.qty);

            price = (TextView) v.findViewById(R.id.price);

            total = (TextView) v.findViewById(R.id.total);


        }
    }

    @Override
    public OrderSummaryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view1 = LayoutInflater.from(context).inflate(R.layout.orderitemlay, parent, false);

        OrderSummaryAdapter.ViewHolder vh = new OrderSummaryAdapter.ViewHolder(view1);
        return vh;
    }

    @Override
    public void onBindViewHolder(OrderSummaryAdapter.ViewHolder holder, final int position) {
        holder.name.setText(singleUser.get(position).getName());
        holder.qty.setText(singleUser.get(position).getQty());

        holder.price.setText(singleUser.get(position).getPrice());

        holder.total.setText(singleUser.get(position).getTotal());


    }

    @Override
    public int getItemCount(){

        return singleUser.size();
    }

}
