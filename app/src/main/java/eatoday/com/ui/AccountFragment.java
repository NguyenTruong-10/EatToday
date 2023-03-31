package eatoday.com.ui;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static android.widget.Toast.LENGTH_SHORT;

import android.content.Intent;
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
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import eatoday.com.R;
import eatoday.com.databinding.FragmentAccountBinding;
import eatoday.com.model.Birthdate;
import eatoday.com.model.Food;
import eatoday.com.model.User;

public class AccountFragment extends Fragment {
    private final int PICK_IMAGE_REQUEST = 22;
    private Uri resultUri = Uri.parse("android.resource://com.example.chetan.printerprinting/" + R.drawable.img_ava);

    private FragmentAccountBinding accountBinding;
    private FirebaseAuth auth;
    private Callback callback;
    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;
    private CircleImageView circle_ava;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private String user;
    private FirebaseAuth mAuth;

    private static final String ACCOUNT_USER_LISTENER = "Account user listener";
    private static final String UPDATE_USER_INFO = "Update user info";

    public interface Callback {
        void onConfirmUpdate();
        void onBack();
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
        accountBinding.btnUpdate.setOnClickListener(v -> onConfirmClicked());
        accountBinding.toolbarBackProfile.setNavigationOnClickListener(v -> onBackPressed());
        //initialize
        auth = FirebaseAuth.getInstance();
        circle_ava = view.findViewById(R.id.img_avatar);
        circle_ava.setOnClickListener(v->SelectImage());
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser().getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(auth.getCurrentUser().getUid());
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }
    private void SelectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
    }

    // Override onActivityResult method
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            circle_ava.setImageURI(resultUri);
        } else {
            Toast.makeText(getActivity(), "Please select file", LENGTH_SHORT).show();
        }
    }
    private void onBackPressed() {
        if (callback != null) {
            callback.onBack();
        }
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
    private void setAvaToFireStorage(Uri imageUri) {
        SimpleDateFormat format = new SimpleDateFormat("HHmmss");
        Date date = new Date();
        final String fileName = "ava" + format.format(date) + ".png";
        if (imageUri != null) {
            StorageReference str = storage.getReference();
            str.child("avatarImage").child(fileName).putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                str.child("avatarImage").child(fileName).getDownloadUrl().addOnSuccessListener(DownloadUri -> {
//                    FirebaseDatabase database = firebaseDatabase;
                    DatabaseReference mref = databaseReference.child("avatar");
                    mref.setValue(DownloadUri.toString());
                    Toast.makeText(getActivity(), "Data updated", LENGTH_SHORT).show();
                }).addOnFailureListener(exception -> Toast.makeText(getActivity(), "Error", LENGTH_SHORT).show());
            });
        }
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
        setAvaToFireStorage(resultUri);
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
            Glide.with(getContext())
                    .load(user.getAvatar())
                    .circleCrop()
                    .into(circle_ava);
        }
    }
}