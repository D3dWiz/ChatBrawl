package be.woutzah.chatbrawl.util;

import be.woutzah.chatbrawl.ChatBrawl;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

public class FireWorkUtil {

    public static void shootFireWorkSync(Player player) {
        Bukkit.getServer().getScheduler().runTask(ChatBrawl.getInstance(), () -> {
            Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);
            FireworkMeta data = firework.getFireworkMeta();
            data.addEffects(
                    FireworkEffect.builder()
                            .withColor(Color.PURPLE)
                            .withColor(Color.GREEN)
                            .with(FireworkEffect.Type.BALL_LARGE)
                            .withFlicker()
                            .build());
            data.setPower(1);
            firework.setFireworkMeta(data);
        });
    }
}
