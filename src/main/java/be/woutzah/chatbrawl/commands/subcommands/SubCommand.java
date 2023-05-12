package be.woutzah.chatbrawl.commands.subcommands;

import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class SubCommand {

    private final String name;
    private final String permission;
    private final List<String> aliases;
    private final boolean canConsoleUse;

    public SubCommand(String name, String permission, List<String> aliases, boolean canConsoleUse) {
        this.name = name;
        this.permission = permission;
        this.aliases = aliases;
        this.canConsoleUse = canConsoleUse;
    }

    public abstract void execute(CommandSender sender, String[] args);

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public boolean isCanConsoleUse() {
        return canConsoleUse;
    }
}
