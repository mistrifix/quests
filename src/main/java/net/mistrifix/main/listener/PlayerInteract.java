package net.mistrifix.main.listener;

import net.mistrifix.main.base.quest.Quest;
import net.mistrifix.main.base.quest.QuestUtils;
import net.mistrifix.main.base.user.User;
import net.mistrifix.main.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteract implements Listener {

    @EventHandler
    public void onInteract(InventoryClickEvent event)
    {
        Player player = (Player) event.getWhoClicked();
        User user = User.get(player.getUniqueId());
        if(event.getClickedInventory().getName().equals(ChatUtils.colored("&3Dostepne zadania")))
        {
            ItemStack item = event.getClickedInventory().getItem(event.getSlot());
            String possibleQuestName = item.getItemMeta().getDisplayName().substring(13);
            try
            {
                Quest foundQuest = Quest.get(possibleQuestName);
                if(event.getClick() == ClickType.LEFT)
                {
                    if(user.meetsRequirements(foundQuest))
                    {
                        event.setCancelled(true);
                        user.applyQuest(foundQuest);
                        player.setScoreboard(user.setupScoreboard());
                        user.sendMessage("&7Pomyslnie podjeto zadanie &f" + user.getQuest().getId() + "&7.");
                        player.closeInventory();
                    } else {
                        event.setCancelled(true);
                        player.closeInventory();
                        user.sendMessage("&cNie mozesz podjac sie tego zadania.");
                    }
                }
                if(event.getClick() == ClickType.RIGHT)
                {
                    if(!user.hasQuest())
                    {
                     event.setCancelled(true);
                     user.sendMessage("&cNie jestes w trakcie wykonywania zadania.");
                     player.closeInventory();
                     return;
                    }
                    Quest quest = user.getQuest();
                    if(quest.getId().equalsIgnoreCase(foundQuest.getId()))
                    {
                        event.setCancelled(true);
                        user.sendMessage("&7Anulowales swoj postep w zadaniu &f" + user.getQuest().getId() + "&7.");
                        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                        user.cancelQuest();

                        return;
                    } else {
                        event.setCancelled(true);
                        user.sendMessage("&cNie jestes w trakcie wykonywania tego zadania.");
                    }
                }
            } catch (Exception ex)
            {
                ex.printStackTrace();
                event.setCancelled(true);
                player.closeInventory();
                user.sendMessage("&cWystapil blad. Sprobuj ponownie pozniej.");
            }
        }
        for(Quest allQuests : QuestUtils.getQuests())
        {
            if(event.getInventory().getName().equalsIgnoreCase(ChatUtils.colored("&3Nagrody: &f" + allQuests.getId())))
            {
                event.setCancelled(true);
            }
        }
    }
}
