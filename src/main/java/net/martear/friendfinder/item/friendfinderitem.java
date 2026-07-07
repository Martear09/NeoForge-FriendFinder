package net.martear.friendfinder.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.UUID;

public class friendfinderitem extends Item {

    private static final String TAG_BOUND_UUID = "BoundPlayerUUID";
    private static final String TAG_BOUND_NAME = "BoundPlayerName";

    // Update the glow duration to 8 seconds (8 * 20 ticks)
    private static final int GLOW_DURATION_TICKS = 8 * 20;
    private static final int COOLDOWN_TICKS = 5 * 20;       // 5 second cooldown per use

    public friendfinderitem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        if (player.level().isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (target == player) {
            player.displayClientMessage(
                    Component.literal("You can't bind this to yourself.").withStyle(ChatFormatting.RED), true);
            return InteractionResult.FAIL;
        }

        if (!(target instanceof ServerPlayer targetPlayer)) {
            player.displayClientMessage(
                    Component.literal("You can only bind this to a player.").withStyle(ChatFormatting.RED), true);
            return InteractionResult.FAIL;
        }

        // Check if the item is already bound
        UUID boundId = getBoundUUID(stack);
        if (boundId != null) {
            player.displayClientMessage(
                    Component.literal("This finder is already bound to a player.")
                            .withStyle(ChatFormatting.RED),
                    true);
            return InteractionResult.FAIL;
        }

        bindPlayer(stack, targetPlayer);
        player.setItemInHand(hand, stack);

        player.displayClientMessage(
                Component.literal("Finder bound to " + targetPlayer.getName().getString() + ".")
                        .withStyle(ChatFormatting.GREEN),
                true);
        player.level().playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME,
                SoundSource.PLAYERS, 1.0F, 1.2F);

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            return InteractionResultHolder.pass(stack);
        }

        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }

        UUID boundId = getBoundUUID(stack);
        if (boundId == null) {
            player.displayClientMessage(
                    Component.literal("Shift + right-click a player first to bind this finder to them.")
                            .withStyle(ChatFormatting.YELLOW),
                    true);
            return InteractionResultHolder.fail(stack);
        }

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        ServerPlayer target = level.getServer() == null ? null : level.getServer().getPlayerList().getPlayer(boundId);

        if (target == null || !target.isAlive()) {
            String name = getBoundName(stack);
            player.displayClientMessage(
                    Component.literal((name != null ? name : "That player") + " isn't online right now.")
                            .withStyle(ChatFormatting.RED),
                    true);
            return InteractionResultHolder.fail(stack);
        }

        target.addEffect(new MobEffectInstance(MobEffects.GLOWING, GLOW_DURATION_TICKS, 0, false, false, true));

        level.playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE,
                SoundSource.PLAYERS, 0.6F, 1.4F);

        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        String name = getBoundName(stack);
        if (name != null) {
            tooltip.add(Component.literal("Bound to: " + name).withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.literal("Right-click to make them glow").withStyle(ChatFormatting.DARK_GRAY));
        } else {
            tooltip.add(Component.literal("Shift + right-click a player to bind").withStyle(ChatFormatting.GRAY));
        }
    }

    private static void bindPlayer(ItemStack stack, ServerPlayer target) {
        CompoundTag tag = new CompoundTag();
        CustomData existing = stack.get(DataComponents.CUSTOM_DATA);
        if (existing != null) {
            tag = existing.copyTag();
        }
        tag.putUUID(TAG_BOUND_UUID, target.getUUID());
        tag.putString(TAG_BOUND_NAME, target.getName().getString());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static UUID getBoundUUID(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return null;
        CompoundTag tag = data.copyTag();
        return tag.hasUUID(TAG_BOUND_UUID) ? tag.getUUID(TAG_BOUND_UUID) : null;
    }

    public static String getBoundName(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return null;
        CompoundTag tag = data.copyTag();
        return tag.contains(TAG_BOUND_NAME) ? tag.getString(TAG_BOUND_NAME) : null;
    }
}