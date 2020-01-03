package in.aaaos.fmgrocery;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UpdateOrEditCustomer extends AppCompatActivity {
TextInputEditText fname,lname,add1,add2,postcode,phone;
TextView edit,confirm,coupon;
String emailval,sub,subtot;
String idval,totvalpre,codeid=null,code,type,amout;
    ArrayList<res> resArrayList;
    String pricenew ;
    TextView totalvalue,removecoupon,subtotal;
    LinearLayout deliverycharge;
    private ProgressDialog dialog;
    Toolbar toolbar;
    String qty;
    private SimpleCartLast mAdapter;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_or_edit_customer);
        SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
        emailval= prefs.getString("email", null);
        edit=(TextView)findViewById(R.id.edit);
        toolbar=(Toolbar)findViewById(R.id.toolbar);

        deliverycharge=(LinearLayout)findViewById(R.id.del);
        phone=(TextInputEditText)findViewById(R.id.phone);
        totalvalue=(TextView)findViewById(R.id.totalvalue);
        subtotal=(TextView)findViewById(R.id.totalsubvalue);
        removecoupon=(TextView)findViewById(R.id.removecoupon);
        dialog = new ProgressDialog(UpdateOrEditCustomer.this);
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
        databaseHelper = new DatabaseHelper(UpdateOrEditCustomer.this);
        recyclerView = (RecyclerView) findViewById(R.id.cartitem);
        recyclerView.setHasFixedSize(true);
        resArrayList = new ArrayList<>();
        fname=(TextInputEditText)findViewById(R.id.firstname);
        lname=(TextInputEditText)findViewById(R.id.lastname);
        coupon=(TextView)findViewById(R.id.coupon);
        confirm=(TextView)findViewById(R.id.confirm);
        add1=(TextInputEditText)findViewById(R.id.address1name);
        add2=(TextInputEditText)findViewById(R.id.address2name);
        postcode=(TextInputEditText)findViewById(R.id.postalname);
        if(isNetworkAvailable()){
            getData();
        }
        else{
            Toast.makeText(UpdateOrEditCustomer.this,"No Network",Toast.LENGTH_SHORT).show();
        }
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edit.getText().toString().equals("Edit")) {
                    edit.setText("Done");
                    fname.setEnabled(true);
                    lname.setEnabled(true);
                    add1.setEnabled(true);
                    add2.setEnabled(true);
                    postcode.setEnabled(true);

                } else if(edit.getText().toString().equals("Done")){
                    edit.setText("Edit");
                    lname.setEnabled(false);
                    fname.setEnabled(false);
                    add1.setEnabled(false);
                    add2.setEnabled(false);
                    postcode.setEnabled(false);
                    JSONObject js = new JSONObject();
                    try {

                        JSONObject jsonObjectt = new JSONObject();
                        JSONObject jsonObjecttsh = new JSONObject();
                        jsonObjectt.put("address_1", add1.getText().toString().trim());
                        jsonObjectt.put("address_2", add2.getText().toString().trim());
                        jsonObjectt.put("postcode", postcode.getText().toString().trim());
                        jsonObjectt.put("first_name", fname.getText().toString().trim());
                        jsonObjectt.put("last_name", lname.getText().toString().trim());
                        jsonObjectt.put("phone", phone.getText().toString().trim());


                        jsonObjecttsh.put("first_name", fname.getText().toString().trim());
                        jsonObjecttsh.put("last_name", lname.getText().toString().trim());
                        jsonObjecttsh.put("address_1", add1.getText().toString().trim());
                        jsonObjecttsh.put("address_2", add2.getText().toString().trim());
                        jsonObjecttsh.put("postcode", postcode.getText().toString().trim());


                        js.put("billing", jsonObjectt);
                        js.put("shipping", jsonObjecttsh);
                        js.put("first_name", fname.getText().toString().trim());
                        js.put("last_name", lname.getText().toString().trim());

                    }catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String url = "https://fmgrocery.in/wp-json/wc/v3/customers/"+idval;

                    try {
                        JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(String.valueOf(js)),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            getData();
                                            VolleyLog.v("Response:%n %s", response.toString(4));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                VolleyLog.e("Error: ", error.getMessage());
                            }
                        }){
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
                        AppController.getInstance().addToRequestQueue(req);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    }
            }

        });
        coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(UpdateOrEditCustomer.this,Coupon.class);
                startActivityForResult(i, 1);
            }
        });
        mLayoutManager = new LinearLayoutManager(UpdateOrEditCustomer.this);
        recyclerView.setLayoutManager(mLayoutManager);
        Cursor sde = databaseHelper.getAllData();
        while (sde.moveToNext()) {
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
        StringBuilder sb=new StringBuilder();

        for(int i=0;i<resArrayList.size();i++){
            sb.append(resArrayList.get(i).getId()+",");
        }
        String laststr=sb.substring(0,sb.length()-1);
        String urmultiple="https://fmgrocery.in/wp-json/wc/v2/products/?include="+laststr;

        StringRequest stringRequest=new StringRequest(Request.Method.GET, urmultiple, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray=new JSONArray(response);
                    for(int i=0;i<jsonArray.length();i++) {
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
                    
                    databaseHelper.close();

                    mAdapter = new SimpleCartLast(resArrayList, UpdateOrEditCustomer.this);
                    recyclerView.setAdapter(mAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
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
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-message"));
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setCancelable(false);
                dialog.setMessage("Please Wait...");
                dialog.show();
                JSONObject js = new JSONObject();
                JSONObject itemvalue=new JSONObject();
                JSONObject jsonObjectt = new JSONObject();
                JSONObject jsonObjcou = new JSONObject();
                JSONObject jsonObjecttshippingline = new JSONObject();
                JSONArray jsonArray = new JSONArray();

                JSONObject jsonObjecttsh = new JSONObject();

                try {
                    jsonObjectt.put("address_1", add1.getText().toString().trim());
                    jsonObjectt.put("address_2", add2.getText().toString().trim());
                    jsonObjectt.put("postcode", postcode.getText().toString().trim());
                    jsonObjectt.put("first_name", fname.getText().toString().trim());
                    jsonObjectt.put("last_name", lname.getText().toString().trim());
                    jsonObjectt.put("phone", phone.getText().toString().trim());
                    jsonObjectt.put("country", "India");
                    jsonObjectt.put("email", emailval);
                    jsonObjectt.put("state", "UP");
                    jsonObjectt.put("city", "Agra");

                    jsonObjecttsh.put("country", "India");
                    jsonObjecttsh.put("state", "UP");
                    jsonObjecttsh.put("city", "Agra");
                    jsonObjecttsh.put("first_name", fname.getText().toString().trim());
                    jsonObjecttsh.put("last_name", lname.getText().toString().trim());
                    jsonObjecttsh.put("address_1", add1.getText().toString().trim());
                    jsonObjecttsh.put("address_2", add2.getText().toString().trim());
                    jsonObjecttsh.put("postcode", postcode.getText().toString().trim());
                    for(int i=0;i<resArrayList.size();i++){
                        itemvalue = new JSONObject();
                        itemvalue.put("product_id", resArrayList.get(i).getId());
                        itemvalue.put("quantity", resArrayList.get(i).getQty());
                        jsonArray.put(itemvalue);
                        }
                    JSONArray jsonArraycou = new JSONArray();
                    JSONArray jsonshiipingline=new JSONArray();

                    if(codeid!=null){
                        jsonObjcou.put("code", code);
                        jsonArraycou.put(jsonObjcou);
                        js.put("coupon_lines",jsonArraycou);
                        }
                        js.put("customer_id",idval);
                    js.put("payment_method","cod");
                    js.put("payment_method_title","Cash on delivery");
                    js.put("line_items",jsonArray);
                    js.put("set_paid",false);
                    js.put("billing", jsonObjectt);
                    js.put("shipping", jsonObjecttsh);
                    double vval= Double.parseDouble(totalvalue.getText().toString());
                    if(vval<500){
                        jsonObjecttshippingline.put("method_id","flat_rate");
                        jsonObjecttshippingline.put("method_title","Flat rate");
                        jsonObjecttshippingline.put("total","50");
                        jsonshiipingline.put(jsonObjecttshippingline);
                        js.put("shipping_lines",jsonshiipingline);
                        }
                    } catch (JSONException e) {
                    e.printStackTrace();
                }
                String url = "https://fmgrocery.in/wp-json/wc/v3/orders";

                try {
                    JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(String.valueOf(js)),
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    dialog.dismiss();
                                        databaseHelper=new DatabaseHelper(UpdateOrEditCustomer.this);
                                        databaseHelper.del();
                                        databaseHelper.close();
                                        Intent i=new Intent(UpdateOrEditCustomer.this,OrderConfirmed.class);
                                        startActivity(i);

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            dialog.dismiss();
                            if(error instanceof NoConnectionError){
                              Toast.makeText(UpdateOrEditCustomer.this,"No Network",Toast.LENGTH_SHORT).show();
                            }
                           else  if(error instanceof ParseError){
                                databaseHelper=new DatabaseHelper(UpdateOrEditCustomer.this);
                                databaseHelper.del();
                                databaseHelper.close();
                                Intent i=new Intent(UpdateOrEditCustomer.this,OrderConfirmed.class);
                                startActivity(i);
                            }
                            else{
                                Toast.makeText(UpdateOrEditCustomer.this,"Some error occurred.please try after some time",Toast.LENGTH_SHORT).show();

                            }
                        }
                    }){
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
                    AppController.getInstance().addToRequestQueue(req);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                }
        });
        removecoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setMessage("Please Wait");
                dialog.show();
                totalvalue.setText(qty);
                subtotal.setText(sub );
                codeid=null;
                coupon.setText("Apply Coupon");
                dialog.dismiss();
                removecoupon.setVisibility(View.GONE);
            }
        });
    }
    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
             qty = intent.getStringExtra("quantity");
            totvalpre=qty;
            if(Integer.parseInt(qty)<500){
                deliverycharge.setVisibility(View.VISIBLE);
                 sub=String.valueOf(Double.parseDouble(qty)+50);
            }
            else{
                deliverycharge.setVisibility(View.GONE);
                sub=qty;
                }
            totalvalue.setText(qty);
            subtotal.setText(sub);

        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            totalvalue.setText(totvalpre);
            if(resultCode == Activity.RESULT_OK){
                codeid=data.getStringExtra("id");
                code=data.getStringExtra("code");
                amout=data.getStringExtra("amount");
                type=data.getStringExtra("type");
                if(type.equals("percent")){
                    String tot=String.valueOf(Double.parseDouble(totalvalue.getText().toString())-((Double.parseDouble(totalvalue.getText().toString())/100)*Double.parseDouble(amout)));
                    coupon.setText("Coupon Applied");
                    removecoupon.setVisibility(View.VISIBLE);
                    if(Double.parseDouble(tot)<500){
                        deliverycharge.setVisibility(View.VISIBLE);
                        subtot=String.valueOf(Double.parseDouble(tot)+50);
                    }
                    else{
                        deliverycharge.setVisibility(View.GONE);
                        subtot=tot;
                        }
                    totalvalue.setText(tot);
                    subtotal.setText(subtot);

                }
                else if(type.equals("fixed_cart")){
                  String tot=String.valueOf(Double.parseDouble(totalvalue.getText().toString())-Double.parseDouble(amout));
                  totalvalue.setText(tot);
                    coupon.setText("Coupon Applied");
                    removecoupon.setVisibility(View.VISIBLE);
                    if(Double.parseDouble(tot)<500){
                        deliverycharge.setVisibility(View.VISIBLE);
                        subtot=String.valueOf(Double.parseDouble(tot)+50);
                    }
                    else{
                        deliverycharge.setVisibility(View.GONE);
                        subtot=tot;
                    }
                    totalvalue.setText(tot);
                    subtotal.setText(subtot);
                }
            }

        }
    }

    private void getData() {
        String url="https://fmgrocery.in/wp-json/wc/v3/customers/?email="+emailval;
        dialog.setMessage("Please Wait");
        dialog.show();
        StringRequest str=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                try {
                    JSONArray jsonArray=new JSONArray(response);
                    JSONObject jsonObject=jsonArray.getJSONObject(0);
                    idval=jsonObject.getString("id");
                    fname.setText(jsonObject.getString("first_name"));
                    lname.setText(jsonObject.getString("last_name"));
                    JSONObject jsonObject1=jsonObject.getJSONObject("billing");
                    add1.setText(jsonObject1.getString("address_1"));
                    add2.setText(jsonObject1.getString("address_2"));
                    phone.setText(jsonObject1.getString("phone"));
                    postcode.setText(jsonObject1.getString("postcode"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(UpdateOrEditCustomer.this,"Some error occurred.Please try after some time.",Toast.LENGTH_SHORT).show();
                }
        }){
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
        AppController.getInstance().addToRequestQueue(str);

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) UpdateOrEditCustomer.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
