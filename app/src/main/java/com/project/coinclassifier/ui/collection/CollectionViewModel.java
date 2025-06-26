package com.project.coinclassifier.ui.collection;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.project.coinclassifier.data.CoinRepository;
import com.project.coinclassifier.data.db.CoinEntity;
import java.util.List;

public class CollectionViewModel extends AndroidViewModel {

    private final CoinRepository repository;
    private final LiveData<List<CoinEntity>> allCoins;

    public CollectionViewModel(@NonNull Application application) {
        super(application);
        repository = CoinRepository.getInstance(application.getApplicationContext());
        allCoins = repository.getAllCoinsFromCollection();
    }

    public LiveData<List<CoinEntity>> getAllCoins() {
        return allCoins;
    }

    public void deleteCoin(CoinEntity coin) {
        repository.deleteCoinFromCollection(coin);
    }
} 