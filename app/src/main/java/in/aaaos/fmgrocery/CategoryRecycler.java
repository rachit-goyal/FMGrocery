package in.aaaos.fmgrocery;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class CategoryRecycler extends RecyclerView.Adapter<CategoryRecycler.ViewHolder> {

    Context context;
    ArrayList<CatBean> singleUser;

    public CategoryRecycler(Context context, ArrayList<CatBean> singleUser) {

        this.context = context;
        this.singleUser = singleUser;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name,desc;
        public ImageView img;
        public LinearLayout liner;


        public ViewHolder(View v) {

            super(v);
            img = (ImageView) v.findViewById(R.id.imgcat);
            desc = (TextView) v.findViewById(R.id.desc);
            name = (TextView) v.findViewById(R.id.name);
            liner=(LinearLayout) v.findViewById(R.id.catrecyle);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view1 = LayoutInflater.from(context).inflate(R.layout.cat_lay, parent, false);

        ViewHolder vh = new ViewHolder(view1);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Picasso.with(this.context).load(singleUser.get(position).getImg()).resize(300,300).into(holder.img);
        holder.name.setText(singleUser.get(position).getName());
        holder.desc.setText(Html.fromHtml(singleUser.get(position).getDesc()));
        holder.liner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable()){
                    Intent i=new Intent(context,CatProducts.class);
                    i.putExtra("Catid",singleUser.get(position).getId());
                    i.putExtra("CatName",singleUser.get(position).getName());
                    context.startActivity(i);
                }
                else{
                    Toast.makeText(context,"No Network",Toast.LENGTH_SHORT).show();
                }
            }
        });
        }

    @Override
    public int getItemCount(){

        return singleUser.size();
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
