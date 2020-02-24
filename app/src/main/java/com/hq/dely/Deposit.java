package com.hq.dely;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Deposit extends AppCompatActivity {
    EditText etdepo;
    ProgressBar dpbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);


        etdepo = (EditText) findViewById(R.id.etdepo);
        Button btdepo = (Button) findViewById(R.id.btdepo);
        dpbar = (ProgressBar) findViewById(R.id.dpbar);

        btdepo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View focusView =null;
                boolean cancel=false;

                if (etdepo.getText().toString().equals("")){
                    etdepo.setError( "Amount is required!" );
                    focusView =etdepo;
                    cancel =true;
                }else if (Integer.valueOf(etdepo.getText().toString()) >= 70000){
                    etdepo.setError( "Amount should be less than 70000!" );
                    focusView =etdepo;
                    cancel =true;
                }

                if(cancel)
                    focusView.requestFocus();
                else
                    depositfunds();
            }
        });

    }
    //Volley Deposit
    public void depositfunds() {
        dpbar.setVisibility(View.VISIBLE);
        final String CO_ROOT_URL = "https://"+getResources().getString(R.string.url)+"/deposit.php";
        final int id = this.getSharedPreferences("MySharedPrefs",Context.MODE_PRIVATE).getInt("Id",0);
        StringRequest sRequest = new StringRequest(Request.Method.POST, CO_ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dpbar.setVisibility(View.INVISIBLE);

                        AlertDialog.Builder alert = new AlertDialog.Builder(Deposit.this);
                        alert.setTitle("Done")
                                .setMessage("Wait for mpesa to reply")
                                .setPositiveButton("Ok",  new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Deposit.this,Home.class);
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        depositfunds();
                                    }
                                })
                                .setCancelable(false)
                                .create()
                                .show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Deposit.this, "check connection", Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("userid",id+"");
                params.put("amount",etdepo.getText().toString());
                return params;
            }
        };
        Singleton.getmInstance(Deposit.this).addToRequestQueue(sRequest);
    }
}
