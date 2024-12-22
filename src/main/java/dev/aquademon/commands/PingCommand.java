package dev.aquademon.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import dev.aquademon.interfaces.BaseCommand;

public class PingCommand extends BaseCommand {
    public PingCommand() {
        super("ping", "Check the bot's latency");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.reply("Hello, world.").queue();
    }
}