package net.mistrifix.main.listener;

import net.mistrifix.main.base.user.User;
import net.mistrifix.main.base.user.UserUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        User user = User.get(player.getUniqueId());
        if(user == null)
        {
            user = UserUtils.create(player.getName(), player.getUniqueId());
            user.sendMessage("&cZostales dodany do bazy.");
            return;
        }
        user.sendMessage("&cJuz istniejesz w bazie.");
        if(user.hasQuest())
        {
            if(!player.getScoreboard().equals(user.setupScoreboard()))
            {
                player.setScoreboard(user.setupScoreboard());
            }
        }
    }
}
