package com.eggiverse.app.myroom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eggiverse.app.R;
import com.eggiverse.app.data.ShopItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private final List<ShopItem> foodList;
    private final Set<String> selectedIds = new HashSet<>();

    public FoodAdapter(List<ShopItem> foodList) {
        this.foodList = new ArrayList<>(foodList);
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
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        ShopItem item = foodList.get(position);
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
        return foodList.size();
    }

    static class FoodViewHolder extends RecyclerView.ViewHolder {
        private final ImageView icon;
        private final View checkContainer;

        FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.foodIcon);
            checkContainer = itemView.findViewById(R.id.checkContainer);
        }

        void bind(ShopItem item, boolean isSelected, Runnable onClickListener) {
            checkContainer.setVisibility(isSelected ? View.VISIBLE : View.GONE);

            // 아이콘 설정
            int iconRes = getFoodIcon(item.getId());
            icon.setImageResource(iconRes);

            itemView.setOnClickListener(v -> onClickListener.run());
        }

        private int getFoodIcon(String itemId) {
            // TODO: Add food icons
            return android.R.drawable.ic_menu_gallery;
        }
    }
}