package in.aaaos.fmgrocery;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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

public class CatProducts extends AppCompatActivity {
    Toolbar toolbar;
    TextView noproduct,retry,nointernet;
    String id,name,proid,proname,proslug,protype,propermalink,proprice,proregularprice,stockstatus;
    boolean onsale;
    LinearLayout noint,nointernetbottom;
    int i=0;
    protected Handler handler;
    StaggeredGridLayoutManager layoutManager;
    ArrayList<ResBean> mailListArrayList;
    ProgressBar progressBar;
    private DataAdapter adapter;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cat_products);
        id=getIntent().getStringExtra("Catid");
        name=getIntent().getStringExtra("CatName");
        nointernetbottom=(LinearLayout)findViewById(R.id.nointernetbottom);
        noproduct=(TextView)findViewById(R.id.noproduct);
        progressBar=(ProgressBar)findViewById(R.id.progress);
        noint=(LinearLayout)findViewById(R.id.noint);
        retry=(TextView)findViewById(R.id.internet);
        nointernet=(TextView)findViewById(R.id.nointernet);
        mailListArrayList=new ArrayList<>();
        recyclerView=(RecyclerView)findViewById(R.id.productitems);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        handler = new Handler();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle(name);
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
        i++;
        if(isNetworkAvailable()) {
            progressBar.setVisibility(View.VISIBLE);
            String url = "https://fmgrocery.in/wp-json/wc/v2/products?category=" + id + "&page=" + i;
            final StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONArray jsonArray=new JSONArray(response);
                        for(int i=0;i<jsonArray.length();i++) {
                            final JSONObject jsonObject = jsonArray.getJSONObject(i);
                            stockstatus = jsonObject.getString("in_stock");
                            if (stockstatus .equals( "true")) {
                                final ResBean resBean = new ResBean();
                                proid = jsonObject.getString("id");
                                proprice = jsonObject.getString("price");
                                proregularprice = jsonObject.getString("regular_price");
                                proname = jsonObject.getString("name");
                                proslug= jsonObject.getString("slug");
                                protype = jsonObject.getString("type");
                                propermalink = jsonObject.getString("permalink");
                                JSONArray jsonArray2 = jsonObject.getJSONArray("default_attributes");
                                        if(jsonArray2.length()==0){
                                            resBean.setOption("");
                                        }
                                        else{
                                            String option=jsonArray2.getJSONObject(0).getString("option");
                                            resBean.setOption(option);
                                        }

                                JSONArray jsonArray1 = jsonObject.getJSONArray("images");
                                for (int j = 0; j < jsonArray1.length(); j++) {
                                    JSONObject jsonObject1 = jsonArray1.getJSONObject(0);
                                    String src = jsonObject1.getString("src");
                                    resBean.setSrc(src);
                                }

                                resBean.setId(proid);
                                resBean.setName(proname);
                                resBean.setSlug(proslug);
                                resBean.setPermalink(propermalink);
                                resBean.setPrice(proprice);
                                resBean.setType(protype);
                                resBean.setRegularprice(proregularprice);
                                resBean.setOnsale(onsale);
                                resBean.setStockstatus(stockstatus);
                                mailListArrayList.add(resBean);



                            }
                        }
                        if(mailListArrayList.size()==0){
                            recyclerView.setVisibility(View.GONE);
                            noproduct.setVisibility(View.VISIBLE);
                        }
                        else{
                            layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

                            //layoutManager1 = new LinearLayoutManager(ProductsCat.this);

                            // use a linear layout manager
                            recyclerView.setLayoutManager(layoutManager);
                            //recyclerView.setLayoutManager(layoutManager);
                            adapter = new DataAdapter(mailListArrayList, recyclerView);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                                @Override
                                public void onLoadMore() {
                                    if(isNetworkAvailable()) {
                                        mailListArrayList.add(null);
                                        adapter.notifyItemInserted(mailListArrayList.size() - 1);
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                i++;
                                                String url = "https://fmgrocery.in/wp-json/wc/v2/products?category=" + id + "&page=" + i;
                                                StringRequest stringRequest1=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        mailListArrayList.remove(mailListArrayList.size() - 1);
                                                        adapter.notifyItemRemoved(mailListArrayList.size());
                                                        JSONArray jsonArray= null;
                                                        try {
                                                            jsonArray = new JSONArray(response);
                                                            if(jsonArray.length()==0){
                                                                Snackbar.make(findViewById(android.R.id.content), "No more product to show", Snackbar.LENGTH_LONG)
                                                                        .show();
                                                            }
                                                            else {
                                                                for(int i=0;i<jsonArray.length();i++) {
                                                                    final JSONObject jsonObject = jsonArray.getJSONObject(i);
                                                                    stockstatus = jsonObject.getString("in_stock");
                                                                    if (stockstatus .equals( "true")) {
                                                                        final ResBean resBean = new ResBean();
                                                                        proid = jsonObject.getString("id");
                                                                        proprice = jsonObject.getString("price");
                                                                        proregularprice = jsonObject.getString("regular_price");
                                                                        proname = jsonObject.getString("name");
                                                                        proslug= jsonObject.getString("slug");
                                                                        protype = jsonObject.getString("type");
                                                                        propermalink = jsonObject.getString("permalink");
                                                                        JSONArray jsonArray2 = jsonObject.getJSONArray("default_attributes");
                                                                        if(jsonArray2.length()==0){
                                                                            resBean.setOption("");
                                                                        }
                                                                        else{
                                                                            String option=jsonArray2.getJSONObject(0).getString("option");
                                                                            resBean.setOption(option);
                                                                        }

                                                                        JSONArray jsonArray1 = jsonObject.getJSONArray("images");
                                                                        for (int j = 0; j < jsonArray1.length(); j++) {
                                                                            JSONObject jsonObject1 = jsonArray1.getJSONObject(0);
                                                                            String src = jsonObject1.getString("src");
                                                                            resBean.setSrc(src);
                                                                        }

                                                                        resBean.setId(proid);
                                                                        resBean.setName(proname);
                                                                        resBean.setSlug(proslug);
                                                                        resBean.setPermalink(propermalink);
                                                                        resBean.setPrice(proprice);
                                                                        resBean.setType(protype);
                                                                        resBean.setRegularprice(proregularprice);
                                                                        resBean.setOnsale(onsale);
                                                                        resBean.setStockstatus(stockstatus);
                                                                        mailListArrayList.add(resBean);



                                                                    }
                                                                }
                                                                adapter.notifyItemInserted(mailListArrayList.size());
                                                                adapter.setLoaded();
                                                            }




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
                                                stringRequest1.setRetryPolicy(new DefaultRetryPolicy(500000,
                                                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                                AppController.getInstance().addToRequestQueue(stringRequest1);
                                            }
                                        });
                                    }
                                    else{
                                        mailListArrayList.remove(mailListArrayList.size() - 1);
                                        adapter.notifyItemRemoved(mailListArrayList.size());
                                        nointernetbottom.setVisibility(View.VISIBLE);
                                        nointernet.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                if(isNetworkAvailable()){
                                                    nointernetbottom.setVisibility(View.GONE);
                                                    onLoadMore();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }

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
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(500000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
            });
        }


    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) CatProducts.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
