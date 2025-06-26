package com.project.coinclassifier.data.db;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "coin_collection")
public class CoinEntity implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "coin_id")
    public String coinId;

    @ColumnInfo(name = "title")
    public String title;
    
    @ColumnInfo(name = "image_path")
    public String imagePath;
    
    @ColumnInfo(name = "country")
    public String country;

    @ColumnInfo(name = "name_amount")
    public String nameAmount;

    @ColumnInfo(name = "name_currency")
    public String nameCurrency;

    @ColumnInfo(name = "currency")
    public String currency;

    @ColumnInfo(name = "amount")
    public String amount;

    @ColumnInfo(name = "currency_id")
    public String currencyId;

    @ColumnInfo(name = "country_id")
    public String countryId;

    public CoinEntity(String coinId, String title, String imagePath, String country,
                      String nameAmount, String nameCurrency, String currency, String amount,
                      String currencyId, String countryId) {
        this.coinId = coinId;
        this.title = title;
        this.imagePath = imagePath;
        this.country = country;
        this.nameAmount = nameAmount;
        this.nameCurrency = nameCurrency;
        this.currency = currency;
        this.amount = amount;
        this.currencyId = currencyId;
        this.countryId = countryId;
    }

    protected CoinEntity(Parcel in) {
        uid = in.readInt();
        coinId = in.readString();
        title = in.readString();
        imagePath = in.readString();
        country = in.readString();
        nameAmount = in.readString();
        nameCurrency = in.readString();
        currency = in.readString();
        amount = in.readString();
        currencyId = in.readString();
        countryId = in.readString();
    }

    public static final Creator<CoinEntity> CREATOR = new Creator<CoinEntity>() {
        @Override
        public CoinEntity createFromParcel(Parcel in) {
            return new CoinEntity(in);
        }

        @Override
        public CoinEntity[] newArray(int size) {
            return new CoinEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(uid);
        dest.writeString(coinId);
        dest.writeString(title);
        dest.writeString(imagePath);
        dest.writeString(country);
        dest.writeString(nameAmount);
        dest.writeString(nameCurrency);
        dest.writeString(currency);
        dest.writeString(amount);
        dest.writeString(currencyId);
        dest.writeString(countryId);
    }
} 