package net.mistrifix.main.base.quest;

import net.mistrifix.main.QuestPlugin;
import net.mistrifix.main.utils.ItemUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

public class Quest implements Comparable<Quest>{

    private String id;
    private int order;
    private List<ItemStack> rewards;
    private String instruction;
    private ItemStack objective;

    private List<String> rewardsAsString;
    private String objectiveAsString;

    public Quest(String id)
    {
        this.id = id;
        this.order = QuestUtils.calculateOrder();
        QuestUtils.addQuest(this);
    }

    private Quest(String id, int order)
    {
        this.id = id;
        this.order = order;
        QuestUtils.addQuest(this);
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public int getOrder()
    {
        return order;
    }

    public void setOrder(int order)
    {
        this.order = order;
    }

    public static Quest create(String id, int order)
    {
        return new Quest(id, order);
    }

    public static Quest get(String name)
    {
        return QuestUtils.getQuest(name);
    }

    public static Quest get(int order)
    {
        return QuestUtils.getQuest(order);
    }

    public List<ItemStack> getRewards() {
        try
        {
            this.rewards = ItemUtils.loadItemStackList(this.rewardsAsString);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return this.rewards;
    }

    public void setRewards(List<ItemStack> items)
    {
        this.rewards = items;
    }

    public List<String> getRewardsAsString()
    {
        return rewardsAsString;
    }

    public void setRewardsAsString(List<String> items)
    {
        this.rewardsAsString = items;
    }

    public ItemStack getObjective()
    {
        try
        {
            this.objective = ItemUtils.parseItem(this.objectiveAsString);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return objective;
    }

    public void setObjective(ItemStack is)
    {
        this.objective = is;
    }

    public int getRequiredAmount()
    {
        return this.objective.getAmount();
    }

    public String getInstruction()
    {
        if(instruction == null || instruction.isEmpty())
        {
            return QuestUtils.defaultInstruction;
        }
        return instruction;
    }

    public void setInstruction(String instruction)
    {
        if(instruction == null || instruction.isEmpty())
        {
            this.instruction = QuestUtils.defaultInstruction;
        }
        this.instruction = instruction;
    }

    public String getObjectiveAsString()
    {
        return objectiveAsString;
    }

    public void setObjectiveAsString(String objective)
    {
        this.objectiveAsString = objective;
    }

    public void save()
    {
        String questPath = QuestPlugin.getInstance().getDataFolder() + File.separator + "quests" + File.separator + this.id + ".yml";
        File questFile = new File(questPath);
        if(!questFile.exists())
        {
            try
            {
                questFile.createNewFile();
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(questFile);
        config.set("id", this.id);
        config.set("order", this.order);
        config.set("objective", this.objectiveAsString);
        config.set("instruction", this.getInstruction());
        config.set("rewards", this.rewardsAsString);
        try
        {
            config.save(questFile);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public int compareTo(Quest quest)
    {
        return quest.getOrder() - this.order;
    }






}
