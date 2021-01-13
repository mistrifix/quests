package net.mistrifix.main.command;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.mistrifix.main.QuestPlugin;
import net.mistrifix.main.base.npc.NPCHandler;
import net.mistrifix.main.data.PluginConfiguration;
import net.mistrifix.main.utils.ChatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import java.util.UUID;

public class QuestCommand implements CommandExecutor {

    private final PluginConfiguration configuration = QuestPlugin.getInstance().configuration;

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!sender.hasPermission("quest.admin"))
        {
            if(sender instanceof Player) {
                sender.sendMessage(ChatUtils.colored("&7Nie posiadasz odpowiednich uprawnien."));
            } else {
                sender.sendMessage("Nie posiadasz odpowiednich uprawnien.");
                return false;
            }
            return false;
        }
        if(!(sender instanceof Player))
        {
            sender.sendMessage("Komenda mozliwa tylko dla graczy.");
            return false;
        }
        Player player = (Player) sender;
        if(args.length == 1)
        {
            if(args[0].equalsIgnoreCase("patch"))
            {
                patch(player);
            }
            if(args[0].equalsIgnoreCase("reload"))
            {
                reload(player);
            }
            if(args[0].equalsIgnoreCase("tp")) {
                teleport(player, false);
            }
            if(args[0].equalsIgnoreCase("tphere"))
            {
                teleport(player, true);
            }
            if(args[0].equalsIgnoreCase("remove"))
            {
                remove(player);
            }
        }
        return false;
    }

    protected void patch(Player sender)
    {
        NPCHandler.setupNPC(sender);
    }

    protected void teleport(Player sender, boolean reversed)
    {
        NPC attempt = CitizensAPI.getNPCRegistry().getByUniqueId(UUID.fromString(configuration.npc_uuid));
        if(attempt == null)
        {
            sender.sendMessage(ChatUtils.colored("&cNPC nie zostal ustawiony."));
            sender.sendMessage(ChatUtils.colored("&7Aby to zrobic, wpisz /quest patch."));
            return;
        }
        if(reversed) {
            sender.teleport(attempt.getStoredLocation().add(0, 0.5, 0));
            sender.sendMessage(ChatUtils.colored("&aPrzeteleportowano do NPC."));
            return;
        }
        attempt.teleport(sender.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
        sender.sendMessage(ChatUtils.colored("&aPrzeteleportowano NPC."));
    }

    protected void reload(Player sender)
    {
        try
        {
            configuration.loadConfiguration();
            sender.sendMessage(ChatUtils.colored("&aKonfiguracja przeladowana pomyslnie."));
        } catch (Exception ex)
        {
            ex.printStackTrace();
            sender.sendMessage(ChatUtils.colored("&cWystapil blad w trakcie ladowania pliku konfiguracyjnego."));
        }
    }

    protected void remove(Player sender)
    {
        NPC attempt = CitizensAPI.getNPCRegistry().getByUniqueId(UUID.fromString(configuration.npc_uuid));
        if(attempt == null)
        {
            sender.sendMessage(ChatUtils.colored("&cNPC nie zostal ustawiony."));
            return;
        }
        try
        {
            NPCHandler.removeEntirely();
            sender.sendMessage("&aNPC zostal pomyslnie usuniety");
        } catch (Exception ex)
        {
            sender.sendMessage(ChatUtils.colored("&cWystapil blad w trakcie wykonywania komendy."));
            ex.printStackTrace();
        }
    }

}

    /*public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if(!(sender instanceof Player))
        {
            sender.sendMessage("Komenda mozliwa do wykonania tylko przez gracza.");
            return false;
        }
        Player player = (Player) sender;
        if(args.length == 0)
        {
            showHelp(player);
            return true;
        }
        if(args.length == 1)
        {
            if(args[0].equalsIgnoreCase("list"))
            {
                showAvailableQuests(player);
                return true;
            }
            if(args[0].equalsIgnoreCase("progress"))
            {
                showQuestProgress(player);
                return true;
            }
            if(args[0].equalsIgnoreCase("cancel"))
            {
                cancelQuest(player);
                return true;
            }
            if(args[0].equalsIgnoreCase("inbox"))
            {
                showInbox(player);
                return true;
            }
            if(args[0].equalsIgnoreCase("reload"))
            {
                if(player.hasPermission("quest.reload"))
                {
                    reload(player);
                    return true;
                }
                return false;
            }
        }
        if(args.length == 2)
        {
            if(args[0].equalsIgnoreCase("rewards"))
            {
                showRewards(player, args);
                return true;
            }
        }
        return false;
    }

    private void showHelp(Player sender)
    {
        User user = User.get(sender.getUniqueId());
        String version = QuestPlugin.getInstance().getDescription().getVersion();
        user.sendMessage(" ");
        user.sendMessage(" &3Quests &7(v" + version + ")");
        user.sendMessage(" ");
        user.sendMessage(" &8» &b/quest list &7- wyswietla liste wszystkich zadan");
        user.sendMessage(" &8» &b/quest progress &7- wyswietla Twoj postep w zadaniu");
        user.sendMessage(" &8» &b/quest rewards <numer zadania> &7- wyswietla nagrody za zadanie");
        user.sendMessage(" &8» &b/quest cancel &7- anuluje obecne zadanie");
        user.sendMessage(" &8» &b/quest inbox &7- nagrody za wykonane zadania");
        user.sendMessage(" ");
    }

    private void showAvailableQuests(Player sender)
    {
        User user = User.get(sender.getUniqueId());
        int size = (int) Math.ceil((double) QuestUtils.getQuests().size() / 9) * 9;
        Inventory questGui = Bukkit.createInventory(null, size, ChatUtils.colored("&3Dostepne zadania"));
        List<Quest> sortedQuests = new ArrayList<>();
        for(int i = 0; i < QuestUtils.getQuests().size(); i++)
        {
            Quest outputQuest = QuestUtils.getQuests().get(i);
            sortedQuests.add(outputQuest);
            Collections.sort(sortedQuests);

            int amount = outputQuest.getObjective().getAmount();
            List<String> questDescription = new ArrayList<>();
            questDescription.add(" ");
            questDescription.add(" &bNumer zadania: &f" + outputQuest.getOrder());
            questDescription.add(" &bOpis: &fChcialbym kiedys zjesc takiego dobrego kebaba bardzo hehe");
            questDescription.add(" &bCel: &f" + amount + "x " + outputQuest.getObjective().getType().name());
            questDescription.add(" &bNagrody: &f/quest rewards " + outputQuest.getId());
            questDescription.add(" &bStatus: " + (user.meetsRequirements(outputQuest) ? "&aDostepny" : "&cNiedostepny"));

            ItemStack questItem = new ItemBuilder(Material.BOOK)
                    .setName("&3Zadanie: &f" + outputQuest.getId(), true)
                    .setLore(questDescription)
                    .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10)
                    .setFlag(ItemFlag.HIDE_ENCHANTS).getItem();

            questGui.setItem(i, questItem);
        }
        sender.openInventory(questGui);
    }

    private void showQuestProgress(Player sender)
    {
        User user = User.get(sender.getUniqueId());
        if(!user.hasQuest())
        {
            user.sendMessage("&7Aktualnie nie jestes w trakcie robienia zadania.");
            return;
        }
        int progress = user.getProgress();
        int goal = user.getQuest().getRequiredAmount();
        String percent = user.progressPercentage();
        user.sendMessage("&7Twoj postep w zadaniu &f" + user.getQuest().getId()
                + " &7to &b" + progress + "&7/" + goal + " &7(&b" + percent + "%&7).");
    }

    private void cancelQuest(Player sender)
    {
        User user = User.get(sender.getUniqueId());
        if(!user.hasQuest())
        {
            user.sendMessage("&7Aktualnie nie jestes w trakcie robienia zadania.");
            return;
        }
        user.sendMessage("&7Anulowales swoj postep w zadaniu &f" + user.getQuest().getId() + "&7.");
        user.cancelQuest();
        sender.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    private void showInbox(Player sender)
    {
        User user = User.get(sender.getUniqueId());
        if(!user.hasInbox())
        {
            user.sendMessage("&7Nie posiadasz zadnych zaleglych nagród w schowku.");
            return;
        }
        Inventory rewardGui = Bukkit.createInventory(sender, 54, ChatUtils.colored("&3Schowek"));
        for(int i = 0; i < user.getTemporaryInbox().size(); i++)
        {
            ItemStack reward = user.getTemporaryInbox().get(i);
            rewardGui.setItem(i, reward);
        }
        sender.openInventory(rewardGui);
    }

    private void showRewards(Player sender, String[] args)
    {
        User user = User.get(sender.getUniqueId());
        if(isInteger(args[1]))
        {
            int questOrder = Integer.parseInt(args[1]);
            if (Quest.get(questOrder) == null) {
                user.sendMessage("&cTakie zadanie nie istnieje");
                return;
            }
            Quest quest = Quest.get(questOrder);
            if(quest.getRewards().isEmpty() || quest.getRewards() == null) {
                user.sendMessage("&cZa wykonanie tego zadania nie ma nagród.");
                return;
            }
            List<ItemStack> questRewards = quest.getRewards();
            int size = (int) Math.ceil((double) questRewards.size() / 9) * 9;

            Inventory rewardGui = Bukkit.createInventory(sender, size, ChatUtils.colored("&3Nagrody: &7" + quest.getId()));
            for (int i = 0; i < questRewards.size(); i++) {
                ItemStack eachReward = questRewards.get(i);
                rewardGui.setItem(i, eachReward);
            }
            sender.openInventory(rewardGui);
        }
    }

    private void reload(Player sender)
    {
        User user = User.get(sender.getUniqueId());
        PluginConfiguration configuration = QuestPlugin.getInstance().configuration;
        configuration.loadConfiguration();

        user.sendMessage("&7Pomyslnie przeladowano.");
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        }
        return true;
    }*/
