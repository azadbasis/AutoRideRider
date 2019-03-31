package org.autoride.autoride.TrackRider;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
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

public class TrackingCancelActivity extends AppCompatActivity {
Dialog cancelInviteDialog;
TextView textCancelMessage,textCancelRiderName;
ImageView imageCancelRiderPhoto;
String title,message,riderFullName,riderPhoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null) {
            String notificationStatus = getIntent().getStringExtra("intent_status");
            if (notificationStatus != null) {
                if (notificationStatus.equalsIgnoreCase("rider_cancel_tracking_request")) {

                    title = getIntent().getStringExtra("title");
                    message = getIntent().getStringExtra("message");
                    riderPhoto = getIntent().getStringExtra("riderPhoto");
                    //  TextView textMessage = (TextView) findViewById(R.id.textMessage);
                    //textMessage.setText(message);
                    createAcceptInviteDialog(message, riderPhoto);

                }
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(cancelInviteDialog!=null){
                    cancelInviteDialog.dismiss();
                    finish();
                }

            }
        },5000);
    }

    private void createAcceptInviteDialog(String message, String riderPhoto) {

        cancelInviteDialog = new Dialog(TrackingCancelActivity.this, R.style.CustomDialog);
        cancelInviteDialog.setCancelable(true);

        cancelInviteDialog.setContentView(R.layout.cancel_invite_dialog);

        imageCancelRiderPhoto = (ImageView) cancelInviteDialog.findViewById(R.id.imageCancelRiderPhoto);
        textCancelMessage = (TextView) cancelInviteDialog.findViewById(R.id.textCancelMessage);
        textCancelRiderName = (TextView) cancelInviteDialog.findViewById(R.id.textCancelRiderName);
        textCancelMessage.setText(message);
       // textCancelRiderName.setText(riderFullName);


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
                        imageCancelRiderPhoto.setImageDrawable(roundedBitmapDrawable);

                    }
                });


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(cancelInviteDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.dimAmount = 0.9f;
        lp.gravity = Gravity.CENTER;
        cancelInviteDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        cancelInviteDialog.getWindow().setAttributes(lp);
        cancelInviteDialog.show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cancelInviteDialog != null) {
            cancelInviteDialog.dismiss();
        }
    }
}
