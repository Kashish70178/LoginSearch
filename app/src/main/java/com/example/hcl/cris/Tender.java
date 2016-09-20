package com.example.hcl.cris;

import android.os.Bundle;
//import org.apache.http.Header;
//compile files('libs/android-async-http-1_4_9.jar')
import cz.msebera.android.httpclient.Header;
import android.preference.PreferenceActivity;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class Tender extends AppCompatActivity {

    Spinner rail,type;
    EditText key;
    Button search;
    String resp;
    private WebView wv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tender);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        search = (Button) findViewById(R.id.button1);
        wv = (WebView) findViewById(R.id.webView1);
        wv.getSettings().setBuiltInZoomControls(true);
        wv.getSettings().setDisplayZoomControls(false);
        wv.getSettings().setSupportZoom(true);
        wv.loadUrl("file:///android_asset/image.html");
        search.setBackgroundResource(R.drawable.button_norm);

        search.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                search.setBackgroundResource(R.drawable.button_border);
                // TODO Auto-generated method stub
                rail = (Spinner) findViewById(R.id.spinner1);
                type = (Spinner) findViewById(R.id.spinner2);
                key = (EditText) findViewById(R.id.editText1);


                String s_key = key.getText().toString();

                String rly=null;
                switch(rail.getSelectedItem().toString())
                {
                    case "Central Railway": rly="CR";
                        break;
                    case "Eastern Railway": rly="ER";
                        break;
                    case "Western Railway": rly="WR";
                        break;
                    default: rly=null;
                        Toast.makeText(getApplicationContext(),"Please select Railway",Toast.LENGTH_LONG).show();
                }
                String tp = null;

                switch(type.getSelectedItem().toString())
                {
                    case "Works Tender": tp="W";
                        break;
                    case "Supply Tender": tp="S";
                        break;
                    default: tp=null;
                        Toast.makeText(getApplicationContext(),"Please select Tender Type",Toast.LENGTH_LONG).show();
                }

                if(rly!=null && tp!=null)
                exe(rly, s_key, tp);
                else
                    Toast.makeText(getApplicationContext(),"Please select all entries",Toast.LENGTH_LONG).show();

            }
        });
    }

    public void exe(String rail, String srch, String tend) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("rly", rail);
        params.add("str", srch);
        params.add("row", tend);

        //String link = "http://www.joserozario.in/ireps/seek10der.php?rly=WR&str=paper&row=S";
        //String link="http://alltutorials.in/ashu/index.php";
        resp = "http://www.joserozario.in/ireps/seek10der.php?rly="+rail+"&str="+srch+"&row="+tend;
        //here "resp" is the link created to show in webview for the search if connection is established

        String link="http://www.joserozario.in/ireps/seek10der.php";
        //here link is used to post data to php web service

        wv = (WebView)findViewById(R.id.webView1);
        wv.setWebViewClient(new MyBrowser());

        client.post(link, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int arg0,Header[] arg1, byte[] arg2) {
                //resp = new String(arg2);
                //resp="http://alltutorials.in/ashu/table.html";

                //if connection is success then only webview("wv") will show the link

                Log.v("success test", resp);
                wv.getSettings().setLoadsImagesAutomatically(true);
                wv.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                wv.loadUrl(resp);

            }

            @Override
            public void onFailure(int arg0,Header[] arg1, byte[] arg2, Throwable arg3) {
                Toast.makeText(getApplicationContext(), "connection failure", Toast.LENGTH_LONG).show();
                String msg = arg3.getMessage();
                Log.v("error", msg);
            }
        });
    }

        private class MyBrowser extends WebViewClient {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        }
}
