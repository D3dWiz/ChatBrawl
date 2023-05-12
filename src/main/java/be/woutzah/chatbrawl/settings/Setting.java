package be.woutzah.chatbrawl.settings;

import be.woutzah.chatbrawl.files.ConfigType;

public interface Setting {
    String getPath();

    ConfigType getConfigType();
}
