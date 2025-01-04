package io.github.qe7.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Packet14BlockDig;
import net.minecraft.src.Packet15Place;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public final class PlayerUtil {

    public PlayerUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void destroyBlock(int x, int y, int z, int side) {
        Minecraft.getMinecraft().playerController.clickBlock(x, y, z, side);
        Minecraft.getMinecraft().playerController.sendBlockRemoving(x, y, z, side);
        Minecraft.getMinecraft().playerController.field_1064_b = Minecraft.getMinecraft().playerController.isBeingUsed();
        Minecraft.getMinecraft().playerController.isBeingUsed();
    }

    public static void placeBlockUnsafe(int x, int y, int z, int side) {
        Minecraft.getMinecraft().playerController.sendPlaceBlock(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().theWorld, Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem(), x, y, z, side);
        Minecraft.getMinecraft().playerController.isBeingUsed();
    }

    public static void placeBlock(int x, int y, int z, int side) {
        if (Minecraft.getMinecraft().isMultiplayerWorld()) {
            Minecraft.getMinecraft().getSendQueue().addToSendQueue(new Packet15Place(x, y, z, side, Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem()));
            return;
        }
        PlayerUtil.placeBlockUnsafe(x, y, z, side);
    }

    public static void destroyBlockInstant(int x, int y, int z, int side) {
        if (Minecraft.getMinecraft().isMultiplayerWorld()) {
            Minecraft.getMinecraft().getSendQueue().addToSendQueue(new Packet14BlockDig(0, x, y, z, side));
            Minecraft.getMinecraft().getSendQueue().addToSendQueue(new Packet14BlockDig(2, x, y, z, side));
            return;
        }
        Minecraft.getMinecraft().playerController.sendBlockRemoved(x, y, z, side);
    }

    public static String getCapeFromGithub(String fileUrl, String username) {
        try {
            // Create a URL object with the GitHub raw file URL
            URL url = new URL(fileUrl);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set request method to GET
            connection.setRequestMethod("GET");

            // Check if the response code is 200 (OK)
            if (connection.getResponseCode() == 200) {
                // Create a BufferedReader to read the input stream
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                // Read lines one by one
                String line;
                while ((line = reader.readLine()) != null) {
                    // Split the line into username and cape using ":"
                    String[] parts = line.split(":");

                    // Ensure the line is formatted correctly
                    if (parts.length == 2) {
                        String name = parts[0].trim();
                        String cape = parts[1].trim();

                        // Check if the username matches
                        if (name.equalsIgnoreCase(username)) {
                            reader.close();
                            return "https://raw.githubusercontent.com/qe7/Hephaestus-Assets/refs/heads/main/capes/" + cape;
                        }
                    }
                }

                // Close the reader
                reader.close();
            } else {
                System.out.println("Failed to fetch file. Response code: " + connection.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Return null if the username is not found
        return null;
    }
}