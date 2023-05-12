package be.woutzah.chatbrawl.files;

import be.woutzah.chatbrawl.ChatBrawl;
import be.woutzah.chatbrawl.util.ErrorHandler;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.EnumMap;

public class ConfigManager{

    private final ChatBrawl plugin;
    private final EnumMap<ConfigType, YamlConfiguration> configMap;

    public ConfigManager(ChatBrawl plugin) {
        this.plugin = plugin;
        this.configMap = new EnumMap<>(ConfigType.class);
        saveDefaultSettings();
    }

    public void saveDefaultSettings(){
        if(!plugin.getDataFolder().exists()){
            plugin.getDataFolder().mkdirs();
        }
        Arrays.stream(ConfigType.values()).forEach(configType ->{
            String pathString = configType.getPath();
            File file = new File(plugin.getDataFolder(),pathString );

            if (!file.exists()) {
                try {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            InputStream inputStream = plugin.getResource(pathString);
            if(inputStream == null) return;
            YamlConfiguration resourceConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream));
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            config.options().header("" +
                    "   _____ _           _   ____                     _ \n" +
                    "  / ____| |         | | |  _ \\                   | |\n" +
                    " | |    | |__   __ _| |_| |_) |_ __ __ ___      _| |\n" +
                    " | |    | '_ \\ / _` | __|  _ <| '__/ _` \\ \\ /\\ / / |\n" +
                    " | |____| | | | (_| | |_| |_) | | | (_| |\\ V  V /| |\n" +
                    "  \\_____|_| |_|\\__,_|\\__|____/|_|  \\__,_| \\_/\\_/ |_|\n" +
                    "                                                    \n" +
                    "  For a detailed explanation of all settings visit:         \n"+
                    "      https://github.com/woutzah/ChatBrawl/wiki    \n"+
                    "\n"+
                    "  Need help? Join the Discord server: https://discord.gg/TvTUWvG"+
                    " \n");
            config.options().copyHeader();
            config.addDefaults(resourceConfig);
            config.options().copyDefaults(true);
            try {
                config.save(file);
            } catch (IOException e) {
                ErrorHandler.error("Something went wrong when initializing the config files!");
            }
            configMap.put(configType,config);
        });

    }

    public YamlConfiguration getConfig(ConfigType type){
        return configMap.get(type);
    }

}
