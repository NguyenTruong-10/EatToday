package eatoday.com.ui;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static android.widget.Toast.LENGTH_SHORT;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import eatoday.com.R;
import eatoday.com.model.Food;

public class MyPostFragment extends Fragment {
    private static final int SELECT_FILE = 1;
    private final int PICK_IMAGE_REQUEST = 22;
    //private Uri filePath;
    Button btnPost;
    private static final int RESULT_LOAD_IMAGE = 1;
    int SELECT_PICTURE = 200;
    //ImageView circleImageView;
    CircleImageView circleImageView;
    Food food;
    private EditText edt_namefood;
    private EditText edt_Ingredient;
    private EditText edt_Describle;
    private EditText edt_link;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private String user;
    Uri resultUri = Uri.parse("android.resource://com.example.chetan.printerprinting/" + R.drawable.ic_food_placeholder);
    SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");
    Date date = new Date();
    String idfood = getRandomString(3) + formatter.format(date);
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onResume() {
        super.onResume();
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edt_Ingredient = view.findViewById(R.id.txtIngredient);

        view.setOnClickListener(v -> {
            edt_Ingredient.clearFocus();

            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_post, container, false);
        btnPost = view.findViewById(R.id.btnPost);
        edt_namefood = view.findViewById(R.id.txtNameFood);
        edt_Describle = view.findViewById(R.id.txtDescrible);
        edt_Ingredient = view.findViewById(R.id.txtIngredient);
        circleImageView = view.findViewById(R.id.imageFood);
        edt_link = view.findViewById(R.id.txtLinkVideo);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser().getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        //databaseReference = firebaseDatabase.getReference("Food");
        databaseReference = firebaseDatabase.getReference("Foods").child("datas").child(user).child(idfood);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        food = new Food();
        edt_Describle.setOnTouchListener((v1, event) -> {
            if (edt_Describle.hasFocus()) {
                v1.getParent().requestDisallowInterceptTouchEvent(true);
                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_SCROLL) {
                    v1.getParent().requestDisallowInterceptTouchEvent(false);
                    return true;
                }
            }
            return false;
        });
        edt_Ingredient.setOnTouchListener((v1, event) -> {
            if (edt_Ingredient.hasFocus()) {
                v1.getParent().requestDisallowInterceptTouchEvent(true);
                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_SCROLL) {
                    v1.getParent().requestDisallowInterceptTouchEvent(false);
                    return true;
                }
            }
            return false;
        });
        btnPost.setOnClickListener(v -> addListener(v));
        circleImageView.setOnClickListener(v12 -> SelectImage());
        return view;
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
            circleImageView.setImageURI(resultUri);
        } else {
            Toast.makeText(getActivity(), "Please select file", LENGTH_SHORT).show();
        }
    }

    private static final String ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";

    private static String getRandomString(final int sizeOfRandomString) {
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(sizeOfRandomString);
        for (int i = 0; i < sizeOfRandomString; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    private void addListener(View v) {
        String namefoods = edt_namefood.getText().toString();
        String ingredient = edt_Ingredient.getText().toString();
        String describle = edt_Describle.getText().toString();
        String link = edt_link.getText().toString();
        if (!namefoods.isEmpty() && !ingredient.isEmpty() && !describle.isEmpty() && !link.isEmpty()) {
            //Log.v(TAG, "index=" + image);
            //Toast.makeText(getActivity(), image + " " + namefoods + " " + ingredient + " " + describle + " " + link, Toast.LENGTH_SHORT).show();
            Food food = new Food();
            food.setNameFood(namefoods);
            food.setIngredient(ingredient);
            food.setDescrible(describle);
            food.setLinKVideo(link);
            // food.setFoodId(idfood);
            edt_namefood.setText("");
            edt_Ingredient.setText("");
            edt_Describle.setText("");
            edt_link.setText("");
            circleImageView.setImageResource(R.drawable.ic_food_placeholder);
            addDatatoFirebase(namefoods, ingredient, describle, link);
        } else {
            Toast.makeText(getActivity(), "Need to fill information!", Toast.LENGTH_SHORT).show();
        }
    }

    private void addDatatoFirebase(String namefood, String Ingredient, String Describle, String link) {
//        food.setNameFood(namefood);
//        food.setIngredient(Ingredient);
//        food.setDescrible(Describle);
//        food.setLinKVideo(link);
//        food.setFoodId(idFood);
//        food.setImageFood(imgFood);
        DatabaseReference myRef = databaseReference;
        if (mAuth.getCurrentUser() != null) {
//            String idfood = getRandomString(6);
//            String UserID = mAuth.getCurrentUser().getUid();
            Map foodInfo = new HashMap();
            foodInfo.put("foodName", namefood);
            foodInfo.put("ingredient", Ingredient);
            foodInfo.put("describle", Describle);
            foodInfo.put("linkVideo", link);
            myRef.updateChildren(foodInfo);
            setToFireStorage(resultUri);
        }
    }

    private void setToFireStorage(Uri imageUri) {
        SimpleDateFormat format = new SimpleDateFormat("DDHHmmss");
        Date date = new Date();
        final String fileName =  getRandomString(4) + format.format(date);
        if (imageUri != null) {
            StorageReference str = storage.getReference();
            str.child("profileImage").child(fileName).putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                str.child("profileImage").child(fileName).getDownloadUrl().addOnSuccessListener(DownloadUri -> {
//                    FirebaseDatabase database = firebaseDatabase;
                    DatabaseReference mref = databaseReference.child("foodImage");
                    mref.setValue(DownloadUri.toString());
                    Toast.makeText(getActivity(), "Data updated", LENGTH_SHORT).show();
                }).addOnFailureListener(exception -> Toast.makeText(getActivity(), "Error", LENGTH_SHORT).show());
            });
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isAcceptingText()) {
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    0
            );
        }

    }

}