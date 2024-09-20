package io.github.qe7.managers.impl;

import io.github.qe7.Hephaestus;
import io.github.qe7.events.packet.IncomingPacketEvent;
import io.github.qe7.utils.ChatUtil;
import io.github.qe7.utils.math.TimerUtil;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import me.zero.alpine.listener.Subscriber;
import net.minecraft.src.Packet3Chat;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PlayerManager implements Subscriber {

    // Join/leave pattern for detecting players entering or leaving
    private static final Pattern PLAYER_JOIN_LEAVE_PATTERN = Pattern.compile("(\\w+) (joined|left) the game\\.?");

    // Instead of using regex to match the entire line, we'll just look for "Players online:"
    private static final String PLAYER_LIST_IDENTIFIER = "Players online:";

    private final TimerUtil timerUtil = new TimerUtil();

    private final List<String> onlinePlayers = new ArrayList<>();

    private boolean test = false;
    private final StringBuilder builder = new StringBuilder();

    public void initialise() {
        Hephaestus.getInstance().getEventBus().subscribe(this);
    }

    @Subscribe
    public final Listener<IncomingPacketEvent> incomingPacketEvent = new Listener<>(event -> {
        if (event.getPacket() instanceof Packet3Chat) {
            String message = ((Packet3Chat) event.getPacket()).message;

            if (message == null) {
                return;
            }

            if (message.startsWith("<")) {
                return;
            }

            // Normalize the message (remove color codes like §e, §7) to make it easier to match
            final String normalisedMessage = message.replaceAll("§[0-9a-f]", "");

            // Check for player join/leave messages (WORKS)
            Matcher joinLeaveMatcher = PLAYER_JOIN_LEAVE_PATTERN.matcher(normalisedMessage);
            if (joinLeaveMatcher.matches()) {
                String playerName = joinLeaveMatcher.group(1);
                String action = joinLeaveMatcher.group(2);
                updatePlayerStatus(playerName, action);
                return;
            }

            // Detect the "Players online" list start hopefully
            if (normalisedMessage.contains(PLAYER_LIST_IDENTIFIER)) {
                test = true;
                builder.setLength(0); // Clear the builder for new list
                timerUtil.reset();
                return; // Wait for next packets to get player names
            }

            // Continue reading the player list if we're in the middle of it
            if (test) {
                // Attempting to detect end of player list, but this is not reliable LOL
                if ((message.contains("<") || message.contains("§"))) {
                    test = false;
                    updateOnlinePlayers(builder.toString());
                }

                builder.append(normalisedMessage);
                timerUtil.reset();
            }
        }

        if (test && timerUtil.hasTimeElapsed(100, false)) {
            test = false;
            updateOnlinePlayers(builder.toString());
        }
    });

    // Updates the list of online players based on the collected player names
    private void updateOnlinePlayers(String playerList) {
        String[] players = playerList.split(", ");
        for (String player : players) {
            if (!onlinePlayers.contains(player)) {
                onlinePlayers.add(player);
            }
        }
    }

    // Updates the player's join/leave status
    private void updatePlayerStatus(String playerName, String action) {
        if ("joined".equals(action)) {
            if (!onlinePlayers.contains(playerName)) {
                onlinePlayers.add(playerName);
            }
        } else if ("left".equals(action)) {
            onlinePlayers.remove(playerName);
        }
    }

    // Retrieves the list of online players
    public List<String> getOnlinePlayers() {
        return new ArrayList<>(onlinePlayers);
    }
}
