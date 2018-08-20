package net.technolords.micro.util;

public class WhitespaceFilter {

    /**
     * Auiliary method to filter some white space (spaces and carriage return)
     *
     * @param original
     *  The original to be filtered.
     *
     * @return
     *  The filtered original.
     */
    public static String filter(String original) {
        return original.replaceAll(" |\n", "");
    }
}
