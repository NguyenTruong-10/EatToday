package eatoday.com.ui;

import static android.content.ContentValues.TAG;
import static android.widget.Toast.LENGTH_SHORT;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;

import eatoday.com.R;
import eatoday.com.model.Food;

public class Detail_Food_Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    boolean check = true;
    String nameFood, desFood, ingre, linkVideo, imgFood, a;
    Food food;
    private Toolbar toolbar;
    private Button btnLink;

    public Detail_Food_Fragment() {

    }

    public Detail_Food_Fragment(String nameFood, String desFood, String ingre, String linkVideo, String imgFood) {
        this.nameFood = nameFood;
        this.desFood = desFood;
        this.ingre = ingre;
        this.linkVideo = linkVideo;
        this.imgFood = imgFood;
    }

    public static Detail_Food_Fragment newInstance(String param1, String param2) {
        Detail_Food_Fragment fragment = new Detail_Food_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail__food, container, false);
        ImageView imglike = view.findViewById(R.id.imgFavorite);
        ImageView imageholder = view.findViewById(R.id.imgThumb);
        TextView nameholder = view.findViewById(R.id.tvTitle);
        TextView desholder = view.findViewById(R.id.tvInstructions);
        TextView inglholder = view.findViewById(R.id.tvIngredients);
        TextView linkVideoholder = view.findViewById(R.id.tvYoutube);
        nameholder.setText(nameFood);
        desholder.setText(desFood);
        inglholder.setText(ingre);
        linkVideoholder.setText(linkVideo);
        linkVideoholder.setText("Watch YouTube");
//        a = linkVideoholder.getText().toString();
        Glide.with(getContext()).load(imgFood).into(imageholder);
        toolbar = view.findViewById(R.id.toolbar_detail_a);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        Log.v(TAG, "YOUTUEBNEEEEE=" + a);
        linkVideoholder.setOnClickListener(v -> openWebPage(linkVideo));
        imglike.setOnClickListener(v -> {
            if (check == false){
                imglike.setColorFilter(Color.parseColor("#ff0303"));
            check = true;
            }
            else if (check == true) {
                imglike.setColorFilter(Color.parseColor("#FFFFFF"));
                check = false;
            }
        });

        return view;
        // View view=inflater.inflate(R.layout.fragment_detail__food, container, false);
    }


    public void openWebPage(String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
        Toast.makeText(getActivity(), url, LENGTH_SHORT).show();

    }


    private void onBackPressed() {
        AppCompatActivity activity = (AppCompatActivity) getContext();
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).addToBackStack(null).commit();

    }
}