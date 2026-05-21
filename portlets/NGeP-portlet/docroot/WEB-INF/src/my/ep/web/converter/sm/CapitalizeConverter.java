package my.ep.web.converter.sm;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * JSF Converter that capitalizes the first letter of every word in a string.
 * Fully Java 7 compatible and aligned with SonarLint/SonarQube best practices.
 */
@FacesConverter("capitalizeConverter")
public class CapitalizeConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        // No transformation needed when sending value back to the model.
        return value;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }

        String text = value.toString().trim();
        if (text.isEmpty()) {
            return "";
        }

        return capitalizeWords(text);
    }

    /**
     * Capitalizes the first letter of each word in the input text.
     * This method avoids high cyclomatic complexity and deeply nested logic.
     *
     * @param text The text to capitalize
     * @return Capitalized text
     */
    private String capitalizeWords(String text) {

        // Avoid unnecessary object creation
        String[] words = text.split("\\s+");
        StringBuilder result = new StringBuilder(text.length());

        for (int i = 0; i < words.length; i++) {
            String word = words[i];

            if (!word.isEmpty()) {
                result.append(capitalizeWord(word));
            }

            // Append a space if this is not the last word
            if (i < words.length - 1) {
                result.append(' ');
            }
        }

        return result.toString();
    }

    /**
     * Capitalizes a single word (first character upper-case, rest lower-case).
     *
     * @param word The input word
     * @return A capitalized version of the word
     */
    private String capitalizeWord(String word) {
        if (word.length() == 1) {
            return word.toUpperCase();
        }

        char firstChar = word.charAt(0);
        String first = String.valueOf(Character.toUpperCase(firstChar));
        String rest = word.substring(1).toLowerCase();

        return first + rest;
    }
}
