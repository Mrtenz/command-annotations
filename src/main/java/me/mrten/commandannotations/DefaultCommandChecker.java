package me.mrten.commandannotations;

/**
 * This is the default command checker, used if no other command checker is specified.
 */
public class DefaultCommandChecker implements CommandChecker {

    @Override
    public boolean check(CommandContext context) {
        return true;
    }
}
