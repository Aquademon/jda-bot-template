package dev.aquademon.interfaces;

import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseCommand implements Command {
    private final String name;
    private final String description;
    private final List<OptionData> options;

    public BaseCommand(String name, String description) {
        this.name = name;
        this.description = description;
        this.options = new ArrayList<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<OptionData> getOptions() {
        return options;
    }

    protected void addOption(OptionData option) {
        options.add(option);
    }
}
