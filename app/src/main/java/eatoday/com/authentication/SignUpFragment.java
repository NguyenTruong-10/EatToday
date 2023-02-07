package eatoday.com.authentication;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import eatoday.com.R;
import eatoday.com.databinding.FragmentSignUpBinding;

public class SignUpFragment extends Fragment {

    private static final String SIGN_UP_METHOD = "Sign up method";
    private static final String ARG_PARAM2 = "param2";
    private FirebaseAuth mAuth;
    private FragmentSignUpBinding signUpBinding;
    private Callback callback;
    private DatabaseReference databaseReference;
    // 10 tháng 10, 2002
    private SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
    Uri resultUriUser = Uri.parse("android.resource://com.example.chetan.printerprinting/" + R.drawable.ic_food_placeholder);

    public interface Callback {
        void onSignUp();

        void alreadyRegistered();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        signUpBinding = FragmentSignUpBinding.inflate(inflater, container, false);
        return signUpBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Button

        signUpBinding.btnSignUp.setOnClickListener(v -> {
            String emailAddress = signUpBinding.edtEmailAddress.getText().toString();
            String password = signUpBinding.edtPassword.getText().toString();

            createNewAccount(emailAddress, password);
        });

        signUpBinding.btnRegisteredLogIn.setOnClickListener(v -> {
            if (callback != null) {
                callback.alreadyRegistered();
            }
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    private void createNewAccount(String email, String password) {
        if (!validateForm(signUpBinding.edtFirstName) ||
                !validateForm(signUpBinding.edtLastName) ||
                !validateForm(signUpBinding.dayOfBirthDate) ||
                !validateForm(signUpBinding.monthOfBirthDate) ||
                !validateForm(signUpBinding.yearOfBirthDate) ||
                !validateForm(signUpBinding.edtEmailAddress) ||
                !validateForm(signUpBinding.edtPassword) ||
                !validateForm(signUpBinding.edtReEnterPassword)) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        Log.d(SIGN_UP_METHOD, "createUserWithE&P:success");
                        if (callback != null) {
                            callback.onSignUp();
                            onSignUpSuccess(task.getResult().getUser());
                        }

                    } else {
                        Log.d(SIGN_UP_METHOD, "createUserWithE&P:failure", task.getException());
                        Toast.makeText(getContext(),
                                "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void onSignUpSuccess(FirebaseUser user) {
        String firstName = signUpBinding.edtFirstName.getText().toString();
        String lastName = signUpBinding.edtLastName.getText().toString();
        String dayOfBirthDate = signUpBinding.dayOfBirthDate.getText().toString();
        String monthOfBirthDate = signUpBinding.monthOfBirthDate.getText().toString();
        String yearOfBirthDate = signUpBinding.yearOfBirthDate.getText().toString();
        String userName = userNameFromEmail(user.getEmail());

        writeNewUser(user.getUid(), firstName,
                lastName, dayOfBirthDate,
                monthOfBirthDate, yearOfBirthDate,
                userName, user.getEmail());
    }

    public void writeNewUser(String userId, String firstName,
                             String lastName, String day,
                             String month, String year,
                             String userName, String email) {
        Map<String, String> birthDate = new HashMap<>();
        Map<String, Object> user = new HashMap<>();
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        user.put("userName", userName);
        user.put("email", email);
        birthDate.put("day", day);
        birthDate.put("month", month);
        birthDate.put("year", year);
        user.put("birthdate", birthDate);

        signUpBinding.edtFirstName.setText("");
        signUpBinding.edtLastName.setText("");
        signUpBinding.dayOfBirthDate.setText("");
        signUpBinding.monthOfBirthDate.setText("");
        signUpBinding.yearOfBirthDate.setText("");
        signUpBinding.edtEmailAddress.setText("");
        signUpBinding.edtPassword.setText("");
        signUpBinding.edtReEnterPassword.setText("");

        databaseReference.child("Users").child(userId).updateChildren(user);
    }

    private String userNameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    private boolean validateForm(EditText editText) {
        boolean valid = true;

        String dataOfEdittext = editText.getText().toString();
        if (TextUtils.isEmpty(dataOfEdittext)) {
            editText.setError("Required");
            valid = false;
        } else {
            editText.setError(null);
        }
        return valid;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        signUpBinding = null;
    }
}