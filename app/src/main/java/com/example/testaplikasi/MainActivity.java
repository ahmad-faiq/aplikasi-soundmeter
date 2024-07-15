package com.example.testaplikasi;

// Mengimpor pustaka
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    // Konstanta untuk izin merekam audio dan logging
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final String LOG_TAG = "SoundMeter";

    // Variabel untuk merekam audio
    private MediaRecorder mediaRecorder;
    private String outputFile;
    private boolean isRecording = false;

    // Elemen UI
    private TextView textViewNoiseLevel;
    private LineChart chart;
    private List<Entry> entries;
    private LineDataSet dataSet;
    private Timer timer;
    private Handler handler;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this); // Inisialisasi DBHelper

        textViewNoiseLevel = findViewById(R.id.textViewNoiseLevel);
        chart = findViewById(R.id.chart);

        Button btnStartStop = findViewById(R.id.btnStartStop);
        btnStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    startRecording(); // Mulai merekam
                    btnStartStop.setText("Stop");
                } else {
                    stopRecording(); // Hentikan merekam
                    btnStartStop.setText("Start");
                }
            }
        });

        Button btnShowHistory = findViewById(R.id.btnShowHistory);
        btnShowHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHistory(); // Tampilkan riwayat
            }
        });

        // Setup chart
        entries = new ArrayList<>();
        dataSet = new LineDataSet(entries, "Noise Level");
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate(); // Segarkan chart

        handler = new Handler(); // Inisialisasi handler
    }

    private void startRecording() {
        // Memeriksa izin untuk merekam audio
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
            return;
        }

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        outputFile = getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/recording.3gp";
        mediaRecorder.setOutputFile(outputFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;

            // Mulai timer untuk memperbarui chart
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    updateChart(); // Perbarui chart setiap 1 detik
                }
            }, 0, 1000); // Perbarui setiap 1 detik
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() gagal");
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;

            // Batalkan timer
            if (timer != null) {
                timer.cancel();
                timer = null;
            }

            // Simpan data rekaman ke database
            saveRecordToDatabase();
        }
    }

    private void updateChart() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                float amplitude = getNoiseLevel(); // Gunakan metode getNoiseLevel
                textViewNoiseLevel.setText("Noise Level: " + amplitude + " dB");

                // Tambahkan entri ke chart
                LineData data = chart.getData();
                if (data != null) {
                    LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);
                    if (set == null) {
                        set = createSet(); // Buat dataset baru
                        data.addDataSet(set);
                    }

                    data.addEntry(new Entry(set.getEntryCount(), amplitude), 0);
                    data.notifyDataChanged();
                    chart.notifyDataSetChanged();
                    chart.invalidate(); // Segarkan chart
                }
            }
        });
    }

    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Noise Level");
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setLineWidth(2f);
        set.setColor(getResources().getColor(R.color.design_default_color_primary)); // Perbarui warna
        return set;
    }

    private float getNoiseLevel() {
        // Gantilah dengan kode yang sesuai untuk mendapatkan level kebisingan
        return (float) (Math.random() * 100); // Contoh: Menghasilkan nilai acak untuk pengujian
    }

    private double getAmplitude() {
        if (mediaRecorder != null) {
            return 20 * Math.log10(mediaRecorder.getMaxAmplitude() / 2700.0);
        } else {
            return 0;
        }
    }

    private void saveRecordToDatabase() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String timestamp = sdf.format(new Date());
        float intensity = getNoiseLevel(); // Gunakan metode baru untuk intensitas

        Log.d(LOG_TAG, "Menyimpan Rekaman: Timestamp: " + timestamp + ", Intensitas: " + intensity); // Debug log

        long id = dbHelper.insertRecord(timestamp, intensity);
        if (id != -1) {
            Toast.makeText(this, "Rekaman disimpan ke database", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Gagal menyimpan rekaman", Toast.LENGTH_SHORT).show();
        }
    }

    private void showHistory() {
        Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
        startActivity(intent); // Mulai aktivitas History
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }
}
