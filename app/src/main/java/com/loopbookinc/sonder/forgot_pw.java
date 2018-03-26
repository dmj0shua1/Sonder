package com.loopbookinc.sonder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class forgot_pw extends AppCompatActivity {
    private static final String TAG = forgot_pw.class.getSimpleName();
    private ProgressDialog pDialog;

    private EditText edtResetPwEmail;
    private Button btnReset;
    private Button btnAlreadyReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pw);

        edtResetPwEmail = findViewById(R.id.edtResetPwEmail);
        btnReset = findViewById(R.id.btnResetCode);
        btnAlreadyReset = findViewById(R.id.btnAlreadyReset);
        btnAlreadyReset.setPaintFlags(btnAlreadyReset.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        btnReset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String email = edtResetPwEmail.getText().toString().trim();
                String forget = "1";
                if (!edtResetPwEmail.toString().isEmpty()){

                    resetPassword(email,forget);
                }else{
                    Toast.makeText(getApplicationContext(), "Please enter email", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnAlreadyReset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // Launch login activity
                Intent intent = new Intent(
                        forgot_pw.this,
                        Confirm_reset.class);
                startActivity(intent);

            }
        });
    }


    private void resetPassword(final String email,final String forget) {
        // Tag used to cancel the request
        String tag_string_req = "req_reset";

        pDialog.setMessage("Requesting code ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_FORGOT_PW, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Request Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    String error = jObj.getString("reply");
                    Log.e(TAG,"Request status: "+error);
                    if (error.contentEquals("1") || error.contentEquals("5")) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                      //  String uid = jObj.getString("user_id");
                        if (error.contentEquals("5")) Toast.makeText(getApplicationContext(), "Reset code already exist", Toast.LENGTH_LONG).show();

                        // Inserting row in users table
                        //    db.addUser(email,"");

                      //  Toast.makeText(getApplicationContext(), "Reset code successful", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(
                                forgot_pw.this,
                                Confirm_reset.class);
                        intent.putExtra("email",email);
                        startActivity(intent);
                        finish();
                    } else if (error.contentEquals("2")){

                        Toast.makeText(getApplicationContext(), "Account does not exist", Toast.LENGTH_LONG).show();
                    }else{
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
                Log.e(TAG, "Request Error: " + error.getMessage());
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
                params.put("forget",forget);


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
