package com.eggiverse.app.myroom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eggiverse.app.R;
import com.eggiverse.app.data.ShopItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FurnitureAdapter extends RecyclerView.Adapter<FurnitureAdapter.FurnitureViewHolder> {

    private final List<ShopItem> furnitureList;
    private final Set<String> selectedIds = new HashSet<>();

    public FurnitureAdapter(List<ShopItem> furnitureList) {
        this.furnitureList = new ArrayList<>(furnitureList);
    }

    public void setSelectedItems(Set<String> selected) {
        selectedIds.clear();
        selectedIds.addAll(selected);
        notifyDataSetChanged();
    }

    public Set<String> getSelectedIds() {
        return new HashSet<>(selectedIds);
    }

    @NonNull
    @Override
    public FurnitureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_furniture, parent, false);
        return new FurnitureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FurnitureViewHolder holder, int position) {
        ShopItem item = furnitureList.get(position);
        boolean isSelected = selectedIds.contains(item.getId());
        holder.bind(item, isSelected, () -> {
            if (selectedIds.contains(item.getId())) {
                selectedIds.remove(item.getId());
            } else {
                selectedIds.add(item.getId());
            }
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return furnitureList.size();
    }

    static class FurnitureViewHolder extends RecyclerView.ViewHolder {
        private final ImageView icon;
        private final View checkContainer;

        FurnitureViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.furnitureIcon);
            checkContainer = itemView.findViewById(R.id.checkContainer);
        }

        void bind(ShopItem item, boolean isSelected, Runnable onClickListener) {
            checkContainer.setVisibility(isSelected ? View.VISIBLE : View.GONE);

            // 아이콘 설정
            int iconRes = getFurnitureIcon(item.getId());
            icon.setImageResource(iconRes);

            itemView.setOnClickListener(v -> onClickListener.run());
        }

        private int getFurnitureIcon(String itemId) {
            switch (itemId) {
                case "deco_cloud_bed": return R.drawable.room_cloudbed;
                case "deco_soft_bed": return R.drawable.room_bed;
                case "deco_aurora_lamp": return R.drawable.room_auroralight;
                case "deco_flower_point": return R.drawable.room_flowerpoint;
                case "deco_mini_bookshelf": return R.drawable.room_minibook;
                case "deco_glass_bookshelf": return R.drawable.room_glassbook;
                case "deco_egg_frame": return R.drawable.room_picture;
                case "deco_mini_table": return R.drawable.room_minitable;
                case "deco_star_rug": return R.drawable.room_rug;
                case "deco_moon_light": return R.drawable.room_moonlight;
                case "deco_earth_mobile": return R.drawable.room_earth;
                case "deco_silk_curtain": return R.drawable.room_silkcurtain;
                default: return android.R.drawable.ic_menu_gallery;
            }
        }
    }
}