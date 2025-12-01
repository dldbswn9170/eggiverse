package com.eggiverse.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.eggiverse.app.R;
import com.eggiverse.app.data.ShopItem;

import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    private final Context context;
    private final List<InventoryItemData> itemList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(InventoryItemData itemData);
    }

    public static class InventoryItemData {
        public final ShopItem item;
        public int quantity;

        public InventoryItemData(ShopItem item, int quantity) {
            this.item = item;
            this.quantity = quantity;
        }
    }

    public InventoryAdapter(Context context, List<InventoryItemData> itemList, OnItemClickListener listener) {
        this.context = context;
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_inventory, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        InventoryItemData itemData = itemList.get(position);
        holder.bind(itemData, listener);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class InventoryViewHolder extends RecyclerView.ViewHolder {
        private final ImageView itemIcon;
        private final TextView itemName;
        private final TextView itemQuantity;

        InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            itemIcon = itemView.findViewById(R.id.itemIcon);
            itemName = itemView.findViewById(R.id.itemName);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
        }

        void bind(final InventoryItemData itemData, final OnItemClickListener listener) {
            itemName.setText(itemData.item.getName());
            itemQuantity.setText("x" + itemData.quantity);
            
            // TODO: Set item icon based on item ID
            // itemIcon.setImageResource(getIconRes(itemData.item.getId()));

            itemView.setOnClickListener(v -> listener.onItemClick(itemData));
        }
    }
}
