package com.example.testaplikasi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private Context context; // Konteks dari aktivitas yang memanggil adapter ini
    private List<NoiseData> noiseDataList; // Daftar data kebisingan
    private DBHelper dbHelper; // DBHelper untuk mengelola database

    // Konstruktor untuk menginisialisasi adapter
    public HistoryAdapter(Context context, List<NoiseData> noiseDataList) {
        this.context = context;
        this.noiseDataList = noiseDataList;
        this.dbHelper = new DBHelper(context);
    }

    // Metode untuk membuat ViewHolder baru dan menginisialisasi tampilan yang diinginkan
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_noise_data, parent, false);
        return new ViewHolder(view);
    }

    // Metode untuk mengikat data ke tampilan ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NoiseData noiseData = noiseDataList.get(position);
        holder.textViewTimestamp.setText("Timestamp: " + noiseData.getTimestamp());
        holder.textViewIntensity.setText("Intensity: " + noiseData.getIntensity());

        // Menambahkan aksi untuk tombol hapus
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Panggil metode untuk menghapus record dari database
                dbHelper.deleteRecord(noiseData.getId());
                // Hapus item dari daftar dan refresh tampilan
                noiseDataList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, noiseDataList.size());
                Toast.makeText(context, "Record deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Metode untuk mendapatkan jumlah item dalam daftar
    @Override
    public int getItemCount() {
        return noiseDataList.size();
    }

    // Kelas ViewHolder untuk mengelola tampilan item dalam RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTimestamp; // Teks untuk menampilkan timestamp
        TextView textViewIntensity; // Teks untuk menampilkan intensitas kebisingan
        Button btnDelete; // Tombol untuk menghapus record

        // Konstruktor ViewHolder untuk menginisialisasi tampilan item
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTimestamp = itemView.findViewById(R.id.textViewTimestamp);
            textViewIntensity = itemView.findViewById(R.id.textViewIntensity);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
