package io.github.qe7.utils.config;

import io.github.qe7.Hephaestus;

import java.io.File;
import java.nio.file.Files;

public final class FileUtil {

    private static final String FILE_EXTENSION = ".json";

    private static final String FILE_DIRECTORY = System.getProperty("user.home") + File.separator + Hephaestus.getInstance().getName() + File.separator;

    private FileUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void createDirectory() {
        File directory = new File(FILE_DIRECTORY);

        if (!directory.exists()) {
            try {
                if (directory.mkdirs()) {
                    System.out.println("Created directory");
                }
            } catch (Exception e) {
                System.out.println("Failed to create directory - " + e.getMessage());
            }
        } else {
            System.out.println("Directory already exists, skipping creation");
        }
    }

    public static void createFile(final String fileName) {
        File file = new File(FILE_DIRECTORY + fileName + FILE_EXTENSION);

        createDirectory();

        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    System.out.println("Created file: " + fileName);
                }
            } catch (Exception e) {
                System.out.println("Failed to create file: " + fileName + " - " + e.getMessage());
            }
        }
    }

    public static void deleteFile(final String fileName) {
        File file = new File(FILE_DIRECTORY + fileName + FILE_EXTENSION);

        if (file.exists()) {
            try {
                if (file.delete()) {
                    System.out.println("Deleted file: " + fileName);
                }
            } catch (Exception e) {
                System.out.println("Failed to delete file: " + fileName + " - " + e.getMessage());
            }
        }
    }

    public static String readFile(final String fileName) {
        File file = new File(FILE_DIRECTORY + fileName + FILE_EXTENSION);

        if (file.exists()) {
            try {
                return new String(Files.readAllBytes(file.toPath()));
            } catch (Exception e) {
                System.out.println("Failed to read file: " + fileName + " - " + e.getMessage());
            }
        }

        return null;
    }

    public static void writeFile(final String fileName, final String content) {
        File file = new File(FILE_DIRECTORY + fileName + FILE_EXTENSION);

        //debug
        System.out.println("Write file dir: " + FILE_DIRECTORY + fileName + FILE_EXTENSION);

        createDirectory();

        if (!file.exists()) {
            createFile(fileName);
        }

        try {
            Files.write(file.toPath(), content.getBytes());
        } catch (Exception e) {
            System.out.println("Failed to write to file: " + fileName + " - " + e.getMessage());
        }
    }
}
