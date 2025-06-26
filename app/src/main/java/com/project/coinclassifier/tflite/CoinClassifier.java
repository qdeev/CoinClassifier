package com.project.coinclassifier.tflite;

import android.content.Context;
import android.graphics.Bitmap;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CoinClassifier {

    private static final String MODEL_FILE = "coin_classification.tflite";
    private static final String LABELS_FILE = "coin_labels.txt";

    private final Interpreter interpreter;
    private final List<String> labels;
    private final int imageWidth;
    private final int imageHeight;
    private final ImageProcessor imageProcessor;

    public CoinClassifier(Context context) throws IOException {
        MappedByteBuffer tfliteModel = FileUtil.loadMappedFile(context, MODEL_FILE);
        Interpreter.Options tfliteOptions = new Interpreter.Options();
        tfliteOptions.setNumThreads(4);
        this.interpreter = new Interpreter(tfliteModel, tfliteOptions);

        this.labels = FileUtil.loadLabels(context, LABELS_FILE);

        // Define input size based on the model's training, not metadata
        this.imageHeight = 224;
        this.imageWidth = 224;

        this.imageProcessor = new ImageProcessor.Builder()
                .add(new ResizeOp(imageHeight, imageWidth, ResizeOp.ResizeMethod.BILINEAR))
                .add(new NormalizeOp(127.5f, 127.5f))
                .build();
    }

    public List<Recognition> classify(Bitmap bitmap) {
        TensorImage inputImage = new TensorImage(interpreter.getInputTensor(0).dataType());
        inputImage.load(bitmap);
        inputImage = imageProcessor.process(inputImage);

        int[] probabilityShape = interpreter.getOutputTensor(0).shape();
        DataType probabilityDataType = interpreter.getOutputTensor(0).dataType();
        TensorBuffer outputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType);

        interpreter.run(inputImage.getBuffer(), outputProbabilityBuffer.getBuffer());

        Map<String, Float> labeledProbability = new TensorLabel(labels, outputProbabilityBuffer).getMapWithFloatValue();

        List<Recognition> recognitions = new ArrayList<>();
        for (Map.Entry<String, Float> entry : labeledProbability.entrySet()) {
            recognitions.add(new Recognition(entry.getKey(), entry.getValue()));
        }

        Collections.sort(recognitions, (o1, o2) -> Float.compare(o2.getConfidence(), o1.getConfidence()));

        if (recognitions.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.singletonList(recognitions.get(0));
    }

    public void close() {
        if (interpreter != null) {
            interpreter.close();
        }
    }
} 