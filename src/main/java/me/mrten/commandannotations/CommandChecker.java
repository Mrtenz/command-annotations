package me.mrten.commandannotations;

public interface CommandChecker {

    /**
     * Get whether the arguments are valid for the command. If returning FALSE, no message will be shown to the
     * executor, by the command handler and the command will be cancelled.
     *
     * @param context the command context
     * @return TRUE if the arguments are valid, otherwise FALSE
     */
    boolean check(CommandContext context);
}
