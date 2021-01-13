package net.mistrifix.main.listener;

import net.mistrifix.main.base.quest.Quest;
import net.mistrifix.main.base.user.User;
import net.mistrifix.main.utils.ItemUtils;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerFish implements Listener {

    @EventHandler
    public void onFish(PlayerFishEvent event)
    {
        Player player = event.getPlayer();
        User user = User.get(player.getUniqueId());
        if(!user.hasQuest()) return;
        if(event.getState().equals(PlayerFishEvent.State.FAILED_ATTEMPT)) return;

        Quest quest = user.getQuest();
        if(event.getCaught() instanceof Item)
        {
            Item result = (Item) event.getCaught();
            ItemStack resultAttempt = ItemUtils.parseItem(quest.getObjectiveAsString());
            if(resultAttempt != null && resultAttempt.isSimilar(result.getItemStack()))
            {
                if(user.getProgress() + resultAttempt.getAmount() >= quest.getRequiredAmount())
                {
                    user.sendMessage("&aZadanie '&f" + user.getQuest().getId() + "&a' zostalo wykonane!");
                    user.sendMessage("&7Nagrody mozesz odebrac pod komenda &f/quest inbox&7.");
                    user.finishQuest();
                }
                user.sendMessage("&");
            }
        }
    }
}
