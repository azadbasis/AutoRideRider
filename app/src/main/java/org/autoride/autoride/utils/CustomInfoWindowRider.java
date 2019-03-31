package org.autoride.autoride.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.autoride.autoride.R;

public class CustomInfoWindowRider implements GoogleMap.InfoWindowAdapter {

    private View view;
    private String time;
    private String msg;

    public CustomInfoWindowRider(Context context, String time, String msg) {
        view = LayoutInflater.from(context).inflate(R.layout.custom_info_window_rider, null);
        this.time = time;
        this.msg = msg;
    } 

    @Override
    public View getInfoWindow(Marker marker) {

        if (time != null) {
            TextView tvInfoWindow = (TextView) view.findViewById(R.id.tv_info_window);
            tvInfoWindow.setText(time);
            tvInfoWindow.setVisibility(View.VISIBLE);
        }

        TextView tvInfoWindow2 = (TextView) view.findViewById(R.id.tv_info_window2);
        tvInfoWindow2.setText(msg);

        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}