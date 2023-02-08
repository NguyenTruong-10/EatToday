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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import eatoday.com.R;
import eatoday.com.databinding.FragmentAccountRecoveryBinding;
import eatoday.com.model.User;

public class AccountRecoveryFragment extends Fragment {
    private FragmentAccountRecoveryBinding accountRecoveryBinding;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private static final String ACCOUNT_RECOVERY = "account_recovery";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        accountRecoveryBinding = FragmentAccountRecoveryBinding.inflate(inflater, container, false);
        return accountRecoveryBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        accountRecoveryBinding.btnSentEmail.setOnClickListener(v -> {
            sendPasswordResetEmail(user.getEmail());
        });

        //initialize
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateUI(user);
    }

    private void sendPasswordResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(ACCOUNT_RECOVERY, "User account recovery: send password reset email success");
                Toast.makeText(getContext(), "Email sent", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(ACCOUNT_RECOVERY, "Error when recovery account : send password reset email failed", task.getException());
                Toast.makeText(getContext(), "Email cannot sent: "+ task.getException() + ", check again",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void updateUI(FirebaseUser user) {
        TextView noteAlert = accountRecoveryBinding.noteAlert;
        if (user != null) {
            noteAlert.setText("Sau khi ấn nút phía dưới, Eatoday sẽ gửi email chứa link khôi phục mật khẩu đến " +
                              "địa chỉ email của bạn là " + user.getEmail() +
                              ". Vui lòng kiểm tra email để tiếp tục.");
        } else {
            Toast.makeText(getContext(),
                    "User = null, request fix userEventListener",
                    Toast.LENGTH_SHORT).show();
        }
    }
}