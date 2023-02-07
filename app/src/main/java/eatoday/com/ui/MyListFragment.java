package eatoday.com.ui;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import eatoday.com.adapter.EditFoodAdapter;
import eatoday.com.adapter.FoodAdapters;
import eatoday.com.model.Food;
import io.reactivex.rxjava3.annotations.NonNull;

public class MyListFragment extends Fragment {
    private List<Food> mList;
    private EditFoodAdapter editFoodAdapter;
    private DatabaseReference mDatabaseReference;
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private String user;
    private Callback callback;
    private Toolbar toolbar_all;

    public interface Callback {
        void onBackProfile();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_list, container, false);
        recyclerView = view.findViewById(R.id.rclist_edit);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mList = new ArrayList<>();
        editFoodAdapter = new EditFoodAdapter(mList);
        recyclerView.setAdapter(editFoodAdapter);
        getListFoodFromRealtime();
        toolbar_all = view.findViewById(R.id.toolbar_detail_all);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar_all);
        toolbar_all.setNavigationOnClickListener(v -> onBackProfile());
        return view;
    }

    private void onBackProfile() {
        if (callback != null) {
            callback.onBackProfile();
        }
    }

    private void getListFoodFromRealtime() {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser().getUid();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Foods").child("datas").child(user);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Food food = dataSnapshot.getValue(Food.class);
                    mList.add(food);
                }
                editFoodAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
            }
        });
    }


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
}