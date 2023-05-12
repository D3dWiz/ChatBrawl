package be.woutzah.chatbrawl.settings.races;

import be.woutzah.chatbrawl.files.ConfigType;
import be.woutzah.chatbrawl.settings.Setting;

public enum FoodRaceSetting implements Setting {

    FOOD("food");

    private final String path;
    private final ConfigType configType;

    FoodRaceSetting(String path) {
        this.path = path;
        this.configType = ConfigType.FOODRACE;
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
