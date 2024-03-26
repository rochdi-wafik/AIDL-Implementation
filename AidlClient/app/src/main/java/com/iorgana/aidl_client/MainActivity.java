package com.iorgana.aidl_client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.iorgana.aidl_server.IAddressProvider;

/**
 * To connect to the server app and access its service, as well as its interface, we have to:
 * 1. Create the same AIDL of the server inside the client.
 * 2. Connect to its service using ServiceConnection
 * ----------------------------------------------------------------------
 * - In our client app, to get the Stub/IAddressProvider, we only need to bind te the Service of server app.
 * - We make `ServiceConnection` and retrieve the `IAddressProvider` Instance.
 * - The Intent of service, we use the same action name we assign in service manifest declaration.
 * - Also, we set the package name of the server.
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "__MainActivity";

    // Server IAddressProvider Instance
    IAddressProvider addressProvider;

    // Service Connection
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            addressProvider = IAddressProvider.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: unable to connect to service");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /**
         * Bind Server Service
         */
        Intent intent = new Intent();
        intent.setAction("StartAddressService");
        intent.setPackage("com.iorgana.aidl_server");
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);


        Button btnGet = findViewById(R.id.btnGet);
        TextView result = findViewById(R.id.txtResult);

        /**
         * Obtain Address From Server
         */
        btnGet.setOnClickListener(view->{
            if(addressProvider==null){
                result.setText("Unable to connect to server service. The server app may be not installed");
                result.setTextColor(getResources().getColor(R.color.danger));
                return;
            }

            try {
                String ipv4 = addressProvider.obtainAddress(false);
                result.setText("IP From Server: "+ipv4);
                result.setTextColor(getResources().getColor(R.color.success));

            } catch (RemoteException e) {
                result.setText(e.getMessage());
                result.setTextColor(getResources().getColor(R.color.danger));
                e.printStackTrace();
            }
        });

    }



}