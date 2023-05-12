package be.woutzah.chatbrawl.settings;

import be.woutzah.chatbrawl.files.ConfigType;

public enum RewardSetting implements Setting {
    REWARDS("rewards");

    private final String path;
    private final ConfigType configType;

    RewardSetting(String path) {
        this.path = path;
        this.configType = ConfigType.REWARDS;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public ConfigType getConfigType() {
        return configType;
    }
}
