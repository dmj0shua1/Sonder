package com.loopbookinc.sonder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.loopbookinc.sonder.app.AppConfig;
import com.loopbookinc.sonder.app.AppController;
import com.loopbookinc.sonder.helper.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VerifyRegistration extends AppCompatActivity {
    private static final String TAG = VerifyRegistration.class.getSimpleName();
    private ProgressDialog pDialog;
    private SQLiteHandler db;

    private EditText edtVCode;
    private Button btnVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_registration);

        btnVerify = (findViewById(R.id.btnVerify));
        edtVCode = (findViewById(R.id.edtVCode));

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input_vcode = edtVCode.getText().toString().trim();
                String vcode = getIntent().getStringExtra("vCode");
                String email = getIntent().getStringExtra("email");

                if (vcode.contentEquals(input_vcode)){
                    registerUser(email,vcode,"1");
                }else{
                    String errorMsg = "Verification code invalid";
                    Toast.makeText(getApplicationContext(),
                            errorMsg, Toast.LENGTH_LONG).show();
                }


            }
        });
    }

    private void registerUser(final String email,
                              final String signupCode,final String confirmSignup) {
        // Tag used to cancel the request
        String tag_string_req = "req_verify";

        pDialog.setMessage("Verifying ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Verification Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    String error = jObj.getString("reply");
                    Log.e(TAG,"Verification status: "+error);
                    if (error.contentEquals("success")) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                          String uid = jObj.getString("user_id");


                        // Inserting row in users table
                    //    db.addUser(email,"");

                        Toast.makeText(getApplicationContext(), "Verification successful", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(
                                VerifyRegistration.this,
                                LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Verification Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("signup_code", signupCode);
                params.put("confirm_signup",confirmSignup);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }
    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }




}
