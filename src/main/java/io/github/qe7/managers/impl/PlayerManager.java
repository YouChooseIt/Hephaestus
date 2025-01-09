package io.github.qe7.managers.impl;

import io.github.qe7.Hephaestus;
import io.github.qe7.events.packet.IncomingPacketEvent;
import io.github.qe7.utils.ChatUtil;
import io.github.qe7.utils.math.TimerUtil;
import lombok.Getter;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import me.zero.alpine.listener.Subscriber;
import net.minecraft.src.Packet3Chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PlayerManager implements Subscriber {

    private static final Pattern PLAYER_JOIN_LEAVE_PATTERN = Pattern.compile("(\\w+) (joined|left) the game\\.?");

    private static final String PLAYER_LIST_IDENTIFIER = "Players online:";

    private final TimerUtil timerUtil = new TimerUtil();

    @Getter
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

            final String normalisedMessage = message.replaceAll("§[0-9a-f]", "");

            Matcher joinLeaveMatcher = PLAYER_JOIN_LEAVE_PATTERN.matcher(normalisedMessage);
            if (joinLeaveMatcher.matches()) {
                String playerName = joinLeaveMatcher.group(1);
                String action = joinLeaveMatcher.group(2);
                updatePlayerStatus(playerName, action);
                return;
            }

            if (normalisedMessage.contains(PLAYER_LIST_IDENTIFIER)) {
                test = true;
                builder.setLength(0);
                timerUtil.reset();
                return;
            }

            if (test) {
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

    private void updateOnlinePlayers(String playerList) {
        String[] players = playerList.split(", ");
        System.out.println(Arrays.toString(players));
        for (String player : players) {
            if (!onlinePlayers.contains(player)) {
                onlinePlayers.add(player);
            }
        }
    }

    private void updatePlayerStatus(String playerName, String action) {
        if ("joined".equals(action)) {
            if (!onlinePlayers.contains(playerName)) {
                onlinePlayers.add(playerName);
            }
        } else if ("left".equals(action)) {
            onlinePlayers.remove(playerName);
        }
    }
}
