package com.hq.dely;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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


/**
 * Created by Korir on 2/16/2018.
 */

public class Profile_Frag extends Fragment implements View.OnClickListener{
    TextView protvname,protvmale,protvemail,protvphone,tvchangeemail,tvchangephone,previously,contact;
    EditText etproemail,etprophone,etpropass,etpronewpass,etproconpass;
    Button btchangepass;
    ImageView proicon;
    SharedPreferences prefs;
    String current;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        boolean isloged = SharedPrefs.getmInstance(getActivity()).UserIsLoged();
        if(isloged){
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        prefs = getActivity().getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE);
            proicon = (ImageView) rootView.findViewById(R.id.proicon);
            protvname = (TextView) rootView.findViewById(R.id.protvname);
            protvmale = (TextView) rootView.findViewById(R.id.protvmale);
            protvemail = (TextView) rootView.findViewById(R.id.protvemail);
            protvphone = (TextView) rootView.findViewById(R.id.protvphone);
            tvchangeemail = (TextView) rootView.findViewById(R.id.tvchangeemail);
            tvchangephone = (TextView) rootView.findViewById(R.id.tvchangephone);
            previously = (TextView) rootView.findViewById(R.id.previously);
            contact = (TextView) rootView.findViewById(R.id.contact);
            etproemail = (EditText) rootView.findViewById(R.id.etproemail);
            etprophone = (EditText) rootView.findViewById(R.id.etprophone);
            etpropass = (EditText) rootView.findViewById(R.id.etpropass);
            etpronewpass = (EditText) rootView.findViewById(R.id.etpronewpass);
            etproconpass = (EditText) rootView.findViewById(R.id.etproconpass);
            btchangepass = (Button) rootView.findViewById(R.id.btchangepass);

            protvname.setText(prefs.getString("Name",null));
            protvname.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            String gender = prefs.getString("Gender",null);
            current = prefs.getString("Password",null);
            protvmale.setText(gender);
            protvemail.setText(prefs.getString("Email",null));
            protvphone.setText("0"+prefs.getInt("Phone",0));

            if (gender.equals("Male")){
                proicon.setImageResource(R.drawable.maleavatar);
            }else if (gender.equals("Female")){
                proicon.setImageResource(R.drawable.femaleavatar);
            }

            tvchangeemail.setOnClickListener(this);
            tvchangephone.setOnClickListener(this);
            btchangepass.setOnClickListener(this);
            previously.setOnClickListener(this);
            contact.setOnClickListener(this);

