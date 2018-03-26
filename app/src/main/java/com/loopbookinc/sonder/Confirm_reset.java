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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Confirm_reset extends AppCompatActivity {
    private static final String TAG = forgot_pw.class.getSimpleName();
    private ProgressDialog pDialog;

    private EditText edtResetCode;
    private EditText edtResetPwEmail;
    private EditText edtNewPassword1;
    private EditText edtNewPassword2;
    private Button btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_reset);

        edtResetCode = findViewById(R.id.edtResetCode2);
        edtResetPwEmail = findViewById(R.id.edtResetPwEmail);
        edtNewPassword1 = findViewById(R.id.edtNewPassword);
        edtNewPassword2 = findViewById(R.id.edtNewPassword2);
        btnReset = findViewById(R.id.btnReset);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Intent intent = getIntent();

        if (intent.hasExtra("email")){
            edtResetPwEmail.setText(intent.getStringExtra("email"));
        }

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edtResetCode.getText().toString().isEmpty() &&
                        !edtResetPwEmail.getText().toString().isEmpty() &&
                        !edtNewPassword1.getText().toString().isEmpty() &&
                        !edtNewPassword2.getText().toString().isEmpty()){

                        if (edtNewPassword1.getText().toString().contentEquals(edtNewPassword2.getText().toString())){
                            String code = edtResetCode.getText().toString().trim();
                            String password = edtNewPassword1.getText().toString().trim();
                            String change_pass = "1";
                            String email = edtResetPwEmail.getText().toString().trim();

                            resetPassword(code,email,password,change_pass);
                        }else{
                            Toast.makeText(getApplicationContext(), "Password not matched", Toast.LENGTH_SHORT).show();
                        }
                    }
            }
        });

    }


    private void resetPassword(final String code,final String email,final String password,final String change_pass) {
        // Tag used to cancel the request
        String tag_string_req = "req_reset";

        pDialog.setMessage("Requesting code ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_RESET_PW, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG,code+","+email+","+password+","+change_pass);
                Log.d(TAG, "Request Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    String error = jObj.getString("reply");
                    Log.e(TAG,"Request status: "+error);
                    if (error.contentEquals("success")) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        //  String uid = jObj.getString("user_id");


                        // Inserting row in users table
                        //    db.addUser(email,"");

                          Toast.makeText(getApplicationContext(), "Password reset successful!", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(
                                Confirm_reset.this,
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
                params.put("code", code);
                params.put("email", email);
                params.put("password", password);
                params.put("change_pass", change_pass);

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
