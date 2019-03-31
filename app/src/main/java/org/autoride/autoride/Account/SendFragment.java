package org.autoride.autoride.Account;


import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.autoride.autoride.Account.barcode.BarcodeCaptureActivity;
import org.autoride.autoride.R;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SendFragment extends Fragment {

    private TextView tvAccountName;
    private EditText etAccountNumber, etTransferAmount;
    private LinearLayout sendMoneyImageContainer, sendActionContainer, containerSendInfo,ContainerAddByBankCard,containerSendByQrCode;
    private Button btnCancelSendMoney,btnContinueSendMoney;
    private static final int BARCODE_READER_REQUEST_CODE = 1;
    private String accountHolderName, AccountNumber, accountHolderImage, accountTransferMoney;
    private CircleImageView imageAccountNumber;
    RelativeLayout sendContainer;

    public SendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_send, container, false);

        btnCancelSendMoney = (Button) rootView.findViewById(R.id.btnCancelSendMoney);
        btnContinueSendMoney = (Button) rootView.findViewById(R.id.btnContinueSendMoney);
        sendMoneyImageContainer = (LinearLayout) rootView.findViewById(R.id.sendMoneyImageContainer);
        sendActionContainer = (LinearLayout) rootView.findViewById(R.id.sendActionContainer);
        containerSendInfo = (LinearLayout) rootView.findViewById(R.id.containerSendInfo);
        containerSendByQrCode = (LinearLayout) rootView.findViewById(R.id.containerSendByQrCode);
        ContainerAddByBankCard = (LinearLayout) rootView.findViewById(R.id.ContainerAddByBankCard);
        sendContainer = (RelativeLayout) rootView.findViewById(R.id.sendContainer);


        tvAccountName = (TextView) rootView.findViewById(R.id.tvAccountName);
        etAccountNumber = (EditText) rootView.findViewById(R.id.etAccountNumber);
        etTransferAmount = (EditText) rootView.findViewById(R.id.etTransferAmount);

        imageAccountNumber = (de.hdodenhof.circleimageview.CircleImageView) rootView.findViewById(R.id.imageAccountNumber);

        containerSendByQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accountHolderName = "";
                AccountNumber = "";
                accountHolderImage = "";
                accountTransferMoney="";

                Intent intent = new Intent(getActivity(), BarcodeCaptureActivity.class);
                startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
            }
        });



        ContainerAddByBankCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sendMoneyImageContainer.getVisibility() == View.VISIBLE) {
                    sendMoneyImageContainer.setVisibility(View.GONE);
                    sendActionContainer.setVisibility(View.VISIBLE);
                    containerSendInfo.setVisibility(View.VISIBLE);

                    tvAccountName.setText("");
                    imageAccountNumber.setImageResource(0);
                    etAccountNumber.setText("");
                    etTransferAmount.setText("");
                } else {
                    sendMoneyImageContainer.setVisibility(View.GONE);
                    sendActionContainer.setVisibility(View.VISIBLE);
                    containerSendInfo.setVisibility(View.VISIBLE);

                    tvAccountName.setText("");
                    imageAccountNumber.setImageResource(0);
                    etAccountNumber.setText("");
                    etTransferAmount.setText("");
                }
            }
        });


        btnCancelSendMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendActionContainer.setVisibility(View.GONE);
                containerSendInfo.setVisibility(View.GONE);
                sendMoneyImageContainer.setVisibility(View.VISIBLE);

            }
        });
        btnContinueSendMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return rootView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Point[] p = barcode.cornerPoints;

                    String barCodeDisplayValue = barcode.displayValue;
                    String[] parts = barCodeDisplayValue.split(",");

                    accountHolderName = parts[0]; // 004
                    AccountNumber = parts[1];
                    accountHolderImage = parts[2];
                    accountTransferMoney = parts[3];

                    if (!accountHolderName.isEmpty() & !AccountNumber.isEmpty() & !accountHolderImage.isEmpty() & !accountTransferMoney.isEmpty()) {
                        sendMoneyImageContainer.setVisibility(View.GONE);
                        sendActionContainer.setVisibility(View.VISIBLE);
                        containerSendInfo.setVisibility(View.VISIBLE);

                        tvAccountName.setText("");
                        imageAccountNumber.setImageResource(0);
                        etAccountNumber.setText("");
                        etTransferAmount.setText("");

                        tvAccountName.setText("You're sending to " + accountHolderName);
                        etAccountNumber.setText(AccountNumber);
                        etTransferAmount.setText(accountTransferMoney);


                        Picasso.with(getContext())
                                .load(accountHolderImage)//image
                                .noFade()
                                .into(imageAccountNumber, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        startPostponedEnterTransition();
                                    }

                                    @Override
                                    public void onError() {
                                        startPostponedEnterTransition();
                                    }
                                });
                    } else {


                    }


                //    Toast.makeText(getActivity(), accountHolderName + "\n" + AccountNumber + "\n" + accountHolderImage, Toast.LENGTH_SHORT).show();


                } else {

                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


}
