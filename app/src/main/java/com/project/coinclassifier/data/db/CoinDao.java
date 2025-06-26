package com.project.coinclassifier.data.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import kotlinx.coroutines.flow.Flow;
import java.util.List;
import androidx.lifecycle.LiveData;

@Dao
public interface CoinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CoinEntity coin);

    @Query("SELECT * FROM coin_collection ORDER BY title ASC")
    LiveData<List<CoinEntity>> getAllCoins();

    @Query("DELETE FROM coin_collection WHERE uid = :coinUid")
    void deleteByUid(int coinUid);

} 