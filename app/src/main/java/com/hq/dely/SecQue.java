package com.hq.dely;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by korir on 1/19/2018.
 */

public class SecQue extends Fragment implements AdapterView.OnItemSelectedListener,View.OnClickListener{
    EditText etsec;
    Button btsec;
    Spinner spinsec;
    int que;
    ProgressBar secpbar;

    ArrayAdapter arrayAdapter;
    List<String> listData=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.secque, container, false);

        listData.add("What was your first pets name ");
        listData.add("What is your mother's middle name");
        listData.add("What is your favourite meal");
        listData.add("What is your favourite restaurant");
        listData.add("What is your favourite music");

        spinsec = (Spinner) rootView.findViewById(R.id.spinsec);
        btsec = (Button) rootView.findViewById(R.id.btsec);
        etsec = (EditText) rootView.findViewById(R.id.etsec);
        secpbar = (ProgressBar) rootView.findViewById(R.id.secpbar);

        arrayAdapter = new ArrayAdapter(getActivity(),R.layout.secquetv, listData);
        spinsec.setAdapter(arrayAdapter);
        spinsec.setOnItemSelectedListener(this);

        btsec.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        View focusView =null;
        boolean cancel=false;

        if (v==btsec){

            if( etsec.getText().toString().length() == 0 ){
                etsec.setError( "Answer is required!" );
                focusView =etsec;
                cancel =true;}

            if(cancel)
                focusView.requestFocus();
            else
                SetSecque();}

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
        que = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {}


    public void SetSecque(){
        final int secque=(que+1);
        final String secans = etsec.getText().toString();
        final String Username = getActivity().getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE).getString("Username",null);

        final String CO_ROOT_URL = "http://192.168.56.1/korirphp/sec_ans.php";

            secpbar.setVisibility(View.VISIBLE);
        StringRequest sRequest = new StringRequest(Request.Method.POST, CO_ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                            secpbar.setVisibility(View.INVISIBLE);
                        try {
                            JSONObject json = new JSONObject(response);
                            if(json.getString("messo") == "1"){
                                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                                    alert.setMessage("Security Question set successfully!")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    ((SignUp)getActivity()).selectFragment(2);
                                                }
                                            })
                                            .setCancelable(false)
                                            .create()
                                            .show();
                            }else if(json.getString("messo") == "2"){
                                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                                alert.setMessage("Invalid Security Question or Security Answer")
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
                params.put("secans",secans);
                params.put("secque", secque+"");
                params.put("Username",Username);
                return params;
            }
        };

        Singleton.getmInstance(getActivity()).addToRequestQueue(sRequest);

    }
}
