package net.mistrifix.main.listener;

import net.mistrifix.main.base.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {

    @EventHandler
    public void onLeave(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        User user = User.get(player.getUniqueId());
        if(user.hasQuest()) player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }
}
