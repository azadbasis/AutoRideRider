package org.autoride.autoride.profiles;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;

import org.autoride.autoride.R;
import org.autoride.autoride.utils.Operation;

public class RiderStreetViewActivity extends AppCompatActivity implements OnStreetViewPanoramaReadyCallback {

    private StreetViewPanoramaFragment fragmentStreetViewPanorama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_street_view);
        fragmentStreetViewPanorama = (StreetViewPanoramaFragment) getFragmentManager().findFragmentById(R.id.rider_fragment_street_view_panorama);
        fragmentStreetViewPanorama.getStreetViewPanoramaAsync(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RiderStreetViewActivity.this, RiderProfileActivity.class));
            }
        });
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {

        String sLat = Operation.getString("SLAT", "");
        String sLng = Operation.getString("SLNG", "");
        double dLat = Double.parseDouble(sLat);
        double dLng = Double.parseDouble(sLng);

        streetViewPanorama.setPosition(new LatLng(dLat, dLng));
        streetViewPanorama.setUserNavigationEnabled(true);
        streetViewPanorama.setPanningGesturesEnabled(true);
        streetViewPanorama.setZoomGesturesEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}