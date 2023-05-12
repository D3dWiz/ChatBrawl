package be.woutzah.chatbrawl.commands;

import be.woutzah.chatbrawl.ChatBrawl;
import be.woutzah.chatbrawl.commands.subcommands.*;
import be.woutzah.chatbrawl.races.types.RaceType;
import be.woutzah.chatbrawl.settings.LanguageSetting;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.util.Printer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandManager implements CommandExecutor, TabCompleter {

    private final String parentCommandName;
    private final ArrayList<SubCommand> subCommandsList;
    private final ChatBrawl plugin;
    private final SettingManager settingManager;

    public CommandManager(ChatBrawl plugin) {
        this.parentCommandName = "chatbrawl";
        this.subCommandsList = new ArrayList<>();
        this.plugin = plugin;
        this.settingManager = plugin.getSettingManager();
        setup();
    }


    public void setup() {
        plugin.getCommand(parentCommandName).setExecutor(this);
        plugin.getCommand(parentCommandName).setTabCompleter(this);
        this.subCommandsList.add(new CurrentCommand(plugin));
        this.subCommandsList.add(new DisableCommand(plugin));
        this.subCommandsList.add(new EnableCommand(plugin));
        this.subCommandsList.add(new StopCommand(plugin));
        this.subCommandsList.add(new StartCommand(plugin));
        this.subCommandsList.add(new HelpCommand(plugin));
        this.subCommandsList.add(new ReloadCommand(plugin));
        this.subCommandsList.add(new LeaderboardCommand(plugin));
    }

    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String s, String[] args) {

        if (!command.getName().equalsIgnoreCase(parentCommandName)) {
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "make sure to type a subcommand!");
            return true;
        }

        SubCommand target = findSubCommand(args[0]);

        if (target == null) {
            Printer.sendMessage(settingManager.getString(LanguageSetting.SUBCOMMAND_NOT_EXIST), sender);
            return true;
        }
        if (!target.isCanConsoleUse() && !(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this subcommand!");
            return true;
        }
        if (!target.getPermission().equalsIgnoreCase("none")) {
            if (!sender.hasPermission(target.getPermission())) {
                Printer.sendMessage(settingManager.getString(LanguageSetting.NO_PERMISSION), sender);
                return true;
            }
        }

        try {
            target.execute(sender, remapArgs(args));
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "An error has occurred while performing the command!");
            e.printStackTrace();
        }
        return true;
    }

    private String[] remapArgs(String[] args) {
        return Arrays.copyOfRange(args, 1, args.length);
    }

    private SubCommand findSubCommand(String name) {
        return subCommandsList.stream().filter(s -> s.getName().equalsIgnoreCase(name) ||
                s.getAliases().contains(name)).findFirst().orElse(null);
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String string, @NotNull String[] args) {
        if (sender.hasPermission("cb.default")) {
            List<String> suggestionList = new ArrayList<>();
            List<String> tempList = new ArrayList<>();
            switch (args.length) {
                case 1 -> {
                    tempList.add("help");
                    tempList.add("leaderboard");
                    if (sender.hasPermission("cb.current") ||
                            sender.hasPermission("cb.admin")) {
                        tempList.add("current");
                    }
                    if (sender.hasPermission("cb.disable") ||
                            sender.hasPermission("cb.admin")) {
                        tempList.add("disable");
                    }
                    if (sender.hasPermission("cb.enable") ||
                            sender.hasPermission("cb.admin")) {
                        tempList.add("enable");
                    }
                    if (sender.hasPermission("cb.stop") ||
                            sender.hasPermission("cb.admin")) {
                        tempList.add("stop");
                    }
                    if (sender.hasPermission("cb.start") ||
                            sender.hasPermission("cb.admin")) {
                        tempList.add("start");
                    }
                    if (sender.hasPermission("cb.reload") ||
                            sender.hasPermission("cb.admin")) {
                        tempList.add("reload");
                    }
                    if (sender.hasPermission("cb.leaderboard") ||
                            sender.hasPermission("cb.admin")) {
                        tempList.add("leaderboard");
                    }
                    tempList.stream().filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                            .forEach(suggestionList::add);
                    Collections.sort(suggestionList);
                    return suggestionList;
                }
                case 2 -> {
                    switch (args[0].toLowerCase()) {
                        case "start" -> {
                            if (sender.hasPermission("cb.start") ||
                                    sender.hasPermission("cb.admin")) {
                                Arrays.stream(RaceType.values()).filter(r -> r != RaceType.NONE)
                                        .forEach(r -> tempList.add(r.toString().toLowerCase()));
                                tempList.stream().filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                                        .forEach(suggestionList::add);
                                Collections.sort(suggestionList);
                            }
                            return suggestionList;
                        }
                        case "leaderboard" -> {
                            if (sender.hasPermission("cb.leaderboard") ||
                                    sender.hasPermission("cb.admin")) {
                                tempList.add("user");
                                tempList.add("race");
                                tempList.stream().filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                                        .forEach(suggestionList::add);
                                Collections.sort(suggestionList);
                            }
                            return suggestionList;
                        }
                        default -> {
                            return null;
                        }
                    }
                }
                case 3 -> {
                    if (args[1].toLowerCase().equals("race")) {
                        if (sender.hasPermission("cb.leaderboard") ||
                                sender.hasPermission("cb.admin")) {
                            Arrays.stream(RaceType.values()).filter(r -> r != RaceType.NONE)
                                    .forEach(r -> tempList.add(r.toString().toLowerCase()));
                            tempList.stream().filter(s -> s.toLowerCase().startsWith(args[2].toLowerCase()))
                                    .forEach(suggestionList::add);
                            Collections.sort(suggestionList);
                        }
                        return suggestionList;
                    }
                    return null;
                }
                case 4 -> {
                    if (args[0].toLowerCase().equals("leaderboard")) {
                        if (sender.hasPermission("cb.leaderboard") ||
                                sender.hasPermission("cb.admin")) {
                            tempList.add("time");
                            tempList.add("wins");
                            tempList.add("total");
                            tempList.stream().filter(s -> s.toLowerCase().startsWith(args[3].toLowerCase()))
                                    .forEach(suggestionList::add);
                            Collections.sort(suggestionList);
                        }
                        return suggestionList;
                    }
                    return null;
                }
            }
        }
        return null;
    }
}