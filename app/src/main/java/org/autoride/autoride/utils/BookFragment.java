package org.autoride.autoride.utils;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.telenav.expandablepager.ExpandablePager;

import org.autoride.autoride.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BookFragment extends Fragment {

    private Book myBook;
    private ExpandablePager exPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            myBook = new Book(getArguments().getString("title"));
            myBook.setAuthor(getArguments().getString("author"));
            myBook.setDescription(getArguments().getString("description"));
            myBook.setUrl(getArguments().getString("url"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.page, container, false);
        rootView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        ((TextView) rootView.findViewById(R.id.carDescription)).setText(myBook.getDescription());
        ((TextView) rootView.findViewById(R.id.header_title)).setText(myBook.getTitle());
        ((TextView) rootView.findViewById(R.id.header_subtitle)).setText(myBook.getAuthor());
        ((ImageView) rootView.findViewById(R.id.header_img)).setImageURI(Uri.parse(myBook.getUrl()));


        TextView rating = ((TextView) rootView.findViewById(R.id.tv_page_suitcases));
        setSpan(rating, "\\d\\.\\d / \\d\\.\\d");

        TextView capacity = ((TextView) rootView.findViewById(R.id.tv_page_passenger));
        setSpan(capacity, "\\d+");

        TextView fairs = ((TextView) rootView.findViewById(R.id.tv_page_fare));
        setSpan(fairs, "\\d+,*\\d+");

        return rootView;
    }

    private void setSpan(TextView textView, String pattern) {
        float relativeSize = 1.5f;
        Pattern pat = Pattern.compile(pattern);
        Matcher m = pat.matcher(textView.getText());
        if (m.find()) {
            SpannableString span = new SpannableString(textView.getText());
            span.setSpan(new RelativeSizeSpan(relativeSize), 0, m.group(0).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(span);
        }
    }
}