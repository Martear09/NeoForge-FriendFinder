package net.martear.friendfinder.registry;

import net.martear.friendfinder.friendfinder;
import net.martear.friendfinder.item.friendfinderitem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(BuiltInRegistries.ITEM, friendfinder.MODID);

    public static final DeferredHolder<Item, friendfinderitem> FRIEND_FINDER = ITEMS.register(
            "friend_finder",
            () -> new friendfinderitem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.UNCOMMON))
    );
}