        return rootView;
        }
        else{
            View rootView = inflater.inflate(R.layout.fragpronet, container, false);
            ImageView ivnonet = (ImageView) rootView.findViewById(R.id.ivnonet);
            ivnonet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setUserVisibleHint(true);
                }
            });
            return rootView;
        }
    }

    private void changeEmail(){
        final String CO_ROOT_URL = "http://"+getResources().getString(R.string.url)+"/korirphp/changeemail.php";
        final String user = getActivity().getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE).getString("Username",null);
        final String email = etproemail.getText().toString();

        StringRequest sRequest = new StringRequest(Request.Method.POST, CO_ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json= new JSONObject(response);
                            if(json.getString("messo") == "1"){
                                SharedPrefs.getmInstance(getActivity()).changeEmail(json.getString("email"));
                                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                                alert.setMessage("Email Changed successfully!")
                                        .setPositiveButton("Ok",null)
                                        .setCancelable(true)
                                        .create()
                                        .show();
                            }else if(json.getString("messo") == "2"){
                                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                                alert.setMessage("Invalid Email")
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
                        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                        alert.setMessage("Failed To Change Email")
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        changeEmail();
                                    }
                                })
                                .setCancelable(false)
                                .create()
                                .show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user",user);
                params.put("email",email);
                return params;
            }
        };
        Singleton.getmInstance(getActivity()).addToRequestQueue(sRequest);
    }

    private void changePhone(){
        final String CO_ROOT_URL = "http://"+getResources().getString(R.string.url)+"/korirphp/changephone.php";
        final String user = getActivity().getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE).getString("Username",null);
        final int phone = Integer.parseInt(etprophone.getText().toString());

        StringRequest sRequest = new StringRequest(Request.Method.POST, CO_ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json= new JSONObject(response);
                            if(json.getString("messo") == "1"){
                                SharedPrefs.getmInstance(getActivity()).changePhone(Integer.parseInt(json.getString("phone")));
                                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                                alert.setMessage("Phone Number Changed successfully!")
                                        .setPositiveButton("Ok",null)
                                        .setCancelable(true)
                                        .create()
                                        .show();
                            }else if(json.getString("messo") == "2"){
                                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                                alert.setMessage("Invalid Phone Number")
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
                        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                        alert.setMessage("Failed To Change Phone Number")
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        changeEmail();
                                    }
                                })
                                .setCancelable(false)
                                .create()
                                .show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user",user);
                params.put("phone",phone+"");
                return params;
            }
        };
        Singleton.getmInstance(getActivity()).addToRequestQueue(sRequest);
    }

    private void changePass(){
        final String CO_ROOT_URL = "http://"+getResources().getString(R.string.url)+"/korirphp/changepass.php";
        final String user = getActivity().getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE).getString("Username",null);
        final String pass = etpronewpass.getText().toString();

        StringRequest sRequest = new StringRequest(Request.Method.POST, CO_ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json= new JSONObject(response);
                            if(json.getString("messo").equals("1")){
                                etpropass.setText("");
                                etproconpass.setText("");
                                etpronewpass.setText("");
                                SharedPrefs.getmInstance(getActivity()).changePass(json.getString("pass"));

                                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                                alert.setMessage("Password Changed successfully!")
                                        .setPositiveButton("Ok",null)
                                        .setCancelable(true)
                                        .create()
                                        .show();
                            }else if(json.getString("messo").equals("2")){
                                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                                alert.setMessage("Invalid Password")
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
                        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                        alert.setMessage("Failed To Change Password")
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        changePass();
                                    }
                                })
                                .setCancelable(false)
                                .create()
                                .show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user",user);
                params.put("pass",pass);
                return params;
            }
        };
        Singleton.getmInstance(getActivity()).addToRequestQueue(sRequest);
    }

    @Override
    public void onClick(View view) {
        View focusView = null;
        boolean cancel = false;

        if (view == tvchangeemail){
            if (etproemail.getText().toString().length() == 0){
                etproemail.setError("Email is required!");
                focusView =etproemail;
                cancel=true;
            }
            else if (!isValidEmail(etproemail.getText().toString())) {
                etproemail.setError("Email is Invalid!");
                focusView =etproemail;
                cancel=true;
            }
            else if (etproemail.getText().toString().equals(prefs.getString("Email",null))) {
                etproemail.setError("Email same as Original!");
                focusView =etproemail;
                cancel=true;
            }

            if(cancel)
                focusView.requestFocus();
            else
                changeEmail();
        }

        else if (view == tvchangephone){
            int phn = etprophone.getText().toString().length();

            if (phn == 0){
                etprophone.setError("Phone Number is required!");
                focusView = etprophone;
                cancel=true;
            }
            else if (phn != 9){
                etprophone.setError("invalid Phone Number!");
                focusView = etprophone;
                cancel=true;
            }
            else if (etprophone.getText().toString().equals(prefs.getInt("Phone",0)+"")){
                etprophone.setError("Phone Number same as Original!");
                focusView = etprophone;
                cancel=true;
            }

            if(cancel)
                focusView.requestFocus();
            else
                changePhone();
        }

        else if (view == btchangepass){
            int pasnew = etpronewpass.getText().toString().length();

            if (!etpropass.getText().toString().equals(current)){
                etpropass.setError("Wrong Password!");
                focusView =etpropass;
                cancel=true;
            }
            else if (pasnew < 8){
                etpronewpass.setError("Password should be more than eight characters!");
                focusView =etpronewpass;
                cancel=true;
            }
            else if (!etproconpass.getText().toString().equals(etpronewpass.getText().toString())){
                etproconpass.setError("Passwords Don't Match!");
                focusView =etproconpass;
                cancel=true;
            }

            if(cancel)
                focusView.requestFocus();
            else
                changePass();
        }
        else if (view == previously){
            Toast.makeText(getActivity(), "previously", Toast.LENGTH_SHORT).show();
        }
        else if (view == contact){
            Toast.makeText(getActivity(), "contact", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        // Refresh tab data:
        if (getFragmentManager() != null) {

            getFragmentManager()
                    .beginTransaction()
                    .detach(this)
                    .attach(this)
                    .commit();
        }
    }
}
