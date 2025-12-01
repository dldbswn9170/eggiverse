package com.eggiverse.app.games;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eggiverse.app.R;

import java.util.List;

public class MemoryCardAdapter extends RecyclerView.Adapter<MemoryCardAdapter.MemoryViewHolder> {

    public interface OnCardClickListener {
        void onCardClick(int position);
    }

    private final List<CardState> cards;
    private final OnCardClickListener listener;
    private final int[] icons = {
            android.R.drawable.btn_star,
            android.R.drawable.btn_star_big_off,
            android.R.drawable.ic_menu_mylocation,
            android.R.drawable.ic_menu_compass,
            android.R.drawable.ic_menu_week,
            android.R.drawable.ic_menu_camera
    };

    public static class CardState {
        public int value;
        public boolean flipped;
        public boolean matched;
    }

    public MemoryCardAdapter(List<CardState> cards, OnCardClickListener listener) {
        this.cards = cards;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MemoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_memory_card, parent, false);
        return new MemoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemoryViewHolder holder, int position) {
        CardState card = cards.get(position);
        holder.bind(card, icons, listener, position);
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    static class MemoryViewHolder extends RecyclerView.ViewHolder {
        private final ImageView icon;

        MemoryViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.cardIcon);
        }

        void bind(CardState card, int[] icons, OnCardClickListener listener, int position) {
            if (card.flipped || card.matched) {
                icon.setImageResource(icons[card.value % icons.length]);
                icon.setBackgroundResource(R.drawable.bg_owned_badge);
            } else {
                icon.setImageResource(android.R.drawable.ic_menu_help);
                icon.setBackgroundColor(0x33000000);
            }

            itemView.setAlpha(card.matched ? 0.6f : 1f);
            itemView.setOnClickListener(v -> listener.onCardClick(position));
        }
    }
}

