package be.woutzah.chatbrawl.settings.races;

import be.woutzah.chatbrawl.files.ConfigType;
import be.woutzah.chatbrawl.settings.Setting;

public enum HuntRaceSetting implements Setting {

    MOBS("mobs");

    private final String path;
    private final ConfigType configType;

    HuntRaceSetting(String path) {
        this.path = path;
        this.configType = ConfigType.HUNTRACE;
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
