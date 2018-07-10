package com.hq.dely;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

/**
 * Created by korir on 1/19/2018.
 */

public class PayMethod extends Fragment implements View.OnClickListener{

    RadioGroup  rgpaym;
    RadioButton rbmpesa,rbairtel,rbeazzypay;
    Button btpaym;
    TextView tvpaym;
    ProgressBar paypbar;
    int rbid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.paymethod, container, false);

        rgpaym = (RadioGroup) rootView.findViewById(R.id.rgpaym);
        rbmpesa = (RadioButton) rootView.findViewById(R.id.rbmpesa);
        rbairtel = (RadioButton) rootView.findViewById(R.id.rbairtel);
        rbeazzypay = (RadioButton) rootView.findViewById(R.id.rbeazzypay);
        btpaym = (Button) rootView.findViewById(R.id.btpaym);
        tvpaym = (TextView) rootView.findViewById(R.id.tvpaym);
        paypbar = (ProgressBar) rootView.findViewById(R.id.paypbar);
        rbid = rgpaym.getCheckedRadioButtonId();

        rgpaym.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (rbmpesa.isChecked()) {
                  tvpaym.setVisibility(View.VISIBLE);
                  tvpaym.setText(getResources().getString(R.string.mpesa));
                  rbid = 1;}
                else if (rbairtel.isChecked()) {
                    tvpaym.setVisibility(View.VISIBLE);
                    tvpaym.setText(getResources().getString(R.string.airtel));
                    rbid = 2;}
                else if(rbeazzypay.isChecked()) {
                    tvpaym.setVisibility(View.VISIBLE);
                    tvpaym.setText(getResources().getString(R.string.eazypay));
                    rbid = 3;}
            }
        });

        btpaym.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (rbid == -1)
            Toast.makeText(getActivity(), "Please Choose Payment Method", Toast.LENGTH_SHORT).show();
        else
            SetPaymentMethod();
    }

    private void SetPaymentMethod() {
        final int method = rbid;
        final String Username = getActivity().getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE).getString("Username",null);

        final String CO_ROOT_URL = "http://"+getResources().getString(R.string.url)+"/korirphp/paymethod.php";

        paypbar.setVisibility(View.VISIBLE);
        StringRequest sRequest = new StringRequest(Request.Method.POST, CO_ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        paypbar.setVisibility(View.INVISIBLE);
                        try {
                            JSONObject json = new JSONObject(response);
                            if(json.getString("messo") == "1"){
                                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                                alert.setMessage("Payment Method set successfully!")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                getActivity().finish();
                                            }
                                        })
                                        .setCancelable(false)
                                        .create()
                                        .show();
                            }else if(json.getString("messo") == "2"){
                                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                                alert.setMessage("Invalid Payment Method")
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
                params.put("method", method+"");
                params.put("Username",Username);
                return params;
            }
        };
        Singleton.getmInstance(getActivity()).addToRequestQueue(sRequest);
    }
}
