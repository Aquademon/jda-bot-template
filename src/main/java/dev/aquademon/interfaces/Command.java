package dev.aquademon.interfaces;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface Command {
    void execute(SlashCommandInteractionEvent event);
    String getName();
    String getDescription();
}