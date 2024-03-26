package com.iorgana.aidl_server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.Random;

/**
 * - The client connect to the server using its Service.
 * - The service return Instance of Stub/IAddressProvider using IBinder. (BoundService)
 * - So the client bind to IAddressProvider using ServiceConnection, like we do to bind any service.
 * - Create bound service, and override onBind() method.
 * - in onBind(), instead of return the service instance,
 *   we return the Stub instance which contains our interface.
 */

/**
 * - Declare the service, make service receive actions from outside (from client)
 * ```xml
 * <service android:name=".AddressService" android:exported="true" android:enabled="true">
 * 	<intent-filter>
 * 		<action android:name="StartAddressService"/>
 * 	</intent-filter>
 * </service>
 * ```
 */
public class AddressService extends Service {
    private static final String TAG = "__AddressService";
    public AddressService() {
    }


    /**
     * onBind
     * ------------------------------------------------------------------------------
     * Instead of return IBinder of the service
     * We return Stub Binder, so that we can get our IAddressProvider
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: get stub binder to extract IAddressProvider instance");
        return mBinder;
    }

    IAddressProvider.Stub mBinder = new IAddressProvider.Stub() {
        @Override
        public String obtainAddress(boolean isIpv6) throws RemoteException {
            Log.d(TAG, "obtainAddress: get an address");

            Random random = new Random();
            String randomAddress = random.nextInt(255)+"."+random.nextInt(255)+"."+random.nextInt(255)+"."+random.nextInt(255);
            return (isIpv6) ? "::1" : randomAddress;
        }
    };
}