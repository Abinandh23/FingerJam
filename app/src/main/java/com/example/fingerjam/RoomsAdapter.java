package com.example.fingerjam;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fingerjam.models.GameRoom;
import java.util.List;

public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.RoomViewHolder> {
    
    private List<GameRoom> rooms;
    private OnRoomClickListener listener;
    
    public interface OnRoomClickListener {
        void onRoomClick(GameRoom room);
    }
    
    public RoomsAdapter(List<GameRoom> rooms, OnRoomClickListener listener) {
        this.rooms = rooms;
        this.listener = listener;
    }
    
    @Override
    public RoomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(RoomViewHolder holder, int position) {
        GameRoom room = rooms.get(position);
        holder.bind(room);
    }
    
    @Override
    public int getItemCount() {
        return rooms.size();
    }
    
    public void updateRooms(List<GameRoom> newRooms) {
        this.rooms.clear();
        this.rooms.addAll(newRooms);
        notifyDataSetChanged();
    }
    
    class RoomViewHolder extends RecyclerView.ViewHolder {
        private TextView roomNameText;
        private TextView gameTypeText;
        private TextView playersText;
        private TextView statusText;
        
        public RoomViewHolder(View itemView) {
            super(itemView);
            roomNameText = itemView.findViewById(R.id.roomNameText);
            gameTypeText = itemView.findViewById(R.id.gameTypeText);
            playersText = itemView.findViewById(R.id.playersText);
            statusText = itemView.findViewById(R.id.statusText);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onRoomClick(rooms.get(position));
                }
            });
        }
        
        public void bind(GameRoom room) {
            roomNameText.setText(room.getRoomName());
            gameTypeText.setText("Type: " + room.getGameType());
            playersText.setText("Players: " + room.getPlayers().size() + "/" + room.getMaxPlayers());
            statusText.setText("Status: " + room.getStatus());
            
            // Set different colors based on room status
            switch (room.getStatus()) {
                case "waiting":
                    statusText.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
                    break;
                case "playing":
                    statusText.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_blue_dark));
                    break;
                case "finished":
                    statusText.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
                    break;
                default:
                    statusText.setTextColor(itemView.getContext().getResources().getColor(android.R.color.darker_gray));
                    break;
            }
        }
    }
} 