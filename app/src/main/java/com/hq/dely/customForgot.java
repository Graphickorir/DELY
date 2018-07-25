package com.hq.dely;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class customForgot extends AppCompatActivity implements View.OnClickListener{
    EditText etforpass,etconfor;
    Button bfor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_forgot);

        etforpass = (EditText) findViewById(R.id.etforpass);
        etconfor = (EditText) findViewById(R.id.etconfor);
        bfor = (Button) findViewById(R.id.bfor);

        bfor.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == bfor){
            int pas = etforpass.getText().toString().length();
            View focusView = null;
            boolean cancel = false;

            if (pas < 8){
                etforpass.setError("Password should be more than eight characters!");
                focusView =etforpass;
                cancel=true;
            }
            else if (!etconfor.getText().toString().equals(etforpass.getText().toString())){
                etconfor.setError("Passwords Don't Match");
                focusView =etconfor;
                cancel=true;
            }

            if(cancel)
                focusView.requestFocus();
            else
                updatepass();
        }
    }

    private void updatepass() {
        final String pass = etforpass.getText().toString();
        Intent i = getIntent();
        final String Email = i.getStringExtra("Email");
        final String CO_ROOT_URL = "http://"+getResources().getString(R.string.url)+"/korirphp/updatepass.php";

        StringRequest sRequest = new StringRequest(Request.Method.POST, CO_ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            if(json.getString("messo") == "1"){
                                AlertDialog.Builder alert = new AlertDialog.Builder(customForgot.this);
                                alert.setMessage("Password updated successfully!")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        })
                                        .setCancelable(false)
                                        .create()
                                        .show();
                            }else if(json.getString("messo") == "2"){
                                AlertDialog.Builder alert = new AlertDialog.Builder(customForgot.this);
                                alert.setMessage("Failed to update password try later")
                                        .setPositiveButton("Retry", null)
                                        .setCancelable(true)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(customForgot.this);
                        alert.setMessage("Failed To Connect!")
                                .setPositiveButton("Retry", null)
                                .setCancelable(false)
                                .create()
                                .show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("pass",pass);
                params.put("email", Email);
                return params;
            }
        };

        Singleton.getmInstance(this).addToRequestQueue(sRequest);

    }
}
