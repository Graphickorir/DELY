package com.hq.dely;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Korir on 2/16/2018.
 */

public class Home_Frag extends Fragment implements View.OnClickListener{
    Button bt,btl;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        bt = (Button) rootView.findViewById(R.id.bthome);
        btl = (Button) rootView.findViewById(R.id.btloghome);
        bt.setOnClickListener(this);
        btl.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if(v == bt){
            SharedPrefs.getmInstance(getActivity()).userlogout();
        }else if(v == btl){
            Intent i = new Intent(getActivity(),Login.class);
            getActivity().startActivity(i);
        }
    }
}
