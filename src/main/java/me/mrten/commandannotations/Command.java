package me.mrten.commandannotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation to specify a command. It has to be placed above a public method. The method can have no
 * parameters or one parameter which takes a CommandContext.
 * <p>
 * For example:<br>
 * <tt>public void myCommand()</tt> or<br>
 * <tt>public void myCommand(CommandContext context)</tt>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {

    /**
     * The base name of the command. This is the first part of the command, for example: /<b>&lt;name&gt;</b>
     *
     * @return the base name
     */
    String name();

    /**
     * The subcommand(s) of the command. These come after the command name, for example: /&lt;name&gt; <b>[subcommand1]
     * [subcommand2]</b>
     *
     * @return the subcommands
     */
    String[] subcommands() default "";

    /**
     * The permission of the command. If the executor does not have the permission a message will be shown. Set this to
     * an empty string to have no permission for the command.
     *
     * @return the permission
     */
    String permission() default "";

    /**
     * The usage of the command. This will be shown if the executor does not specify the required number of arguments.
     *
     * @return the usage
     */
    String usage();

    /**
     * The minimum number of arguments.
     *
     * @return the minimum number of arguments
     */
    int min() default 0;

    /**
     * The maximum number of arguments. Set this to -1 to have no maximum number of arguments.
     *
     * @return the maximum number of arguments
     */
    int max() default -1;

    /**
     * Whether the command is executable by the console. If this is set to false, a message will be shown to the console
     * when executing the command.
     *
     * @return whether the command is executable by the console
     */
    boolean executableByConsole() default true;

    /**
     * The command checkers. These will be executed in order and can be used to check if the specified arguments are
     * valid.
     *
     * @return the command checkers
     */
    Class<? extends CommandChecker>[] checkers() default DefaultCommandChecker.class;
}
