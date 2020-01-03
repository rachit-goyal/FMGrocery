package in.aaaos.fmgrocery;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;



public class SimpleRecy extends RecyclerView.Adapter<SimpleRecy.MyViewHolder> {

    private ArrayList<res> dataSet;
    Context context;
    int val;
    ArrayList<res> resArrayList;
    int total=0;
    DatabaseHelper databaseHelper;


    public SimpleRecy(ArrayList<res> data, Context context) {
        this.dataSet = data;
        resArrayList = new ArrayList<>();
        this.context=context;
        call();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView qty;
        TextView edit,amount,quan,price;
        ImageView img;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.title = (TextView) itemView.findViewById(R.id.proname);
            this.qty = (TextView) itemView.findViewById(R.id.qtyval);
            this.edit = (TextView) itemView.findViewById(R.id.qtyvaledit);
            this.quan = (TextView) itemView.findViewById(R.id.qtyvalue);
            this.price = (TextView) itemView.findViewById(R.id.price);
            this.amount = (TextView) itemView.findViewById(R.id.amtval);
            this.img = (ImageView) itemView.findViewById(R.id.image_cartlist);
            }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_resulted, parent, false);

       // view.setOnClickListener(MainActivity.myOnClickListener);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        holder.title.setText(Html.fromHtml(dataSet.get(listPosition).getName()));
        holder.qty.setText(Html.fromHtml(dataSet.get(listPosition).getQty()));
        holder.quan.setText(Html.fromHtml(dataSet.get(listPosition).getQty()));
        holder.price.setText(Html.fromHtml(dataSet.get(listPosition).getAmount()));
        holder.amount.setText(String.valueOf(Integer.parseInt(holder.quan.getText().toString())*Integer.parseInt(holder.price.getText().toString())));
        Picasso.with(context).load(dataSet.get(listPosition).getImg()).into(holder.img);
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Cursor sde=   databaseHelper.getIndividualData(String.valueOf(dataSet.get(listPosition).getLocalid()));
                if (sde.moveToFirst())
                {
                    do
                    {
                        final Dialog dialog = new Dialog(context);
                        dialog.setTitle(sde.getString(2));
                        dialog.setCancelable(true);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                        dialog.setContentView(R.layout.dia_edit);

                        final TextView edit = (TextView) dialog.findViewById(R.id.counter);
                        ImageView imgval=(ImageView)dialog.findViewById(R.id.imgval);
                        TextView proname=(TextView)dialog.findViewById(R.id.proname);
                        TextView submit=(TextView)dialog.findViewById(R.id.submit);
                        ImageView plus=(ImageView)dialog.findViewById(R.id.plus);
                        ImageView minus=(ImageView)dialog.findViewById(R.id.minus);
                        Picasso.with(context).load(dataSet.get(listPosition).getImg()).into(imgval);
                        plus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(val==10){
                                    Toast.makeText(context,"You cannot add more than 10 quantity",Toast.LENGTH_SHORT).show();

                                }
                                else{
                                    val++;
                                    edit.setText(String.valueOf(val));
                                }
                            }
                        });
                        minus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(val==1){
                                    Toast.makeText(context,"Value cannot be less than 1",Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    val--;
                                    edit.setText(String.valueOf(val));
                                }
                            }
                        });
                        val=Integer.parseInt(sde.getString(3));
                        edit.setText(String.valueOf(val));
                        final String loid=sde.getString(0);
                        proname.setText(sde.getString(1));
                        submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                databaseHelper = new DatabaseHelper(context);

                                holder.qty.setText(edit.getText().toString());
                                holder.quan.setText(edit.getText().toString());
                                String qt=edit.getText().toString();
                                String amountval= String.valueOf(Integer.parseInt(holder.quan.getText().toString())*Integer.parseInt(holder.price.getText().toString()));
                                holder.amount.setText(amountval);
                                final Cursor updaterow = databaseHelper.updateval(qt,loid);

                                if (updaterow.getCount() > 0) {
                                    Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show();

                                }
                                databaseHelper.close();
                                call();
                                dialog.dismiss();
                            }

                        });
                        databaseHelper.close();

                        dialog.show();


                    }while (sde.moveToNext());
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Single_product.class);
                intent.putExtra("id", String.valueOf(dataSet.get(listPosition).getId()));
                intent.putExtra("name",dataSet.get(listPosition).getName());

                view.getContext().startActivity(intent);
            }
        });



    }

    private void call() {
        total=0;
        resArrayList.clear();
        databaseHelper=new DatabaseHelper(context);
        Cursor sde = databaseHelper.getAllData();
        while (sde.moveToNext()) {
            res res = new res();
            res.setQty(sde.getString(3));
            res.setAmount(sde.getString(5));
            resArrayList.add(res);
        }

        for (int i=0;i<resArrayList.size();i++){

            total=total+Integer.parseInt(resArrayList.get(i).getQty())*Integer.parseInt(resArrayList.get(i).getAmount());
        }
        Intent intent = new Intent("custom-message");
        //            intent.putExtra("quantity",Integer.parseInt(quantity.getText().toString()));
        intent.putExtra("quantity",String.valueOf(total));
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }


    @Override
    public int getItemCount() {

        return dataSet.size();
    }
}
