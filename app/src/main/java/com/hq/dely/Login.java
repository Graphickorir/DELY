package com.hq.dely;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
    EditText etuser,etpass,etemail;
    Button blogin,btoreg,btforgot;
    CheckBox cbdata;
    ProgressBar pbar;
    TextView forgot;
    LinearLayout lvforgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etuser =(EditText) findViewById(R.id.etuser);
        etpass =(EditText) findViewById(R.id.etpass);
        blogin =(Button) findViewById(R.id.blogin);
        btoreg =(Button) findViewById(R.id.btoreg);
        cbdata =(CheckBox) findViewById(R.id.checkbox);
        forgot = (TextView) findViewById(R.id.forgot);
        lvforgot = (LinearLayout) findViewById(R.id.lvforgot);
        etemail =(EditText) findViewById(R.id.etemail);
        btforgot =(Button) findViewById(R.id.btforgot);
        pbar = (ProgressBar) findViewById(R.id.pbar);

        forgot.setOnClickListener(this);
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

        else if(v==forgot){
            lvforgot.setVisibility(View.VISIBLE);

            btforgot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View focusView = null;
                    boolean cancel = false;
                    if (etemail.getText().toString().length() == 0){
                        etemail.setError("Email is required!");
                        focusView =etemail;
                        cancel=true;
                    }
                    else if (!isValidEmail(etemail.getText().toString())) {
                        etemail.setError("Email is Invalid!");
                        focusView =etemail;
                        cancel=true;
                    }
                    if(cancel)
                        focusView.requestFocus();
                    else
                        getSecurityAns();
                }
            });
        }
    }
    private void getSecurityAns(){
        final String email = etemail.getText().toString();
        String LOGIN_ROOT_URL = "http://"+getResources().getString(R.string.url)+"/korirphp/forgot.php";

        StringRequest sRequest = new StringRequest(Request.Method.POST, LOGIN_ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jobject = new JSONObject(response);
                            if (jobject.getString("messo") == "1") {
                                AlertDialog.Builder custombuilder = new AlertDialog.Builder(Login.this);
                                LayoutInflater inflater = LayoutInflater.from(Login.this);
                                View dialogView = inflater.inflate(R.layout.custom_forgot, null);
                                custombuilder.setTitle("Answer security question ");
                                custombuilder.setView(dialogView);
                                custombuilder.setCancelable(true);

                                TextView tvque = (TextView) dialogView.findViewById(R.id.tvque);
                                final TextView etans = (TextView) dialogView.findViewById(R.id.etans);
                                tvque.setText("Question: "+jobject.getString("Question"));
                                final String ans = jobject.getString("Answer");
                                custombuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (etans.getText().toString().equals(ans) ){
                                            Intent intent =new Intent(Login.this,customForgot.class);
                                            intent.putExtra("Email",email);
                                            startActivity(intent);
                                        }else{
                                            Toast.makeText(Login.this, "Wrong answer ! \nCheck spelling and capitalization", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                AlertDialog customalert = custombuilder.create();
                                customalert.show();
                            }else if (jobject.getString("messo") == "2"){
                                Toast.makeText(Login.this, "Security question not set", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(Login.this, "Email address doesn't exist", Toast.LENGTH_SHORT).show();
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
                params.put("Email", email);
                return params;
            }
        };
        Singleton.getmInstance(this).addToRequestQueue(sRequest);
    }

    private void loginUser() {
        final String Username = etuser.getText().toString();
        final String Password = etpass.getText().toString();
        String LOGIN_ROOT_URL = "http://"+getResources().getString(R.string.url)+"/korirphp/Login.php";
        pbar.setVisibility(View.VISIBLE);

        StringRequest sRequest = new StringRequest(Request.Method.POST, LOGIN_ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pbar.setVisibility(View.INVISIBLE);
                        try {
                            JSONObject jobject = new JSONObject(response);

                            if(jobject.getString("messo") == "1"){
                                int id = Integer.parseInt((jobject.getString("Id")));
                                String name = jobject.getString("Name");
                                String user = jobject.getString("Username");
                                String pass = jobject.getString("Password");
                                String email = jobject.getString("Email");
                                int phone = Integer.parseInt((jobject.getString("Phone")));
                                String gender = jobject.getString("Gender");

                                SharedPrefs.getmInstance(Login.this).userlogIn(user,gender);
                                SharedPrefs.getmInstance(Login.this).userChecked(cbdata.isChecked());
                                SharedPrefs.getmInstance(Login.this).userDetails(id,name,pass,email,phone);

                                AlertDialog.Builder alert = new AlertDialog.Builder(Login.this);
                                alert.setMessage("Company address not set")
                                        .setNeutralButton("Later", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        })
                                        .setPositiveButton("Set Now", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent =new Intent(Login.this, SignUp.class);
                                                intent.putExtra("Tab",0);
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
                                int id = Integer.parseInt((jobject.getString("Id")));
                                String name = jobject.getString("Name");
                                String user = jobject.getString("Username");
                                String pass = jobject.getString("Password");
                                String email = jobject.getString("Email");
                                int phone = Integer.parseInt((jobject.getString("Phone")));
                                String gender = jobject.getString("Gender");

                                SharedPrefs.getmInstance(Login.this).userlogIn(user,gender);
                                SharedPrefs.getmInstance(Login.this).userChecked(cbdata.isChecked());
                                SharedPrefs.getmInstance(Login.this).userDetails(id,name,pass,email,phone);

                                AlertDialog.Builder alert = new AlertDialog.Builder(Login.this);
                                alert.setMessage("Security Question not Answered")
                                        .setNeutralButton("Later", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        })
                                        .setPositiveButton("Answer now", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent =new Intent(Login.this, SignUp.class);
                                                intent.putExtra("Tab",1);
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
                                int id = Integer.parseInt((jobject.getString("Id")));
                                String name = jobject.getString("Name");
                                String user = jobject.getString("Username");
                                String pass = jobject.getString("Password");
                                String email = jobject.getString("Email");
                                int phone = Integer.parseInt((jobject.getString("Phone")));
                                String gender = jobject.getString("Gender");

                                SharedPrefs.getmInstance(Login.this).userlogIn(user,gender);
                                SharedPrefs.getmInstance(Login.this).userChecked(cbdata.isChecked());
                                SharedPrefs.getmInstance(Login.this).userDetails(id,name,pass,email,phone);

                                AlertDialog.Builder alert = new AlertDialog.Builder(Login.this);
                                alert.setMessage("Payment Method not set")
                                        .setNeutralButton("Later", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        })
                                        .setPositiveButton("Set Now", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent =new Intent(Login.this, SignUp.class);
                                                intent.putExtra("Tab",2);
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
                                int id = Integer.parseInt((jobject.getString("Id")));
                                String name = jobject.getString("Name");
                                String user = jobject.getString("Username");
                                String pass = jobject.getString("Password");
                                String email = jobject.getString("Email");
                                int phone = Integer.parseInt((jobject.getString("Phone")));
                                String gender = jobject.getString("Gender");

                                SharedPrefs.getmInstance(Login.this).userlogIn(user,gender);
                                SharedPrefs.getmInstance(Login.this).userChecked(cbdata.isChecked());
                                SharedPrefs.getmInstance(Login.this).userDetails(id,name,pass,email,phone);
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

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

//    //onbackpressed
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent intent =new Intent(Login.this, Home.class);
//        startActivity(intent);
//    }
//
//    //onpause
//    @Override
//    protected void onPause() {
//        super.onPause();
//        finish();
//    }
}
