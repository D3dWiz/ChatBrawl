package be.woutzah.chatbrawl.settings.races;

import be.woutzah.chatbrawl.files.ConfigType;
import be.woutzah.chatbrawl.settings.Setting;

public enum BlockRaceSetting implements Setting {

    BLOCKS("blocks");

    private final String path;
    private final ConfigType configType;

    BlockRaceSetting(String path) {
        this.path = path;
        this.configType = ConfigType.BLOCKRACE;
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
