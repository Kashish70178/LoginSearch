package com.example.hcl.cris;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import cz.msebera.android.httpclient.Header;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
//import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;



public class CrisMain extends AppCompatActivity {

    EditText uname,upass;
    Button enter;
    String resp=null;
    Intent tend;
    String ph;
    private static final int PhoneState = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cris_main);

        uname=(EditText)findViewById(R.id.editText3);
        upass=(EditText)findViewById(R.id.editText2);
        enter=(Button)findViewById(R.id.button1);
        uname.setBackgroundColor(Color.TRANSPARENT);
        upass.setBackgroundColor(Color.TRANSPARENT);

        uname.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                uname.setBackgroundResource(R.drawable.border);
                return false;
            }
        });

        upass.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                upass.setBackgroundResource(R.drawable.border);
                return false;
            }
        });

        enter.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0)
            {
                imei();
                exec(uname.getText().toString(),upass.getText().toString());
                //exec(uname.getText().toString(),upass.getText().toString(),"ki9800");
            }
        });

    }

    public void imei()
    {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED)
        {
            takePermission();
            ph="NotGranted";
        }
        else {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            ph=tm.getDeviceId();
        }
    }

    private void takePermission()
    {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_PHONE_STATE))
        {
            new AlertDialog.Builder(CrisMain.this).setTitle("Permission Grant Please")
                    .setMessage(getString(R.string.phoneId))
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(CrisMain.this,new String[]{Manifest.permission.READ_PHONE_STATE},PhoneState);
                        }
                    })
                    .setIcon(R.drawable.ecris)
                    .show();
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, PhoneState);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults)
    {
        if(requestCode==PhoneState) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                ph = tm.getDeviceId();
            } else {
                alert("Please Grant Phone State permission to log in");
            }
        }
    }

    private void alert(String msg) {
        new AlertDialog.Builder(CrisMain.this)
                .setTitle("Permission Request")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                       ph="NotGranted";
                    }
                })
                .setIcon(R.drawable.ecris)
                .show();
    }

    public void exec(String name,String id)
    {
        String user=name;
        String uid=id;
        String ime=null;
        //String link="http://www.joserozario.in/ireps/seek10der.php?rly=WR&str=paper&row=S";
        //String link="http://alltutorials.in/ashu/index.php";
        String link="http://www.joserozario.in/ireps/ilogin.php";       //link to connect

        if(ph.compareTo("NotGranted")==0)
        {
            Toast.makeText(getApplicationContext(),"Read_Phone_State Permission not provided",Toast.LENGTH_LONG).show();
            Log.v("permission error","Read_Phone_State Permission not provided");
            imei();
        }
        else
        {
            ime=ph;
        }

        if(ime!=null) {
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.add("user", user);
            params.add("uid", uid);
            params.add("ime", ime);
            client.post(link, params, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                    String resp = new String(arg2);
                    // Here "resp" is message echoed by the php web service

                    Toast.makeText(getApplicationContext(), "succ: " + resp, Toast.LENGTH_LONG).show();

                    //Here you need to write the message that will come when user gives Wrong credentials
                    if (resp.compareTo("No") != 0) {
                        tend = new Intent(CrisMain.this, Tender.class);
                        startActivity(tend);
                    }

                    Log.v("success test", resp);

                }

                //This function is called if connection is not made successfully
                @Override
                public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                    String msg = arg3.getMessage();
                    Toast.makeText(getApplicationContext(), "connection failure: ", Toast.LENGTH_LONG).show();
                    Log.v("error", msg);
                }
            });
        }
    }

}
