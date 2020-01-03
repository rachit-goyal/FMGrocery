package in.aaaos.fmgrocery;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawer;
    NavigationView navigationView;
    private ViewPager vp_slider;
    private LinearLayout ll_dots;
    int page_position = 0;
    List list;
    DatabaseHelper databaseHelper;
    String image;
    TextView catll;
    RecyclerView products,categories,recyclerView;
    HorizontalAdapter horizontalAdapter;
    Gson gson;
    double latitude, longitude;
    String cityName;
    ComboAdapter comboAdapter;
    private RecyclerView.Adapter mAdapter;
    String[]postTitle;
    Map<String,Object> img;
    Map<String,Object> mapPost;
    ArrayList<String> slider_image_list;
    ArrayList<CatBean> catBeanArrayList;
    public static int notificationCountCart = 0;
    StaggeredGridLayoutManager layoutManager;
    ArrayList<ResBean> resBeanArrayList;
    TextView viewall,viewallcombo;
    LinearLayout searchlinear;
    ArrayList<Combobean> combobeanArrayList;
    String emailval;
    ProgressBar progressBar;
    EditText editTextphone,editTextMail,editTextName,editTextMessage;
    TextView send;
    Toolbar toolbar;
    LinearLayout sevicing;
    RelativeLayout relate;
    SliderPagerAdapter sliderPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
        emailval= prefs.getString("email", null);
     toolbar = (Toolbar) findViewById(R.id.toolbar);
        vp_slider = (ViewPager)findViewById(R.id.vp_slider);
        ll_dots = (LinearLayout)findViewById(R.id.ll_dots);
        setSupportActionBar(toolbar);
        sevicing=(LinearLayout)findViewById(R.id.servicing);
        viewall=(TextView)findViewById(R.id.viewall);
        viewallcombo=(TextView)findViewById(R.id.viewallcombo);
        notificationCountCart=0;
        relate=(RelativeLayout)findViewById(R.id.relate);
        progressBar=(ProgressBar)findViewById(R.id.progress);
        catll=(TextView)findViewById(R.id.catall);
        products=(RecyclerView)findViewById(R.id.products);
        categories=(RecyclerView)findViewById(R.id.category);
        searchlinear=(LinearLayout)findViewById(R.id.searchlinear);
        databaseHelper=new DatabaseHelper(MainActivity.this);
        recyclerView=(RecyclerView)findViewById(R.id.combo);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("FM Grocery");
        toolbar.setTitleTextColor(Color.WHITE);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        relate.setVisibility(View.GONE);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem selectedMenuItem) {
                drawer.closeDrawers();
                if (selectedMenuItem.getItemId() == R.id.nav_order) {
                    Intent i=new Intent(MainActivity.this,MyOrders.class);
                    startActivity(i);
                }
                if (selectedMenuItem.getItemId() == R.id.nav_category) {
                    Intent i=new Intent(MainActivity.this,AllCategories.class);
                    startActivity(i);
                }
                if (selectedMenuItem.getItemId() == R.id.nav_contact) {
                    Intent i=new Intent(MainActivity.this,ContactUs.class);
                    startActivity(i);
                }
                if (selectedMenuItem.getItemId() == R.id.nav_about) {
                    Intent i=new Intent(MainActivity.this,AboutUs.class);
                    startActivity(i);
                }
                if (selectedMenuItem.getItemId() == R.id.nav_kart) {
                    Intent i=new Intent(MainActivity.this,CartActivity.class);
                    startActivity(i);
                }
                if (selectedMenuItem.getItemId() == R.id.nav_feedback) {
                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.custom_feedback);
                    dialog.setTitle("Enquiry...");
                    dialog.setCancelable(true);
                    editTextphone=(EditText)dialog.findViewById(R.id.editTextPhone);
                    editTextMail=(EditText)dialog.findViewById(R.id.editTextEmail);
                    editTextName=(EditText)dialog.findViewById(R.id.editTextName);
                    editTextMessage=(EditText)dialog.findViewById(R.id.editTextMessage);
                    send = (TextView)dialog. findViewById(R.id.buttonSend);
                    send.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String email = editTextMail.getText().toString().trim();
                            String Phone = editTextphone.getText().toString().trim();
                            String name = editTextName.getText().toString().trim();
                            String message = editTextMessage.getText().toString().trim();
                            if(email.isEmpty()){
                                editTextMail.setError("Field can not be blank");
                            }
                            else if(!isValidEmail(email)){
                                editTextMail.setError("Please enter valid email");
                            }
                            else if(name.isEmpty()){
                                editTextName.setError("Field can not be blank");
                            }
                            else if(Phone.isEmpty()){
                                editTextphone.setError("Field can not be blank");
                            }
                            else if(!Phone.matches("^[789]\\d{9}$")){
                                editTextphone.setError("Please enter valid phone no");
                            }
                            else if(message.isEmpty()){
                                editTextMessage.setError("Field can not be blank");
                            }
                            else{
                                if(isNetworkAvailable()) {
                                    String url1="https://fmgrocery.in/Api/emailSend.php";
                                    final ProgressDialog pre=new ProgressDialog(MainActivity.this);
                                    pre.setMessage("Please wait...");
                                    pre.setCancelable(false);
                                    pre.show();
                                    final String tosend="Email:"+editTextMail.getText()+"\n"+"Name:"+editTextName.getText()+"\n"+"Mobile:"+editTextphone.getText()+"\n"+"Message:"+editTextMessage.getText();
                                    final StringRequest stringRequest=new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            pre.dismiss();
                                            dialog.dismiss();
                                            if(response.equals("mail sent")){
                                                Toast.makeText(MainActivity.this,"Thanks for enquiry.We will contact you soon.",Toast.LENGTH_SHORT).show();

                                            }
                                            else if(response.equals("mail not sent")){
                                                Toast.makeText(MainActivity.this,"Some error occur.Please try after some time",Toast.LENGTH_SHORT).show();

                                            }

                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            pre.dismiss();
                                            dialog.dismiss();
                                            if(error instanceof NoConnectionError){
                                                Toast.makeText(MainActivity.this,"No Network",Toast.LENGTH_SHORT).show();

                                            }
                                            else {
                                                Toast.makeText(MainActivity.this,"Some error occur.Please try after some time",Toast.LENGTH_SHORT).show();

                                            }

                                        }
                                    }){
                                        @Override
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            Map<String, String> params = new HashMap<String, String>();
                                            params.put("to", "admin@fmgrocery.in");
                                            params.put("subject", "Enq");
                                            params.put("text", tosend);
                                            params.put("from", "FM Grocery <noreply@fmgrocery.in>");
                                            return params;
                                        }
                                    };
                                    AppController.getInstance().addToRequestQueue(stringRequest);
                                }
                                else{
                                    Toast.makeText(MainActivity.this,"No Network",Toast.LENGTH_SHORT).show();
                                }


                            }

                        }
                    });


                    dialog.show();
                }
                if (selectedMenuItem.getItemId() == R.id.nav_share) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Download FM Grocery from Play Store free"+"\n"+"https://play.google.com/store/apps/details?id=in.aaaos.fmgrocery");
                    startActivity(Intent.createChooser(shareIntent, "Share Via..."));
                }
                return false;

            }
        });
        View header = navigationView.getHeaderView(0);
        TextView loginlogup=(TextView)header.findViewById(R.id.loginlogup);
        if(emailval!=null){
            loginlogup.setText(emailval);
        }
        loginlogup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(emailval!=null){
                    Intent i = new Intent(MainActivity.this, UserProfile.class);
                    startActivity(i);
                }
                else {
                    Intent i = new Intent(MainActivity.this, NewPreviousCustomer.class);
                    i.putExtra("val", "main");
                    startActivity(i);
                }
            }
        });
        navigationView.setItemIconTintList(null);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        catll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this,AllCategories.class);
                startActivity(i);
            }
        });
            viewall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i=new Intent(MainActivity.this,AllProduct.class);
                    startActivity(i);
                }
            });
            viewallcombo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i=new Intent(MainActivity.this,CatProducts.class);
                    i.putExtra("Catid","31");
                    i.putExtra("CatName","FM Combos");
                    startActivity(i);
                }
            });
            searchlinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i=new Intent(MainActivity.this,SearchProduct.class);
                    startActivity(i);
                }
            });
        if (isNetworkAvailable()) {
            checkRunTimePermission();
        } else {
            Toast.makeText(MainActivity.this, "No Network", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkRunTimePermission() {
        String[] permissionArrays = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissionArrays, 11111);
        } else {
            callMethod();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 11111) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callMethod();
            } else {
               Toast.makeText(MainActivity.this,"We are currently servicing in Agra only.Thanks",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void callMethod() {
        find_Location();

    }

    private void find_Location() {
        LocationManager locManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);

        boolean network_enabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean gps_enabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        Location location;

        if (network_enabled) {

            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if(location!=null){
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                getdaaress(latitude,longitude);
            }
            else {
                //loadData();
                init();
                Toast.makeText(MainActivity.this,"We are currently servicing in Agra only.Thanks",Toast.LENGTH_SHORT).show();
            }
        }
        else if(gps_enabled){
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if(location!=null){
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                getdaaress(latitude,longitude);
            }
            else {
                //loadData();
                init();
                Toast.makeText(MainActivity.this,"We are currently servicing in Agra only.Thanks",Toast.LENGTH_SHORT).show();
            }
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Location Services Not Active");
            builder.setMessage("Please enable Location Services");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent,99);
                }
            });
            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==99){
            callMethod();
        }
        else{
            Toast.makeText(MainActivity.this,"We are currently servicing in Agra only.Thanks",Toast.LENGTH_SHORT).show();
            //getdata();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getdaaress(Double lat, Double lon) {
        Geocoder geoCoder = new Geocoder(MainActivity.this, Locale.getDefault()); //it is Geocoder
        try {
            List<Address> address = geoCoder.getFromLocation(lat, lon, 1);
            cityName = address.get(0).getLocality();
            loadData();
        } catch (IOException e) {
            Log.d("exception",e.getMessage());
        }
        catch (NullPointerException e) {
            Log.d("exception",e.getMessage());

        }
    }

    private void loadData() {
        if(cityName.equalsIgnoreCase("Agra")){
            init();
        }
        else{
            //init();
            sevicing.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this,"We are currently servicing in Agra only.Thanks",Toast.LENGTH_SHORT).show();
        }

    }

    private void init() {
        progressBar.setVisibility(View.VISIBLE);
        if(isNetworkAvailable()){
            catBeanArrayList=new ArrayList<>();
            resBeanArrayList=new ArrayList<>();
            combobeanArrayList=new ArrayList<>();
            slider_image_list = new ArrayList<>();
            String url = "https://fmgrocery.in/wp-json/wp/v2/posts/?page=1&categories=1";


            StringRequest str = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if(relate.getVisibility()==View.GONE){
                        relate.setVisibility(View.VISIBLE);
                    }
                    if(progressBar.getVisibility()==View.VISIBLE){
                        progressBar.setVisibility(View.GONE);
                    }
                    gson = new Gson();
                    list = (List) gson.fromJson(response, List.class);
                    postTitle = new String[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        mapPost = (Map<String, Object>) list.get(i);
                        img = (Map<String, Object>) mapPost.get("better_featured_image");
                        image = (String) img.get("source_url");
                        slider_image_list.add(image);
                    }
                    sliderPagerAdapter = new SliderPagerAdapter(MainActivity.this, slider_image_list);

                    vp_slider.setAdapter(null);
                    vp_slider.setAdapter(sliderPagerAdapter);
                    vp_slider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
                    addBottomDots(0);
                    final Handler handler = new Handler();

                    final Runnable update = new Runnable() {
                        public void run() {
                            if(slider_image_list.size()!=0){
                                vp_slider.setVisibility(View.VISIBLE);
                            }
                            if (page_position == slider_image_list.size()) {
                                page_position = 0;
                            } else {
                                page_position = page_position + 1;
                            }
                            sliderPagerAdapter.notifyDataSetChanged();

                            vp_slider.setCurrentItem(page_position, true);
                        }
                    };
                    new Timer().schedule(new TimerTask() {

                        @Override
                        public void run() {
                            handler.post(update);
                        }
                    }, 100, 5000);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            str.setRetryPolicy(new DefaultRetryPolicy(500000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(str);
           /* */
            }
        else{
            vp_slider.setVisibility(View.GONE);
        }
        databaseHelper.close();
        new Thread(new Runnable() {
            public void run() {
                Cursor sde = databaseHelper.getAllData();
                while (sde.moveToNext()) {
                    MainActivity.notificationCountCart++;
                }
                String urlpro = "https://fmgrocery.in/wp-json/wc/v3/products";
                StringRequest stringRequest=new StringRequest(Request.Method.GET, urlpro, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(relate.getVisibility()==View.GONE){
                            relate.setVisibility(View.VISIBLE);
                        }
                        if(progressBar.getVisibility()==View.VISIBLE){
                            progressBar.setVisibility(View.GONE);
                        }
                        try {
                            JSONArray jsonArray=new JSONArray(response);
                            for(int i=0;i<jsonArray.length();i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String stockstatus = jsonObject.getString("stock_status");
                                if (stockstatus .equals( "instock")) {
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
                                    resBeanArrayList.add(resBean);
                                }
                            }
                            horizontalAdapter=new HorizontalAdapter(MainActivity.this,resBeanArrayList);
                            LinearLayoutManager horizontalLayoutManagaer
                                    = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
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
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String urlcat = "https://fmgrocery.in/wp-json/wc/v3/products/categories?per_page=50";
                StringRequest stri=new StringRequest(Request.Method.GET, urlcat, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(relate.getVisibility()==View.GONE){
                            relate.setVisibility(View.VISIBLE);
                        }
                        if(progressBar.getVisibility()==View.VISIBLE){
                            progressBar.setVisibility(View.GONE);
                        }
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
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);                            recyclerView.setLayoutManager(layoutManager);
                            categories.setLayoutManager(mLayoutManager);
                            mAdapter = new CategoryRecycler(MainActivity.this, catBeanArrayList);
                            categories.setAdapter(mAdapter);

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
                stri.setRetryPolicy(new DefaultRetryPolicy(500000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                AppController.getInstance().addToRequestQueue(stri);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String urlcat = "https://fmgrocery.in/wp-json/wc/v2/products?category=31";
                StringRequest stri=new StringRequest(Request.Method.GET, urlcat, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(relate.getVisibility()==View.GONE){
                            relate.setVisibility(View.VISIBLE);
                        }
                        if(progressBar.getVisibility()==View.VISIBLE){
                            progressBar.setVisibility(View.GONE);
                        }
                        int len;
                        try {
                            JSONArray jsonArray=new JSONArray(response);
                            if(jsonArray.length()>4){
                                len=4;
                            }
                            else{
                                len=jsonArray.length();
                            }
                            for(int i=0;i<len;i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String stockstatus = jsonObject.getString("in_stock");
                                if (stockstatus.equals("true")) {
                                    Combobean resBean = new Combobean();
                                    String id = jsonObject.getString("id");
                                    String name = jsonObject.getString("name");
                                    String description = jsonObject.getString("description");
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
                                    resBean.setDesc(description);
                                    resBean.setRegularprice(regularprice);
                                    resBean.setOnsale(onsale);

                                    combobeanArrayList.add(resBean);
                                }
                            }
                            recyclerView.setNestedScrollingEnabled(false);
                            recyclerView.setHasFixedSize(true);
                            layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

                            recyclerView.setLayoutManager(layoutManager);

                            comboAdapter = new ComboAdapter(MainActivity.this,combobeanArrayList);
                            recyclerView.setAdapter(comboAdapter);
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
                stri.setRetryPolicy(new DefaultRetryPolicy(500000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                AppController.getInstance().addToRequestQueue(stri);
            }
        }).start();
    }
    private void addBottomDots(int currentPage) {
        TextView[] dots = new TextView[slider_image_list.size()];

        ll_dots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(MainActivity.this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(Color.parseColor("#673ab7"));
            ll_dots.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(Color.parseColor("#FFFFFF"));
    }
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_cart) {
            if(isNetworkAvailable()) {
                startActivity(new Intent(MainActivity.this, CartActivity.class));
                return true;
            }
            else{
                Toast.makeText(MainActivity.this,"No Network",Toast.LENGTH_SHORT).show();

            }
        }

        if (id == R.id.action_user) {
            if(isNetworkAvailable()) {
                startActivity(new Intent(MainActivity.this,UserProfile.class));
                return true;
            }
            else{
                Toast.makeText(MainActivity.this,"No Network",Toast.LENGTH_SHORT).show();

            }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem item = menu.findItem(R.id.action_cart);
        NotificationCountSetClass.setAddToCart(MainActivity.this, item,notificationCountCart);


        invalidateOptionsMenu();
        return super.onPrepareOptionsMenu(menu);
    }
    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}
