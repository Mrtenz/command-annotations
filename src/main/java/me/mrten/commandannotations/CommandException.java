package me.mrten.commandannotations;

/**
 * Throwing this exception in a command will show the message to the command executor.
 */
public class CommandException extends Exception {

    public CommandException(String message) {
        super(message);
    }

    public CommandException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
