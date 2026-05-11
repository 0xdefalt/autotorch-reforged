package io.github.defalt.autotorch.client.torch;

import com.google.common.collect.ImmutableSet;
import io.github.defalt.autotorch.client.config.AutoTorchConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public final class AutoTorchPlacement {

    private static final ImmutableSet<Item> TORCH_ITEMS = ImmutableSet.of(Items.TORCH, Items.SOUL_TORCH);

    private AutoTorchPlacement() {
        // TODO: not yet implemented
    }

    public static void tryPlace(Minecraft client, AutoTorchConfig config) {
        if (client.player == null || client.level == null || client.gameMode == null) {
            return;
        }
        if (!TORCH_ITEMS.contains(client.player.getOffhandItem().getItem())) {
            return;
        }
        BlockPos blockPos = client.player.blockPosition();
        if (client.level.getBrightness(LightLayer.BLOCK, blockPos) < config.lightLevel && canPlaceTorch(client, blockPos)) {
            offHandRightClickBlock(client, config, blockPos);
        }
    }

    private static void offHandRightClickBlock(Minecraft client, AutoTorchConfig config, BlockPos pos) {
        Vec3 vec3 = Vec3.atBottomCenterOf(pos);
        if (config.accuratePlacement) {
            ServerboundMovePlayerPacket.Rot packet = new ServerboundMovePlayerPacket.Rot(client.player.getYRot(), 90.0F, true, false);
            client.player.connection.send(packet);
        }
        client.gameMode.useItemOn(client.player, InteractionHand.OFF_HAND, new BlockHitResult(vec3, Direction.DOWN, pos, false));
        client.gameMode.useItem(client.player, InteractionHand.OFF_HAND);
    }

    private static boolean canPlaceTorch(Minecraft client, BlockPos pos) {
        return client.level.getBlockState(pos).getFluidState().isEmpty() && Block.canSupportCenter(client.level, pos.below(), Direction.UP);
    }

}