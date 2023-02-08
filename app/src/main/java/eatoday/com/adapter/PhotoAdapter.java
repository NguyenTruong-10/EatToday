package eatoday.com.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import eatoday.com.R;
import eatoday.com.model.Photo;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>{
    private final List<Photo> photo;
    public PhotoAdapter(List<Photo> photo) {
        this.photo = photo;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo,parent,false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
         Photo photo1 = photo.get(position);
         if(photo1 == null){
             return;
         }
         holder.imageView.setImageResource(photo1.getIdPhoto());
    }

    @Override
    public int getItemCount() {
        if(photo != null){
            return photo.size();
        }
        return 0;
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder{
        private final ImageView imageView;
         public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_photo);
        }
    }
}
