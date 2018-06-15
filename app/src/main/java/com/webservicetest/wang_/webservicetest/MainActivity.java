package com.webservicetest.wang_.webservicetest;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    Button button;
    TextView textView;
    String result;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    textView.setText("连接服务器失败");
                    break;
                case 1:
                    textView.setText(result);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnClick();
            }
        });
    }

    public void BtnClick() {
        final String SERVICE_NS = "http://tempuri.org/";
        final String SOAP_ACTION = "http://tempuri.org/HelloWorld";
        final String SERVICE_URL = "http://58.247.112.34:8080/WebService1.asmx";
        String methodName = "HelloWorld";
        final HttpTransportSE ht = new HttpTransportSE(SERVICE_URL);
        ht.debug = true;
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        SoapObject soapObject = new SoapObject(SERVICE_NS, methodName);
        envelope.dotNet = true;
        envelope.bodyOut = soapObject;

        new Thread() {
            @Override
            public void run() {
                try {
                    ht.call(SOAP_ACTION, envelope);
                    if (envelope.getResponse() != null) {
                        SoapObject so = (SoapObject) envelope.bodyIn;
                        result = so.getPropertyAsString(0);
                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    } else {
                        Message msg = new Message();
                        msg.what = 0;
                        handler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}