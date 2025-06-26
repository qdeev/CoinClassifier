package com.project.coinclassifier.ui.result;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.project.coinclassifier.R;
import com.project.coinclassifier.databinding.FragmentResultBinding;
import com.project.coinclassifier.model.CoinInfo;
import com.project.coinclassifier.tflite.Recognition;
import android.widget.Toast;
import android.widget.TextView;
import androidx.navigation.Navigation;

import java.io.IOException;
import java.io.File;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

public class ResultFragment extends Fragment {

    private FragmentResultBinding binding;
    private ResultViewModel viewModel;
    private String imageUriString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ResultViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentResultBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            final ResultFragmentArgs args = ResultFragmentArgs.fromBundle(getArguments());
            imageUriString = args.getImageUri();
            viewModel.processImage(imageUriString);
        }

        viewModel.getResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                binding.resultImage.setImageURI(Uri.parse(imageUriString));
                updateUIWithResult(result);
            } else {
                Toast.makeText(getContext(), "Не удалось классифицировать монету.", Toast.LENGTH_SHORT).show();
            }
        });

        binding.addToCollectionButton.setOnClickListener(v -> {
            if (imageUriString != null) {
                viewModel.addToCollection(imageUriString);
                Toast.makeText(getContext(), "Монета добавлена в коллекцию", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(v).navigate(R.id.action_resultFragment_to_collectionFragment);
            }
        });
    }

    private void updateUIWithResult(RecognitionWithInfo result) {
        CoinInfo coinInfo = result.getCoinInfo();

        if (coinInfo != null) {
            binding.recognitionNameText.setText(coinInfo.getName());
        } else {
            // Fallback to ID if no info is found
            binding.recognitionNameText.setText(result.getRecognition().getTitle());
        }

        NumberFormat percentFormat = NumberFormat.getPercentInstance(new Locale("ru", "RU"));
        percentFormat.setMinimumFractionDigits(1);
        String confidenceString = getString(R.string.confidence_format, percentFormat.format(result.getRecognition().getConfidence()));
        binding.confidenceText.setText(confidenceString);

        // Details
        if (coinInfo != null) {
            binding.addToCollectionButton.setEnabled(true);
            binding.coinDetailsLayout.setVisibility(View.VISIBLE);
            setupDetailView(binding.nameAmountText, "Номинал", coinInfo.getNameAmount());
            setupDetailView(binding.nameCurrencyText, "Валюта", coinInfo.getNameCurrency());
            setupDetailView(binding.currencyText, "Символ валюты", coinInfo.getCurrency());
            setupDetailView(binding.currencyIdText, "ID валюты", coinInfo.getCurrencyId());
            setupDetailView(binding.countryText, "Страна", coinInfo.getCountry());
            setupDetailView(binding.countryIdText, "ID страны", coinInfo.getCountryId());
        } else {
            binding.addToCollectionButton.setEnabled(false);
            binding.coinDetailsLayout.setVisibility(View.GONE);
        }
    }

    private void setupDetailView(TextView textView, String label, String value) {
        if (value != null && !value.isEmpty()) {
            textView.setText(getString(R.string.detail_format, label, value));
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 