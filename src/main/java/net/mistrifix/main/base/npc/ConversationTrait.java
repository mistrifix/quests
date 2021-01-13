package net.mistrifix.main.base.npc;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class ConversationTrait extends Trait {

    public ConversationTrait()
    {
        super("conversationTrait");
    }

    @EventHandler
    public void onClick(NPCRightClickEvent event)
    {
        Player player = event.getClicker();
        if(event.getNPC().equals(this.getNPC()))
        {
            NPCHandler.openQuestMenu(player);
        }
    }
}
