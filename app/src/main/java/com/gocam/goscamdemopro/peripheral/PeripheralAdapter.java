package com.gocam.goscamdemopro.peripheral;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gocam.goscamdemopro.R;
import com.gos.platform.api.devparam.PeripheralElement;

import java.util.ArrayList;
import java.util.List;

public class PeripheralAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private PeripheralListener listener;

    private List<PeripheralElement> peripherals = new ArrayList<>();

    public PeripheralAdapter(PeripheralListener listener) {
        this.listener = listener;
    }

    public void setPeripherals(List<PeripheralElement> peripherals) {
        if (peripherals == null) {
            return;
        }
        this.peripherals.clear();
        this.peripherals.addAll(peripherals);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_peripheral, parent, false);
        return new PeripheralHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
       if (holder instanceof PeripheralHolder) {
            PeripheralHolder peripheralHolder = (PeripheralHolder) holder;
            PeripheralElement dataBen = peripherals.get(position);
            peripheralHolder.tvName.setText(holder.itemView.getContext().getString(R.string.string_reminder) + position);
            peripheralHolder.tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.itemClick(dataBen);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return peripherals.size();
    }

    interface PeripheralListener {
        void itemClick(PeripheralElement dataBen);
    }

    private class PeripheralHolder extends RecyclerView.ViewHolder {
        TextView tvName;

        public PeripheralHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.btn_add);
        }
    }
}
