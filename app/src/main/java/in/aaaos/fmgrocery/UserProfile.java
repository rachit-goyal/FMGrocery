package in.aaaos.fmgrocery;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Base64;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserProfile extends AppCompatActivity {
    TextInputEditText email,fname,lname,add1,add2,postcode,phone;
    String emailval,idval;
    TextView edit,logout;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
        emailval= prefs.getString("email", null);
        edit=(TextView)findViewById(R.id.edit);
        email=(TextInputEditText)findViewById(R.id.email);
        logout=(TextView)findViewById(R.id.logout);
        dialog = new ProgressDialog(UserProfile.this);

        phone=(TextInputEditText)findViewById(R.id.phone);
        fname=(TextInputEditText)findViewById(R.id.firstname);
        lname=(TextInputEditText)findViewById(R.id.lastname);
        add1=(TextInputEditText)findViewById(R.id.address1name);
        add2=(TextInputEditText)findViewById(R.id.address2name);
        postcode=(TextInputEditText)findViewById(R.id.postalname);
        if(isNetworkAvailable()){
            getData();
        }
        else{
            Toast.makeText(UserProfile.this,"No Network",Toast.LENGTH_SHORT).show();
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
                    fname.setEnabled(false);
                    lname.setEnabled(false);
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
    }

    private void getData() {
        this.dialog.setMessage("Please Wait");
        this.dialog.show();
        if(emailval!=null){
            String url="https://fmgrocery.in/wp-json/wc/v3/customers/?email="+emailval;
            StringRequest str=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    dialog.dismiss();
                    try {
                        JSONArray jsonArray=new JSONArray(response);
                        JSONObject jsonObject=jsonArray.getJSONObject(0);
                        idval=jsonObject.getString("id");
                        email.setText(emailval);
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
                    Toast.makeText(UserProfile.this,"Some error occurred.Please try after some time.",Toast.LENGTH_SHORT).show();
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
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor editor = getSharedPreferences("Login", MODE_PRIVATE).edit();
                    editor.putString("email", null);
                    editor.putString("userid", null);

                    editor.apply();
                    Intent intent = new Intent(UserProfile.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
        }
        else{
            Intent i=new Intent(UserProfile.this,NewPreviousCustomer.class);
            i.putExtra("val","main");
            startActivity(i);
            finish();
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) UserProfile.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
