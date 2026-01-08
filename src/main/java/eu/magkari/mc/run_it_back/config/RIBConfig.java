package eu.magkari.mc.run_it_back.config;

import folk.sisby.kaleido.api.ReflectiveConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;
import folk.sisby.kaleido.lib.quiltconfig.api.values.ComplexConfigValue;
import folk.sisby.kaleido.lib.quiltconfig.api.values.ConfigSerializableObject;
import folk.sisby.kaleido.lib.quiltconfig.api.values.TrackedValue;
import folk.sisby.kaleido.lib.quiltconfig.api.values.ValueList;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;

import java.util.List;

public class RIBConfig extends ReflectiveConfig {
    @Comment("The toggle.")
    @Comment("Whether the mod is enabled")
    public final TrackedValue<Boolean> enabled = this.value(true);

    @Comment("The count section.")
    public final Count count = new Count();
    public static class Count extends Section {
        @Comment("The base count.")
        @Comment("How many times one needs to mine a block before it finally works.")
        public final TrackedValue<Integer> count = this.value(4);


        @Comment("The randomness.")
        @Comment("Enable deterministic randomness for block breaking attempts")
        public final TrackedValue<Boolean> randomnessEnabled = this.value(true);

        @Comment("The random variance.")
        @Comment("The +/- variance applied to the count. Example: count=4, variance=2 means 2-6 attempts needed")
        public final TrackedValue<Integer> randomVariance = this.value(2);

        @Comment("The random formula.")
        @Comment("Formula type for calculating attempts")
        @Comment("MODULO - Prime-based spatial hashing, evenly distributed")
        @Comment("HASH - Java-style hash mixing, well-distributed randomness")
        @Comment("SINE - Wave-based patterns, smooth gradients across blocks")
        public final TrackedValue<FormulaType> randomFormula = this.value(FormulaType.MODULO);
    }

    @Comment("The permissions section.")
    public final Permissions permissions = new Permissions();
    public static class Permissions extends Section {
        @Comment("The creative.")
        @Comment("Whether creative mode players bypass the annoyance")
        public final TrackedValue<Boolean> creative = this.value(true);

        @Comment("The bypass permission.")
        @Comment("The permission for which to check wheter one can bypass the annoyance")
        public final TrackedValue<String> bypassPermission = this.value("run-it-back.bypass");

        @Comment("The permission.")
        @Comment("The permission for which to check whether one can run the command")
        public final TrackedValue<String> permission = this.value("run-it-back.command");
    }

    @Comment("The list.")
    @Comment("Use #id:tag for blocktags, and #id:block for blocks.")
    public final TrackedValue<List<TagOrBlock>> list = this.value(ValueList.create(new TagOrBlock(Identifier.withDefaultNamespace("diamond_ore"), Type.BLOCK), new TagOrBlock(Identifier.withDefaultNamespace("diamond_ore"), Type.BLOCK)));

    public int getAttemptsForBlock(BlockPos pos) {
        if (!count.randomnessEnabled.value()) {
            return count.count.value();
        }

        int variance = count.randomVariance.value();
        int baseCount = count.count.value();
        int offset;

        switch (count.randomFormula.value()) {
            case MODULO:
                offset = Math.abs((pos.getX() * 73856093 ^ pos.getY() * 19349663 ^ pos.getZ() * 83492791)) % (variance * 2 + 1) - variance;
                break;

            case HASH:
                int hash = pos.getX();
                hash = 31 * hash + pos.getY();
                hash = 31 * hash + pos.getZ();
                hash = hash ^ (hash >>> 16);
                offset = Math.abs(hash) % (variance * 2 + 1) - variance;
                break;

            case SINE:
                double angle = (pos.getX() * 0.1 + pos.getY() * 0.2 + pos.getZ() * 0.15);
                double sineValue = Math.sin(angle) * Math.cos(angle * 1.3);
                offset = (int) Math.round(sineValue * variance);
                break;

            default:
                offset = 0;
        }

        return Math.max(1, baseCount + offset);
    }

    public enum FormulaType {
        MODULO,
        HASH,
        SINE
    }

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