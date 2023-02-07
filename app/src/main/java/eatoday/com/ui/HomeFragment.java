package eatoday.com.ui;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import eatoday.com.model.Food;
import io.reactivex.rxjava3.annotations.NonNull;

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

    //    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        View view = inflater.inflate(R.layout.fragment_home, container, false);
//
//        recview = (RecyclerView) view.findViewById(R.id.list_item);
////        recview.setLayoutManager(new LinearLayoutManager(getContext()));
//        recview.setLayoutManager(new GridLayoutManager(getContext(), 2));
//        mAuth = FirebaseAuth.getInstance();
//        user = mAuth.getCurrentUser().getUid();
//        FirebaseRecyclerOptions<Food> options =
//                new FirebaseRecyclerOptions.Builder<Food>()
//                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Foods").child("datas").child(user), Food.class).build();
//        foodAdapters = new FoodAdapters(options);
//        recview.setAdapter(foodAdapters);
//        return view;
//
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        foodAdapters.startListening();
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        foodAdapters.stopListening();
//    }
//}
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
        mList = new ArrayList<>();
        getListFoodFromRealtime();
        foodAdapters = new FoodAdapters(mList);
        recview.setAdapter(foodAdapters);
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
}