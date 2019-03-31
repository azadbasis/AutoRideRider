package org.autoride.autoride.TrackRider;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.autoride.autoride.R;
import org.autoride.autoride.TrackRider.Model.TrackingPeopleListItem;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ProductListAdapter extends ArrayAdapter<TrackingPeopleListItem>{

    private List<TrackingPeopleListItem> products;

    public ProductListAdapter(Context context, int resource, List<TrackingPeopleListItem> objects) {
        super(context, resource, objects);
        products = objects;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.custom_track_userlist, parent, false);
        }

        TrackingPeopleListItem product = products.get(position);

        TextView nameText = (TextView) convertView.findViewById(R.id.contactName);
        nameText.setText(product.name);


        String price =product.authStatus;
        TextView priceText = (TextView) convertView.findViewById(R.id.textPrice);
        priceText.setText(price);

    /*    ImageView iv = (ImageView) convertView.findViewById(R.id.imageView);
        Bitmap bitmap = getBitmapFromAsset(product.getProductId());
        iv.setImageBitmap(bitmap);*/

        return convertView;
    }

    private Bitmap getBitmapFromAsset(String productId) {
        AssetManager assetManager = getContext().getAssets();
        InputStream stream = null;

        try {
            stream = assetManager.open(productId + ".png");
            return BitmapFactory.decodeStream(stream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
