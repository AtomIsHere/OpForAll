package com.github.atomishere.opforalall.command;

import com.github.atomishere.opforalall.OpForAll;
import com.github.atomishere.opforalall.service.OFService;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class CommandManager extends OFService {
    public CommandManager(OpForAll plugin) {
        super(plugin, true);
    }

    @Getter
    private final List<OFCommand> commands = new ArrayList<>();

    @Override
    protected void onStart() {
        loadCommands();
    }

    @Override
    protected void onStop() {

    }

    @Override
    public String getName() {
        return "commandManager";
    }

    public void loadCommand(Class<? extends OFCommand> clazz, String commandName) {
        CommandInfo commandInfo;
        try {
            commandInfo = clazz.getAnnotation(CommandInfo.class);
        } catch(NullPointerException ex) {
            plugin.getLogger().severe("CommandInfo not present in command " + commandName + ".");
            return;
        }

        OFCommand command;
        try {
            Constructor<? extends OFCommand> con = clazz.getConstructor(OpForAll.class, String.class);

            command = con.newInstance(plugin, commandName);
        } catch (NoSuchMethodException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not load command " + commandName + ". Invalid constructor.", ex);
            return;
        } catch (IllegalAccessException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not load command " + commandName + ". Constructor is private.", ex);
            return;
        } catch (InstantiationException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not load command " + commandName + ". Class is abstract.", ex);
            return;
        } catch (InvocationTargetException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not load command " + commandName +  ". An exception was thrown in the constructor.", ex);
            return;
        }

        command.setDescription(commandInfo.description());
        command.setUsage(commandInfo.usage());

        commands.add(command);

        try {
            final Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");

            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());

            commandMap.register(commandName, command);
        } catch (IllegalAccessException | NoSuchFieldException | IllegalArgumentException | NullPointerException | ExceptionInInitializerError ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not register command " + commandName + ". Failed to load command map.", ex);
            return;
        }

        Bukkit.getServer().getPluginManager().registerEvents(command, plugin);
        plugin.getLogger().info("Registered command: /" + commandName);
    }

    private void loadCommands() {
        Reflections reflections = new Reflections("com.github.atomishere.opforalall.command");

        Set<Class<? extends OFCommand>> classes = reflections.getSubTypesOf(OFCommand.class);

        for(Class<? extends OFCommand> clazz : classes) {
            if(clazz.getSimpleName().startsWith("Command_")) {
                loadCommand(clazz, clazz.getSimpleName().replace("Command_", ""));
            }
        }
    }
}
