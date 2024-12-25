package dev.aquademon.interfaces;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import java.util.List;

public interface Command {
    void execute(SlashCommandInteractionEvent event);
    String getName();
    String getDescription();
    List<OptionData> getOptions();
}
