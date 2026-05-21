package com.phimhay.juanng.common.utils;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class SlugHelper {
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]+");
    private static final Pattern DIACRITICS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    public static String toSlug(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }
        
        // Remove accents / diacritics
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String plain = DIACRITICS.matcher(normalized).replaceAll("");
        
        // Custom replacement for Vietnamese characters that are not fully decomposed by NFD
        plain = plain.replace("đ", "d")
                     .replace("Đ", "d")
                     .replace("o\u0302", "o") // ô
                     .replace("a\u0302", "a") // â
                     .replace("e\u0302", "e") // ê
                     .replace("u\u031b", "u") // ư
                     .replace("o\u031b", "o") // ơ
                     .replace("a\u0306", "a"); // ă
        
        // Convert to lowercase and replace whitespaces with hyphens
        String slug = WHITESPACE.matcher(plain.trim().toLowerCase(Locale.ENGLISH)).replaceAll("-");
        
        // Remove any remaining special characters
        slug = NONLATIN.matcher(slug).replaceAll("");
        
        // Remove consecutive hyphens
        while (slug.contains("--")) {
            slug = slug.replace("--", "-");
        }
        
        // Remove leading or trailing hyphens
        if (slug.startsWith("-")) {
            slug = slug.substring(1);
        }
        if (slug.endsWith("-")) {
            slug = slug.substring(0, slug.length() - 1);
        }
        
        return slug;
    }
}
