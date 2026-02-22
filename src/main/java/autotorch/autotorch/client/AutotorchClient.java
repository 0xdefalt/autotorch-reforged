package autotorch.autotorch.client;

import com.google.common.collect.ImmutableSet;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class AutoTorchClient implements ClientModInitializer {

    private MinecraftClient client;
    public ConfigHolder<ModConfig> CONFIG;
    private ModConfig CDATA;
    private static final ImmutableSet<Item> TorchSet = ImmutableSet.of(Items.TORCH, Items.SOUL_TORCH);
    private static final KeyBinding.Category AUTOTORCH_CATEGORY = KeyBinding.Category.create(Identifier.of("autotorch", "main"));
    private static final KeyBinding AutoPlaceBinding = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                    "autotorch.autotorch.toggle",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_LEFT_ALT,
                    AUTOTORCH_CATEGORY
            )
    );

    @Override
    public void onInitializeClient() {
        this.client = MinecraftClient.getInstance();
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
        CONFIG = AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        CDATA = CONFIG.getConfig();
        CONFIG.registerLoadListener((manager, data) -> {
            CDATA = data;
            return ActionResult.SUCCESS;
        });
    }

    public void tick(MinecraftClient client) {
        if (client.player != null && client.world != null) {
            if (AutoPlaceBinding.wasPressed()) {
                CDATA.enabled = !CDATA.enabled;
                var message = CDATA.enabled ? Text.translatable("autotorch.message.enabled") : Text.translatable("autotorch.message.disabled");
                client.player.sendMessage(message, false);
            }
            if (!CDATA.enabled) {
                return;
            }
            if (!TorchSet.contains(client.player.getOffHandStack().getItem())) {
                return;
            }
            BlockPos PlayerBlock = client.player.getBlockPos();
            if (client.world.getLightLevel(LightType.BLOCK, PlayerBlock) < CDATA.lightLevel && canPlaceTorch(PlayerBlock)) {
                offHandRightClickBlock(PlayerBlock);
            }
        }
    }

    private void offHandRightClickBlock(BlockPos pos) {
        Vec3d vec3d = Vec3d.ofBottomCenter(pos);
        if (CDATA.accuratePlacement) {
            PlayerMoveC2SPacket.LookAndOnGround packet = new PlayerMoveC2SPacket.LookAndOnGround(client.player.getYaw(), 90.0F, true, false);
            client.player.networkHandler.sendPacket(packet);
        }
        client.interactionManager.interactBlock(client.player, Hand.OFF_HAND, new BlockHitResult(vec3d, Direction.DOWN, pos, false));
        client.interactionManager.interactItem(client.player, Hand.OFF_HAND);
    }

    public boolean canPlaceTorch(BlockPos pos) {
        return (client.world.getBlockState(pos).getFluidState().isEmpty() && Block.sideCoversSmallSquare(client.world, pos.down(), Direction.UP));
    }

}