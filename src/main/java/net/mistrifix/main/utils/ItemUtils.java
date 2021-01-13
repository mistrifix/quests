package net.mistrifix.main.utils;

import net.mistrifix.main.base.user.User;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ItemUtils {

    public static ItemStack parseItem(String string)
    {
        String[] split = string.split(" ");
        String[] typeSplit = split[1].split(":");
        String subtype = typeSplit.length > 1 ? typeSplit[1] : "0";

        Material mat = Material.matchMaterial(typeSplit[0]);

        int stack;
        int data;

        try {
            stack = Integer.parseInt(split[0]);
            data = Integer.parseInt(subtype);
        } catch (NumberFormatException e) {
            stack = 1;
            data = 0;
        }

        ItemBuilder item = new ItemBuilder(mat, stack, data);

        for (int i = 2; i < split.length; i++) {
            String str = split[i];

            if (str.contains("name")) {
                String[] splitName = str.split(":");
                item.setName(StringUtils.replace(ChatUtils.colored(String.join(":", Arrays.copyOfRange(splitName, 1, splitName.length))), "_", " "), true);
            } else if (str.contains("lore")) {
                String[] splitLore = str.split(":");
                String loreArgs = String.join(":", Arrays.copyOfRange(splitLore, 1, splitLore.length));
                String[] lores = loreArgs.split("#");
                List<String> lore = new ArrayList<>();

                for (String s : lores) {
                    lore.add(StringUtils.replace(StringUtils.replace(ChatUtils.colored(s), "_", " "), "{HASH}", "#"));
                }

                item.setLore(lore);
            } else if (str.contains("enchant")) {
                String[] parse = str.split(":");
                String enchantName = parse[1];
                int level;

                try {
                    level = Integer.parseInt(parse[2]);
                } catch (NumberFormatException e) {
                    level = 1;
                }

                Enchantment enchant = Enchantment.getByName(enchantName.toUpperCase());
                if (enchant == null) {
                    return null;
                }

                item.addEnchant(enchant, level);
            } else if (str.contains("skullowner")) {
                if (item.getMeta() instanceof SkullMeta) {
                    ((SkullMeta) item.getMeta()).setOwner(str.split(":")[1]);
                    item.refreshMeta();
                }
            } else if (str.contains("armorcolor")) {
                if (item.getMeta() instanceof LeatherArmorMeta) {
                    String[] color = str.split(":")[1].split("_");

                    try {
                        ((LeatherArmorMeta) item.getMeta()).setColor(Color.fromRGB(Integer.parseInt(color[0]),
                                Integer.parseInt(color[1]), Integer.parseInt(color[2])));
                        item.refreshMeta();
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
            }
        }
        return item.getItem();
    }

    public static List<ItemStack> loadItemStackList(List<String> strings) {
        List<ItemStack> items = new ArrayList<>();
        for (String item : strings) {
            if (item == null || "".equals(item)) {
                continue;
            }

            ItemStack itemstack = ItemUtils.parseItem(item);
            if (itemstack != null) {
                items.add(itemstack);
            }
            return null;
        }

        return items;
    }

    public static ItemStack[] toArray(Collection<ItemStack> collection) {
        return collection.toArray(new ItemStack[0]);
    }

    public static boolean isEnough(User user, ItemStack item)
    {
        return user.getPlayer().getInventory().containsAtLeast(item, item.getAmount());
    }
}
