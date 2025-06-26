package com.project.coinclassifier.ui.home;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.project.coinclassifier.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Uri imageUri;

    private final ActivityResultLauncher<Uri> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            success -> {
                if (success) {
                    navigateToResultFragment(imageUri);
                }
            });

    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    navigateToResultFragment(uri);
                }
            });

    private final ActivityResultLauncher<String> requestCameraPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    launchCamera();
                } else {
                    Toast.makeText(getContext(), "Camera permission is required to take photos", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<String> requestStoragePermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    pickImageLauncher.launch("image/*");
                } else {
                    Toast.makeText(getContext(), "Storage permission is required to select images", Toast.LENGTH_SHORT).show();
                }
            });


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        binding.buttonCaptureImage.setOnClickListener(v -> handleCameraPermission());
        binding.buttonLoadImage.setOnClickListener(v -> handleGalleryPermission());

        return binding.getRoot();
    }

    private void handleCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void handleGalleryPermission() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ?
                Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            pickImageLauncher.launch("image/*");
        } else {
            requestStoragePermissionLauncher.launch(permission);
        }
    }

    private void launchCamera() {
        imageUri = createImageUri(requireContext());
        if (imageUri != null) {
            takePictureLauncher.launch(imageUri);
        }
    }

    private Uri createImageUri(Context context) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Coin Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the CoinClassifier App");
        return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    private void navigateToResultFragment(Uri imageUri) {
        NavDirections action = HomeFragmentDirections.actionHomeToResult(imageUri.toString());
        NavHostFragment.findNavController(this).navigate(action);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 