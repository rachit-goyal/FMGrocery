package in.aaaos.fmgrocery;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import java.util.HashMap;
import java.util.Map;

public class OrderSummary extends AppCompatActivity {
String id;
    ProgressBar progressBar;
    String name,price,qty,total;
    TextView overalltotal,deleviry;
    RecyclerView recyclerView;
    OrderSummaryAdapter adapter;
    ArrayList<MyOrderBean> mailListArrayList;
    private RecyclerView.LayoutManager layoutManager;
    Toolbar toolbar;
    TextView retry,nointernet;
    LinearLayout noint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);
        id=getIntent().getStringExtra("orderid");
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mailListArrayList=new ArrayList<>();
        overalltotal=(TextView)findViewById(R.id.totalvaluesumm);
        noint=(LinearLayout)findViewById(R.id.noint);
        deleviry=(TextView)findViewById(R.id.deli);
        progressBar=(ProgressBar)findViewById(R.id.progress);
        recyclerView=(RecyclerView)findViewById(R.id.productitems);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        retry=(TextView)findViewById(R.id.internet);
        nointernet=(TextView)findViewById(R.id.nointernet);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle(id);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getdata();
    }

    private void getdata() {
        if(isNetworkAvailable()){
            progressBar.setVisibility(View.VISIBLE);
            String url="https://fmgrocery.in/wp-json/wc/v3/orders/"+id;
            StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        overalltotal.setText(jsonObject.getString("total"));
                        if(Double.parseDouble(overalltotal.getText().toString())<500){
                            deleviry.setVisibility(View.VISIBLE);
                        }
                        JSONArray jsonArray=jsonObject.getJSONArray("line_items");
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject1=jsonArray.getJSONObject(i);
                            MyOrderBean myOrderBean=new MyOrderBean();
                                name=jsonObject1.getString("name");
                                qty=jsonObject1.getString("quantity");
                                total=jsonObject1.getString("total");
                                price=jsonObject1.getString("price");
                                myOrderBean.setTotal(total);
                                myOrderBean.setQty(qty);
                                myOrderBean.setPrice(price);
                                myOrderBean.setName(name);
                                mailListArrayList.add(myOrderBean);
                        }
                        layoutManager = new LinearLayoutManager(OrderSummary.this);

                        //layoutManager1 = new LinearLayoutManager(ProductsCat.this);

                        // use a linear layout manager
                        recyclerView.setLayoutManager(layoutManager);
                        //recyclerView.setLayoutManager(layoutManager);
                        adapter = new OrderSummaryAdapter(OrderSummary.this,mailListArrayList);
                        recyclerView.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);


                }
            }){
                    @Override
                    public Map<String, String> getHeaders() throws
                    AuthFailureError {
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
        else{
            noint.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isNetworkAvailable()){
                        noint.setVisibility(View.GONE);
                        getdata();
                    }
                }
            });        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)OrderSummary.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
