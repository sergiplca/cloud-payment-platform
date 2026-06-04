package com.sergiplca.payment_assistant_service.util;

import java.util.List;
import java.util.StringJoiner;

public class EmbeddingUtils {

    public static float[] toFloatArray(List<Double> vector) {

        float[] result = new float[vector.size()];

        for (int i = 0; i < vector.size(); i++) {
            result[i] = vector.get(i).floatValue();
        }

        return result;
    }

    public static String toVectorString(List<Double> vector) {

        StringJoiner joiner = new StringJoiner(", ", "[", "]");
        vector.forEach(v -> joiner.add(String.valueOf(v)));
        return joiner.toString();
    }
}
