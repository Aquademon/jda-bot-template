package dev.aquademon.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import dev.aquademon.interfaces.BaseCommand;

public class SampleCommand extends BaseCommand {
    public SampleCommand() {
        super("ping", "Check the bot's latency");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.reply("Hello, world.").queue();
    }
}
