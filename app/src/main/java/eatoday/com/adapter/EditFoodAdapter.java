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
import eatoday.com.ui.Detail_Food_Fragment;
import eatoday.com.ui.EditPostFragment;

public class EditFoodAdapter extends RecyclerView.Adapter<EditFoodAdapter.EditViewholder> {
    private List<Food> mlist;
    private IClickListener iClickListener;
    public interface IClickListener{
        void onClickDelete(Food food);

    }
    public EditFoodAdapter(List<Food> mlist, IClickListener listener) {

        this.mlist = mlist;
        this.iClickListener = listener;
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
        holder.imgBtnDelete.setOnClickListener(v -> iClickListener.onClickDelete(food));
        holder.imgEditFood.setOnClickListener(view -> {
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout,
                            new Detail_Food_Fragment(food.getFoodName(), food.getDescrible(), food.getIngredient(), food.getLinkVideo(), food.getFoodImage())).addToBackStack(null).commit();
        });
        holder.imgBtnEdit.setOnClickListener(view -> {
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout,
                            new EditPostFragment(food.getIdFood(),food.getFoodName(), food.getDescrible(), food.getIngredient(), food.getLinkVideo(), food.getFoodImage())).addToBackStack(null).commit();
        });

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
        private ImageButton imgBtnEdit;

        public EditViewholder(@NonNull View itemView) {
            super(itemView);
            nameEditFood = itemView.findViewById(R.id.tvFood);
            imgEditFood = itemView.findViewById(R.id.imgVFood);
            imgBtnDelete = itemView.findViewById(R.id.ic_delete);
            imgBtnEdit = itemView.findViewById(R.id.ic_edit);
        }
    }
}



