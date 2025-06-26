package com.project.coinclassifier.ui.result;

import android.app.Application;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.project.coinclassifier.data.CoinRepository;
import com.project.coinclassifier.data.db.CoinEntity;
import com.project.coinclassifier.model.CoinInfo;
import com.project.coinclassifier.tflite.CoinClassifier;
import com.project.coinclassifier.tflite.Recognition;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResultViewModel extends AndroidViewModel {

    private final MutableLiveData<RecognitionWithInfo> _recognitionResult = new MutableLiveData<>();
    public final LiveData<RecognitionWithInfo> recognitionResult = _recognitionResult;

    private final MutableLiveData<Boolean> _saveStatus = new MutableLiveData<>();
    public final LiveData<Boolean> saveStatus = _saveStatus;

    private final CoinRepository coinRepository;
    private CoinClassifier coinClassifier;
    private final ExecutorService executorService;

    public ResultViewModel(@NonNull Application application) {
        super(application);
        executorService = Executors.newSingleThreadExecutor();
        coinRepository = CoinRepository.getInstance(application.getApplicationContext());

        executorService.execute(() -> {
            try {
                coinClassifier = new CoinClassifier(application.getApplicationContext());
            } catch (IOException e) {
                e.printStackTrace();
                _recognitionResult.postValue(new RecognitionWithInfo(null, null));
            }
        });
    }

    public void classifyCoin(Bitmap bitmap) {
        executorService.execute(() -> {
            if (coinClassifier != null) {
                final List<Recognition> results = coinClassifier.classify(bitmap);
                if (results != null && !results.isEmpty()) {
                    Recognition topResult = results.get(0);
                    coinRepository.getCoinInfo(topResult.getTitle(), new CoinRepository.DataCallback<CoinInfo>() {
                        @Override
                        public void onComplete(CoinInfo coinInfo) {
                            _recognitionResult.postValue(new RecognitionWithInfo(topResult, coinInfo));
                        }

                        @Override
                        public void onError(Exception e) {
                            _recognitionResult.postValue(new RecognitionWithInfo(topResult, null));
                        }
                    });
                } else {
                     _recognitionResult.postValue(new RecognitionWithInfo(null, null));
                }
            }
        });
    }

    public void addToCollection(String imageUriString) {
        RecognitionWithInfo currentResult = recognitionResult.getValue();
        if (currentResult != null && currentResult.getCoinInfo() != null) {
            Uri imageUri = Uri.parse(imageUriString);
            coinRepository.insertCoinIntoCollection(currentResult.getCoinInfo(), imageUri);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
        if (coinClassifier != null) {
            coinClassifier.close();
        }
    }

    public LiveData<RecognitionWithInfo> getResult() {
        return recognitionResult;
    }

    public void processImage(String imageUriString) {
        executorService.execute(() -> {
            try {
                Uri imageUri = Uri.parse(imageUriString);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), imageUri);
                classifyCoin(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                _recognitionResult.postValue(null);
            }
        });
    }
}
