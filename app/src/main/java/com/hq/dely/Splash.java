package com.hq.dely;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (SharedPrefs.getmInstance(this).UserIsLoged()){
            finish();
            startActivity(new Intent(this,Home.class));
            return;
        }
//        TextView tvsplash = (TextView) findViewById(R.id.tvsplash);
//        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Lobster.otf");
//        tvsplash.setTypeface(custom_font);

        Thread timer =new Thread(){
            @Override
            public void run() {
                try{
                    sleep(5000);
                    Intent intent = new Intent(Splash.this, Home.class);
                    Splash.this.startActivity(intent);
                }catch (InterruptedException ex){
                    ex.printStackTrace();
                }
            }
        };
        timer.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
