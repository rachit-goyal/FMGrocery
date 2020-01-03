package in.aaaos.fmgrocery;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CartActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    LinearLayout nocart;
    Button shopnow;
    Toolbar toolbar;
    TextView totalvalue;
    private ProgressDialog dialog;
    TextView checkout;
    LinearLayout enquiry;
    private SimpleRecy mAdapter;
    ArrayList<res> resArrayList;
    String pricenew;
    DatabaseHelper databaseHelper;
    String emailval=null;
    RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
        emailval = prefs.getString("email", null);
        shopnow = (Button) findViewById(R.id.bAddNew);
        resArrayList = new ArrayList<>();
        dialog = new ProgressDialog(CartActivity.this);
        checkout = (TextView) findViewById(R.id.checkout);
        totalvalue = (TextView) findViewById(R.id.totalvalue);
        enquiry = (LinearLayout) findViewById(R.id.enquiry);
        nocart = (LinearLayout) findViewById(R.id.nocartitem);
        databaseHelper = new DatabaseHelper(CartActivity.this);
        recyclerView = (RecyclerView) findViewById(R.id.cartitem);
        recyclerView.setHasFixedSize(true);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mLayoutManager = new LinearLayoutManager(CartActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        Cursor sde = databaseHelper.getAllData();
        if (sde.getCount() == 0) {
            nocart.setVisibility(View.VISIBLE);
            shopnow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(CartActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
        } else {
            while (sde.moveToNext()) {
                dialog.setMessage("Please Wait");
                dialog.show();
                res res = new res();
                res.setLocalid(sde.getInt(0));
                res.setImg(sde.getString(2));
                res.setQty(sde.getString(3));
                res.setId(sde.getString(4));
                res.setAmount(sde.getString(5));
                res.setName(sde.getString(1));
                resArrayList.add(res);
            }

        Collections.reverse(resArrayList);
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < resArrayList.size(); i++) {
            sb.append(resArrayList.get(i).getId() + ",");
        }
        String laststr = sb.substring(0, sb.length() - 1);
        String urmultiple = "https://fmgrocery.in/wp-json/wc/v2/products/?include=" + laststr;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, urmultiple, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String stockstatus = jsonObject.getString("in_stock");
                        if (!stockstatus.equals("true")) {
                            databaseHelper.deleteData(String.valueOf(resArrayList.get(i).getLocalid()));
                        } else {
                            String onsale = jsonObject.getString("on_sale");
                            if (onsale.equals("false")) {
                                pricenew = jsonObject.getString("price");
                            } else {
                                pricenew = jsonObject.getString("regular_price");
                            }
                            databaseHelper.updatepri(pricenew, String.valueOf(resArrayList.get(i).getLocalid()));
                        }
                    }
                    resArrayList.clear();
                    Cursor sde1 = databaseHelper.getAllData();
                    if (sde1.getCount() == 0) {
                        nocart.setVisibility(View.VISIBLE);
                        shopnow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(CartActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                    } else {
                        enquiry.setVisibility(View.VISIBLE);
                        while (sde1.moveToNext()) {
                            res res = new res();
                            res.setLocalid(sde1.getInt(0));
                            res.setImg(sde1.getString(2));
                            res.setQty(sde1.getString(3));
                            res.setId(sde1.getString(4));
                            res.setAmount(sde1.getString(5));
                            res.setName(sde1.getString(1));
                            resArrayList.add(res);
                        }
                    }
                    databaseHelper.close();
                    if(dialog.isShowing()){
                        dialog.dismiss();
                    }
                    mAdapter = new SimpleRecy(resArrayList, CartActivity.this);
                    recyclerView.setAdapter(mAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                // add headers <key,value>
                String credentials = "ck_4f54f2e70e7c9c4ac145a8f694938baca9a1a589" + ":" + "cs_3b3c8964bc91ddf33ff26eeec8aea7f2621422a0";
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(),
                        Base64.NO_WRAP);
                headers.put("Authorization", auth);
                return headers;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        if(resArrayList.size()!=0){
            Toast.makeText(CartActivity.this,"Swipe to remove from cart",Toast.LENGTH_SHORT).show();
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-message"));
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int totalval=Integer.parseInt(totalvalue.getText().toString());
                if(totalval<500){
                    final Dialog dialog = new Dialog(CartActivity.this);
                    dialog.setCancelable(true);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                    dialog.setContentView(R.layout.diafivety);
                    TextView cancel=dialog.findViewById(R.id.cancel);
                    TextView submit=dialog.findViewById(R.id.submit);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(emailval==null) {
                                Intent i = new Intent(CartActivity.this, NewPreviousCustomer.class);
                                i.putExtra("val","allow");
                                startActivity(i);
                                dialog.dismiss();
                            }
                            else{
                                Intent i = new Intent(CartActivity.this, UpdateOrEditCustomer.class);
                                startActivity(i);
                                dialog.dismiss();
                            }
                        }
                    });
                    dialog.show();
                }
                else{
                    if(emailval==null) {
                        Intent i = new Intent(CartActivity.this, NewPreviousCustomer.class);
                        i.putExtra("val","allow");

                        startActivity(i);

                    }
                    else{
                        Intent i = new Intent(CartActivity.this, UpdateOrEditCustomer.class);
                        startActivity(i);
                    }
                }
            }
        });
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String qty = intent.getStringExtra("quantity");
            totalvalue.setText(qty);
        }
    };
    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

            int position = viewHolder.getAdapterPosition();
            databaseHelper=new DatabaseHelper(CartActivity.this);
            Integer deleterow=databaseHelper.deleteData(String.valueOf(resArrayList.get(position).getLocalid()));

            if(deleterow>0){
                MainActivity.notificationCountCart--;
                NotificationCountSetClass.setNotifyCount(MainActivity.notificationCountCart);
                Toast.makeText(CartActivity.this,"Item removed",Toast.LENGTH_SHORT).show();
            }

            resArrayList.remove(position);
            if(resArrayList.size()==0){
                if(enquiry.getVisibility()==View.VISIBLE) {
                    enquiry.setVisibility(View.GONE);
                }
                nocart.setVisibility(View.VISIBLE);
                shopnow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(CartActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
            }

            mAdapter.notifyDataSetChanged();
            databaseHelper.close();
        }
    };

    @Override
    protected void onResume() {
        SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
        emailval = prefs.getString("email", null);
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CartActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        super.onBackPressed();
    }
}
