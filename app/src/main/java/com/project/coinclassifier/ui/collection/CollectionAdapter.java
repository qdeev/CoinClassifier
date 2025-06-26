package com.project.coinclassifier.ui.collection;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.project.coinclassifier.R;
import com.project.coinclassifier.data.db.CoinEntity;

import java.io.File;
import java.util.Locale;
import java.util.Objects;

public class CollectionAdapter extends ListAdapter<CoinEntity, CollectionAdapter.CoinViewHolder> {

    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(CoinEntity coin);
    }

    public CollectionAdapter(OnItemClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public CoinViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_collection_coin, parent, false);
        return new CoinViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CoinViewHolder holder, int position) {
        CoinEntity coin = getItem(position);
        holder.bind(coin, listener);
    }

    public CoinEntity getCoinAt(int position) {
        return getItem(position);
    }

    static class CoinViewHolder extends RecyclerView.ViewHolder {
        private final TextView coinTitle;
        private final ImageView coinImage;

        public CoinViewHolder(@NonNull View itemView) {
            super(itemView);
            coinTitle = itemView.findViewById(R.id.text_view_coin_title);
            coinImage = itemView.findViewById(R.id.image_view_coin);
        }

        public void bind(final CoinEntity coin, final OnItemClickListener listener) {
            coinTitle.setText(String.format(Locale.getDefault(), "%s - %s", coin.title, coin.country));
            Glide.with(itemView.getContext())
                    .load(new File(coin.imagePath))
                    .into(coinImage);
            itemView.setOnClickListener(v -> listener.onItemClick(coin));
        }
    }

    private static final DiffUtil.ItemCallback<CoinEntity> DIFF_CALLBACK = new DiffUtil.ItemCallback<CoinEntity>() {
        @Override
        public boolean areItemsTheSame(@NonNull CoinEntity oldItem, @NonNull CoinEntity newItem) {
            return oldItem.uid == newItem.uid;
        }

        @Override
        public boolean areContentsTheSame(@NonNull CoinEntity oldItem, @NonNull CoinEntity newItem) {
            return oldItem.title.equals(newItem.title) &&
                    oldItem.country.equals(newItem.country) &&
                    oldItem.imagePath.equals(newItem.imagePath) &&
                    Objects.equals(oldItem.nameAmount, newItem.nameAmount) &&
                    Objects.equals(oldItem.nameCurrency, newItem.nameCurrency) &&
                    Objects.equals(oldItem.currency, newItem.currency) &&
                    Objects.equals(oldItem.amount, newItem.amount) &&
                    Objects.equals(oldItem.currencyId, newItem.currencyId) &&
                    Objects.equals(oldItem.countryId, newItem.countryId);
        }
    };
}
 