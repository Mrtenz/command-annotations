package me.mrten.commandannotations;

import lombok.Data;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Data
public class CommandContext {

    private final CommandSender commandSender;
    private final String[] arguments;

    /**
     * Get whether the executor is a player.
     *
     * @return TRUE if the executor is a player, otherwise FALSE
     */
    public boolean isPlayer() {
        return commandSender instanceof Player;
    }

    /**
     * Get the player. Make sure to check if the executor is a player by using {@link #isPlayer()} first. This will
     * throw a <tt>ClassCastException</tt> if the executor is not a player.
     *
     * @return the player
     */
    public Player getPlayer() {
        return (Player) commandSender;
    }

    /**
     * Get the length of the arguments.
     *
     * @return the length of the arguments
     */
    public int length() {
        return arguments.length;
    }
}
