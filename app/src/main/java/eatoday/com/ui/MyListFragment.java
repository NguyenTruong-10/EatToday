package eatoday.com.ui;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import eatoday.com.R;
import eatoday.com.adapter.EditFoodAdapter;
import eatoday.com.model.Food;

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
        editFoodAdapter = new EditFoodAdapter(mList, new EditFoodAdapter.IClickListener() {
            @Override
            public void onClickDelete(Food food) {
                oncClickDelete(food);
            }
        });
        recyclerView.setAdapter(editFoodAdapter);
        toolbar_all = view.findViewById(R.id.toolbar_detail_all);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar_all);
        toolbar_all.setNavigationOnClickListener(v -> onBackProfile());
        getListFoodFromRealtime();
        return view;
    }

    private void oncClickDelete(Food food) {
        new AlertDialog.Builder(getActivity()).setTitle(getString(R.string.app_name))
                .setMessage("Are you sure want delete?")
                .setPositiveButton("Yes", (dialog, which) -> {
                            DatabaseReference mDatabaseReference = FirebaseDatabase
                                    .getInstance().
                                    getReference("Foods").
                                    child("datas").child(user);
                            mDatabaseReference.child(String.valueOf(food.getIdFood())).removeValue((error, ref) -> {
                                Log.v(TAG, "tesds=" + mDatabaseReference.child(String.valueOf(food.getIdFood())));
                            });
                            DatabaseReference allPost = FirebaseDatabase
                                    .getInstance().
                                    getReference("Foods").
                                    child("allData");
                            allPost.child(String.valueOf(food.getIdFood())).removeValue((error, ref) -> {
                                Log.v(TAG, "tsssesds=" + allPost.child(String.valueOf(food.getIdFood())));
                            });
                        }

                ).setNegativeButton("Cancel", null).show();
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
        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Food food = snapshot.getValue(Food.class);
                if (food != null) {
                    mList.add(food);
                    editFoodAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Food food = snapshot.getValue(Food.class);
                if (food == null || mList == null || mList.isEmpty()) {
                    Toast.makeText(getActivity(), "Delete not good", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (int i = 0; i < mList.size(); i++) {
                    if (food.getIdFood() == mList.get(i).getIdFood()) {
                        mList.remove(mList.get(i));
                        Toast.makeText(getActivity(), "Delete success", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                editFoodAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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