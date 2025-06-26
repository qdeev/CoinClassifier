package com.project.coinclassifier.tflite;

public class Recognition {
    private final String title;
    private final float confidence;

    public Recognition(String title, float confidence) {
        this.title = title;
        this.confidence = confidence;
    }

    public String getTitle() {
        return title;
    }

    public float getConfidence() {
        return confidence;
    }
} 