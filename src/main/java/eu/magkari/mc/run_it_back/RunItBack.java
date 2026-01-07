package eu.magkari.mc.run_it_back;

import eu.magkari.mc.run_it_back.command.RIBCommand;
import eu.magkari.mc.run_it_back.config.RIBConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.serializers.TomlSerializer;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RunItBack implements ModInitializer {
    public static final String MOD_ID = "run-it-back";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final RIBConfig CONFIG = RIBConfig.createToml(FabricLoader.getInstance().getConfigDir(), "", MOD_ID, RIBConfig.class);

    public static List<Block> BLOCKS = new ArrayList<>();
    public static List<TagKey<Block>> TAGS = new ArrayList<>();

    private final Map<BlockPos, Integer> cache = new HashMap<>();

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(RIBCommand::register);

        fillLists(false);

        PlayerBlockBreakEvents.BEFORE.register((level, player, blockPos,  blockState, blockEntity) -> {
            if (!CONFIG.enabled.value() || player.getAbilities().instabuild && CONFIG.creative.value() || Permissions.check(player, CONFIG.bypassPermission.value())) return true;

            if (BLOCKS.contains(blockState.getBlock())) {
                return handle(blockPos);
            } else {
                for (TagKey<Block> tag : TAGS) {
                    if (blockState.getBlock().builtInRegistryHolder().is(tag)) {
                        return handle(blockPos);
                    }
                }
            }

            return true;
        });
    }

    private boolean handle(BlockPos blockPos) {
        if (cache.containsKey(blockPos)) {
            if (cache.get(blockPos) < CONFIG.count.value() - 1) {
                cache.put(blockPos, cache.get(blockPos) + 1);
                return false;
            } else {
                cache.remove(blockPos);
                return true;
            }
        } else {
            cache.put(blockPos, 1);
            return false;
        }
    }

    public static boolean fillLists(boolean reload) {
        if (reload) {
            try {
                TomlSerializer.INSTANCE.deserialize(CONFIG, Files.newInputStream(FabricLoader.getInstance().getConfigDir().resolve(MOD_ID + ".toml")));
            } catch (IOException e) {
                LOGGER.error("Failed to load from file: ", e);
                return false;
            }
        }

        for (RIBConfig.TagOrBlock entry : CONFIG.list.value()) {
            boolean block = entry.type() == RIBConfig.Type.BLOCK;

            if (block) BLOCKS.add(BuiltInRegistries.BLOCK.getValue(entry.id()));
            else TAGS.add(TagKey.create(Registries.BLOCK, entry.id()));
        }
        return true;
    }
}
