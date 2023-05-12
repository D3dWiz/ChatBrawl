package be.woutzah.chatbrawl.settings.races;

import be.woutzah.chatbrawl.files.ConfigType;
import be.woutzah.chatbrawl.settings.Setting;

public enum ChatRaceSetting implements Setting {
    WORDS("words");

    private final String path;
    private final ConfigType configType;

    ChatRaceSetting(String path) {
        this.path = path;
        this.configType = ConfigType.CHATRACE;
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
