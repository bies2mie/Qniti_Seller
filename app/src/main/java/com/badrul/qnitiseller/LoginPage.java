package com.badrul.qnitiseller;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginPage extends AppCompatActivity {

    //boolean variable to check user is logged in or not
    //initially it is false
    private boolean loggedIn = false;
    List<User> userList;
    String userEmailID;
    String passwordP;
    private EditText inputICnum;
    private EditText inputPassword;
    int sellerID;
    String sellerUserName;
    String sellerName;
    String sellerPhone;
    String sellerLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);


        inputICnum = findViewById(R.id.icnum);
        inputPassword = findViewById(R.id.password);

        userList = new ArrayList<>();
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                userEmailID = inputICnum.getText().toString().trim();
                passwordP = inputPassword.getText().toString().trim();
                // Check for empty data in the form
                if (!userEmailID.isEmpty() && !passwordP.isEmpty()) {
                    // login user
                    checkLogin();

                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),"Please enter login Info",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

    }    @Override
    protected void onResume() {
        super.onResume();
        //In onresume fetching value from sharedpreference
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        //Fetching the boolean value form sharedpreferences
        loggedIn = sharedPreferences.getBoolean(Config.LOGGEDIN_SHARED_PREF, false);

        //If we will get true
        if(loggedIn){
            //We will start the Profile Activity
            Intent intent = new Intent(LoginPage.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void checkLogin(){
        //Getting values from edit texts
        final ProgressDialog loading = ProgressDialog.show(this,"Please Wait","Contacting Server",false,false);
        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        //If we are getting success from server
                        if(response.equalsIgnoreCase(Config.LOGIN_SUCCESS)){
                            //Creating a shared preference
                            SharedPreferences sharedPreferences = LoginPage.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

                            //Creating editor to store values to shared preferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            //Adding values to editor
                            editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, true);
                            editor.putString(Config.ID_SHARED_PREF, userEmailID);

                            //Saving values to editor
                            editor.commit();

                            loadUser();

                        }else{
                            //If the server response is not success
                            //Displaying an error message on toast
                            loading.dismiss();
                            Toast.makeText(LoginPage.this, "Invalid ID or password", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                        loading.dismiss();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(LoginPage.this,"No internet . Please check your connection",
                                    Toast.LENGTH_LONG).show();
                        }
                        else{

                            Toast.makeText(LoginPage.this, error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }){


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                //Adding parameters to request
                params.put("sellerUserName", "seller1");
                params.put("sellerPass", "123");

                //returning parameter
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void loadUser(){
        final ProgressDialog loading = ProgressDialog.show(this,"Please Wait","Contacting Server",false,false);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.PROFILE+userEmailID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject user = array.getJSONObject(i);

                                //adding the product to product list
                                userList.add(new User(
                                        sellerID = user.getInt("sellerID"),
                                        sellerUserName = user.getString("sellerUserName"),
                                        sellerName = user.getString("sellerName"),
                                        sellerPhone = user.getString("sellerPhone"),
                                        sellerLocation = user.getString("sellerLocation")

                                ));

                            }

                            //add shared preference ID,nama,credit here
                            SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME,
                                    Context.MODE_PRIVATE);

                            // Creating editor to store values to shared preferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            // Adding values to editor

                            editor.putString(Config.S_ID2, String.valueOf(sellerID));
                            editor.putString(Config.SELLER_ID2, sellerUserName);
                            editor.putString(Config.SELLER_NAME2, sellerName);
                            editor.putString(Config.SELLER_PHONE2, sellerPhone);
                            editor.putString(Config.SELLER_LOCATION, sellerLocation);

                            // Saving values to editor
                            editor.commit();

                            loading.dismiss();

                            //Starting profile activity
                            Intent intent = new Intent(LoginPage.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                            loading.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(LoginPage.this,"No internet . Please check your connection",
                                    Toast.LENGTH_LONG).show();
                        }
                        else{

                            Toast.makeText(LoginPage.this, error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //adding our stringrequest to queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}