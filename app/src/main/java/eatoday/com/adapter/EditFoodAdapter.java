package eatoday.com.adapter;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

public class EditFoodAdapter extends RecyclerView.Adapter<EditFoodAdapter.EditViewholder> {
    private List<Food> mlist;

    public EditFoodAdapter(List<Food> mlist) {
        this.mlist = mlist;
    }

    @NonNull
    @Override
    public EditFoodAdapter.EditViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_category, parent, false);
        return new EditFoodAdapter.EditViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EditFoodAdapter.EditViewholder holder, int position) {
        Food food = mlist.get(position);
        if (food == null) {
            return;
        }
        holder.nameEditFood.setText(food.getFoodName());
        Glide.with(holder.imgEditFood.getContext())
                .load(food.getFoodImage())
                .placeholder(R.drawable.ic_food_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imgEditFood);

    }

    @Override
    public int getItemCount() {
        if (mlist != null) {
            return mlist.size();
        }
        return 0;
    }

    public class EditViewholder extends RecyclerView.ViewHolder {
        private TextView nameEditFood;
        private ImageView imgEditFood;
        private ImageButton imgBtnDelete;

        public EditViewholder(@NonNull View itemView) {
            super(itemView);
            nameEditFood = itemView.findViewById(R.id.tvFood);
            imgEditFood = itemView.findViewById(R.id.imgVFood);
            imgBtnDelete = itemView.findViewById(R.id.ic_delete);

        }
    }
}

//    public FoodAdapters(
//            @NonNull FirebaseRecyclerOptions<Food> options)
//    {
//        super(options);
//    }
//
//    @Override
//    protected void onBindViewHolder(@NonNull foodViewholder holder, int position, @NonNull Food model) {
//        holder.nameFood.setText(model.getNameFood());
//        Glide.with(holder.imgFood.getContext())
//                .load(model.getImageFood())
//                .placeholder(R.drawable.ic_food_placeholder)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(holder.imgFood);
//    }
//
//    @NonNull
//    @Override
//    public foodViewholder
//    onCreateViewHolder(@NonNull ViewGroup parent,
//                       int viewType)
//    {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.list_item_category, parent, false);
//        return new FoodAdapters.foodViewholder(view);
//    }
//    class foodViewholder extends RecyclerView.ViewHolder {
//        TextView nameFood;
//        ImageView imgFood;
//        public foodViewholder(@NonNull View itemView)
//        {
//            super(itemView);
//            nameFood = itemView.findViewById(R.id.tvCategory);
//            imgFood = itemView.findViewById(R.id.imgCategory);
//        }
//
//    }
//}}
//public class EditFoodAdapter extends FirebaseRecyclerAdapter<Food, EditFoodAdapter.editViewholder> {
//
//    public EditFoodAdapter(
//            @NonNull FirebaseRecyclerOptions<Food> options)
//    {
//        super(options);
//    }
//
//    @Override
//    protected void onBindViewHolder(@NonNull editViewholder holder, int position, @NonNull Food food) {
//        holder.nameEditFood.setText(food.getNameFood());
//        Glide.with(holder.imgEditFood.getContext())
//                .load(food.getImageFood())
//                .placeholder(R.drawable.ic_food_placeholder)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(holder.imgEditFood);
//    }
//
//    @NonNull
//    @Override
//    public editViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
//    {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_category, parent, false);
//        return new EditFoodAdapter.editViewholder(view);
//    }
//    class editViewholder extends RecyclerView.ViewHolder {
//        TextView nameEditFood;
//        ImageView imgEditFood;
//        public editViewholder(@NonNull View itemView)
//        {
//            super(itemView);
//            nameEditFood = itemView.findViewById(R.id.tvFood);
//            imgEditFood = itemView.findViewById(R.id.imgVFood);
//
//        }
//
//    }

