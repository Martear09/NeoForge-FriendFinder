package net.martear.Moddh.item;

import net.martear.Moddh.Moddh;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS  = DeferredRegister.createItems(Moddh.MOD_ID);

    public static final DeferredItem<Item>  mysteryseed = ITEMS.register("mysteryseed",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item>  aqsus = ITEMS.register("aqsus",
            () -> new Item(new Item.Properties()));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}