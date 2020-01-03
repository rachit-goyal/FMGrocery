package in.aaaos.fmgrocery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.ClientError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreateCustomer extends AppCompatActivity {
    TextInputEditText email,fname,lname,address1,address2,postalcode,phone,password;
    TextView cretae;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_customer);
        email = (TextInputEditText) findViewById(R.id.email);
        fname = (TextInputEditText) findViewById(R.id.firstname);
        password=(TextInputEditText)findViewById(R.id.signuppass);
        lname = (TextInputEditText) findViewById(R.id.lastname);
        address1 = (TextInputEditText) findViewById(R.id.address1name);
        address2 = (TextInputEditText) findViewById(R.id.address2name);
        postalcode = (TextInputEditText) findViewById(R.id.postalname);
        phone = (TextInputEditText) findViewById(R.id.phonename);
        cretae = (TextView) findViewById(R.id.createcus);
        cretae.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email.getText().toString().trim().isEmpty()||password.getText().toString().trim().isEmpty() || fname.getText().toString().trim().isEmpty()
                        || lname.getText().toString().trim().isEmpty() || address1.getText().toString().trim().isEmpty()
                        || postalcode.getText().toString().trim().isEmpty() || phone.getText().toString().trim().isEmpty()) {
                    Toast.makeText(CreateCustomer.this, "Please fill all values", Toast.LENGTH_SHORT).show();

                } else if (!isvalid(email.getText().toString().trim())) {
                    email.setError("Please enter valid email");
                } else if (!isValidMobile(phone.getText().toString().trim())) {
                    email.setError("Please enter valid phone no.");


                }
                else if(password.getText().toString().trim().length()<6){
                    password.setError("password should be greater than 6 character");
                }
                else {
                    JSONObject js = new JSONObject();
                    try {

                        JSONObject jsonObjectt = new JSONObject();
                        JSONObject jsonObjecttsh = new JSONObject();
                        jsonObjectt.put("address_1", address1.getText().toString().trim());
                        jsonObjectt.put("address_2", address2.getText().toString().trim());
                        jsonObjectt.put("city", "Agra");
                        jsonObjectt.put("state", "UP");
                        jsonObjectt.put("phone", phone.getText().toString().trim());
                        jsonObjectt.put("postcode", postalcode.getText().toString().trim());
                        jsonObjectt.put("email", email.getText().toString().trim());
                        jsonObjectt.put("country", "India");
                        jsonObjectt.put("first_name", fname.getText().toString().trim());
                        jsonObjectt.put("last_name", lname.getText().toString().trim());

                        jsonObjecttsh.put("first_name", fname.getText().toString().trim());
                        jsonObjecttsh.put("last_name", lname.getText().toString().trim());
                        jsonObjecttsh.put("address_1", address1.getText().toString().trim());
                        jsonObjecttsh.put("address_2", address2.getText().toString().trim());
                        jsonObjecttsh.put("city", "Agra");
                        jsonObjecttsh.put("state", "UP");
                        jsonObjecttsh.put("postcode", postalcode.getText().toString().trim());
                        jsonObjecttsh.put("country", "India");
                        js.put("billing", jsonObjectt);
                        js.put("shipping", jsonObjecttsh);
                        js.put("email", email.getText().toString().trim());
                        js.put("password", password.getText().toString().trim());
                        js.put("first_name", fname.getText().toString().trim());
                        js.put("last_name", lname.getText().toString().trim());

                        }catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String url = "https://fmgrocery.in/wp-json/wc/v3/customers";

                    try {
                        JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(String.valueOf(js)),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            String value=response.getString("email");
                                            SharedPreferences.Editor editor = getSharedPreferences("Login", MODE_PRIVATE).edit();
                                            editor.putString("email", value);
                                            editor.apply();
                                            Intent i=new Intent(CreateCustomer.this,UpdateOrEditCustomer.class);
                                            startActivity(i);
                                            VolleyLog.v("Response:%n %s", response.toString(4));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if(error instanceof ClientError) {
                                    Toast.makeText(CreateCustomer.this, "Use already exist", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(CreateCustomer.this, "Some error occurred.Please try after somr time", Toast.LENGTH_SHORT).show();

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
            }
        });
    }
    private boolean isvalid(String target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    private static boolean isValidMobile(String mobile)
    {
        return !TextUtils.isEmpty(mobile)&& Patterns.PHONE.matcher(mobile).matches();
    }
}