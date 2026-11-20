/**
 * Copyright (c) 2024 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 **/

package rpggods.data.perk.action;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.ForgeRegistries;
import rpggods.RGRegistry;

import java.util.Locale;

/**
 * Passive attribute modifier granted while the perk's favor range is met.
 * <p>
 * The attribute is stored as a {@link ResourceLocation} and resolved at runtime, so a
 * data pack that references an attribute from an optional mod still loads when that mod
 * is not installed. In that case the action simply has no effect.
 * <p>
 * Attribute actions are not executed by the regular perk dispatcher. Instead they are
 * reconciled by {@link rpggods.util.AttributeModifiers} whenever favor levels change (or
 * the player logs in / respawns / the data pack reloads).
 */
public class AttributeAction extends PerkAction {

    public static final Codec<AttributeModifier.Operation> OPERATION_CODEC = Codec.either(Codec.INT, Codec.STRING)
            .flatXmap(either -> either.map(AttributeAction::operationFromId, AttributeAction::operationFromName),
                    operation -> DataResult.success(Either.left(operation.ordinal())));

    public static final Codec<AttributeAction> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
            .and(ResourceLocation.CODEC.fieldOf("attribute").forGetter(o -> o.attribute))
            .and(Codec.DOUBLE.fieldOf("amount").forGetter(o -> o.amount))
            .and(OPERATION_CODEC.optionalFieldOf("operation", AttributeModifier.Operation.ADDITION).forGetter(o -> o.operation))
            .apply(instance, AttributeAction::new));

    private final ResourceLocation attribute;
    private final double amount;
    private final AttributeModifier.Operation operation;

    public AttributeAction(boolean isHidden, ResourceLocation attribute, double amount, AttributeModifier.Operation operation) {
        super(isHidden);
        this.attribute = attribute;
        this.amount = amount;
        this.operation = operation;
    }

    /**
     * Attribute modifiers are applied continuously by {@link rpggods.util.AttributeModifiers},
     * not by the regular perk dispatcher.
     *
     * @param context the action context
     * @return false, always
     */
    @Override
    public boolean apply(PerkActionContext context) {
        return false;
    }

    @Override
    public Component createDescription(RegistryAccess registryAccess) {
        final Component attributeName;
        final Attribute resolved = ForgeRegistries.ATTRIBUTES.getValue(this.attribute);
        if (resolved != null) {
            attributeName = Component.translatable(resolved.getDescriptionId());
        } else {
            attributeName = Component.literal(this.attribute.toString());
        }
        final Component amountText = Component.literal(formatAmount(this.amount, this.operation))
                .withStyle(this.amount >= 0.0D ? ChatFormatting.GREEN : ChatFormatting.RED);
        return Component.translatable(PREFIX + "attribute" + SUFFIX, amountText, attributeName);
    }

    @Override
    public Codec<? extends PerkAction> getCodec() {
        return RGRegistry.PerkActionReg.ATTRIBUTE.get();
    }

    public ResourceLocation getAttribute() {
        return attribute;
    }

    public double getAmount() {
        return amount;
    }

    public AttributeModifier.Operation getOperation() {
        return operation;
    }

    /**
     * @param operation the attribute modifier operation
     * @return the serialized name used by data packs, for example {@code add_multiplied_base}
     */
    public static String operationName(final AttributeModifier.Operation operation) {
        return switch (operation) {
            case ADDITION -> "add_value";
            case MULTIPLY_BASE -> "add_multiplied_base";
            case MULTIPLY_TOTAL -> "add_multiplied_total";
        };
    }

    private static DataResult<AttributeModifier.Operation> operationFromId(final int id) {
        final AttributeModifier.Operation[] values = AttributeModifier.Operation.values();
        if (id < 0 || id >= values.length) {
            return DataResult.error(() -> "Unknown attribute operation id: " + id);
        }
        return DataResult.success(values[id]);
    }

    private static DataResult<AttributeModifier.Operation> operationFromName(final String name) {
        for (AttributeModifier.Operation operation : AttributeModifier.Operation.values()) {
            if (operationName(operation).equals(name)) {
                return DataResult.success(operation);
            }
        }
        // accept a few common aliases as well
        return switch (name) {
            case "addition", "add" -> DataResult.success(AttributeModifier.Operation.ADDITION);
            case "multiply_base", "multiply" -> DataResult.success(AttributeModifier.Operation.MULTIPLY_BASE);
            case "multiply_total" -> DataResult.success(AttributeModifier.Operation.MULTIPLY_TOTAL);
            default -> DataResult.error(() -> "Unknown attribute operation: " + name);
        };
    }

    /**
     * @param amount the attribute amount
     * @param operation the modifier operation
     * @return a signed amount, formatted as a percentage for multiplicative operations
     */
    private static String formatAmount(final double amount, final AttributeModifier.Operation operation) {
        final String sign = amount >= 0.0D ? "+" : "";
        if (operation == AttributeModifier.Operation.ADDITION) {
            return sign + trim(amount);
        }
        return sign + trim(amount * 100.0D) + "%";
    }

    private static String trim(final double value) {
        return String.format(Locale.ROOT, "%.2f", value)
                .replaceAll("0*$", "")
                .replaceAll("\\.$", "");
    }
}
