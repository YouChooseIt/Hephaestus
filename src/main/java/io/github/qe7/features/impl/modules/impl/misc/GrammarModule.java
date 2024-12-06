package io.github.qe7.features.impl.modules.impl.misc;

import io.github.qe7.events.packet.OutgoingPacketEvent;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.src.Packet3Chat;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GrammarModule extends Module {

    private final HashMap<String, String> abbreviations = new HashMap<>();

    public GrammarModule() {
        super("Grammar", "Grammar nazi", ModuleCategory.MISC);

        abbreviations.put("wassup", "what's up");
        abbreviations.put("sup", "what's up");
        abbreviations.put("tf", "the fuck");
        abbreviations.put("u", "you");
        abbreviations.put("ur", "your");
        abbreviations.put("ure", "you are");
        abbreviations.put("r", "are");
        abbreviations.put("y", "why");
        abbreviations.put("l8r", "later");
        abbreviations.put("cya", "see you");
        abbreviations.put("ttyl", "talk to you later");
        abbreviations.put("brb", "be right back");
        abbreviations.put("idk", "I don't know");
        abbreviations.put("lol", "laugh out loud");
        abbreviations.put("rofl", "rolling on the floor laughing");
        abbreviations.put("lmao", "laughing my ass off");
        abbreviations.put("omg", "oh my god");
        abbreviations.put("thx", "thanks");
        abbreviations.put("np", "no problem");
        abbreviations.put("jk", "just kidding");
        abbreviations.put("bbl", "be back later");
        abbreviations.put("afk", "away from keyboard");
        abbreviations.put("gtg", "got to go");
        abbreviations.put("imo", "in my opinion");
        abbreviations.put("smh", "shaking my head");
        abbreviations.put("fyi", "for your information");
        abbreviations.put("nvm", "never mind");
        abbreviations.put("tbh", "to be honest");
        abbreviations.put("ikr", "I know, right?");
        abbreviations.put("wyd", "what are you doing?");
        abbreviations.put("hmu", "hit me up");
        abbreviations.put("bff", "best friends forever");
        abbreviations.put("asap", "as soon as possible");
        abbreviations.put("irl", "in real life");
        abbreviations.put("dm", "direct message");
        abbreviations.put("tmi", "too much information");
        abbreviations.put("btw", "by the way");
        abbreviations.put("gr8", "great");
        abbreviations.put("plz", "please");
        abbreviations.put("pls", "please");
        abbreviations.put("wth", "what the heck");
        abbreviations.put("wtf", "what the fuck");
        abbreviations.put("xoxo", "hugs and kisses");
        abbreviations.put("glhf", "good luck, have fun");
        abbreviations.put("gg", "good game");
        abbreviations.put("wp", "well played");
        abbreviations.put("ggwp", "good game, well played");
        abbreviations.put("ez", "easy");
        abbreviations.put("afaik", "as far as I know");
        abbreviations.put("omw", "on my way");
        abbreviations.put("m8", "mate");
        abbreviations.put("GH", "GameHerobrine");
        abbreviations.put("gh", "GameHerobrine");
        abbreviations.put("Cliff", "Shliff");
        abbreviations.put("wsndow0", "skidder");
        abbreviations.put("color", "colour");
        abbreviations.put("colorful", "colourful");
        abbreviations.put("armor", "armour");
        abbreviations.put("favorite", "favourite");
        abbreviations.put("neighbor", "neighbour");
        abbreviations.put("Heshaestus", "Hephaestus");
        abbreviations.put("fabrick", "fabric");
        abbreviations.put("reliq", "relique");
        abbreviations.put("da", "the");
        abbreviations.put("wat", "what");
        abbreviations.put("wut", "what");
        abbreviations.put("fabric", "mcp");
        abbreviations.put("license", "licence");
        abbreviations.put("stfu", "shut the fuck up");
        abbreviations.put("kys", "keep yourself safe");
        abbreviations.put("woooow_0", "nebuladreamer4");
        abbreviations.put("nig", "nigga"); //N-Word pass by wsndow0
        abbreviations.put("fr", "for real");
        abbreviations.put("iirc", "if I recall correctly");
        abbreviations.put("wb", "welcome back");
        abbreviations.put("ong", "on god");
    }

    @Subscribe
    public final Listener<OutgoingPacketEvent> outgoingPacketEventListener = new Listener<>(event -> {
        if (event.getPacket() instanceof Packet3Chat) {
            Packet3Chat chatPacket = (Packet3Chat) event.getPacket();
            String message = chatPacket.message;

            for (String abbreviation : abbreviations.keySet()) {
                String regex = "\\b" + Pattern.quote(abbreviation) + "\\b";
                message = message.replaceAll(regex, Matcher.quoteReplacement(abbreviations.get(abbreviation)));
            }

            if (!message.endsWith(".") && !message.endsWith("?") && !message.endsWith("!") && !message.startsWith(".") && !message.startsWith("/") && !message.startsWith("!") && !message.startsWith("?")) {
                message += ".";
            }

            try {
                message = message.replaceFirst(message.charAt(0) + "", Character.toUpperCase(message.charAt(0)) + "");
            } catch(Exception e){
                //bals
            }
            //chat bugs osiris moment message.substring(0, 100); :fire: @shae
            chatPacket.message = message;
        }
    });
}
