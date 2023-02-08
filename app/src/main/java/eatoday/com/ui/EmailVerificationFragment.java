package eatoday.com.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import eatoday.com.databinding.FragmentEmailVerificationBinding;
import eatoday.com.model.User;

public class EmailVerificationFragment extends Fragment {
    private FragmentEmailVerificationBinding verificationBinding;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    private String userId;

    private static final String EMAIL_VERIFICATION = "Email verification";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        verificationBinding = FragmentEmailVerificationBinding
                .inflate(inflater, container, false);
        return verificationBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        verificationBinding.btnSentEmail.setOnClickListener(v -> {
            sendEmailVerification();
        });

        verificationBinding.btnReload.setOnClickListener(v -> {
            reload();
        });

        //initialize
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
    }

    @Override
    public void onStart() {
        super.onStart();
        readNameUserListener(databaseReference);
        switchStateVerify(mAuth.getCurrentUser());
    }

    private void sendEmailVerification() {
        mAuth.getCurrentUser().sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(EMAIL_VERIFICATION, "Email verification: send email success");
                        Toast.makeText(getContext(), "Email sent", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(EMAIL_VERIFICATION, "Error when verify email : send email failed", task.getException());
                        Toast.makeText(getContext(), "Email cannot sent: "+ task.getException() + ", check again",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void readNameUserListener(DatabaseReference reference) {
        ValueEventListener userInfoListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                updateUI(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(EMAIL_VERIFICATION, "Reauthenticate load user name: onCancelled", error.toException());
                Toast.makeText(getContext(), "Failed to load user name",
                        Toast.LENGTH_SHORT).show();
            }
        };
        reference.addValueEventListener(userInfoListener);

        valueEventListener = userInfoListener;
    }

    @SuppressLint("SetTextI18n")
    private void updateUI(User user) {
        if (user != null) {
            verificationBinding.tvNotVerified.setText("Chào " + user.getFirstName() +
                    ", chúc mừng bạn chính thức có tài khoản ở Eatoday nha.");
        } else {
            Toast.makeText(getContext(),
                    "User = null, request fix userEventListener",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void reload() {
        mAuth.getCurrentUser().reload().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.e(EMAIL_VERIFICATION, "Success");
                if (!mAuth.getCurrentUser().isEmailVerified()) {
                    Toast.makeText(getContext(),
                            "Email still not verified",
                            Toast.LENGTH_SHORT).show();
                }
                switchStateVerify(mAuth.getCurrentUser());
            } else {
                Log.e(EMAIL_VERIFICATION, "Reload error when check email verify status", task.getException());
                Toast.makeText(getContext(),
                        "Failed to reload email verify status",
                        Toast.LENGTH_SHORT).show();
                switchStateVerify(null);
            }
        }); //reload user information - Testing only
    }

    private void switchStateVerify(FirebaseUser user) {
        if (!user.isEmailVerified()) {
            verificationBinding.groupNotVerified.setVisibility(View.VISIBLE);
            verificationBinding.groupVerifiedSuccess.setVisibility(View.GONE);

        } else {
            verificationBinding.groupNotVerified.setVisibility(View.GONE);
            verificationBinding.groupVerifiedSuccess.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (valueEventListener != null) {
            databaseReference.removeEventListener(valueEventListener);
        }
    }
}