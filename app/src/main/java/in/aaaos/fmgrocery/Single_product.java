package in.aaaos.fmgrocery;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Single_product extends AppCompatActivity {

    SliderPager sliderPagerAdapter;
    String id, name,image;
    ArrayList<String> prid;
    LinearLayoutManager layoutManager;
    TextView amt, information,priceval ,regular,avai,selsize,sizeval;
    private TextView[] dots;
    ArrayList<String> slider_image_list;
    private LinearLayout ll_dots;
    ViewPager viewPager;
    String nametool;
    RecyclerView products;
    HorizontalAdapter horizontalAdapter;
    ArrayList <String> ayyayval;
    ArrayList<ResBean> resBeanArrayList,reatra;
    LinearLayout ll,per;
    String shortdesc;
    private ProgressDialog dialog;
    RelativeLayout productpage;
    String finalval;
    DatabaseHelper databaseHelper;
    TextView addtocart,cartcounter;
    ImageView pus,minus;
    TextView productname;
    int i=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_product);
        id = getIntent().getStringExtra("id");
        nametool=getIntent().getStringExtra("name");
        information = (TextView) findViewById(R.id.info);
        productname=(TextView)findViewById(R.id.productname);
        products=(RecyclerView)findViewById(R.id.products);
        dialog = new ProgressDialog(Single_product.this);
        productpage=(RelativeLayout)findViewById(R.id.productpage);
        databaseHelper=new DatabaseHelper(Single_product.this);
        addtocart=(TextView)findViewById(R.id.addtocat);
        per=(LinearLayout)findViewById(R.id.per);
        ayyayval=new ArrayList<>();
        cartcounter=(TextView)findViewById(R.id.counter);
        pus=(ImageView) findViewById(R.id.plus);
        minus=(ImageView) findViewById(R.id.minus);
        sizeval=(TextView)findViewById(R.id.seqt);
        ll = (LinearLayout) findViewById(R.id.tagsbgs);
        selsize=(TextView)findViewById(R.id.selqty);
        avai=(TextView)findViewById(R.id.avai);
        priceval=(TextView)findViewById(R.id.price);
        regular=(TextView)findViewById(R.id.regular);
        amt=(TextView)findViewById(R.id.amt);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        productname.setText(nametool);
        toolbar.setTitle("");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if(isNetworkAvailable()){
            loaddata();

        }
        else{
            Toast.makeText(Single_product.this,"No Network",Toast.LENGTH_SHORT).show();
        }


    }

    private void loaddata() {
        dialog.setMessage("Please Wait");
        dialog.show();
        final String url = "https://fmgrocery.in/wp-json/wc/v2/products/" + id;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                productpage.setVisibility(View.VISIBLE);
                dialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    slider_image_list = new ArrayList<>();
                    JSONArray jsonArray = jsonObject.getJSONArray("images");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String img = jsonObject1.getString("src");
                        slider_image_list.add(img);
                    }
                    final String price = jsonObject.getString("price");
                    final String regularprice = jsonObject.getString("regular_price");

                    information.setText(Html.fromHtml(jsonObject.getString("description")));
                    shortdesc = jsonObject.getString("short_description");
                    JSONArray jsonArray2=jsonObject.getJSONArray("related_ids");
                    StringBuilder stringBuilder=new StringBuilder();
                    for(int i=0;i<jsonArray2.length();i++){
                        String relatedid=jsonArray2.get(i).toString();
                        stringBuilder.append(relatedid+",");
                        finalval=stringBuilder.substring(0,stringBuilder.length()-1);
                    }
                    String urmultiple="https://fmgrocery.in/wp-json/wc/v2/products/?include="+finalval;
                    StringRequest stringRequest=new StringRequest(Request.Method.GET, urmultiple, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONArray jsonArray=new JSONArray(response);
                                reatra=new ArrayList<>();
                                for(int i=0;i<jsonArray.length();i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String stockstatus = jsonObject.getString("in_stock");
                                    if (stockstatus .equals( "true")) {
                                        ResBean resBean = new ResBean();
                                        String id = jsonObject.getString("id");
                                        String name = jsonObject.getString("name");
                                        String slug = jsonObject.getString("slug");
                                        String permalink = jsonObject.getString("permalink");
                                        String price = jsonObject.getString("price");
                                        String regularprice = jsonObject.getString("regular_price");
                                        boolean onsale = jsonObject.getBoolean("on_sale");

                                        JSONArray jsonArray1 = jsonObject.getJSONArray("images");
                                        for (int j = 0; j < jsonArray1.length(); j++) {
                                            JSONObject jsonObject1 = jsonArray1.getJSONObject(0);
                                            String src = jsonObject1.getString("src");
                                            resBean.setSrc(src);
                                        }
                                        JSONArray jsonArray2 = jsonObject.getJSONArray("default_attributes");
                                        for (int k = 0; k < jsonArray2.length(); k++) {
                                            JSONObject jsonObject1 = jsonArray2.getJSONObject(0);
                                            String option = jsonObject1.getString("option");
                                            resBean.setOption(option);
                                        }
                                        resBean.setId(id);
                                        resBean.setName(name);
                                        resBean.setSlug(slug);
                                        resBean.setPermalink(permalink);
                                        resBean.setPrice(price);
                                        resBean.setRegularprice(regularprice);
                                        resBean.setOnsale(onsale);
                                        resBean.setStockstatus(stockstatus);
                                        reatra.add(resBean);
                                    }
                                }
                                horizontalAdapter=new HorizontalAdapter(Single_product.this,reatra);
                                LinearLayoutManager horizontalLayoutManagaer
                                        = new LinearLayoutManager(Single_product.this, LinearLayoutManager.HORIZONTAL, false);
                                products.setLayoutManager(horizontalLayoutManagaer);
                                products.setAdapter(horizontalAdapter);
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

                    JSONArray jsonArray1 = jsonObject.getJSONArray("variations");
                    if(jsonArray1.length()==0){
                        ll.setVisibility(View.GONE);
                        avai.setVisibility(View.GONE);
                        selsize.setVisibility(View.GONE);
                        sizeval.setVisibility(View.GONE);
                        priceval.setText(price);
                        regular.setText(regularprice);
                        regular.setPaintFlags(regular.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        if(!regularprice.equals("")){
                            per.setVisibility(View.VISIBLE);
                            String reg= String.valueOf(regular.getText());
                            String pri= String.valueOf(priceval.getText());
                            int perval= (Integer.parseInt(reg)-Integer.parseInt(pri));
                            if(perval==0){
                                per.setVisibility(View.INVISIBLE);
                                regular.setVisibility(View.INVISIBLE);
                            }
                            else {
                                amt.setText(String.valueOf(perval) + " ");
                            }
                        }
                    }
                    else{
                        selsize.setVisibility(View.VISIBLE);
                        sizeval.setVisibility(View.VISIBLE);
                        avai.setVisibility(View.GONE);
                        ll.setVisibility(View.GONE);

                        String url1="https://fmgrocery.in/wp-json/wc/v2/products/" + id+"/variations";
                        StringRequest str=new StringRequest(Request.Method.GET, url1, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONArray jsonArray2=new JSONArray(response);
                                    resBeanArrayList=new ArrayList<>();
                                    for(int i=0;i<jsonArray2.length();i++){
                                        JSONObject jsonObject1=jsonArray2.getJSONObject(i);
                                        boolean stockstatus = jsonObject1.getBoolean("in_stock");
                                        if (stockstatus) {
                                          /*      String id=jsonObject1.getString("id");
                                                String price=jsonObject1.getString("price");
                                                String regular=jsonObject1.getString("regular_price");*/
                                            String id = jsonObject1.getString("id");

                                            JSONArray jsonArray3 = jsonObject1.getJSONArray("attributes");
                                            for (int j = 0; j < jsonArray3.length(); j++) {
                                                ResBean resBean = new ResBean();
                                                JSONObject jsonObject11 = jsonArray3.getJSONObject(0);
                                                String option = jsonObject11.getString("option");
                                                resBean.setOption(option);
                                                resBean.setId(id);
                                                resBeanArrayList.add(resBean);
                                            }
                                        }

                                    }

                                    final String id=resBeanArrayList.get(0).getId();
                                    String url1="https://fmgrocery.in/wp-json/wc/v2/products/" + id+"/variations/"+id;
                                    StringRequest str=new StringRequest(Request.Method.GET, url1, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject jsonObject1=new JSONObject(response);
                                                String price = jsonObject1.getString("price");
                                                String regularprice = jsonObject1.getString("regular_price");
                                                priceval.setText(price);
                                                regular.setText(regularprice);
                                                regular.setPaintFlags(regular.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                                if(!regularprice.equals("")){
                                                    per.setVisibility(View.VISIBLE);
                                                    String reg= String.valueOf(regular.getText());
                                                    String pri= String.valueOf(priceval.getText());
                                                    int perval= (Integer.parseInt(reg)-Integer.parseInt(pri));
                                                    amt.setText(String.valueOf(perval)+" ");
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
                                    AppController.getInstance().addToRequestQueue(str);


                                    for (int k = 0; k < resBeanArrayList.size(); k++) {
                                        final TextView tv = new TextView(Single_product.this);
                                        tv.setText(resBeanArrayList.get(k).getOption());
                                        tv.setBackgroundResource(R.drawable.textback);
                                        tv.setTextColor(R.drawable.textlink);
                                        tv.setPadding(8, 8, 8, 8);
                                        tv.setTag(resBeanArrayList.get(k).getId());
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        params.bottomMargin = 5;
                                        params.leftMargin = 10;
                                        ll.addView(tv, params);
                                        sizeval.setText(resBeanArrayList.get(0).getOption());
                                        final int finalK = k;
                                        tv.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                sizeval.setText(tv.getText());
                                                String url1="https://fmgrocery.in/wp-json/wc/v2/products/" + id+"/variations/"+resBeanArrayList.get(finalK).getId();
                                                StringRequest str=new StringRequest(Request.Method.GET, url1, new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        try {
                                                            JSONObject jsonObject1=new JSONObject(response);
                                                            String price = jsonObject1.getString("price");
                                                            String regularprice = jsonObject1.getString("regular_price");
                                                            priceval.setText(price);
                                                            regular.setText(regularprice);
                                                            regular.setPaintFlags(regular.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                                            if(!regularprice.equals("")){
                                                                per.setVisibility(View.VISIBLE);
                                                                String reg= String.valueOf(regular.getText());
                                                                String pri= String.valueOf(priceval.getText());
                                                                int perval= (Integer.parseInt(reg)-Integer.parseInt(pri));
                                                                amt.setText(String.valueOf(perval)+" ");
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
                                                AppController.getInstance().addToRequestQueue(str);
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
                        AppController.getInstance().addToRequestQueue(str);

                    }
                    init();

                    addBottomDots(0);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                productpage.setVisibility(View.GONE);
                dialog.dismiss();
                Toast.makeText(Single_product.this,"Some error occurred.Please try after som time",Toast.LENGTH_SHORT).show();

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
        cartcounter.setText(String.valueOf(i));
        pus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(i==10){
                    Toast.makeText(Single_product.this,"You cannot add more than 10 quantity",Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(Single_product.this,"Value cannot be less than 1",Toast.LENGTH_SHORT).show();
                }
                else{
                    i--;
                    cartcounter.setText(String.valueOf(i));
                }
            }
        });
        addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor sde = databaseHelper.getAllData();
                prid = new ArrayList<String>();
                while (sde.moveToNext()) {
                    prid.add(sde.getString(4));
                }
                boolean isExist = false;
                for (int i = 0; i < prid.size(); i++) {
                    if (id.equals(prid.get(i))) {
                        Toast.makeText(Single_product.this, "Already in your cart change quantity their.", Toast.LENGTH_SHORT).show();
                        isExist = true;
                        break;
                    }
                }
                if (!isExist) {
                    String amount=priceval.getText().toString();
                    boolean isinserted = databaseHelper.inserdatacart(nametool, slider_image_list.get(0), cartcounter.getText().toString(), id,amount);

                    if (isinserted) {
                        Toast.makeText(Single_product.this, "Item added to cart.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Single_product.this, "Unable to add to cart", Toast.LENGTH_SHORT).show();
                    }
                    MainActivity.notificationCountCart++;
                    NotificationCountSetClass.setNotifyCount(MainActivity.notificationCountCart);
                }

            }
        });
        databaseHelper.close();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) Single_product.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



    private void init() {
        viewPager = (ViewPager) findViewById(R.id.vp_slider);
        ll_dots = (LinearLayout) findViewById(R.id.ll_dots);


        sliderPagerAdapter = new SliderPager(Single_product.this, slider_image_list);
        viewPager.setAdapter(sliderPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[slider_image_list.size()];

        ll_dots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(Color.parseColor("#1469A9"));
            ll_dots.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(Color.parseColor("#FFFFFF"));
    }



}

