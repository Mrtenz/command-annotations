package me.mrten.commandannotations;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class CommandHandler implements CommandExecutor {

    private final @NonNull Plugin plugin;
    private final @NonNull String noPermissionsMessage;
    private final @NonNull String cannotExecuteAsConsoleMessage;
    private final @NonNull String invalidArgumentsMessage;
    private final @NonNull String errorMessage;
    private ClassToInstanceMap<CommandChecker> checkers = MutableClassToInstanceMap.create();
    private @Getter Map<String, CommandInfo> commands = new HashMap<>();

    /**
     * Register a class containing commands.
     *
     * @param commandsClass the class
     */
    public void addCommands(Class<?> commandsClass) {
        for (Method method : commandsClass.getMethods()) {
            Command command = method.getAnnotation(Command.class);
            if (command != null) {
                CommandInfo commandInfo = new CommandInfo(command, method);
                StringBuilder builder = new StringBuilder(command.name());
                for (String subcommands : command.subcommands()) {
                    builder.append(" ").append(subcommands);
                }
                commands.put(builder.toString().trim(), commandInfo);
                plugin.getServer().getPluginCommand(command.name()).setExecutor(this);
            }
        }
    }

    private CommandChecker getCommandChecker(Class<? extends CommandChecker> clazz) {
        if (!checkers.containsKey(clazz)) {
            try {
                CommandChecker instance = clazz.newInstance();
                checkers.put(clazz, instance);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return checkers.get(clazz);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String label, String[] arguments) {
        CommandInfo commandInfo = getCommandInfo(command.getName(), arguments);
        if (commandInfo != null) {
            if (!(commandSender instanceof Player) && !commandInfo.getCommand().executableByConsole()) {
                commandSender.sendMessage(cannotExecuteAsConsoleMessage);
                return true;
            }

            if (!commandInfo.getCommand().permission().isEmpty() && !commandSender.hasPermission(commandInfo.getCommand().permission())) {
                commandSender.sendMessage(noPermissionsMessage);
                return true;
            }

            if (arguments.length < commandInfo.getCommand().min() || (commandInfo.getCommand().max() > 0 && arguments.length > commandInfo.getCommand().max())) {
                commandSender.sendMessage(invalidArgumentsMessage.replace("{usage}", commandInfo.getCommand().usage()));
                return true;
            }

            CommandContext context = new CommandContext(commandSender, arguments);
            Class<? extends CommandChecker>[] checkers = commandInfo.getCommand().checkers();
            for (Class<? extends CommandChecker> clazz : checkers) {
                CommandChecker checker = getCommandChecker(clazz);
                if (!checker.check(context)) {
                    return true;
                }
            }

            try {
                Object instance = commandInfo.getMethod().getDeclaringClass().newInstance();
                if (commandInfo.getMethod().getParameterCount() > 0) {
                    commandInfo.getMethod().invoke(instance, context);
                } else {
                    commandInfo.getMethod().invoke(instance);
                }
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                if (e.getCause().getClass() == CommandException.class) {
                    CommandException commandException = (CommandException) e.getCause();
                    commandSender.sendMessage(errorMessage.replace("{error}", commandException.getMessage()));
                    if (commandException.getCause() != null) {
                        commandException.getCause().printStackTrace();
                    }
                } else {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    private CommandInfo getCommandInfo(String command, String[] arguments) {
        command = command.toLowerCase();
        int matches = 0;
        CommandInfo current = null;
        commandLoop:
        for (Map.Entry<String, CommandInfo> entry : commands.entrySet()) {
            if (entry.getValue().getCommand().name().equalsIgnoreCase(command)) {
                String[] subcommands = entry.getValue().getCommand().subcommands();
                if ((subcommands.length == 0 || subcommands[0].isEmpty()) && current == null) {
                    current = entry.getValue();
                    continue;
                }

                int currentMatches = 0;
                for (int i = 0; i < subcommands.length; i++) {
                    if (arguments.length >= i + 1 && subcommands.length <= arguments.length && subcommands[i].equalsIgnoreCase(arguments[i])) {
                        currentMatches++;
                    } else {
                        continue commandLoop;
                    }
                }

                if (currentMatches > matches) {
                    matches = currentMatches;
                    current = entry.getValue();
                }
            }
        }
        return current;
    }

    @Data
    private class CommandInfo {

        private final Command command;
        private final Method method;
    }
}
