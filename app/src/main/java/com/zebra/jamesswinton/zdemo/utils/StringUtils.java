package com.zebra.jamesswinton.zdemo.utils;

public class StringUtils {
  public static String toTitleCase(String string) {

    // Check if String is null
    if (string == null) {
      return null;
    }

    if (string.equals("NFC")) {
      return "NFC";
    }

    if (string.contains("_")) {
      string = string.replace("_", " ");
    }

    boolean whiteSpace = true;

    StringBuilder builder = new StringBuilder(string); // String builder to store string
    final int builderLength = builder.length();

    // Loop through builder
    for (int i = 0; i < builderLength; ++i) {

      char c = builder.charAt(i); // Get character at builders position

      if (whiteSpace) {

        // Check if character is not white space
        if (!Character.isWhitespace(c)) {

          // Convert to title case and leave whitespace mode.
          builder.setCharAt(i, Character.toTitleCase(c));
          whiteSpace = false;
        }
      } else if (Character.isWhitespace(c)) {

        whiteSpace = true; // Set character is white space

      } else {

        builder.setCharAt(i, Character.toLowerCase(c)); // Set character to lowercase
      }
    }

    return builder.toString(); // Return builders text
  }
}
