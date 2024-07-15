package com.example.testaplikasi;

public class NoiseData {

    // Variabel untuk menyimpan data kebisingan
    private long id; // ID unik untuk setiap data kebisingan
    private String timestamp; // Waktu pengambilan data kebisingan
    private float intensity; // Intensitas kebisingan dalam desibel (dB)

    // Konstruktor untuk menginisialisasi objek NoiseData
    public NoiseData(long id, String timestamp, float intensity) {
        this.id = id;
        this.timestamp = timestamp;
        this.intensity = intensity;
    }

    // Metode untuk mendapatkan ID
    public long getId() {
        return id;
    }

    // Metode untuk mendapatkan timestamp
    public String getTimestamp() {
        return timestamp;
    }

    // Metode untuk mendapatkan intensitas kebisingan
    public float getIntensity() {
        return intensity;
    }
}
