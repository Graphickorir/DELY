package com.hq.dely;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class Register extends AppCompatActivity implements View.OnClickListener{
    EditText etname, etuser, etpass,etemail,etphone,etconfirm;
    RadioGroup rggender;
    Button breg,bclear;
    ViewGroup linear;
    ProgressBar regpbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etname = (EditText) findViewById(R.id.etname);
        etuser = (EditText) findViewById(R.id.etuser);
        etpass = (EditText) findViewById(R.id.etpass);
        etconfirm = (EditText) findViewById(R.id.etconfirm);
        etemail = (EditText) findViewById(R.id.etemail);
        etphone = (EditText) findViewById(R.id.etphone);
        rggender = (RadioGroup) findViewById(R.id.rggender);
        breg = (Button) findViewById(R.id.breg);
        linear = (ViewGroup) findViewById(R.id.lyreg);
        bclear = (Button) findViewById(R.id.bclear);
        regpbar = (ProgressBar) findViewById(R.id.regpbar);

        breg.setOnClickListener(this);
        bclear.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == breg){
            int phn = etphone.getText().toString().length();
            int pas = etpass.getText().toString().length();
            View focusView = null;
            boolean cancel = false;

            if (etname.getText().toString().length() == 0){
                etname.setError("Name is required!");
                focusView = etname;
                cancel=true;
            }
            else if (etuser.getText().toString().length() == 0){
                etuser.setError("Username is required!");
                focusView = etuser;
                cancel=true;
            }
            else if (pas == 0){
                etpass.setError("Password is required!");
                focusView =etpass;
                cancel=true;
            }
            else if (pas < 8){
                etpass.setError("Password should be more than eight characters!");
                focusView =etpass;
                cancel=true;
            }
            else if (!etconfirm.getText().toString().equals(etpass.getText().toString())){
                etconfirm.setError("Passwords Don't Match");
                focusView =etconfirm;
                cancel=true;
            }
            else if (etemail.getText().toString().length() == 0){
                etemail.setError("Email is required!");
                focusView =etemail;
                cancel=true;
            }
            else if (!isValidEmail(etemail.getText().toString())) {
                etemail.setError("Email is Invalid!");
                focusView =etemail;
                cancel=true;
            }
            else if (phn == 0){
                etphone.setError("Phone Number is required!");
                focusView =etphone;
                cancel=true;
            }
            else if (phn >= 10){
                etphone.setError("invalid Phone Number!");
                focusView =etphone;
                cancel=true;
            }


            if(cancel)
                focusView.requestFocus();
            else if (rggender.getCheckedRadioButtonId() == -1)
                Toast.makeText(Register.this, "Please Choose Gender", Toast.LENGTH_SHORT).show();
            else
                registerUser();
        }

        else if(v==bclear){
            resetForm(linear);
        }

    }

    private void registerUser() {
        String REG_ROOT_URL = "http://"+getResources().getString(R.string.url)+"/korirphp/Reg.php";
        int chosen = rggender.getCheckedRadioButtonId();
        RadioButton rb = (RadioButton) rggender.findViewById(chosen);

        final String Name = etname.getText().toString();
        final String Username = etuser.getText().toString();
        final String Password = etpass.getText().toString();
        final String Email = etemail.getText().toString();
        final int Phone = Integer.parseInt(etphone.getText().toString());
        final String Gender = rb.getText().toString();

        regpbar.setVisibility(View.VISIBLE);

        StringRequest sRequest = new StringRequest(Request.Method.POST, REG_ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        regpbar.setVisibility(View.INVISIBLE);
                        try {
                            JSONObject jobject = new JSONObject(response);
                            if (jobject.getString("messo") == "1") {
                                AlertDialog.Builder alert = new AlertDialog.Builder(Register.this);
                                alert.setMessage("Registered Successfully")
                                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                SharedPrefs.getmInstance(Register.this).userlogIn(Username,Gender);
                                                Intent intent = new Intent(Register.this, SignUp.class);
                                                Register.this.startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .create()
                                        .show();
                            } else if (jobject.getString("messo") == "2") {
                                AlertDialog.Builder alert = new AlertDialog.Builder(Register.this);
                                alert.setMessage("User Already Exists")
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                            } else if (jobject.getString("messo") == "3") {
                                AlertDialog.Builder alert = new AlertDialog.Builder(Register.this);
                                alert.setMessage("Username Already Exists")
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                            } else {
                                AlertDialog.Builder alert = new AlertDialog.Builder(Register.this);
                                alert.setMessage("Register Failed")
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
                        regpbar.setVisibility(View.INVISIBLE);
                        AlertDialog.Builder alert = new AlertDialog.Builder(Register.this);
                        alert.setMessage("Failed To Connect")
                                .setNegativeButton("Retry", null)
                                .create()
                                .show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Name", Name);
                params.put("Username", Username);
                params.put("Password", Password);
                params.put("Email", Email);
                params.put("Phone", Phone + "");
                params.put("Gender", Gender);

                return params;
            }
        };
        Singleton.getmInstance(this).addToRequestQueue(sRequest);
    }

    public static void resetForm(ViewGroup group) {
        for (int i = 0, count = group.getChildCount(); i < count; ++i) {
            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                ((EditText) view).getText().clear();
            }

            if (view instanceof RadioGroup) {
                ((RadioButton)((RadioGroup) view).getChildAt(0)).setChecked(false);
                ((RadioButton)((RadioGroup) view).getChildAt(1)).setChecked(false);
            }

            if (view instanceof ViewGroup && (((ViewGroup) view).getChildCount() > 0))
                resetForm((ViewGroup) view);
        }
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }


}
