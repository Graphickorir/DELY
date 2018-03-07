package com.hq.dely;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Korir on 3/5/2018.
 */

public class addCompDialogue extends AppCompatDialogFragment {
    EditText etcompname,etcompaddress;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_addcomp, null);
        etcompname = (EditText) view.findViewById(R.id.etcompname);
        etcompaddress = (EditText) view.findViewById(R.id.etcompaddress);

        alert.setView(view)
                .setTitle("Add Company")
                .setMessage("Send email with company name and company address")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        View focusView = null;
                        boolean cancel = false;

                        if (etcompname.getText().toString().length() == 0) {
                            etcompname.setError("Name is required!");
                            focusView = etcompname;
                            cancel = true;
                        } else if (etcompaddress.getText().toString().length() == 0) {
                            etcompaddress.setError("Name is required!");
                            focusView = etcompaddress;
                            cancel = true;
                        }

                        if (cancel)
                            focusView.requestFocus();
                        else {
                            String[] to = {"delycustomercare.co.ke"};
                            Intent emailintent = new Intent(Intent.ACTION_SEND);
                            emailintent.setType("text/plain");

                            emailintent.putExtra(Intent.EXTRA_EMAIL, to);
                            emailintent.putExtra(Intent.EXTRA_SUBJECT, "Add User Company");
                            emailintent.putExtra(Intent.EXTRA_TEXT, etcompname.getText().toString()
                                    + "\n" + etcompaddress.getText().toString());
                            startActivity(emailintent);
                        }
                    }
                });

        return alert.create();
    }
}
