package net.martear.Moddh.item;

import net.martear.Moddh.Moddh;
import net.martear.Moddh.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Moddh.MOD_ID);

    public static final Supplier<CreativeModeTab> AQSUS_ITEMS_TAB = CREATIVE_MODE_TAB.register("aqsus_items_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.aqsus.get()))
                    .title(Component.translatable("creativetab.moddh.aqsus_items"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.aqsus);
                    }).build());

    public static final Supplier<CreativeModeTab> AQSUS_BLOCK_TAB = CREATIVE_MODE_TAB.register("aqsus_blocks_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.AQSUS_BLOCK))
                    .withTabsBefore(ResourceLocation.fromNamespaceAndPath(Moddh.MOD_ID, "aqsus_items_tab"))
                    .title(Component.translatable("creativetab.moddh.aqsus_blocks"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModBlocks.AQSUS_BLOCK);
                        output.accept(ModBlocks.AQSUS_ORE);
                    }).build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}