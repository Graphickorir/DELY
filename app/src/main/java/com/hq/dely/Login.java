package com.hq.dely;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity implements View.OnClickListener{
    EditText etuser,etpass;
    Button blogin,btoreg;
    CheckBox cbdata;
    ProgressBar pbar;
    final String LOGIN_ROOT_URL = "http://192.168.56.1/korirphp/Login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etuser =(EditText) findViewById(R.id.etuser);
        etpass =(EditText) findViewById(R.id.etpass);
        blogin =(Button) findViewById(R.id.blogin);
        btoreg =(Button) findViewById(R.id.btoreg);
        cbdata =(CheckBox) findViewById(R.id.checkbox);
        pbar = (ProgressBar) findViewById(R.id.pbar);

        blogin.setOnClickListener(this);
        btoreg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        View focusView =null;
        boolean cancel=false;

        if (v==blogin){

            if( etuser.getText().toString().length() == 0 ){
                etuser.setError( "Username is required!" );
                focusView =etuser;
                cancel =true;}
            else if(etpass.getText().toString().length()==0){
                etpass.setError("Password is required!");
                focusView =etpass;
                cancel =true;}

            if(cancel)
                focusView.requestFocus();
            else
                loginUser();}

        else if(v==btoreg){
            startActivity(new Intent(this,Register.class));}
    }

    private void loginUser() {
        final String Username = etuser.getText().toString();
        final String Password = etpass.getText().toString();

        pbar.setVisibility(View.VISIBLE);

        StringRequest sRequest = new StringRequest(Request.Method.POST, LOGIN_ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pbar.setVisibility(View.INVISIBLE);
                        try {
                            JSONObject jobject = new JSONObject(response);

                            if(jobject.getString("messo") == "1"){
                                int id = jobject.getInt("Id");
                                String user = jobject.getString("Username");
                                String gender = jobject.getString("Gender");
                                SharedPrefs.getmInstance(Login.this).userlogIn(id,user,gender);

                                AlertDialog.Builder alert = new AlertDialog.Builder(Login.this);
                                alert.setMessage("Company address not set")
                                        .setNeutralButton("Later", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent =new Intent(Login.this, Home.class);
                                                startActivity(intent);
                                            }
                                        })
                                        .setPositiveButton("Set Now", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent =new Intent(Login.this, SignUp.class);
                                                intent.putExtra("Tab",0);
                                                intent.putExtra("choice",cbdata.isChecked());
                                                startActivity(intent);
                                                finish();

                                            }
                                        })
                                        .setNegativeButton("Cancel", null)
                                        .setCancelable(false)
                                        .create()
                                        .show();
                            }
                            else if(jobject.getString("messo") == "2"){
                                int id = jobject.getInt("Id");
                                String user = jobject.getString("Username");
                                String gender = jobject.getString("Gender");
                                SharedPrefs.getmInstance(Login.this).userlogIn(id,user,gender);

                                AlertDialog.Builder alert = new AlertDialog.Builder(Login.this);
                                alert.setMessage("Security Question not Answered")
                                        .setNeutralButton("Later", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent =new Intent(Login.this, Home.class);
                                                startActivity(intent);
                                            }
                                        })
                                        .setPositiveButton("Answer now", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent =new Intent(Login.this, SignUp.class);
                                                intent.putExtra("Tab",1);
                                                intent.putExtra("choice",cbdata.isChecked());
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .setNegativeButton("Cancel", null)
                                        .setCancelable(false)
                                        .create()
                                        .show();
                            }
                            else if(jobject.getString("messo") == "3"){
                                int id = jobject.getInt("Id");
                                String user = jobject.getString("Username");
                                String gender = jobject.getString("Gender");
                                SharedPrefs.getmInstance(Login.this).userlogIn(id,user,gender);

                                AlertDialog.Builder alert = new AlertDialog.Builder(Login.this);
                                alert.setMessage("Payment Method not set")
                                        .setNeutralButton("Later", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent =new Intent(Login.this, Home.class);
                                                startActivity(intent);
                                            }
                                        })
                                        .setPositiveButton("Set Now", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent =new Intent(Login.this, SignUp.class);
                                                intent.putExtra("Tab",2);
                                                intent.putExtra("choice",cbdata.isChecked());
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .setNegativeButton("Cancel", null)
                                        .setCancelable(false)
                                        .create()
                                        .show();
                            }
                            else if (jobject.getString("messo") == "4") {
                                int id = jobject.getInt("Id");
                                String user = jobject.getString("Username");
                                String gender = jobject.getString("Gender");
                                SharedPrefs.getmInstance(Login.this).userlogIn(id,user,gender);

                                Intent intent =new Intent(Login.this, Home.class);
                                intent.putExtra("choice",cbdata.isChecked());
                                startActivity(intent);
                                finish();
                            }
                            else {
                                AlertDialog.Builder alert = new AlertDialog.Builder(Login.this);
                                alert.setMessage("Username or Password is Incorrect")
                                        .setNegativeButton("Retry", null)
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
                        pbar.setVisibility(View.INVISIBLE);
                        AlertDialog.Builder alert = new AlertDialog.Builder(Login.this);
                        alert.setMessage("Failed To Connect")
                                .setNegativeButton("Retry", null)
                                .create()
                                .show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Username", Username);
                params.put("Password", Password);
                return params;
            }
        };
        Singleton.getmInstance(this).addToRequestQueue(sRequest);
    }

    //onbackpressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent =new Intent(Login.this, Home.class);
        startActivity(intent);
    }

    //onpause
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
