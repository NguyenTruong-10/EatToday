package eatoday.com.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import eatoday.com.R;
import eatoday.com.databinding.FragmentProfilesBinding;
import eatoday.com.model.User;

public class ProfileFragment extends Fragment {
    private FragmentManager fragmentManager;
    private MyPostFragment myPostFragment = new MyPostFragment();
    private FragmentProfilesBinding profilesBinding;
    private Callback callback;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private ValueEventListener userEventListener;

    private static final String RELOAD_INFO = "Reload profiles info";
    private static final String EVENT_USER_LISTENER = "Event user listener";

    public interface Callback {
        void onClickFood();

        void onClickUser();

        void onLogOut();

        void onClickList();

        void onClickChangePassword();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        profilesBinding = FragmentProfilesBinding.inflate(inflater, container, false);
        return profilesBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        profilesBinding.btnChangeuser.setOnClickListener(v -> {
            if (callback != null) {
                callback.onClickUser();
            }
        });

        profilesBinding.btnFoods.setOnClickListener(v -> {
            if (callback != null) {
                callback.onClickFood();
            }
        });
        profilesBinding.btnList.setOnClickListener(v -> {
            if (callback != null) {
                callback.onClickList();
            }
        });

        profilesBinding.btnLogout.setOnClickListener(v -> {
            if (callback != null) {
                mAuth.signOut();
                Toast.makeText(getContext(),
                        "Đăng xuất",
                        Toast.LENGTH_SHORT).show();
                callback.onLogOut();
            }
        });

        profilesBinding.btnChangepass.setOnClickListener(v -> {
            if (callback != null) {
                callback.onClickChangePassword();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(mAuth.getCurrentUser().getUid());
    }

    @Override
    public void onStart() {
        super.onStart();
        readUserEventListener(databaseReference);
    }

    private void readUserEventListener(DatabaseReference databaseReference) {
        //To get user data at a path and listen for changes to refresh data
        ValueEventListener myUserListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Get User object and use the values to update the UI
                User user = snapshot.getValue(User.class);
                updateUI(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Getting user failed, log a message
                Log.e(EVENT_USER_LISTENER, "Load user: onCancelled", error.toException());
                Toast.makeText(getContext(), "Failed to load user.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        databaseReference.addValueEventListener(myUserListener);

        // Keep copy of post listener so we can remove it when app stops
        userEventListener = myUserListener;
    }

    @Override
    public void onStop() {
        super.onStop();
        //remove user value event listener
        if (userEventListener != null) {
            databaseReference.removeEventListener(userEventListener);
        }
    }

    private void updateUI(User user) {
        if (user != null) {
            profilesBinding.txtName.
                    setText(user.getLastName() + " " + user.getFirstName());
        } else {
            Toast.makeText(getContext(),
                    "User = null, request fix userEventListener",
                    Toast.LENGTH_SHORT).show();
        }
    }
}