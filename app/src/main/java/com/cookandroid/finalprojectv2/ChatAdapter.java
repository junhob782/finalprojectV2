package com.cookandroid.finalprojectv2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatMessage> chatMessages;

    public ChatAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = chatMessages.get(position);

        if (message.isUserMessage()) {
            // 사용자 메시지: 오른쪽 정렬
            holder.userMessage.setText(message.getMessage());
            holder.userMessage.setVisibility(View.VISIBLE);
            holder.gptMessage.setVisibility(View.GONE);
        } else {
            // GPT 메시지: 왼쪽 정렬
            holder.gptMessage.setText(message.getMessage());
            holder.gptMessage.setVisibility(View.VISIBLE);
            holder.userMessage.setVisibility(View.GONE);
        }
    }



    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView userMessage;
        TextView gptMessage;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            userMessage = itemView.findViewById(R.id.userMessage);
            gptMessage = itemView.findViewById(R.id.gptMessage);
        }
    }

}
