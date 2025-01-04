package io.github.qe7.features.impl.commands.impl.client.impl;

import io.github.qe7.Hephaestus;
import io.github.qe7.features.impl.commands.impl.client.api.ClientCommand;
import io.github.qe7.relations.Relation;
import io.github.qe7.relations.enums.RelationType;
import io.github.qe7.utils.ChatUtil;

import java.util.List;
import java.util.stream.Collectors;

public final class EnemyClientCommand extends ClientCommand {

    public EnemyClientCommand() {
        super("Enemy", "Targets a player");

        this.setAliases(new String[] { "target", "enemies", "e" });

        this.setUsage("enemy <add/del/list> <player>");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2 || args.length > 3) {
            ChatUtil.addPrefixedMessage("Enemy", "Invalid arguments");
            ChatUtil.addPrefixedMessage("Enemy", this.getUsage());
            return;
        }

        final String action = args[1];

        switch (action.toLowerCase()) {
            case "add": {
                if (Hephaestus.getInstance().getRelationManager().isFriend(args[2])) {
                    ChatUtil.addPrefixedMessage("Enemy", "Already friends with " + args[2]);
                    return;
                }
                if (Hephaestus.getInstance().getRelationManager().isEnemy(args[2])) {
                    ChatUtil.addPrefixedMessage("Enemy", "Already enemies with " + args[2]);
                    return;
                }
                Hephaestus.getInstance().getRelationManager().addRelation(args[2], RelationType.ENEMY);
                ChatUtil.addPrefixedMessage("Enemy", "Added " + args[2] + " to enemies");
                break;
            }
            case "remove":
            case "del": {
                if (!Hephaestus.getInstance().getRelationManager().isEnemy(args[2])) {
                    ChatUtil.addPrefixedMessage("Enemy", "Not enemies with " + args[2]);
                    return;
                }
                Hephaestus.getInstance().getRelationManager().removeRelation(args[2]);
                ChatUtil.addPrefixedMessage("Enemy", "Removed " + args[2] + " from enemies");
                break;
            }
            case "list": {
                List<Relation> enemies = Hephaestus.getInstance().getRelationManager().getRegistry().keySet().stream().filter(relation -> relation.getType() == RelationType.ENEMY).collect(Collectors.toList());

                if (enemies.isEmpty()) {
                    ChatUtil.addPrefixedMessage("Enemy", "No enemies");
                    return;
                }

                String display = enemies.stream().map(Relation::getName).reduce((a, b) -> a + ", " + b).orElse("");

                ChatUtil.addPrefixedMessage("Enemy", "Enemies: " + display);
                break;
            }
            default:
                ChatUtil.addPrefixedMessage("Enemy", "Invalid action");
        }
    }
}
