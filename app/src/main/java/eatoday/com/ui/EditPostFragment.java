package eatoday.com.ui;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static android.widget.Toast.LENGTH_SHORT;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import eatoday.com.R;
import eatoday.com.model.Food;


public class EditPostFragment extends Fragment {
    private final int PICK_IMAGE_REQUEST = 22;
    private Uri resultUri = Uri.parse("android.resource://com.example.chetan.printerprinting/" + R.drawable.ic_food_placeholder);

    private Button btnUpdate;
    private CircleImageView cir_imgEditFood;
    private Food food;
    private EditText edt_edit_namefood;
    private EditText edt_edit_Ingredient;
    private EditText edt_edt_Describle;
    private EditText edt_edt_link;
    private Callback callback;
    private Toolbar toolbar_3;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private String user;
    String nameFood, desFood, ingre, linkVideo, imgFood,idFood;

    public interface Callback {
        void onBack();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }
    public EditPostFragment() {

    }

    public EditPostFragment(String idFood,String nameFood, String desFood, String ingre, String linkVideo, String imgFood) {
        this.nameFood = nameFood;
        this.desFood = desFood;
        this.ingre = ingre;
        this.linkVideo = linkVideo;
        this.idFood = idFood;
        this.imgFood = imgFood;
    }
    private static final String ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";

    private static String getRandomString(final int sizeOfRandomString) {
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(sizeOfRandomString);
        for (int i = 0; i < sizeOfRandomString; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_post, container, false);
        btnUpdate = view.findViewById(R.id.btnEdit);
        edt_edit_namefood = view.findViewById(R.id.edtEditNameFood);
        edt_edt_Describle = view.findViewById(R.id.edtEditDescrible);
        edt_edit_Ingredient = view.findViewById(R.id.edtEditIngredient);
        cir_imgEditFood = view.findViewById(R.id.imageEditFood);
        edt_edt_link = view.findViewById(R.id.edtEditLinkVideo);
        toolbar_3 = view.findViewById(R.id.tb_back_list);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser().getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Foods").child("datas").child(user).child(idFood);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        edt_edit_namefood.setText(nameFood);
        edt_edt_Describle.setText(desFood);
        edt_edit_Ingredient.setText(ingre);
        edt_edt_link.setText(linkVideo);
        Glide.with(getContext()).load(imgFood).into(cir_imgEditFood);

        toolbar_3.setNavigationOnClickListener(v -> onBackPressed());
        edt_edt_Describle.setOnTouchListener((v1, event) -> {
            if (edt_edt_Describle.hasFocus()) {
                v1.getParent().requestDisallowInterceptTouchEvent(true);
                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_SCROLL) {
                    v1.getParent().requestDisallowInterceptTouchEvent(false);
                    return true;
                }
            }
            return false;
        });
        edt_edit_Ingredient.setOnTouchListener((v1, event) -> {
            if (edt_edit_Ingredient.hasFocus()) {
                v1.getParent().requestDisallowInterceptTouchEvent(true);
                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_SCROLL) {
                    v1.getParent().requestDisallowInterceptTouchEvent(false);
                    return true;
                }
            }
            return false;
        });
        btnUpdate.setOnClickListener(v -> addListener(v));
        cir_imgEditFood.setOnClickListener(v12 -> SelectImage());
        return view;
    }
    private void onBackPressed() {
        if (callback != null) {
            callback.onBack();
        }
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
            cir_imgEditFood.setImageURI(resultUri);
        } else {
            Toast.makeText(getActivity(), "Please select file", LENGTH_SHORT).show();
        }
    }
    private void addListener(View v) {
        String foodName = edt_edit_namefood.getText().toString();
        String ingredient = edt_edit_Ingredient.getText().toString();
        String describle = edt_edt_Describle.getText().toString();
        String link = edt_edt_link.getText().toString();
       // String IdFood = idfood;
        if (!foodName.isEmpty() && !ingredient.isEmpty() && !describle.isEmpty() && !link.isEmpty()) {
            //Log.v(TAG, "index=" + image);
            //Toast.makeText(getActivity(), image + " " + namefoods + " " + ingredient + " " + describle + " " + link, Toast.LENGTH_SHORT).show();
            Food food = new Food();
            //food.setIdFood(IdFood);
            food.setFoodName(foodName);
            food.setIngredient(ingredient);
            food.setDescrible(describle);
            food.setLinkVideo(link);
//            edt_namefood.setText("");
//            edt_Ingredient.setText("");
//            edt_Describle.setText("");
//            edt_link.setText("");
//            circleImageView.setImageResource(R.drawable.ic_food_placeholder);
            updateToFiresBase(foodName, ingredient, describle, link);
        } else {
            Toast.makeText(getActivity(), "Need to fill information!", Toast.LENGTH_SHORT).show();
        }
    }
    private void updateToFiresBase(String namefood, String Ingredient, String Describle, String link) {
        DatabaseReference myRef = databaseReference;
        DatabaseReference mypost = FirebaseDatabase.getInstance().getReference("Foods").child("allData").child(idFood);
        if (mAuth.getCurrentUser() != null) {
            Map foodInfo = new HashMap();
            foodInfo.put("idFood",idFood);
            foodInfo.put("foodName", namefood);
            foodInfo.put("ingredient", Ingredient);
            foodInfo.put("describle", Describle);
            foodInfo.put("linkVideo", link);
            myRef.updateChildren(foodInfo);
            mypost.updateChildren(foodInfo);
            setToFireStorage(resultUri);
        }
    }

    private void setToFireStorage(Uri imageUri) {
        SimpleDateFormat format = new SimpleDateFormat("HHmmss");
        Date date = new Date();
        final String fileName = getRandomString(4) + format.format(date) + ".png";
        if (imageUri != null) {
            StorageReference str = storage.getReference();
            str.child("profileImage").child(fileName).putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                str.child("profileImage").child(fileName).getDownloadUrl().addOnSuccessListener(DownloadUri -> {
//                    FirebaseDatabase database = firebaseDatabase;
                    DatabaseReference mref = databaseReference.child("foodImage");
                    DatabaseReference mypost = firebaseDatabase.getReference("Foods").child("allData").child(idFood).child("foodImage");
                    mref.setValue(DownloadUri.toString());
                    mypost.setValue(DownloadUri.toString());
                    Toast.makeText(getActivity(), "Data updated", LENGTH_SHORT).show();
                }).addOnFailureListener(exception -> Toast.makeText(getActivity(), "Error", LENGTH_SHORT).show());
            });
        }
    }
}