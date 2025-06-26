package com.project.coinclassifier.ui.collection;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.FlowLiveDataConversions;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.project.coinclassifier.data.db.CoinEntity;
import com.project.coinclassifier.databinding.FragmentCollectionBinding;

public class CollectionFragment extends Fragment {

    private FragmentCollectionBinding binding;
    private CollectionViewModel collectionViewModel;
    private CollectionAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        collectionViewModel =
                new ViewModelProvider(this).get(CollectionViewModel.class);

        binding = FragmentCollectionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupRecyclerView();
        observeViewModel();
        setupSwipeToDelete();

        return root;
    }

    private void setupRecyclerView() {
        binding.recyclerViewCollection.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CollectionAdapter(coin -> {
            CoinDetailsBottomSheetFragment bottomSheet = CoinDetailsBottomSheetFragment.newInstance(coin);
            bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());
        });
        binding.recyclerViewCollection.setAdapter(adapter);
    }

    private void observeViewModel() {
        collectionViewModel.getAllCoins().observe(getViewLifecycleOwner(), coins -> {
            if (coins != null && !coins.isEmpty()) {
                adapter.submitList(coins);
                binding.textViewEmptyCollection.setVisibility(View.GONE);
                binding.recyclerViewCollection.setVisibility(View.VISIBLE);
            } else {
                binding.textViewEmptyCollection.setVisibility(View.VISIBLE);
                binding.recyclerViewCollection.setVisibility(View.GONE);
            }
        });
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                CoinEntity coinToDelete = adapter.getCoinAt(position);
                collectionViewModel.deleteCoin(coinToDelete);
            }
        });

        itemTouchHelper.attachToRecyclerView(binding.recyclerViewCollection);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
