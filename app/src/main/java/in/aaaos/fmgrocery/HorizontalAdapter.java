package in.aaaos.fmgrocery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by RACHIT GOYAL on 5/12/2018.
 */

class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.ViewHolder> {

    Context context;

    ArrayList<ResBean> singleUser;

    public HorizontalAdapter(Context context, ArrayList<ResBean> singleUser) {

        this.context = context;
        this.singleUser = singleUser;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView img;
        public TextView price,regular,name,kg,rs;

        public ViewHolder(View v) {

            super(v);
            img = (ImageView) v.findViewById(R.id.img);
            price=(TextView)v.findViewById(R.id.price);
            regular=(TextView)v.findViewById(R.id.regular);
            name=(TextView)v.findViewById(R.id.pdtname);
            kg=(TextView)v.findViewById(R.id.kg);
        }
    }

    @Override
    public HorizontalAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view1 = LayoutInflater.from(context).inflate(R.layout.horizontalrecycler, parent, false);

        HorizontalAdapter.ViewHolder vh = new HorizontalAdapter.ViewHolder(view1);
        return vh;
    }

    @Override
    public void onBindViewHolder(HorizontalAdapter.ViewHolder holder, final int position) {
        Picasso.with(this.context).load(singleUser.get(position).getSrc()).resize(300,300).placeholder(R.drawable.app_logo).into(holder.img);
        holder.price.setText(singleUser.get(position).getPrice());
        holder.regular.setText(singleUser.get(position).getRegularprice());
        holder.name.setText(singleUser.get(position).getName());
        holder.kg.setText(singleUser.get(position).getOption());
        holder.regular.setPaintFlags(holder.regular.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        if(!singleUser.get(position).getRegularprice().equals("")){
            int perval= (Integer.parseInt(singleUser.get(position).getRegularprice())-Integer.parseInt(singleUser.get(position).getPrice()));
            if(perval==0){
                holder.regular.setVisibility(View.GONE);
            }
        }

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable()) {
                    Intent i=new Intent(context,Single_product.class);
                    i.putExtra("id",singleUser.get(position).getId());
                    i.putExtra("name",singleUser.get(position).getName());
                    context.startActivity(i);
                }
                else{
                    Toast.makeText(context,"No Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {

        return singleUser.size();
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
