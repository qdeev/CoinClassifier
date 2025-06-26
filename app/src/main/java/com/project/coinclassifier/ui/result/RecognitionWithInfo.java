package com.project.coinclassifier.ui.result;

import androidx.annotation.Nullable;
import com.project.coinclassifier.model.CoinInfo;
import com.project.coinclassifier.tflite.Recognition;

public class RecognitionWithInfo {
    
    public final Recognition recognition;
    @Nullable
    public final CoinInfo coinInfo;

    public RecognitionWithInfo(Recognition recognition, @Nullable CoinInfo coinInfo) {
        this.recognition = recognition;
        this.coinInfo = coinInfo;
    }

    public Recognition getRecognition() {
        return recognition;
    }

    public CoinInfo getCoinInfo() {
        return coinInfo;
    }
} 