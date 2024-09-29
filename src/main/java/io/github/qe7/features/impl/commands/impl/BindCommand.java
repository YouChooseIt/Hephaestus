package io.github.qe7.features.impl.commands.impl;

import io.github.qe7.Hephaestus;
import io.github.qe7.features.impl.commands.api.Command;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.utils.ChatUtil;
import org.lwjgl.input.Keyboard;

public class BindCommand extends Command {

    public BindCommand() {
        super("Bind", "Bind a Module to a keybind");

        this.setUsage("Bind <Module> <Key/\"None\">");
    }

    @Override
    public void execute(String[] args) {
        if (args.length != 3) {
            ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Invalid arguments");
            ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Usage: " + this.getUsage());
        }

        Module targetModule = null;

        for (Module module : Hephaestus.getInstance().getModuleManager().getRegistry().values()) {
            if (module.getName().equalsIgnoreCase(args[1])) {
                targetModule = module;
                break;
            }
        }

        if (targetModule != null) {
            int key = Keyboard.getKeyIndex(args[2].toUpperCase());

            if (args[2].equalsIgnoreCase("none")) {
                targetModule.setKeyBind(Keyboard.KEY_NONE);
                ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Unbound " + targetModule.getName());
                return;
            }

            if (key != Keyboard.KEY_NONE) {
                targetModule.setKeyBind(key);

                ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Bound " + targetModule.getName() + " to " + args[2].toUpperCase());
            } else {
                ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Invalid key bind");
            }
        } else {
            ChatUtil.addPrefixedMessage(this.getClass().getSimpleName(), "Module not found");
        }
    }
}
