package in.aaaos.fmgrocery;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.ClientError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static in.aaaos.fmgrocery.MainActivity.isValidEmail;

public class NewPreviousCustomer extends AppCompatActivity {
    RadioGroup rg;
    LinearLayout login;
    TextInputEditText email,pass;
    TextView forgot,send;
    TextView loginval,proceed;
    private ProgressDialog dialog;
    EditText editTextMail;
    int MY_SOCKET_TIMEOUT_MS=500;
    //Toolbar toolbar;

    String val=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_previous_customer);
        val=getIntent().getStringExtra("val");
        login=(LinearLayout)findViewById(R.id.login);
        forgot=(TextView)findViewById(R.id.forgot);
        proceed=(TextView)findViewById(R.id.proceed);
        email=(TextInputEditText)findViewById(R.id.email);
        dialog = new ProgressDialog(NewPreviousCustomer.this);
      /*  toolbar=(Toolbar)findViewById(R.id.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });*/
        pass=(TextInputEditText)findViewById(R.id.password);
        loginval=(TextView)findViewById(R.id.loginval);
        rg = (RadioGroup) findViewById(R.id.radioGroup);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radioButton:
                        login.setVisibility(View.GONE);
                      proceed.setVisibility(View.VISIBLE);
                            break;
                    case R.id.radioButton2:
                        proceed.setVisibility(View.GONE);
                        login.setVisibility(View.VISIBLE);
                            break;
                }
            }
        });
        loginval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setMessage("Please Wait");
                dialog.show();
                String url="https://fmgrocery.in/api/auth/generate_auth_cookie/";
                StringRequest str=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            if(jsonObject.getString("status").equals("ok")){
                            JSONObject jsonObject1=jsonObject.getJSONObject("user");
                            String email=jsonObject1.getString("email");
                                String userid=jsonObject1.getString("id");
                                SharedPreferences.Editor editor = getSharedPreferences("Login", MODE_PRIVATE).edit();
                                editor.putString("email", email);
                                editor.putString("userid", userid);
                                editor.apply();

                                    switch (val) {
                                        case "main": {
                                            dialog.dismiss();
                                            Intent intent = new Intent(NewPreviousCustomer.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            break;
                                        }
                                        case "myorder": {
                                            dialog.dismiss();
                                            Intent intent = new Intent(NewPreviousCustomer.this, MyOrders.class);
                                            startActivity(intent);
                                            finish();
                                            break;
                                        }
                                        case "allow": {
                                            dialog.dismiss();
                                            Intent i = new Intent(NewPreviousCustomer.this, UpdateOrEditCustomer.class);
                                            startActivity(i);
                                            finish();
                                            break;
                                        }
                                    }
                                }

                            else{
                                String val=jsonObject.getString("error");
                                Toast.makeText(NewPreviousCustomer.this,val,Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.d("value",response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        Toast.makeText(NewPreviousCustomer.this,"Some error occurred.Please try after some time.",Toast.LENGTH_SHORT).show();

                    }
                }){
                    @Override
                    protected Map<String,String> getParams(){
                        Map<String,String> params = new HashMap<String, String>();
                        params.put("username", email.getText().toString().trim());
                        params.put("password", pass.getText().toString().trim());

                        return params;
                    }
                };
                AppController.getInstance().addToRequestQueue(str);
            }
        });
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(NewPreviousCustomer.this,CreateCustomer.class);
                startActivity(i);
            }
        });
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(NewPreviousCustomer.this);
                dialog.setContentView(R.layout.forgotpaas);
                dialog.setTitle("Enter email address...");
                dialog.setCancelable(true);
                editTextMail=(EditText)dialog.findViewById(R.id.editTextEmail);
                send = (TextView)dialog. findViewById(R.id.buttonSend);
                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String email = editTextMail.getText().toString().trim();

                        if(email.isEmpty()){
                            editTextMail.setError("Field can not be blank");
                        }
                        else if(!isValidEmail(email)){
                            editTextMail.setError("Please enter valid email");
                        }

                        else{
                            if(isNetworkAvailable()) {
                                try {
                                    String url1="https://fmgrocery.in/wp-json/wp/v2/users/lostpassword";
                                    final ProgressDialog pre=new ProgressDialog(NewPreviousCustomer.this);
                                    pre.setMessage("Please wait...");
                                    pre.setCancelable(false);
                                    pre.show();
                                    JSONObject jsonObjectt = new JSONObject();
                                    jsonObjectt.put("user_login",editTextMail.getText().toString().trim());

                                    JsonObjectRequest req = new JsonObjectRequest(url1, new JSONObject(String.valueOf(jsonObjectt)),
                                            new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    pre.dismiss();
                                                    Toast.makeText(NewPreviousCustomer.this, "Password reset Link has been send to your mail id", Toast.LENGTH_SHORT).show();


                                                }
                                            }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            pre.dismiss();
                                            dialog.dismiss();
                                            Toast.makeText(NewPreviousCustomer.this, "Password reset Link has been send to your mail id", Toast.LENGTH_SHORT).show();

                                        }
                                    }){

                                    };
                                    req.setRetryPolicy(new DefaultRetryPolicy(
                                            MY_SOCKET_TIMEOUT_MS,
                                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                    AppController.getInstance().addToRequestQueue(req);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else{
                                Toast.makeText(NewPreviousCustomer.this,"No Network",Toast.LENGTH_SHORT).show();
                            }


                        }

                    }
                });


                dialog.show();
            }
        });
    }

    @Override
    protected void onResume() {

        super.onResume();
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) NewPreviousCustomer.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
