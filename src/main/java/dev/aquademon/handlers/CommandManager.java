package dev.aquademon.handlers;

import dev.aquademon.interfaces.BaseCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import dev.aquademon.interfaces.Command;
import dev.aquademon.listeners.CommandListener;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class CommandManager {
    private final Map<String, Command> commands = new HashMap<>();
    private final JDA jda;
    private final String commandsPackage;

    public CommandManager(JDA jda, String commandsPackage) {
        this.jda = jda;
        this.commandsPackage = commandsPackage;
        System.out.println("Initializing CommandManager with package: " + commandsPackage);
        jda.addEventListener(new CommandListener(this));
        loadCommands();
    }

    private void loadCommands() {
        try {
            System.out.println("Starting command loading process...");

            // Get all classes in the commands package
            Set<Class<?>> classes = findClasses(commandsPackage);
            System.out.println("Found " + classes.size() + " classes in package");

            // Debug print all found classes
            classes.forEach(clazz -> System.out.println("Found class: " + clazz.getName()));

            List<CommandData> commandDataList = new ArrayList<>();

            for (Class<?> clazz : classes) {
                System.out.println("Checking class: " + clazz.getName());

                // Check if the class is a command
                if (Command.class.isAssignableFrom(clazz) && !clazz.equals(BaseCommand.class)) {
                    try {
                        System.out.println("Creating instance of command: " + clazz.getName());
                        Command command = (Command) clazz.getDeclaredConstructor().newInstance();
                        commands.put(command.getName(), command);

                        CommandData commandData = Commands.slash(
                                command.getName(),
                                command.getDescription()
                        ).addOptions(command.getOptions());
                        commandDataList.add(commandData);

                        System.out.println("Successfully loaded command: " + command.getName());
                    } catch (Exception e) {
                        System.err.println("Failed to load command: " + clazz.getName());
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Skipping class as it's not a command: " + clazz.getName());
                }
            }

            if (commandDataList.isEmpty()) {
                System.out.println("Warning: No commands were loaded!");
            } else {
                System.out.println("Updating slash commands with " + commandDataList.size() + " commands");
                jda.updateCommands()
                        .addCommands(commandDataList)
                        .queue(
                                success -> System.out.println("Successfully updated slash commands"),
                                error -> {
                                    System.err.println("Failed to update slash commands");
                                    error.printStackTrace();
                                }
                        );
            }

        } catch (Exception e) {
            System.err.println("Error in loadCommands()");
            e.printStackTrace();
        }
    }

    private Set<Class<?>> findClasses(String packageName) throws Exception {
        Set<Class<?>> classes = new HashSet<>();
        String path = packageName.replace('.', '/');

        // Get the ClassLoader and resources
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(path);

        System.out.println("Searching for classes in path: " + path);

        // Handle both IDE and JAR running environments
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String protocol = resource.getProtocol();
            System.out.println("Found resource: " + resource + " with protocol: " + protocol);

            if (protocol.equals("file")) {
                // Handle files (IDE environment)
                String filePath = URLDecoder.decode(resource.getFile(), StandardCharsets.UTF_8);
                System.out.println("Scanning directory: " + filePath);
                findClassesInDirectory(new File(filePath), packageName, classes);
            } else if (protocol.equals("jar")) {
                // Handle JARs (production environment)
                System.out.println("Scanning JAR file");
                findClassesInJar(resource, path, packageName, classes);
            }
        }

        return classes;
    }

    private void findClassesInDirectory(File directory, String packageName, Set<Class<?>> classes) {
        if (!directory.exists()) {
            System.out.println("Directory does not exist: " + directory.getPath());
            return;
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    findClassesInDirectory(file, packageName + "." + file.getName(), classes);
                } else if (file.getName().endsWith(".class")) {
                    String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                    try {
                        System.out.println("Loading class: " + className);
                        classes.add(Class.forName(className));
                    } catch (ClassNotFoundException e) {
                        System.err.println("Failed to load class: " + className);
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void findClassesInJar(URL resource, String path, String packageName, Set<Class<?>> classes) {
        try {
            String jarPath = resource.getPath().substring(5, resource.getPath().indexOf("!"));
            jarPath = URLDecoder.decode(jarPath, StandardCharsets.UTF_8);

            try (JarFile jar = new JarFile(jarPath)) {
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();

                    if (entryName.startsWith(path) && entryName.endsWith(".class")) {
                        String className = entryName.substring(0, entryName.length() - 6).replace('/', '.');
                        System.out.println("Found class in JAR: " + className);
                        try {
                            classes.add(Class.forName(className));
                        } catch (ClassNotFoundException e) {
                            System.err.println("Failed to load class from JAR: " + className);
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error processing JAR file");
            e.printStackTrace();
        }
    }

    public Command getCommand(String name) {
        return commands.get(name);
    }

    public Collection<Command> getCommands() {
        return commands.values();
    }
}
