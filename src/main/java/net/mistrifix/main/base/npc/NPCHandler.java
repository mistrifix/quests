package net.mistrifix.main.base.npc;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitFactory;
import net.mistrifix.main.QuestPlugin;
import net.mistrifix.main.data.PluginConfiguration;
import net.mistrifix.main.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.UUID;


public class NPCHandler {

    private static final PluginConfiguration config = QuestPlugin.getInstance().configuration;

    public static boolean isReady()
    {
        return Bukkit.getPluginManager().getPlugin("Citizens") != null &&
                Bukkit.getPluginManager().getPlugin("Citizens").isEnabled();
    }

    public static void setupNPC(Player executor)
    {
        NPCRegistry npcs = CitizensAPI.getNPCRegistry();
        NPC questNpc;
        String world = config.npc_world;
        int x = config.npc_x;
        int y = config.npc_y;
        int z = config.npc_z;
        try
        {
            questNpc = npcs.getByUniqueId(UUID.fromString(config.npc_uuid));
            if(questNpc != null)
            {
                Class<? extends Trait> convTrait = ConversationTrait.class;
                if(!questNpc.hasTrait(convTrait))
                {
                    questNpc.addTrait(convTrait); // further setting starts from here
                }
                Location spawnLocation = matchLocation(Bukkit.getWorld(world), x, y, z);
                if(spawnLocation != null)
                {
                    questNpc.teleport(spawnLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
                    questNpc.setBukkitEntityType(EntityType.VILLAGER);
                    questNpc.getEntity().setCustomName(ChatUtils.colored("&6&l" + config.npc_name));
                } else {
                    throw new NullPointerException("Wykryto blad w trakcie tworzenia NPC. Sprawdz, czy ustawiona lokalizacja" +
                            " jest poprawna.");
                }
            }
            questNpc = CitizensAPI.getNPCRegistry().createNPC();

            }
        } catch (Exception ex)
        {
            executor.sendMessage(ChatUtils.colored("&cWystapil blad w trakcie tworzenia NPC. Sprawdz konsole."));
            ex.printStackTrace();
        }
        /*try
        {
            questNpc = npcs.getByUniqueId(UUID.fromString(config.npc_uuid));
            if(!questNpc.hasTrait(ConversationTrait.class))
            {
                questNpc.addTrait(ConversationTrait.class);
            }
            questNpc.teleport(new Location(Bukkit.getWorld("world"), x + 0.5, y, z + 0.5), PlayerTeleportEvent.TeleportCause.PLUGIN);
            questNpc.setBukkitEntityType(EntityType.VILLAGER);
            questNpc.getEntity().setCustomName(ChatUtils.colored("&6&l" + config.npc_name));
        } catch(Exception ex)
        {
            questNpc = npcs.createNPC(EntityType.VILLAGER, UUID.fromString(config.npc_uuid), 1, config.npc_name);
            questNpc.addTrait(ConversationTrait.class);
            if(safeTeleport) {
                questNpc.teleport(executor.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                questNpc.getStoredLocation().setPitch(executor.getLocation().getPitch());
                questNpc.getStoredLocation().setYaw(executor.getLocation().getYaw());
                questNpc.getEntity().setCustomName(ChatUtils.colored("&6&l" + config.npc_name));
            }
            questNpc.spawn(new Location(Bukkit.getWorld(config.npc_world), x + 0.5, y, z + 0.5));
            questNpc.getEntity().setCustomName(ChatUtils.colored("&6&l" + config.npc_name));
        */
    }


         /*if(exists()) {
            removeEntirely(1);
            NPC found = CitizensAPI.getNPCRegistry().createNPC(EntityType.VILLAGER, config.npc_name);
            Entity npcEntity = found.getEntity();
            if(npcEntity instanceof LivingEntity)
            {
                LivingEntity npcLivingEntity = (LivingEntity) npcEntity;
                npcLivingEntity.setCustomName(ChatUtils.colored("&6&lRybak"));
                npcLivingEntity.setCustomNameVisible(true);
            }
            found.teleport(new Location(Bukkit.getWorld("world"), 0, 72, 0), PlayerTeleportEvent.TeleportCause.PLUGIN);
            if(!found.hasTrait(ConversationTrait.class))
            {
                found.addTrait(ConversationTrait.class);
            }
        } else {
            NPC newNpc = CitizensAPI.getNPCRegistry().createNPC(EntityType.VILLAGER, config.npc_name);
            Entity npcEntity = newNpc.getEntity();
            if(npcEntity instanceof LivingEntity)
            {
                LivingEntity npcLivingEntity = (LivingEntity) npcEntity;
                npcLivingEntity.setCustomName(ChatUtils.colored("&6&lRybak"));
                npcLivingEntity.setCustomNameVisible(true);
            }
            newNpc.addTrait(ConversationTrait.class);
            newNpc.spawn(new Location(Bukkit.getWorld("world"), 0, 72, 0));
        } */

    public static void openQuestMenu(Player player)
    {
        Inventory gui = Bukkit.createInventory(null, 27, ChatUtils.colored("&3Zadania"));
        player.openInventory(gui);
    }

    public static void removeEntirely()
    {
        NPC found = CitizensAPI.getNPCRegistry().getByUniqueId(UUID.fromString(config.npc_uuid));
        found.despawn();
        found.destroy();
        CitizensAPI.getNPCRegistry().deregister(found);
    }

    private static Location matchLocation(World world, int x, int y, int z)
    {
        Location attempt;
        if(world != null)
        {
            attempt = new Location(world, x, y, z);
        } else {
            attempt = null;
        }
        return attempt;
    }

}
