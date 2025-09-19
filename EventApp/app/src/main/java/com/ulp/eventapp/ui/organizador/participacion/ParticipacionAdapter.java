package com.ulp.eventapp.ui.organizador.participacion;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ulp.eventapp.R;
import com.ulp.eventapp.model.Participation;

import java.util.ArrayList;
import java.util.List;

public class ParticipacionAdapter extends RecyclerView.Adapter<ParticipacionAdapter.ViewHolderVH> {

    private List<Participation> listaP;
    private final LayoutInflater li;
    private final OnParticipationToggleListener listener;
    private final boolean switchesHabilitados; // 👈 flag global

    public interface OnParticipationToggleListener {
        void onToggle(Participation participation, boolean isChecked);
    }

    public ParticipacionAdapter(List<Participation> listaP, LayoutInflater li,
                                OnParticipationToggleListener listener, boolean switchesHabilitados) {
        this.listaP = listaP != null ? listaP : new ArrayList<>();
        this.li = li;
        this.listener = listener;
        this.switchesHabilitados = switchesHabilitados;
    }

    @NonNull
    @Override
    public ViewHolderVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = li.inflate(R.layout.item_participacion, parent, false);
        return new ViewHolderVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderVH holder, int position) {
        Participation participation = listaP.get(position);

        if (participation.getUser() != null) {
            holder.tvNameUser.setText(participation.getUser().getUsername());
            holder.tvEmailUser.setText(participation.getUser().getEmail());
            holder.avatar.setImageResource(R.drawable.avatar);
        } else {
            holder.tvNameUser.setText("—");
            holder.tvEmailUser.setText("—");
        }

        holder.tvStatusUser.setText(participation.isConfirmed() ? "Confirmado" : "Sin confirmar");

        holder.switchConfirm.setOnCheckedChangeListener(null);
        holder.switchConfirm.setChecked(participation.isConfirmed());

        if (!switchesHabilitados) {
            holder.switchConfirm.setEnabled(false);
            holder.tvStatusUser.setText("Confirmación no disponible hasta que se realice el evento");
            holder.tvStatusUser.setTextColor(Color.GRAY);
        } else {
            holder.switchConfirm.setEnabled(true);
            holder.tvStatusUser.setTextColor(Color.parseColor("#E91E63"));
            holder.switchConfirm.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onToggle(participation, isChecked);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return (listaP != null) ? listaP.size() : 0;
    }

    public static class ViewHolderVH extends RecyclerView.ViewHolder {
        TextView tvNameUser, tvEmailUser, tvStatusUser;
        ImageView avatar;
        Switch switchConfirm;

        public ViewHolderVH(@NonNull View itemView) {
            super(itemView);
            tvNameUser = itemView.findViewById(R.id.tvNameUser);
            tvEmailUser = itemView.findViewById(R.id.tvEmailUser);
            avatar = itemView.findViewById(R.id.ivAvatar);
            tvStatusUser = itemView.findViewById(R.id.tvStatusUser);
            switchConfirm = itemView.findViewById(R.id.switchConfirm);
        }
    }
}

