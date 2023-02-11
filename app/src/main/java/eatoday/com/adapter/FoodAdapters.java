package eatoday.com.adapter;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;


import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import eatoday.com.R;
import eatoday.com.model.Food;
import eatoday.com.ui.Detail_Food_Fragment;

public class FoodAdapters extends RecyclerView.Adapter<FoodAdapters.FoodViewholder> {
    private List<Food> mlist;
    public FoodAdapters(List<Food> mlist) {
        this.mlist = mlist;
    }

    @NonNull
    @Override
    public FoodViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_category, parent, false);
        return new FoodViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodAdapters.FoodViewholder holder, int position) {
        Food food = mlist.get(position);
        if (food == null) {
            return;
        }
        holder.nameFood.setText(food.getFoodName());
        Glide.with(holder.imgFood.getContext())
                .load(food.getFoodImage())
                .placeholder(R.drawable.ic_food_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imgFood);
        holder.imgFood.setOnClickListener(view -> {
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout,
                            new Detail_Food_Fragment(food.getFoodName(), food.getDescrible(), food.getIngredient(), food.getLinkVideo(), food.getFoodImage())).addToBackStack(null).commit();
        });
    }

    @Override
    public int getItemCount() {
        if (mlist != null) {
            return mlist.size();
        }
        return 0;
    }

    public class FoodViewholder extends RecyclerView.ViewHolder {
        private TextView nameFood;
        private ImageView imgFood;

        public FoodViewholder(@NonNull View itemView) {
            super(itemView);
            nameFood = itemView.findViewById(R.id.tvCategory);
            imgFood = itemView.findViewById(R.id.imgCategory);
//            Log.e(TAG, "PPPPP" + nameFood);
        }

    }
}
//public class FoodAdapters extends FirebaseRecyclerAdapter<Food, FoodAdapters.foodViewholder> {
//
//    public FoodAdapters(
//            @NonNull FirebaseRecyclerOptions<Food> options)
//    {
//        super(options);
//    }
//
//    @Override
//    protected void onBindViewHolder(@NonNull foodViewholder holder, int position, @NonNull Food food) {
//        holder.foodName.setText(food.getNameFood());
//        Glide.with(holder.imgFood.getContext())
//                .load(food.getImageFood())
//                .placeholder(R.drawable.ic_food_placeholder)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(holder.imgFood);
//        holder.imgFood.setOnClickListener(view -> {
//            AppCompatActivity activity=(AppCompatActivity)view.getContext();
//            activity.getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.frameLayout,
//                            new Detail_Food_Fragment(food.getNameFood(),food.getDescrible(),food.getIngredient(),food.getLinKVideo(),food.getImageFood())).addToBackStack(null).commit();
//        });
//    }
//
//    @NonNull
//    @Override
//    public foodViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
//    {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_category, parent, false);
//        return new FoodAdapters.foodViewholder(view);
//    }
//    class foodViewholder extends RecyclerView.ViewHolder {
//        TextView foodName;
//        ImageView imgFood;
//        public foodViewholder(@NonNull View itemView)
//        {
//            super(itemView);
//            foodName = itemView.findViewById(R.id.tvCategory);
//            imgFood = itemView.findViewById(R.id.imgCategory);
//        }
//
//    }
//}
//