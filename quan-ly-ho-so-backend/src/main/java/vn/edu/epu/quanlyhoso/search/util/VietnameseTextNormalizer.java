package vn.edu.epu.quanlyhoso.search.util;

import java.text.Normalizer;
import java.util.Locale;

public final class VietnameseTextNormalizer {

    private VietnameseTextNormalizer() {
    }

    public static String normalize(String text) {
        if (text == null) {
            return "";
        }
        // Normalize Vietnamese text for accent-insensitive matching in both indexing and querying.
        String normalized = text.replace('\u0111', 'd').replace('\u0110', 'd');
        normalized = Normalizer.normalize(normalized, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        return normalized.toLowerCase(Locale.ROOT)
                .trim()
                .replaceAll("\\s+", " ");
    }
}
