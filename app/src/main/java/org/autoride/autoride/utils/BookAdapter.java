package org.autoride.autoride.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.telenav.expandablepager.adapter.ExpandablePagerAdapter;

import org.autoride.autoride.R;
import org.autoride.autoride.constants.AppsConstants;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BookAdapter extends ExpandablePagerAdapter<Book> implements AppsConstants {

    private Context context;
    private List<Book> items;
    private Integer[] imgId;
    private String[] suitcases;
    private String[] passenger;
    private String[] estimateFare;

    public BookAdapter(Context context, List<Book> items, Integer[] imgId, String[] passenger) {
        super(items);
        this.context = context;
        this.items = items;
        this.imgId = imgId;
        this.passenger = passenger;
    }

    public BookAdapter(Context context, List<Book> items, Integer[] imgId, String[] suitcases, String[] passenger, String[] estimateFare) {
        super(items);
        this.context = context;
        this.items = items;
        this.imgId = imgId;
        this.suitcases = suitcases;
        this.passenger = passenger;
        this.estimateFare = estimateFare;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {

        final ViewGroup rootView = (ViewGroup) LayoutInflater.from(container.getContext()).inflate(R.layout.page, container, false);
        rootView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        ImageView headerImage = (ImageView) rootView.findViewById(R.id.header_img);

        TextView headerTitle = ((TextView) rootView.findViewById(R.id.header_title));
        TextView headerSubtitle = ((TextView) rootView.findViewById(R.id.header_subtitle));
        TextView carDescription = ((TextView) rootView.findViewById(R.id.carDescription));
        TextView txtPerson = ((TextView) rootView.findViewById(R.id.txtPerson));

        TextView tvPageSuitcases = ((TextView) rootView.findViewById(R.id.tv_page_suitcases));
        TextView tvPagePassenger = ((TextView) rootView.findViewById(R.id.tv_page_passenger));
        TextView tvPageFare = ((TextView) rootView.findViewById(R.id.tv_page_fare));

        headerImage.setImageResource(imgId[position]);

        headerTitle.setText(items.get(position).getTitle());
        headerSubtitle.setText(items.get(position).getAuthor());
        carDescription.setText(items.get(position).getDescription());
        txtPerson.setText(passenger[position]);

        tvPageSuitcases.setText(suitcases[position] + " suitcases\nCapacity");
        tvPagePassenger.setText(passenger[position] + " passenger\nCapacity");
        fareStyle(tvPageFare, estimateFare[position]);

        // tvPageFare.setText(String.format("%s%s\nEstimate Fare", CURRENCY, estimateFare[position]));

        switch (position) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            default:
                break;
        }

        setSpan(tvPageSuitcases);
        setSpan(tvPagePassenger);
        // setSpan(tvPageFare, "\\d+,*\\d+");

        return attach(container, rootView, position);
    }

    private void setSpan(TextView textView) {
        float relativeSize = 1.5f;
        Pattern pat = Pattern.compile("\\d+");
        Matcher m = pat.matcher(textView.getText());
        if (m.find()) {
            SpannableString span = new SpannableString(textView.getText());
            span.setSpan(new RelativeSizeSpan(relativeSize), 0, m.group(0).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(span);
        }
    }

    private void fareStyle(TextView textView, String fare) {
        float relativeSize = 1.5f;
        SpannableString span = new SpannableString(fare);
        span.setSpan(new RelativeSizeSpan(relativeSize), 0, span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.append(CURRENCY);
        textView.append(span);
        textView.append("\n");
        textView.append("Estimate Fare");
    }
}