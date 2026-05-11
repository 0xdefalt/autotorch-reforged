package io.github.defalt.autotorch.client.torch;

import com.google.common.collect.ImmutableSet;
import io.github.defalt.autotorch.client.config.AutoTorchConfig;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;

public final class AutoTorchPlacement {

    private static final ImmutableSet<Item> TORCH_ITEMS = ImmutableSet.of(Items.TORCH, Items.SOUL_TORCH);

    private AutoTorchPlacement() {
        // TODO: not yet implemented
    }

    public static void tryPlace(MinecraftClient client, AutoTorchConfig config) {
        if (client.player == null || client.world == null || client.interactionManager == null) {
            return;
        }
        if (!TORCH_ITEMS.contains(client.player.getOffHandStack().getItem())) {
            return;
        }

        BlockPos playerBlock = client.player.getBlockPos();
        if (client.world.getLightLevel(LightType.BLOCK, playerBlock) < config.lightLevel && canPlaceTorch(client, playerBlock)) {
            offHandRightClickBlock(client, config, playerBlock);
        }
    }

    private static void offHandRightClickBlock(MinecraftClient client, AutoTorchConfig config, BlockPos pos) {
        Vec3d vec3d = Vec3d.ofBottomCenter(pos);
        if (config.accuratePlacement) {
            PlayerMoveC2SPacket.LookAndOnGround packet = new PlayerMoveC2SPacket.LookAndOnGround(client.player.getYaw(), 90.0F, true, false);
            client.player.networkHandler.sendPacket(packet);
        }
        client.interactionManager.interactBlock(client.player, Hand.OFF_HAND, new BlockHitResult(vec3d, Direction.DOWN, pos, false));
        client.interactionManager.interactItem(client.player, Hand.OFF_HAND);
    }

    private static boolean canPlaceTorch(MinecraftClient client, BlockPos pos) {
        return client.world.getBlockState(pos).getFluidState().isEmpty() && Block.sideCoversSmallSquare(client.world, pos.down(), Direction.UP);
    }

}