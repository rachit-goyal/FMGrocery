package in.aaaos.fmgrocery;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.ProgressBar;
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

public class AllCategories extends AppCompatActivity {
    ArrayList<CatBean> catBeanArrayList;
    RecyclerView categories;
    private RecyclerView.Adapter mAdapter;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_categories);
        progressBar=(ProgressBar)findViewById(R.id.progress);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        categories=(RecyclerView)findViewById(R.id.category);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("All Categories");
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
            catBeanArrayList=new ArrayList<>();
            String urlcat = "https://fmgrocery.in/wp-json/wc/v3/products/categories?per_page=50";
            StringRequest stri=new StringRequest(Request.Method.GET, urlcat, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONArray jsonArray=new JSONArray(response);
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject=jsonArray.getJSONObject(i);
                            CatBean catbean=new CatBean();
                            String id=jsonObject.getString("id");
                            String name=jsonObject.getString("name");
                            String desc=jsonObject.getString("description");
                            JSONObject jsonObject1=jsonObject.getJSONObject("image");
                            String src=jsonObject1.getString("src");
                            catbean.setImg(src);
                            catbean.setDesc(desc);
                            catbean.setName(name);
                            catbean.setId(id);
                            catBeanArrayList.add(catbean);
                        }
                        categories.setNestedScrollingEnabled(false);
                        categories.setHasFixedSize(true);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(AllCategories.this);
                        categories.setLayoutManager(mLayoutManager);
                        mAdapter = new CategoryRecycler(AllCategories.this, catBeanArrayList);
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
            Toast.makeText(AllCategories.this, "No Network", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) AllCategories.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
