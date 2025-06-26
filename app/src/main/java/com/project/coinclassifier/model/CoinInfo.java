package com.project.coinclassifier.model;

import com.google.gson.annotations.SerializedName;

public class CoinInfo {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("name_amount")
    private String nameAmount;

    @SerializedName("name_currency")
    private String nameCurrency;

    @SerializedName("amount")
    private String amount;

    @SerializedName("currency")
    private String currency;

    @SerializedName("currency_id")
    private String currencyId;

    @SerializedName("country")
    private String country;

    @SerializedName("country_id")
    private String countryId;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNameAmount() {
        return nameAmount;
    }

    public String getNameCurrency() {
        return nameCurrency;
    }

    public String getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public String getCountry() {
        if (country == null || country.isEmpty()) {
            return "";
        }
        return country.substring(0, 1).toUpperCase() + country.substring(1).toLowerCase();
    }

    public String getCountryId() {
        return countryId;
    }
} 