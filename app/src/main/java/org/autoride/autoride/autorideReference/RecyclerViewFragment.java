package org.autoride.autoride.autorideReference;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.autoride.autoride.R;
import org.autoride.autoride.RiderMainActivity;
import org.autoride.autoride.utils.reference.ReferenceGalleryAdapter;
import org.autoride.autoride.utils.reference.ReferenceItem;
import org.autoride.autoride.utils.reference.ReferenceItemClickListener;

public class RecyclerViewFragment extends Fragment implements ReferenceItemClickListener {

    public static final String TAG = RecyclerViewFragment.class.getSimpleName();

    public RecyclerViewFragment() {
        // Required empty public constructor
    }

    public static RecyclerViewFragment newInstance() {
        return new RecyclerViewFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //   ReferenceGalleryAdapter referenceGalleryAdapter = new ReferenceGalleryAdapter(Utils.generateReferenceItems(getContext()), this);
        ReferenceGalleryAdapter referenceGalleryAdapter = new ReferenceGalleryAdapter(RiderMainActivity.generateReferenceItems(getContext()), this);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(referenceGalleryAdapter);
    }

    @Override
    public void onRefereneItemClick(int pos, ReferenceItem referenceItem, ImageView sharedImageView) {
        Fragment referenceViewPagerFragment = ReferenceViewPagerFragment.newInstance(pos, RiderMainActivity.generateReferenceItems(getContext()));
        getFragmentManager()
                .beginTransaction()
                .addSharedElement(sharedImageView, ViewCompat.getTransitionName(sharedImageView))
                .addToBackStack(TAG)
                .replace(R.id.content, referenceViewPagerFragment)
                .commit();
    }
}