package org.autoride.autoride.TrackRider;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import org.autoride.autoride.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class TrackingAcceptActivity extends AppCompatActivity {


    Dialog acceptInviteDialog;
    TextView textAcceptMessage, textAcceptRiderName;
    ImageView imageAcceptRiderPhoto;
    String message, riderFullName, riderPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.tracking_accept);

        if (getIntent() != null) {
            String notificationStatus = getIntent().getStringExtra("intent_status");
            if (notificationStatus != null) {
                if (notificationStatus.equalsIgnoreCase("rider_accept_tracking_request")) {

                    String title = getIntent().getStringExtra("title");
                    message = getIntent().getStringExtra("message");
                    riderFullName = getIntent().getStringExtra("riderFullName");
                    riderPhoto = getIntent().getStringExtra("riderPhoto");
                  //  TextView textMessage = (TextView) findViewById(R.id.textMessage);
                    //textMessage.setText(message);
                    createAcceptInviteDialog(message, riderFullName, riderPhoto);

                }
            }
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (acceptInviteDialog.isShowing()) {
                    acceptInviteDialog.dismiss();
                    finish();
                }
            }
        }, 5000);

    }

    private void createAcceptInviteDialog(String message, String riderFullName, String riderPhoto) {

        acceptInviteDialog = new Dialog(TrackingAcceptActivity.this, R.style.CustomDialog);
        acceptInviteDialog.setCancelable(true);

        acceptInviteDialog.setContentView(R.layout.accept_invite_dialog);

        imageAcceptRiderPhoto = (ImageView) acceptInviteDialog.findViewById(R.id.imageAcceptRiderPhoto);
        textAcceptMessage = (TextView) acceptInviteDialog.findViewById(R.id.textAcceptMessage);
        textAcceptRiderName = (TextView) acceptInviteDialog.findViewById(R.id.textAcceptRiderName);
        textAcceptMessage.setText(message);
        textAcceptRiderName.setText(riderFullName);


        Glide.with(getBaseContext())
                .load(riderPhoto)
                .apply(new RequestOptions().optionalCircleCrop()

                )
                .into(new SimpleTarget<Drawable>() {
                    @SuppressLint("NewApi")
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {

                        Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();

                        Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmap, 90, 90, false);

                        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmapResized);
                        roundedBitmapDrawable.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);
                        roundedBitmapDrawable.setCircular(true);
//                        toolbar.setNavigationIcon(roundedBitmapDrawable);
                        imageAcceptRiderPhoto.setImageDrawable(roundedBitmapDrawable);

                    }
                });


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(acceptInviteDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.dimAmount = 0.9f;
        lp.gravity = Gravity.CENTER;
        acceptInviteDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        acceptInviteDialog.getWindow().setAttributes(lp);
        acceptInviteDialog.show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (acceptInviteDialog != null) {
            acceptInviteDialog.dismiss();
        }
    }
}
