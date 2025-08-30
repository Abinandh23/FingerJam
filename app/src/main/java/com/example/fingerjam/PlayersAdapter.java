package com.example.fingerjam;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fingerjam.models.Player;
import java.util.List;

public class PlayersAdapter extends RecyclerView.Adapter<PlayersAdapter.PlayerViewHolder> {
    
    private List<Player> players;
    
    public PlayersAdapter(List<Player> players) {
        this.players = players;
    }
    
    @Override
    public PlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_player, parent, false);
        return new PlayerViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(PlayerViewHolder holder, int position) {
        Player player = players.get(position);
        holder.bind(player);
    }
    
    @Override
    public int getItemCount() {
        return players.size();
    }
    
    class PlayerViewHolder extends RecyclerView.ViewHolder {
        private TextView playerNameText;
        private TextView playerStatusText;
        private TextView playerScoreText;
        private View hostIndicator;
        
        public PlayerViewHolder(View itemView) {
            super(itemView);
            playerNameText = itemView.findViewById(R.id.playerNameText);
            playerStatusText = itemView.findViewById(R.id.playerStatusText);
            playerScoreText = itemView.findViewById(R.id.playerScoreText);
            hostIndicator = itemView.findViewById(R.id.hostIndicator);
        }
        
        public void bind(Player player) {
            playerNameText.setText(player.getName());
            
            // Show host indicator
            if (player.isHost()) {
                hostIndicator.setVisibility(View.VISIBLE);
                playerNameText.setText(player.getName() + " 👑");
            } else {
                hostIndicator.setVisibility(View.GONE);
                playerNameText.setText(player.getName());
            }
            
            // Show ready status
            if (player.isReady()) {
                playerStatusText.setText("Ready ✓");
                playerStatusText.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
            } else {
                playerStatusText.setText("Not Ready");
                playerStatusText.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
            }
            
            // Show score if game is in progress
            if ("playing".equals(player.getStatus()) || "finished".equals(player.getStatus())) {
                playerScoreText.setVisibility(View.VISIBLE);
                playerScoreText.setText("Score: " + player.getScore());
            } else {
                playerScoreText.setVisibility(View.GONE);
            }
        }
    }
} 