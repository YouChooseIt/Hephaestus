package io.github.qe7.ui.click.components.propertyComponents;

import io.github.qe7.features.impl.modules.api.Module;
import net.minecraft.src.FontRenderer;

import java.util.ArrayList;
import java.util.List;

public class DescriptionComponent extends AbstractPropertyComponent {

    private final Module module;

    private int x, y;

    public DescriptionComponent(int width, int height, Module module) {
        super(width, height);
        this.module = module;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks, int x, int y) {
        this.x = x;
        this.y = y;

        for (String line : splitStringsIntoArrays(module.getDescription(), width - 7, fontRenderer)) {
            fontRenderer.drawStringWithShadow(line, x + 7, y + 3, -1);
            y += 10;
        }

        this.height = y - this.y + 3;
    }

    public static List<String> splitStringsIntoArrays(String string, int panelWidth, FontRenderer fontRenderer) {
        List<String> result = new ArrayList<>();

        if (string == null || string.isEmpty()) {
            return result; // Return an empty list for null or empty strings
        }

        String[] lines = string.split("\\n"); // Split the string by explicit newlines

        for (String line : lines) {
            String[] words = line.split(" "); // Split each line into words
            StringBuilder currentLine = new StringBuilder();

            for (String word : words) {
                String testLine;

                // Add the word to the current line and test its width
                if (currentLine.length() == 0) {
                    testLine = word;
                } else {
                    testLine = currentLine + " " + word;
                }

                if (fontRenderer.getStringWidth(testLine) > panelWidth) {
                    // If adding this word exceeds the width, finalize the current line
                    if (currentLine.length() > 0) {
                        result.add(currentLine.toString());
                        currentLine = new StringBuilder(word); // Start a new line with the current word
                    } else {
                        // If a single word is too long, split the word itself
                        for (int i = 0; i < word.length(); i++) {
                            int endIndex = i + 1;
                            while (endIndex <= word.length() && fontRenderer.getStringWidth(word.substring(i, endIndex)) <= panelWidth) {
                                endIndex++;
                            }
                            result.add(word.substring(i, endIndex - 1));
                            i = endIndex - 2; // Adjust the index to account for the processed part
                        }
                    }
                } else {
                    // Otherwise, keep building the current line
                    if (currentLine.length() > 0) {
                        currentLine.append(" ");
                    }
                    currentLine.append(word);
                }
            }

            // Add the last line of the current segment if it's not empty
            if (currentLine.length() > 0) {
                result.add(currentLine.toString());
            }
        }

        return result;
    }
}
