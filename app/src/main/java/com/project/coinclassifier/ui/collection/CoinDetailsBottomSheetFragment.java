package com.project.coinclassifier.ui.collection;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.project.coinclassifier.R;
import com.project.coinclassifier.data.db.CoinEntity;

import java.io.File;

public class CoinDetailsBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String ARG_COIN_ENTITY = "coin_entity";

    public static CoinDetailsBottomSheetFragment newInstance(CoinEntity coinEntity) {
        CoinDetailsBottomSheetFragment fragment = new CoinDetailsBottomSheetFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_COIN_ENTITY, coinEntity);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_coin_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView imageView = view.findViewById(R.id.bottom_sheet_image);
        TextView nameView = view.findViewById(R.id.bottom_sheet_name);
        TextView countryView = view.findViewById(R.id.bottom_sheet_country);
        TextView nameAmountView = view.findViewById(R.id.bottom_sheet_name_amount);
        TextView currencyView = view.findViewById(R.id.bottom_sheet_currency);
        TextView idsView = view.findViewById(R.id.bottom_sheet_ids);

        if (getArguments() != null) {
            CoinEntity coin = getArguments().getParcelable(ARG_COIN_ENTITY);
            if (coin != null) {
                // Main info
                nameView.setText(coin.title);
                Glide.with(this)
                        .load(new File(coin.imagePath))
                        .into(imageView);

                // Optional info
                setTextOrHide(countryView, "Country", coin.country);
                setTextOrHide(currencyView, "Currency", coin.currency);

                String nameAmountText = String.format("%s %s", coin.nameAmount, coin.nameCurrency);
                setTextOrHide(nameAmountView, "Nominal", nameAmountText);

                String idsText = String.format("IDs: %s/%s", coin.countryId, coin.currencyId);
                setTextOrHide(idsView, null, idsText);
            }
        }
    }

    private void setTextOrHide(TextView textView, @Nullable String label, @Nullable String value) {
        if (value != null && !value.isEmpty()) {
            if (label != null) {
                textView.setText(String.format("%s: %s", label, value));
            } else {
                textView.setText(value);
            }
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }
} 