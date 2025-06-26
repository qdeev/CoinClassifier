package com.project.coinclassifier.data;

import android.content.Context;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.project.coinclassifier.LabelHelper;
import com.project.coinclassifier.model.CoinInfo;
import com.project.coinclassifier.data.db.AppDatabase;
import com.project.coinclassifier.data.db.CoinDao;
import com.project.coinclassifier.data.db.CoinEntity;
import androidx.lifecycle.LiveData;
import kotlinx.coroutines.flow.Flow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.UUID;

public class CoinRepository {

    private static volatile CoinRepository instance;
    private final Context context;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private Map<String, CoinInfo> coinInfoMap;
    private final CoinDao coinDao;

    private CoinRepository(Context context) {
        this.context = context.getApplicationContext();
        AppDatabase db = AppDatabase.getDatabase(context);
        this.coinDao = db.coinDao();
        loadCoinData();
    }

    public static CoinRepository getInstance(Context context) {
        if (instance == null) {
            synchronized (CoinRepository.class) {
                if (instance == null) {
                    instance = new CoinRepository(context);
                }
            }
        }
        return instance;
    }

    private void loadCoinData() {
        executor.execute(() -> {
            String jsonFileString = LabelHelper.loadJSONFromAsset(context);
            if (jsonFileString != null) {
                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, CoinInfo>>() {}.getType();
                coinInfoMap = gson.fromJson(jsonFileString, type);
            }
        });
    }

    public LiveData<List<CoinEntity>> getAllCoinsFromCollection() {
        return coinDao.getAllCoins();
    }

    public void deleteCoinFromCollection(CoinEntity coin) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            coinDao.deleteByUid(coin.uid);
        });
    }

    public void insertCoinIntoCollection(CoinInfo coinInfo, Uri imageUri) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            // 1: Save the image to internal storage
            String imagePath = saveImageToInternalStorage(imageUri);
            if (imagePath == null) {
                return;
            }

            // 2: Create a CoinEntity with the constructor
            CoinEntity coinEntity = new CoinEntity(
                    coinInfo.getId(),
                    coinInfo.getName(),
                    imagePath,
                    coinInfo.getCountry(),
                    coinInfo.getNameAmount(),
                    coinInfo.getNameCurrency(),
                    coinInfo.getCurrency(),
                    coinInfo.getAmount(),
                    coinInfo.getCurrencyId(),
                    coinInfo.getCountryId()
            );

            // 3: Insert into database
            coinDao.insert(coinEntity);
        });
    }

    private String saveImageToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            File directory = context.getDir("collection_images", Context.MODE_PRIVATE);
            File file = new File(directory, UUID.randomUUID().toString() + ".jpg");

            try (OutputStream outputStream = new FileOutputStream(file)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, len);
                }
            } finally {
                inputStream.close();
            }
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void getCoinInfo(String coinId, final DataCallback<CoinInfo> callback) {
        executor.execute(() -> {
            if (coinInfoMap != null && coinInfoMap.containsKey(coinId)) {
                callback.onComplete(coinInfoMap.get(coinId));
            } else {
                callback.onError(new Exception("Coin not found"));
            }
        });
    }

    public interface DataCallback<T> {
        void onComplete(T result);
        void onError(Exception e);
    }
} 