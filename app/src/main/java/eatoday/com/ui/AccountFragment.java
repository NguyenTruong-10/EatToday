package eatoday.com.ui;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import eatoday.com.databinding.FragmentAccountBinding;
import eatoday.com.model.Birthdate;
import eatoday.com.model.User;

public class AccountFragment extends Fragment {
    private FragmentAccountBinding accountBinding;
    private FirebaseAuth auth;
    private Callback callback;
    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;

    private static final String ACCOUNT_USER_LISTENER = "Account user listener";
    private static final String UPDATE_USER_INFO = "Update user info";

    public interface Callback {
        void onConfirmUpdate();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        accountBinding = FragmentAccountBinding.inflate(inflater, container, false);
        return accountBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        accountBinding.btnUpdate.setOnClickListener(v -> {
            onConfirmClicked();
        });

        //initialize
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(auth.getCurrentUser().getUid());
    }

    @Override
    public void onStart() {
        super.onStart();
        readUserEventListener(databaseReference);
    }

    private void readUserEventListener(DatabaseReference databaseReference) {
        //To get user data at a path and listen for changes to refresh data
        ValueEventListener userInfoListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                updateUI(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(ACCOUNT_USER_LISTENER, "Account update load user: onCancelled", error.toException());
                Toast.makeText(getContext(), "Failed to load user to account.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        databaseReference.addValueEventListener(userInfoListener);

        eventListener = userInfoListener;
    }

    private void onConfirmClicked() {
        String firstName = accountBinding.edtFirstName.getText().toString();
        String lastName = accountBinding.edtLastName.getText().toString();
        String day = accountBinding.edtDayBirthdate.getText().toString();
        String month = accountBinding.edtMonthBirthdate.getText().toString();
        String year = accountBinding.edtYearBirthdate.getText().toString();
        String userName = accountBinding.edtUserName.getText().toString();

        if (!validateForm(accountBinding.edtFirstName) ||
                !validateForm(accountBinding.edtLastName) ||
                !validateForm(accountBinding.edtDayBirthdate) ||
                !validateForm(accountBinding.edtMonthBirthdate) ||
                !validateForm(accountBinding.edtYearBirthdate) ||
                !validateForm(accountBinding.edtUserName)) {
            return;
        }
        updateUserInfo(firstName, lastName, day, month, year, userName);
    }

    private void updateUserInfo(String firstName, String lastName,
                                String day, String month,
                                String year, String userName) {
        Map<String, String> birthDate = new HashMap<>();
        Map<String, Object> user = new HashMap<>();
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        user.put("userName", userName);
        birthDate.put("day", day);
        birthDate.put("month", month);
        birthDate.put("year", year);
        user.put("birthdate", birthDate);

        databaseReference.updateChildren(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                accountBinding.edtFirstName.setText("");
                accountBinding.edtLastName.setText("");
                accountBinding.edtDayBirthdate.setText("");
                accountBinding.edtMonthBirthdate.setText("");
                accountBinding.edtYearBirthdate.setText("");
                accountBinding.edtUserName.setText("");
                if (callback != null) {
                    callback.onConfirmUpdate();
                }
                Toast.makeText(getContext(), "Successfully updated",
                        Toast.LENGTH_SHORT).show();

            } else {
                Log.e(UPDATE_USER_INFO, "Error when update user", task.getException());
                Toast.makeText(getContext(), "Cannot update user for some reason, check again",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (eventListener != null) {
            databaseReference.removeEventListener(eventListener);
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

    private void updateUI(User user) {
        if (user != null) {
            accountBinding.edtFirstName.setText(user.getFirstName());
            accountBinding.edtLastName.setText(user.getLastName());
            accountBinding.edtUserName.setText(user.getUserName());
            accountBinding.edtDayBirthdate.setText(String.valueOf(user.getBirthdate().getDay()));
            accountBinding.edtMonthBirthdate.setText(String.valueOf(user.getBirthdate().getMonth()));
            accountBinding.edtYearBirthdate.setText(String.valueOf(user.getBirthdate().getYear()));
        } else {
            Toast.makeText(getContext(),
                    "User = null, request fix userEventListener",
                    Toast.LENGTH_SHORT).show();
        }
    }
}