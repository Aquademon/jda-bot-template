package dev.aquademon;

import dev.aquademon.handlers.CommandManager;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {

    public Main() {
        Dotenv dotenv = Dotenv.configure().load();
        String token = dotenv.get("BOT_TOKEN");

        JDA jda = JDABuilder.createDefault(token)
                .enableIntents(
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MESSAGES
                )
                .build();

        // Initialize command manager with our commands package
        CommandManager commandManager = new CommandManager(jda, "dev.aquademon.commands");
    }

    public static void main(String[] args) {
        new Main();
    }
}