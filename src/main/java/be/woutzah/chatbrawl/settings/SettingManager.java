package be.woutzah.chatbrawl.settings;

import be.woutzah.chatbrawl.ChatBrawl;
import be.woutzah.chatbrawl.files.ConfigManager;
import be.woutzah.chatbrawl.files.ConfigType;
import be.woutzah.chatbrawl.races.types.RaceType;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingManager {

    private final ConfigManager configManager;
    private final Map<RaceType, ConfigType> raceTypeConfigMap;

    public SettingManager(ChatBrawl plugin) {
        this.configManager = plugin.getConfigManager();
        this.raceTypeConfigMap = new HashMap<>();
        raceTypeConfigMap.put(RaceType.CHAT, ConfigType.CHATRACE);
        raceTypeConfigMap.put(RaceType.BLOCK, ConfigType.BLOCKRACE);
        raceTypeConfigMap.put(RaceType.FISH, ConfigType.FISHRACE);
        raceTypeConfigMap.put(RaceType.FOOD, ConfigType.FOODRACE);
        raceTypeConfigMap.put(RaceType.HUNT, ConfigType.HUNTRACE);
        raceTypeConfigMap.put(RaceType.QUIZ, ConfigType.QUIZRACE);
        raceTypeConfigMap.put(RaceType.CRAFT, ConfigType.CRAFTRACE);
        raceTypeConfigMap.put(RaceType.SCRAMBLE, ConfigType.SCRAMBLERACE);
    }

    public String getString(Setting setting) {
        return configManager.getConfig(setting.getConfigType()).getString(setting.getPath());
    }

    public String getString(ConfigType configType, String path) {
        return configManager.getConfig(configType).getString(path);
    }

    public String getString(RaceType raceType, Setting setting) {
        return configManager.getConfig(raceTypeConfigMap.get(raceType)).getString(setting.getPath());
    }

    public int getInt(Setting setting) {
        return configManager.getConfig(setting.getConfigType()).getInt(setting.getPath());
    }

    public int getInt(RaceType raceType, Setting setting) {
        return configManager.getConfig(raceTypeConfigMap.get(raceType)).getInt(setting.getPath());
    }

    public int getInt(ConfigType configType, String path) {
        return configManager.getConfig(configType).getInt(path);
    }

    public boolean getBoolean(Setting setting) {
        return configManager.getConfig(setting.getConfigType()).getBoolean(setting.getPath());
    }

    public boolean getBoolean(RaceType raceType, Setting setting) {
        return configManager.getConfig(raceTypeConfigMap.get(raceType)).getBoolean(setting.getPath());
    }

    public List<String> getStringList(Setting setting) {
        return configManager.getConfig(setting.getConfigType()).getStringList(setting.getPath());
    }

    public List<String> getStringList(RaceType raceType, Setting setting) {
        return configManager.getConfig(raceTypeConfigMap.get(raceType)).getStringList(setting.getPath());
    }

    public List<String> getStringList(ConfigType configType, String path) {
        return configManager.getConfig(configType).getStringList(path);
    }

    public ConfigurationSection getConfigSection(Setting setting) {
        return configManager.getConfig(setting.getConfigType()).getConfigurationSection(setting.getPath());
    }

    public ConfigurationSection getConfigSection(RaceType raceType, Setting setting) {
        return configManager.getConfig(raceTypeConfigMap.get(raceType)).getConfigurationSection(setting.getPath());
    }

    public List<Integer> getIntegerList(ConfigType configType, String path) {
        return configManager.getConfig(configType).getIntegerList(path);
    }
}
