package io.github.qe7.features.impl.commands.impl.chat.impl;

import io.github.qe7.features.impl.commands.impl.chat.api.ChatCommand;
import io.github.qe7.utils.ChatUtil;

public final class QuoteChatCommand extends ChatCommand {

    private final String[] quotes = new String[]{
            "\"Believe you can and youre halfway there.\" - Theodore Roosevelt",
            "\"What you do speaks so loudly, I cannot hear what you say.\" - Ralph Waldo Emerson",
            "\"Life is what happens when youre busy making other plans.\" - John Lennon",
            "\"To be yourself in a world that is constantly trying to make you something else is the greatest accomplishment.\" - Ralph Waldo Emerson",
            "\"If you judge people, you have no time to love them.\" - Mother Teresa",
            "\"If you cannot do great things, do small things in a great way.\" - Napoleon Hill",
            "\"If opportunity doesn’t knock, build a door.\" - Milton Berle",
            "\"Life is short, and it is up to you to make it sweet.\" - Sarah Louise Delany",
            "\"Love all, trust a few, do wrong to none.\" - William Shakespeare",
            "\"Happiness depends upon ourselves.\" - Aristotle"
    };

    public QuoteChatCommand() {
        super("Quote", "Tells a joke");
    }

    @Override
    public void execute(String username, String[] args) {
        int index = (int) (Math.random() * quotes.length);
        String joke = quotes[index];
        ChatUtil.sendMessage(joke);
    }
}
