package org.autoride.autoride.TrackRider;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.autoride.autoride.R;

/**
 * Created by goldenreign on 7/15/2018.
 */

public class AlertDialogFragment extends DialogFragment {
    com.suke.widget.SwitchButton switchButton;
    TextView textGPSUpdate;
    ImageView imageGlobalGPS;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog globalSettingDialog = new Dialog(getActivity());
        globalSettingDialog.setCancelable(true);
        //set content
        //  inviteDialog.setContentView(R.layout.custom_invite_dialog);
        globalSettingDialog.setContentView(R.layout.show_protected_switch);

        switchButton = (com.suke.widget.SwitchButton) globalSettingDialog.findViewById(R.id.switch_button);
        imageGlobalGPS = (ImageView) globalSettingDialog.findViewById(R.id.imageGlobalGPS);
        textGPSUpdate = (TextView) globalSettingDialog.findViewById(R.id.textGPSUpdate);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(globalSettingDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        lp.dimAmount = 0.9f;
        lp.gravity = Gravity.CENTER;
        globalSettingDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        globalSettingDialog.getWindow().setAttributes(lp);
        globalSettingDialog.show();
        return globalSettingDialog;
    }
}
