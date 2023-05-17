package be.woutzah.chatbrawl.races.types;

import org.bukkit.entity.Player;

public interface Announceable {
    void announceStart();

    void sendStart(Player player);

    void announceEnd();

    void announceWinner(Player player);

    void showBossBar();

    void stopBossBar();

    void showActionBar();

    void stopActionBar();

    String replacePlaceholders(String message);
}
