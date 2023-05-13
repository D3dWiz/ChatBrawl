package be.woutzah.chatbrawl.settings.races;

import be.woutzah.chatbrawl.files.ConfigType;
import be.woutzah.chatbrawl.settings.Setting;

public enum QuizRaceSetting implements Setting {

    QUESTIONS("questions");

    private final String path;
    private final ConfigType configType;

    QuizRaceSetting(String path) {
        this.path = path;
        this.configType = ConfigType.QUIZRACE;
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
