package eu.magkari.mc.run_it_back.config;

import folk.sisby.kaleido.api.ReflectiveConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;
import folk.sisby.kaleido.lib.quiltconfig.api.values.ComplexConfigValue;
import folk.sisby.kaleido.lib.quiltconfig.api.values.ConfigSerializableObject;
import folk.sisby.kaleido.lib.quiltconfig.api.values.TrackedValue;
import folk.sisby.kaleido.lib.quiltconfig.api.values.ValueList;
import net.minecraft.resources.Identifier;

import java.util.List;

public class RIBConfig extends ReflectiveConfig {
    @Comment("The toggle.")
    @Comment("Whether the mod is enabled")
    public final TrackedValue<Boolean> enabled = this.value(true);

    @Comment("The count.")
    @Comment("How many times one needs to mine a block before it finally works.")
    @Comment("Example: count = 4, 1: fail, 2: fail, 3: fail, 4: works")
    public final TrackedValue<Integer> count = this.value(4);

    @Comment("The creative.")
    @Comment("Whether creative mode players bypass the annoyance")
    public final TrackedValue<Boolean> creative = this.value(true);

    @Comment("The bypass permission.")
    @Comment("The permission for which to check wheter one can bypass the annoyance")
    public final TrackedValue<String> bypassPermission = this.value("run-it-back.bypass");

    @Comment("The permission.")
    @Comment("The permission for which to check whether one can run the command")
    public final TrackedValue<String> permission = this.value("run-it-back.command");

    @Comment("The list.")
    @Comment("Use #id:tag for blocktags, and #id:block for blocks.")
    public final TrackedValue<List<TagOrBlock>> list = this.value(ValueList.create(new TagOrBlock(Identifier.withDefaultNamespace("diamond_ore"), Type.BLOCK), new TagOrBlock(Identifier.withDefaultNamespace("diamond_ore"), Type.BLOCK)));

    public record TagOrBlock(Identifier id, Type type) implements ConfigSerializableObject<String> {
        @Override
        public ConfigSerializableObject<String> convertFrom(String s) {
            if (s.charAt(0) == '#') return new TagOrBlock(Identifier.tryParse(s.substring(1)), Type.TAG);
            return new TagOrBlock(Identifier.tryParse(s), Type.BLOCK);
        }

        @Override
        public String getRepresentation() {
            return type == Type.BLOCK ? id.toString() : "#" + id.toString();
        }

        @Override
        public ComplexConfigValue copy() {
            return new TagOrBlock(id, type);
        }
    }

    public enum Type {
        BLOCK,
        TAG
    }
}
