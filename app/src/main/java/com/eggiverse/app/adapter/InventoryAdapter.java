package com.eggiverse.app.adapter;

import android.content.Context;
import android.graphics.Color;
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

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {

    private Context context;
    private List<InventoryItemData> items;
    private OnItemClickListener listener;

    // 인벤토리 아이템 데이터 클래스 (아이템 + 수량)
    public static class InventoryItemData {
        public ShopItem item;
        public int quantity;

        public InventoryItemData(ShopItem item, int quantity) {
            this.item = item;
            this.quantity = quantity;
        }
    }

    // 클릭 리스너 인터페이스
    public interface OnItemClickListener {
        void onItemClick(InventoryItemData itemData);
    }

    public InventoryAdapter(Context context, List<InventoryItemData> items, OnItemClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_inventory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InventoryItemData itemData = items.get(position);
        ShopItem item = itemData.item;

        // 아이템 이름
        holder.itemName.setText(item.getName());

        // 아이템 설명 (ShopData의 description에서 가져옴)
        holder.itemDescription.setText(item.getDescription());

        // 아이템 아이콘 설정
        holder.itemIcon.setImageResource(getIconResource(item.getId()));

        // 효과 표시
        if (item.getType() == ShopItem.ItemType.FOOD) {
            holder.itemEffect.setText("포만감 +" + item.getEffectValue());
            holder.itemEffect.setTextColor(context.getColor(R.color.amber_accent));
            holder.itemEffect.setBackgroundColor(Color.parseColor("#20FFA726"));
        } else if (item.getType() == ShopItem.ItemType.TOY) {
            holder.itemEffect.setText("행복감 +" + item.getEffectValue());
            holder.itemEffect.setTextColor(context.getColor(R.color.pink_accent));
            holder.itemEffect.setBackgroundColor(Color.parseColor("#20E91E63"));
        }

        // 수량 표시
        holder.itemQuantity.setText("x" + itemData.quantity);

        // 클릭 이벤트
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(itemData);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // 아이템 ID에 따라 drawable 리소스 반환
    private int getIconResource(String itemId) {
        switch (itemId) {
            // 먹이
            case "food_star_berry":
                return R.drawable.food_starfruit;
            case "food_dream_powder":
                return R.drawable.food_starfruit; // 실제 drawable로 변경
            case "food_galaxy_jelly":
                return R.drawable.food_starfruit; // 실제 drawable로 변경
            case "food_moon_cake":
                return R.drawable.food_starfruit; // 실제 drawable로 변경

            // 장난감
            case "toy_star_rattle":
                return R.drawable.food_starfruit; // 실제 drawable로 변경
            case "toy_soft_star":
                return R.drawable.food_starfruit; // 실제 drawable로 변경
            case "toy_glow_ball":
                return R.drawable.food_starfruit; // 실제 drawable로 변경
            case "toy_jumping_jelly":
                return R.drawable.food_starfruit; // 실제 drawable로 변경

            default:
                return R.drawable.food_starfruit; // 기본 아이콘
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemIcon;
        TextView itemName;
        TextView itemDescription;
        TextView itemEffect;
        TextView itemQuantity;

        ViewHolder(View itemView) {
            super(itemView);
            // item_inventory.xml의 ID와 연결
            itemIcon = itemView.findViewById(R.id.itemIcon);
            itemName = itemView.findViewById(R.id.itemName);
            itemDescription = itemView.findViewById(R.id.itemDescription);
            itemEffect = itemView.findViewById(R.id.itemEffect);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
        }
    }
}