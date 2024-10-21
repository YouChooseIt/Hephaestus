package io.github.qe7.features.impl.commands.impl.chat.impl;

import io.github.qe7.features.impl.commands.impl.chat.api.ChatCommand;
import io.github.qe7.utils.ChatUtil;

public class JokeChatCommand extends ChatCommand {

    private final String[] jokes = new String[]{
            "Why did the scarecrow win an award? Because he was outstanding in his field!",
            "What do you call a belt made out of watches? A waist of time!",
            "Why dont skeletons fight each other? They dont have the guts!",
            "Why did the tomato turn red? Because it saw the salad dressing!",
            "What do you call a fish wearing a crown? A king fish!",
            "Why did the math book look sad? Because it had too many problems!",
            "What do you call a bear with no teeth? A gummy bear!",
            "Why did the golfer bring two pairs of pants? In case he got a hole in one!",
            "What do you call a pile of cats? A meowtain!",
            "Why did the cookie go to the doctor? Because it was feeling crumbly!",
            "What do you call a cow with no legs? Ground beef!",
            "Why did the computer go to the doctor? Because it had a virus!",
            "What do you call a bear with no teeth? A gummy bear!",
            "Why did the tomato turn red? Because it saw the salad dressing!",
            "What do you call a fish wearing a crown? A king fish!",
            "Why did the math book look sad? Because it had too many problems!",
            "What do you call a bear with no teeth? A gummy bear!",
            "Why did the golfer bring two pairs of pants? In case he got a hole in one!",
            "What do you call a pile of cats? A meowtain!",
            "Why did the cookie go to the doctor? Because it was feeling crumbly!",
            "What do you call a cow with no legs? Ground beef!",
            "Why did the computer go to the doctor? Because it had a virus!",
            "What do you call a bear with no teeth? A gummy bear!",
            "Why did the tomato turn red? Because it saw the salad dressing!",
            "What do you call a fish wearing a crown? A king fish!",
            "Why did the math book look sad? Because it had too many problems!",
            "What do you call a bear with no teeth? A gummy bear!",
            "Why did the golfer bring two pairs of pants? In case he got a hole in one!",
            "What do you call a pile of cats? A meowtain!",
    };

    public JokeChatCommand() {
        super("Joke", "Tells a joke");
    }

    @Override
    public void execute(String username, String[] args) {
        int index = (int) (Math.random() * jokes.length);
        String joke = jokes[index];
        ChatUtil.sendMessage(joke);
    }
}
