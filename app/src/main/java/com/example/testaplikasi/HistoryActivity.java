package com.example.testaplikasi;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView; // RecyclerView untuk menampilkan data kebisingan
    private HistoryAdapter adapter; // Adapter untuk mengelola data dalam RecyclerView
    private List<NoiseData> noiseDataList; // Daftar data kebisingan
    private DBHelper dbHelper; // DBHelper untuk mengelola database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        dbHelper = new DBHelper(this); // Inisialisasi DBHelper

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Atur layout manager untuk RecyclerView

        // Ambil data dari database
        noiseDataList = dbHelper.getAllRecords();
        adapter = new HistoryAdapter(this, noiseDataList); // Inisialisasi adapter dengan data yang diambil
        recyclerView.setAdapter(adapter); // Set adapter untuk RecyclerView
    }

}
