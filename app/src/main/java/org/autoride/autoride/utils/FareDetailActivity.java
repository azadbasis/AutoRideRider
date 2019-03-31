package org.autoride.autoride.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import org.autoride.autoride.R;
import org.autoride.autoride.constants.AppsConstants;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FareDetailActivity extends AppCompatActivity implements AppsConstants {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Xerox Serif Wide.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fare_detail);

        setUiComponent();
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void setUiComponent() {

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_in_fare_details);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_in_fare_details);
        collapsingToolbar.setTitle("Fare Details");

        TextView tvBasFare = (TextView) findViewById(R.id.tv_base_fare);
        TextView tvMinimumFare = (TextView) findViewById(R.id.tv_minimum_fare);
        TextView tvFarePerMinute = (TextView) findViewById(R.id.tv_fare_per_minute);
        TextView tvFarePerKilometer = (TextView) findViewById(R.id.tv_fare_per_kilometer);
        TextView tvEstimateToll = (TextView) findViewById(R.id.tv_estimate_toll);
        TextView tvEstimateSurCharge = (TextView) findViewById(R.id.tv_estimate_sur_charge);

        if (getIntent() != null) {
            tvBasFare.setText(CURRENCY + (int) getIntent().getDoubleExtra("base_fare", 0.00));
            tvMinimumFare.setText(CURRENCY + (int) getIntent().getDoubleExtra("minimum_fare", 0.00));
            tvFarePerMinute.setText(CURRENCY + (int) getIntent().getDoubleExtra("fare_per_minute", 0.00));
            tvFarePerKilometer.setText(CURRENCY + (int) getIntent().getDoubleExtra("fare_per_km", 0.00));
            tvEstimateToll.setText(CURRENCY + String.format("%.2f", getIntent().getDoubleExtra("estimate_toll", 0.00)));
            tvEstimateSurCharge.setText(CURRENCY + String.format("%.2f", getIntent().getDoubleExtra("estimate_sur_charge", 0.00)));
        }
    }
}