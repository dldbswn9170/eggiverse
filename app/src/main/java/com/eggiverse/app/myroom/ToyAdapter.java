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

public class ToyAdapter extends RecyclerView.Adapter<ToyAdapter.ToyViewHolder> {

    private final List<ShopItem> toyList;
    private final Set<String> selectedIds = new HashSet<>();

    public ToyAdapter(List<ShopItem> toyList) {
        this.toyList = new ArrayList<>(toyList);
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
    public ToyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_toy, parent, false);
        return new ToyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToyViewHolder holder, int position) {
        ShopItem item = toyList.get(position);
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
        return toyList.size();
    }

    static class ToyViewHolder extends RecyclerView.ViewHolder {
        private final ImageView icon;
        private final View checkContainer;

        ToyViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.toyIcon);
            checkContainer = itemView.findViewById(R.id.checkContainer);
        }

        void bind(ShopItem item, boolean isSelected, Runnable onClickListener) {
            checkContainer.setVisibility(isSelected ? View.VISIBLE : View.GONE);

            // 아이콘 설정
            int iconRes = getToyIcon(item.getId());
            icon.setImageResource(iconRes);

            itemView.setOnClickListener(v -> onClickListener.run());
        }

        private int getToyIcon(String itemId) {
            // TODO: Add toy icons
            return android.R.drawable.ic_menu_gallery;
        }
    }
}