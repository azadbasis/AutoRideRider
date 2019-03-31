package org.autoride.autoride.utils.reference.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.autoride.autoride.applications.AutoRideRiderApps;

/**
 * Created by goldenreign on 5/13/2018.
 */

public class NetworkConnectionReciever extends BroadcastReceiver {

    public static ConnectivityRecieverListener connectivityRecieverListener;
    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if(connectivityRecieverListener !=null){
            connectivityRecieverListener.OnNetworkChange(isConnected);
        }

    }

    public static boolean isConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) AutoRideRiderApps.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }


    public interface ConnectivityRecieverListener{
        public void OnNetworkChange(boolean inConnected);
    }
}