package eatoday.com.ui;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import eatoday.com.R;
import eatoday.com.adapter.FoodAdapters;
import eatoday.com.adapter.PhotoAdapter;
import eatoday.com.model.Food;
import eatoday.com.model.Photo;
import io.reactivex.rxjava3.annotations.NonNull;
import me.relex.circleindicator.CircleIndicator3;

public class HomeFragment extends Fragment {
    private List<Food> mList;
    private FoodAdapters foodAdapters;
    private DatabaseReference mDatabaseReference;
    private RecyclerView recview;
    private FirebaseAuth mAuth;
    private String user;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private ViewPager2 mViewPage2;
    private CircleIndicator3 mcircleIndicator3;
    private List<Photo> mListPhoto;
    private Handler mhandler = new Handler(Looper.getMainLooper());
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int currentPosition = mViewPage2.getCurrentItem();
            if(currentPosition == mListPhoto.size()-1){
                mViewPage2.setCurrentItem(0);
            }else{
                mViewPage2.setCurrentItem(currentPosition + 1);

            }
        }
    };
    public HomeFragment() {
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@androidx.annotation.NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recview = (RecyclerView) view.findViewById(R.id.list_item);
        recview.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mViewPage2 = view.findViewById(R.id.viewPage2);
        mcircleIndicator3 = view.findViewById(R.id.circle3);

        mViewPage2.setOffscreenPageLimit(3);
        mViewPage2.setClipToPadding(false);
        mViewPage2.setClipChildren(false);
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(80));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });
        mViewPage2.setPageTransformer(compositePageTransformer);

        mListPhoto = getListPhoto();
        PhotoAdapter photoAdapter = new PhotoAdapter(mListPhoto);
        mViewPage2.setAdapter(photoAdapter);
        mcircleIndicator3.setViewPager(mViewPage2);
        mViewPage2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mhandler.removeCallbacks(runnable);
                mhandler.postDelayed(runnable,3000);
            }
        });
        mList = new ArrayList<>();
        getListFoodFromRealtime();
        foodAdapters = new FoodAdapters(mList);
        recview.setAdapter(foodAdapters);
    }
    private List<Photo> getListPhoto(){
        List<Photo> list = new ArrayList<>();
        list.add(new Photo(R.drawable.img_chef_1));
        list.add(new Photo(R.drawable.img_chef_1));
        list.add(new Photo(R.drawable.img_chef_1));
        list.add(new Photo(R.drawable.img_chef_1));
        list.add(new Photo(R.drawable.img_chef_1));
        list.add(new Photo(R.drawable.img_chef_1));
        list.add(new Photo(R.drawable.img_chef_1));
        list.add(new Photo(R.drawable.img_chef_1));
        list.add(new Photo(R.drawable.img_chef_1));
        return list;
    }

    private void getListFoodFromRealtime() {
        mAuth = FirebaseAuth.getInstance();
//        user = mAuth.getCurrentUser().getUid();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Foods").child("allData");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Food food = dataSnapshot.getValue(Food.class);
                    mList.add(food);
                }
                foodAdapters.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
            }
        });
    }
//    @Override
//    protected void onPaused(){
//        super.onPause();
//        mhandler.removeCallbacks(runnable);
//    }
//    protected
}