package eatoday.com.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.telecom.Call;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import eatoday.com.R;
import eatoday.com.databinding.FragmentReAuthenticateUserBinding;
import eatoday.com.model.User;

public class ReAuthenticateUserFragment extends Fragment {
    private FragmentReAuthenticateUserBinding reAuthenticateUserBinding;
    private FirebaseAuth mAuth;
    private DatabaseReference mReference;
    private FirebaseUser user;
    private Callback callback;
    private ValueEventListener eventListener;

    public interface Callback{
        void onBack();
        void onContinue();
        void onForgotPasswordClicked();
    }
    public void setCallback(Callback callback) {
        this.callback = callback;
    }
    private static final String RE_AUTHENTICATE = "re_authenticate_user";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        reAuthenticateUserBinding = FragmentReAuthenticateUserBinding
                .inflate(inflater, container, false);
        return reAuthenticateUserBinding.getRoot();
    }
    private void onBackPressed() {
        if (callback != null) {
            callback.onBack();
        }
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reAuthenticateUserBinding.tbBackPro.setNavigationOnClickListener(v -> onBackPressed());
        reAuthenticateUserBinding.edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                TextInputLayout password = reAuthenticateUserBinding.passTextInputLayout;
                if (reAuthenticateUserBinding.edtPassword.length() != 0) {
                    password.setEndIconMode(password.END_ICON_PASSWORD_TOGGLE);
                } else {
                    password.setEndIconMode(password.END_ICON_NONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        reAuthenticateUserBinding.btnContinue.setOnClickListener(v -> onContinueClicked());

        reAuthenticateUserBinding.tvForgotPassword.setOnClickListener(v -> {
            if (callback != null) {
                callback.onForgotPasswordClicked();
            }
        });

        //initialize
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
    }

    @Override
    public void onStart() {
        super.onStart();
        readNameEventListener(mReference);
    }

    private void readNameEventListener(DatabaseReference reference) {
        ValueEventListener userInfoListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                updateUI(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(RE_AUTHENTICATE, "Reauthenticate load user name: onCancelled", error.toException());
                Toast.makeText(getContext(), "Failed to load user name",
                        Toast.LENGTH_SHORT).show();
            }
        };
        reference.addValueEventListener(userInfoListener);

        eventListener = userInfoListener;
    }

    private void onContinueClicked() {
        String passwordAuth = reAuthenticateUserBinding.edtPassword.getText().toString();
        if (!validateForm()) {
            return;
        }

        reAuthenticateUser(passwordAuth);
    }

    private void reAuthenticateUser(String password) {
        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), password);

// Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(RE_AUTHENTICATE, "User re authenticated");

                if (callback != null) {
                    reAuthenticateUserBinding.edtPassword.setText("");
                    callback.onContinue();
                    Toast.makeText(getContext(), "User re authenticated", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(RE_AUTHENTICATE, "Error when re authenticate user", task.getException());
                Toast.makeText(getContext(), "Cannot re authenticate user for some reason, check again",
                Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        String dataOfEdittext = reAuthenticateUserBinding.edtPassword.getText().toString();
        if (TextUtils.isEmpty(dataOfEdittext)) {
            reAuthenticateUserBinding.edtPassword.setError("Required");
            valid = false;
        } else {
            reAuthenticateUserBinding.edtPassword.setError(null);
        }
        return valid;
    }

    private void updateUI(User user) {
        if (user != null) {
            reAuthenticateUserBinding.tvWelcome.setText("Chào " + user.getFirstName());
        } else {
            Toast.makeText(getContext(),
                    "User = null, request fix userEventListener",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (eventListener != null) {
            mReference.removeEventListener(eventListener);
        }
    }
}