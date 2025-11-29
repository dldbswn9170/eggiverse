package com.eggiverse.app.games;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eggiverse.app.R;

import java.util.List;

public class GameOptionAdapter extends RecyclerView.Adapter<GameOptionAdapter.GameViewHolder> {

    public static class GameOption {
        public final String title;
        public final String description;
        public final int iconRes;
        public final Class<?> activityClass;

        public GameOption(String title, String description, int iconRes, Class<?> activityClass) {
            this.title = title;
            this.description = description;
            this.iconRes = iconRes;
            this.activityClass = activityClass;
        }
    }

    private final List<GameOption> options;

    public GameOptionAdapter(List<GameOption> options) {
        this.options = options;
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_option, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        holder.bind(options.get(position));
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    static class GameViewHolder extends RecyclerView.ViewHolder {
        private final ImageView icon;
        private final TextView title;
        private final TextView description;

        GameViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.gameIcon);
            title = itemView.findViewById(R.id.gameTitle);
            description = itemView.findViewById(R.id.gameDescription);
        }

        void bind(GameOption option) {
            icon.setImageResource(option.iconRes);
            title.setText(option.title);
            description.setText(option.description);
            itemView.setOnClickListener(v -> {
                Context context = itemView.getContext();
                context.startActivity(new Intent(context, option.activityClass));
            });
        }
    }
}

