package com.eggiverse.app.shop;

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

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(ShopItem item);
    }

    private List<ShopItem> items;
    private final OnItemClickListener listener;
    private Set<String> ownedItems = new HashSet<>();

    public ShopAdapter(List<ShopItem> items, OnItemClickListener listener) {
        this.items = new ArrayList<>(items);
        this.listener = listener;
    }

    public void setOwnedItems(Set<String> owned) {
        this.ownedItems = new HashSet<>(owned);
        notifyDataSetChanged();
    }

    public void updateItems(List<ShopItem> newItems) {
        this.items = new ArrayList<>(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop, parent, false);
        return new ShopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopViewHolder holder, int position) {
        ShopItem item = items.get(position);
        holder.bind(item, ownedItems.contains(item.getId()), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ShopViewHolder extends RecyclerView.ViewHolder {
        private final ImageView icon;
        private final TextView title;
        private final TextView desc;
        private final TextView price;
        private final TextView badge;

        ShopViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.itemIcon);
            title = itemView.findViewById(R.id.itemTitle);
            desc = itemView.findViewById(R.id.itemDesc);
            price = itemView.findViewById(R.id.itemPrice);
            badge = itemView.findViewById(R.id.itemBadge);
        }

        void bind(ShopItem item, boolean owned, OnItemClickListener listener) {
            title.setText(item.getName());
            desc.setText(item.getDescription());
            price.setText(String.valueOf(item.getPrice()));
            badge.setVisibility(owned ? View.VISIBLE : View.GONE);

            // 아이템 ID에 따라 개별 이미지 설정
            int iconRes = getIconResource(item.getId());
            icon.setImageResource(iconRes);

            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }

        private int getIconResource(String itemId) {
            switch (itemId) {
                // === 펫 먹이 ===
                case "food_star_berry":
                    return R.drawable.food_starfruit;
                case "food_dream_powder":
                    return R.drawable.food_dreamsugar;
                case "food_galaxy_jelly":
                    return R.drawable.food_jelly;
                case "food_moon_cake":
                    return R.drawable.food_bunnycookie;

                // === 장난감 (임시 파일명 1~5) ===
                case "toy_star_rattle":
                    return R.drawable.toy_starbell;
                case "toy_soft_star":
                    return R.drawable.toy_startoy;
                case "toy_glow_ball":
                    return R.drawable.toy_ball;
                case "toy_jumping_jelly":
                    return R.drawable.toy_jelly;

                // === 마이룸 가구 (임시 파일명 6~16) ===
                case "deco_cloud_bed":
                    return R.drawable.room_cloudbed;
                case "deco_soft_bed":
                    return R.drawable.room_bed;
                case "deco_aurora_lamp":
                    return R.drawable.room_auroralight;
                case "deco_flower_point":
                    return R.drawable.room_flowerpoint;
                case "deco_mini_bookshelf":
                    return R.drawable.room_minibook;
                case "deco_glass_bookshelf":
                    return R.drawable.room_glassbook;
                case "deco_egg_frame":
                    return R.drawable.room_picture;
                case "deco_mini_table":
                    return R.drawable.room_minitable;
                case "deco_star_rug":
                    return R.drawable.room_rug;
                case "deco_moon_light":
                    return R.drawable.room_moonlight;
                case "deco_earth_mobile":
                    return R.drawable.room_earth;
                case "deco_silk_curtain":
                    return R.drawable.room_silkcurtain;

                default:
                    return android.R.drawable.ic_menu_manage;
            }
        }
    }
}