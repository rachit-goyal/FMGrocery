package in.aaaos.fmgrocery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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

public class Coupon extends AppCompatActivity {
    ArrayList<CouponBean> couponBeanArrayList;
    RecyclerView categories;
    ArrayList usedby;
    String userid;
    int totalusedperper=0;
    private RecyclerView.Adapter mAdapter;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);
        progressBar=(ProgressBar)findViewById(R.id.progress);
        SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
        userid= prefs.getString("userid", null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        categories=(RecyclerView)findViewById(R.id.allcoupon);
        usedby=new ArrayList();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Coupons");
        toolbar.setTitleTextColor(Color.WHITE);
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
            couponBeanArrayList=new ArrayList<>();
            String urlcat = "https://fmgrocery.in/wp-json/wc/v3/coupons";
            StringRequest stri=new StringRequest(Request.Method.GET, urlcat, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONArray jsonArray=new JSONArray(response);
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject=jsonArray.getJSONObject(i);
                            CouponBean catbean=new CouponBean();
                            String id=jsonObject.getString("id");
                            String code=jsonObject.getString("code");
                            String amount=jsonObject.getString("amount");
                            String usageperlimit=jsonObject.getString("usage_limit_per_user");
                            String discount=jsonObject.getString("discount_type");
                            JSONArray jsonArray1=jsonObject.getJSONArray("used_by");
                            for(int j=0;j<jsonArray1.length();j++){
                                usedby.add(jsonArray1.get(i).toString());
                            }
                            for(int j=0;j<usedby.size();j++){
                                if(userid==usedby.get(i)){
                                    totalusedperper++;
                                }
                            }
                            if(totalusedperper<Integer.parseInt(usageperlimit)) {
                                catbean.setId(id);
                                catbean.setAmount(amount);
                                catbean.setCode(code);
                                catbean.setType(discount);
                                couponBeanArrayList.add(catbean);
                            }
                        }
                        categories.setNestedScrollingEnabled(false);
                        categories.setHasFixedSize(true);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(Coupon.this);
                        categories.setLayoutManager(mLayoutManager);
                        mAdapter = new CouponRecycler(Coupon.this, couponBeanArrayList);
                        categories.setAdapter(mAdapter);

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
            stri.setRetryPolicy(new DefaultRetryPolicy(500000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(stri);
        }
        else{
            Toast.makeText(Coupon.this, "No Network", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) Coupon.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public class CouponRecycler extends RecyclerView.Adapter<CouponRecycler.ViewHolder> {


        private Context mActivity;

        ArrayList<CouponBean> singleUser;

        public CouponRecycler(Activity context, ArrayList<CouponBean> singleUser) {

            this.mActivity = context;
            this.singleUser = singleUser;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView code,amount,perfix;



            public ViewHolder(View v) {

                super(v);
                code=(TextView)v.findViewById(R.id.codeval);
                amount=(TextView)v.findViewById(R.id.amount);
                perfix=(TextView)v.findViewById(R.id.perfix);
            }
        }

        @Override
        public CouponRecycler.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view1 = LayoutInflater.from(mActivity).inflate(R.layout.coupon_lay, parent, false);

            CouponRecycler.ViewHolder vh = new CouponRecycler.ViewHolder(view1);
            return vh;
        }

        @Override
        public void onBindViewHolder(CouponRecycler.ViewHolder holder, final int position) {
            holder.code.setText(singleUser.get(position).getCode());
            holder.amount.setText(singleUser.get(position).getAmount());
            if(singleUser.get(position).getType().equals("percent")){
                holder.perfix.setText("%");
            }
            else{
                holder.perfix.setText(R.string.Rs);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isNetworkAvailable()){
                        Intent i=new Intent(Coupon.this,UpdateOrEditCustomer.class);
                        i.putExtra("id",singleUser.get(position).getId());
                        i.putExtra("code",singleUser.get(position).getCode());
                        i.putExtra("amount",singleUser.get(position).getAmount());
                        i.putExtra("type",singleUser.get(position).getType());
                        setResult(Activity.RESULT_OK,i);
                        finish();
                    }
                    else{
                        Toast.makeText(mActivity,"No Network",Toast.LENGTH_SHORT).show();
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
                    = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

    }


}
