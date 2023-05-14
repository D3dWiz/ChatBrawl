package be.woutzah.chatbrawl.settings.races;

import be.woutzah.chatbrawl.files.ConfigType;
import be.woutzah.chatbrawl.settings.Setting;

public enum TypeRaceSetting implements Setting {
    WORDS("words");

    private final String path;
    private final ConfigType configType;

    TypeRaceSetting(String path) {
        this.path = path;
        this.configType = ConfigType.TYPERACE;
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
