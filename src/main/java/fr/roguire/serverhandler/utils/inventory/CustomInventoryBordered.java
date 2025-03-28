package fr.roguire.serverhandler.utils.inventory;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class CustomInventoryBordered extends CustomInventory {

    protected ItemStack borderItem;

    public CustomInventoryBordered(int size, Material border, Component component) {
        super(size, component);
        borderItem = new ItemStack(border);
        fillBorders();
    }

    private void fillBorders() {
        ItemMeta meta = borderItem.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.empty());
            borderItem.setItemMeta(meta);
        }
        int invSize = this.inventory.getSize();
        for(int i = 0; i < 9; i++) {
            inventory.setItem(i, borderItem);
            inventory.setItem(invSize-1-i, borderItem);
            if(i*9<invSize)
                inventory.setItem(i*9, borderItem);
            if(invSize-1-i*9>0)
                inventory.setItem(invSize-1-i*9, borderItem);
        }
    }
}
