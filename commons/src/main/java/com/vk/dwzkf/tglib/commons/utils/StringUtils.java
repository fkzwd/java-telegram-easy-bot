package com.vk.dwzkf.tglib.commons.utils;

import lombok.AllArgsConstructor;

/**
 * @author Roman Shageev
 * @since 12.11.2023
 */
public class StringUtils {

    @AllArgsConstructor
    public enum BlockLanguage {
        LOG("log"),
        CPP("cpp"),
        JAVA("java"),
        JAVASCRIPT("javascript"),
        SQL("sql");

        private final String language;
    }

    private static final BlockLanguage DEFAULT = BlockLanguage.JAVASCRIPT;

    public static final String BLOCK_START_FMT = "<pre><code class=\"language-%s\">";
    public static final String BLOCK_START = String.format(BLOCK_START_FMT, DEFAULT.language);
    public static final String BLOCK_END = "</code></pre>\n";

    public static String codeBlock(String data) {
        return BLOCK_START + data + BLOCK_END;
    }

    public static String codeBlock(String data, BlockLanguage blockLanguage) {
        blockLanguage = careNullLanguage(blockLanguage);
        return String.format(BLOCK_START_FMT, blockLanguage) + data + BLOCK_END;
    }

    public static BlockLanguage careNullLanguage(BlockLanguage blockLanguage) {
        return blockLanguage == null ? DEFAULT : blockLanguage;
    }

    public static String codeBlockStart(BlockLanguage blockLanguage) {
        blockLanguage = careNullLanguage(blockLanguage);
        return String.format(BLOCK_START_FMT, blockLanguage.language);
    }

    public static String codeBlockStart() {
        return codeBlockStart(DEFAULT);
    }

    public static String codeBlockEnd() {
        return BLOCK_END;
    }

    public static String printTable(String[][] table) {
        // Find out what the maximum number of columns is in any row
        int maxColumns = 0;
        for (int i = 0; i < table.length; i++) {
            maxColumns = Math.max(table[i].length, maxColumns);
        }

        // Find the maximum length of a string in each column
        int[] lengths = new int[maxColumns];
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[i].length; j++) {
                lengths[j] = Math.max(table[i][j].length(), lengths[j]);
            }
        }

        // Generate a format string for each column
        String[] formats = new String[lengths.length];
        for (int i = 0; i < lengths.length; i++) {
            formats[i] = "%1$" + lengths[i] + "s"
                    + (i + 1 == lengths.length ? "\n" : " ");
        }

        // Print 'em out
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[i].length; j++) {
                sb.append(String.format(formats[j], table[i][j]));
            }
        }
        return sb.toString();
    }
}
