package io.github.qe7.features.impl.modules.impl.misc;

import io.github.qe7.events.UpdateEvent;
import io.github.qe7.events.packet.IncomingPacketEvent;
import io.github.qe7.events.packet.OutgoingPacketEvent;
import io.github.qe7.features.impl.modules.api.Module;
import io.github.qe7.features.impl.modules.api.ModuleCategory;
import io.github.qe7.utils.ChatUtil;
import io.github.qe7.utils.math.TimerUtil;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.src.MapInfo;
import net.minecraft.src.Packet1Login;
import net.minecraft.src.Packet3Chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SchizoBotModule extends Module {

    private final TimerUtil timer = new TimerUtil();

    private final Random random = new Random();

    private final String[] messages = {
            "They're watching me...",
            "Did you hear that? It's them again.",
            "The walls have ears. Be careful.",
            "You can't trust anyone here.",
            "Something's not right. They're close.",
            "Stop whispering to me!",
            "I swear, I saw something in the corner.",
            "They know we're talking about them...",
            "Don't believe the lies. It's all connected.",
            "The government is controlling our thoughts.",
            "They're out there... I can feel them.",
            "Don't let them see what we're doing.",
            "The shadows are moving again.",
            "The TV is sending me messages. I need to turn it off.",
            "You're in on it too, aren't you?",
            "I hear the voices when I close my eyes.",
            "They're trying to control my thoughts with those signals.",
            "The numbers… they don't add up. Something's wrong.",
            "I can't trust anyone anymore. Even you.",
            "The code… it's all hidden in the code!",
            "I'm not alone here, I can hear them whispering.",
            "They're following us. Don't look back.",
            "I saw someone behind me in the mirror... but there's no one there.",
            "There are eyes in the ceiling. Watching.",
            "The walls are breathing. Can't you see it?",
            "It's all a setup. They're testing us.",
            "They want me to fail, but I won't let them.",
            "Every time I blink, they get closer.",
            "I'm not paranoid, I'm just paying attention.",
            "They're messing with my mind. I can feel it.",
            "I can hear footsteps, but no one is there.",
            "Don't trust the clocks. They've been tampered with.",
            "You think I don't notice, but I see everything.",
            "I need to cover the windows. They can see me.",
            "They're trying to erase my memory. I can't let them win.",
            "I swear, there's something in the walls.",
            "The numbers are talking to me again.",
            "It's all connected. Every single thing.",
            "Do you smell that? It's a trap.",
            "I can feel the radio waves in my brain.",
            "Stop talking to me like that! I know what you're planning.",
            "There's someone in the room with us. I know it.",
            "It's not safe here. We need to leave, now.",
            "I've seen them in my dreams. They're real.",
            "The colors… they're all wrong.",
            "I'm being watched from the corner of the room.",
            "I need to write it down before they erase it.",
            "They're using my reflections to spy on me.",
            "Why are the lights flickering? It's a sign.",
            "I can feel their presence. They're here.",
            "This is exactly what they wanted. Don't you see?",
            "The sky isn't supposed to look like that.",
            "Everything around us is fake. None of this is real.",
            "I saw someone out the window. But they disappeared.",
            "The government knows. They're hiding it from us.",
            "Why are you acting strange? Did they get to you?",
            "They planted microphones in the walls, I can hear them buzzing.",
            "Why can't I remember yesterday?",
            "They've erased parts of my memory. I'm sure of it.",
            "I'm seeing the same people over and over again.",
            "I think they replaced the neighbors with impostors.",
            "The voices are telling me to stop talking, but I can't.",
            "Everything is listening to me. The phone, the TV, the fridge.",
            "Why does the sun look different today?",
            "The sound... It's driving me crazy. Do you hear it?",
            "I saw someone in the mirror, but it wasn't me.",
            "I need to keep moving, or they'll catch up to me.",
            "Do you hear the static? It's their way of communicating.",
            "The streetlights... they flickered for a reason.",
            "I know they're listening through the radio.",
            "My phone's been hacked. It's them.",
            "They're reading my thoughts. I can feel it.",
            "The sky isn't real, it's just a projection.",
            "Someone's been moving things in my room when I'm not here.",
            "I can see patterns in the clouds. They're trying to communicate.",
            "They're sending me messages through the static.",
            "I saw the same car three times today... they're watching.",
            "Have you ever felt like you're being watched, but can't see them?",
            "I think they've replaced my reflection with someone else's.",
            "That noise isn't just in my head. It's real.",
            "The animals are acting strange... they know something.",
            "The government is altering the weather. It's obvious.",
            "They put something in the food. Don't eat it.",
            "The power went out again. They're controlling the grid.",
            "That song… it has a hidden message. You just need to listen closely.",
            "They've been tampering with my mail. I know it.",
            "I see faces in the static. They're watching me.",
            "There's a camera hidden in the smoke detector.",
            "I'm not safe. They're closing in.",
            "Have you noticed the same people everywhere? They're following us.",
            "The wind is talking to me, warning me.",
            "They've put something in the water. I'm sure of it.",
            "The birds aren't real. They're drones watching us.",
            "The voices are louder today. They know I'm onto them.",
            "Why does everyone look at me like they know something?",
            "I've seen the same number sequence everywhere today. It's a code.",
            "The street's been too quiet lately... they're planning something.",
            "They can track me through my shoes. I need to change them.",
            "I feel like someone's in my head, making decisions for me.",
            "Do you feel that? The air is different. They've changed something.",
            "They're controlling our dreams. That's why I can't sleep.",
            "The moon isn't supposed to be that bright. What's going on?",
            "I can't talk too loud. They've got this place bugged.",
            "My thoughts aren't my own anymore.",
            "Why do the lights keep flickering? It's a sign.",
            "I hear the same voice on every phone call, no matter who I talk to.",
            "Someone's been erasing files from my computer... It's them.",
            "I think my reflection is watching me.",
            "The street cameras have been tracking me all day.",
            "I keep getting strange calls from unknown numbers. They know.",
            "There's a pattern in the shadows. It's trying to tell me something.",
            "Why does the news sound different today? It's not the same.",
            "The wind carries messages. I can hear them.",
            "I've been seeing the same bird everywhere I go.",
            "I'm starting to notice glitches in reality. They're becoming more frequent.",
            "The stars are forming shapes, but I can't decipher them.",
            "I think they've replaced my neighbors. Something's off.",
            "There's someone in the house... I can hear them breathing.",
            "I've been getting messages in my dreams. Warnings.",
            "The colors keep shifting. It's all a simulation.",
            "Everything feels wrong today. They've changed something.",
            "The numbers are everywhere... they're part of the plan.",
            "The clouds are moving too fast. What are they hiding?",
            "Someone's been in my house while I was gone.",
            "They know what I'm going to do before I do it.",
            "I heard whispers from the vents. Someone's listening.",
            "The trees are watching. I can feel their eyes on me.",
            "I haven't seen the mailman in weeks. They've taken him."
    };

    private final List<String> lastMessages = new ArrayList<>();

    private String message;

    public SchizoBotModule() {
        super("SchizoBot", "Prints messages to the chat that makes you look like a schizo", ModuleCategory.MISC);
    }

    @Subscribe
    public final Listener<UpdateEvent> updateEventListener = new Listener<>(event -> {
        if (Minecraft.getMinecraft().thePlayer == null) {
            timer.reset();
            return;
        }

        if (!Minecraft.getMinecraft().theWorld.multiplayerWorld) {
            timer.reset();
            return;
        }

        if (message == null) {
            message = printRandomMessage();
            timer.reset();
            return;
        }

        if (timer.hasTimeElapsed(30000 + ((message.length() * 60000L) / 100), true)) {
            System.out.println("sending: " + message);
            ChatUtil.sendMessage(message);
            message = null;
        }
    });

    @Subscribe
    public final Listener<IncomingPacketEvent> incomingPacketEventListener = new Listener<>(event -> {
        if (event.getPacket() instanceof Packet1Login) {
            timer.reset();
        }
    });

    @Subscribe
    public final Listener<OutgoingPacketEvent> outgoingPacketEventListener = new Listener<>(event -> {
        if (event.getPacket() instanceof Packet3Chat) {
            timer.reset();
        }
    });

    private String printRandomMessage() {
        String message = messages[random.nextInt(messages.length)];

        if (message.length() >= 100) {
            return printRandomMessage();
        }

        if (lastMessages.contains(message)) {
            return printRandomMessage();
        }

        // last messages should save the last 10 messages
        if (lastMessages.size() >= 10) {
            lastMessages.remove(0);
        }

        lastMessages.add(message);
        return message;
    }
}
