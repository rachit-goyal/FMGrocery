package in.aaaos.fmgrocery;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

class ComboAdapter extends RecyclerView.Adapter<ComboAdapter.ViewHolder> {

    Context context;
    int i=1;
    ArrayList<String> prid;

    ArrayList<Combobean> singleUser;
    DatabaseHelper databaseHelper;
    public ComboAdapter(Context context, ArrayList<Combobean> singleUser) {

        this.context = context;
        this.singleUser = singleUser;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView img,about;
        public TextView price,regular,name,kg,rs;

        public ViewHolder(View v) {

            super(v);
            img = (ImageView) v.findViewById(R.id.img);
            about = (ImageView) v.findViewById(R.id.about);
            price=(TextView)v.findViewById(R.id.price);
            regular=(TextView)v.findViewById(R.id.regular);
            name=(TextView)v.findViewById(R.id.pdtname);
            kg=(TextView)v.findViewById(R.id.kg);
        }
    }

    @Override
    public ComboAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view1 = LayoutInflater.from(context).inflate(R.layout.combooffer, parent, false);

        ComboAdapter.ViewHolder vh = new ComboAdapter.ViewHolder(view1);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ComboAdapter.ViewHolder holder, final int position) {
        Picasso.with(this.context).load(singleUser.get(position).getSrc()).resize(300,300).placeholder(R.drawable.app_logo).into(holder.img);
        holder.price.setText(singleUser.get(position).getPrice());
        holder.regular.setText(singleUser.get(position).getRegularprice());
        holder.name.setText(singleUser.get(position).getName());
        holder.kg.setText(singleUser.get(position).getOption());
        holder.regular.setPaintFlags(holder.regular.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        holder.about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable()) {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.custom_enquiry);
                    ImageView plus=dialog.findViewById(R.id.plus);
                    ImageView minus=dialog.findViewById(R.id.minus);
                    final TextView cartcounter=dialog.findViewById(R.id.counter);
                    TextView textView=dialog.findViewById(R.id.desc);
                    TextView send=dialog.findViewById(R.id.buttonSend);
                    dialog.setTitle(singleUser.get(position).getName());
                    dialog.setCancelable(true);
                    textView.setText(Html.fromHtml(singleUser.get(position).getDesc()));
                    cartcounter.setText(String.valueOf(i));
                    plus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if(i==10){
                                Toast.makeText(context,"You cannot add more than 10 quantity",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                i++;
                                cartcounter.setText(String.valueOf(i));
                            }
                        }
                    });
                    minus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if(i==1){
                                Toast.makeText(context,"Value cannot be less than 1",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                i--;
                                cartcounter.setText(String.valueOf(i));
                            }
                        }
                    });
                    send.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            databaseHelper = new DatabaseHelper(context);

                            Cursor sde = databaseHelper.getAllData();
                            prid = new ArrayList<String>();
                            while (sde.moveToNext()) {
                                prid.add(sde.getString(4));
                            }
                            boolean isExist = false;
                            for (int i = 0; i < prid.size(); i++) {
                                if (singleUser.get(position).getId().equals(prid.get(i))) {
                                    Toast.makeText(context, "Already in your cart change quantity their.", Toast.LENGTH_SHORT).show();
                                    isExist = true;
                                    break;
                                }
                            }
                            if (!isExist) {
                                String amount=singleUser.get(position).getPrice();
                                boolean isinserted = databaseHelper.inserdatacart(singleUser.get(position).getName(), singleUser.get(position).getSrc(), cartcounter.getText().toString(), singleUser.get(position).getId(),amount);

                                if (isinserted) {
                                    Toast.makeText(context, "Item added to cart.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Unable to add to cart", Toast.LENGTH_SHORT).show();
                                }
                                MainActivity.notificationCountCart++;
                                NotificationCountSetClass.setNotifyCount(MainActivity.notificationCountCart);
                            }
                            dialog.dismiss();
                            databaseHelper.close();

                        }
                    });
                    dialog.show();
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
