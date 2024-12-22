package dev.aquademon.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import dev.aquademon.handlers.CommandManager;
import dev.aquademon.interfaces.Command;

public class CommandListener extends ListenerAdapter {
    private final CommandManager manager;

    public CommandListener(CommandManager manager) {
        this.manager = manager;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String commandName = event.getName();
        Command command = manager.getCommand(commandName);

        if (command != null) {
            command.execute(event);
        }
    }
}