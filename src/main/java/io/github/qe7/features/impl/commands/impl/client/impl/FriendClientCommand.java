package io.github.qe7.features.impl.commands.impl.client.impl;

import io.github.qe7.Hephaestus;
import io.github.qe7.features.impl.commands.impl.client.api.ClientCommand;
import io.github.qe7.relations.Relation;
import io.github.qe7.relations.enums.RelationType;
import io.github.qe7.utils.ChatUtil;

import java.util.List;
import java.util.stream.Collectors;

public class FriendClientCommand extends ClientCommand {

    public FriendClientCommand() {
        super("Friend", "Whitelists a player");

        this.setAliases(new String[] { "f", "friends", "whitelist" });

        this.setUsage("friend <add/del/list> <player>");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2 || args.length > 3) {
            ChatUtil.addPrefixedMessage("Friend", "Invalid arguments");
            ChatUtil.addPrefixedMessage("Friend", this.getUsage());
            return;
        }

        final String action = args[1];

        switch (action.toLowerCase()) {
            case "add": {
                if (Hephaestus.getInstance().getRelationManager().isFriend(args[2])) {
                    ChatUtil.addPrefixedMessage("Friend", "Already friends with " + args[2]);
                    return;
                }
                if (Hephaestus.getInstance().getRelationManager().isEnemy(args[2])) {
                    ChatUtil.addPrefixedMessage("Friend", "Already enemies with " + args[2]);
                    return;
                }
                Hephaestus.getInstance().getRelationManager().addRelation(args[2], RelationType.FRIEND);
                ChatUtil.addPrefixedMessage("Friend", "Added " + args[2] + " to friends");
                break;
            }
            case "remove":
            case "del": {
                if (!Hephaestus.getInstance().getRelationManager().isFriend(args[2])) {
                    ChatUtil.addPrefixedMessage("Friend", "Not friends with " + args[2]);
                    return;
                }
                Hephaestus.getInstance().getRelationManager().removeRelation(args[2]);
                ChatUtil.addPrefixedMessage("Friend", "Removed " + args[2] + " from friends");
                break;
            }
            case "list": {
                List<Relation> friends = Hephaestus.getInstance().getRelationManager().getRegistry().keySet().stream().filter(relation -> relation.getType() == RelationType.FRIEND).collect(Collectors.toList());

                if (friends.isEmpty()) {
                    ChatUtil.addPrefixedMessage("Friend", "No friends");
                    return;
                }

                String display = friends.stream().map(Relation::getName).reduce((a, b) -> a + ", " + b).orElse("");

                ChatUtil.addPrefixedMessage("Friend", "Friends: " + display);
                break;
            }
            default:
                ChatUtil.addPrefixedMessage("Friend", "Invalid action");
        }
    }
}
