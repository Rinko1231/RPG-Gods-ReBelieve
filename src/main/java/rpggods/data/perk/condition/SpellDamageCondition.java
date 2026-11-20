/**
 * Copyright (c) 2024 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 **/

package rpggods.data.perk.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import rpggods.RGRegistry;
import rpggods.util.RGComponentUtils;

import java.util.Optional;

/**
 * Matches when a spell deals damage. {@code spell} limits the condition to one spell id and
 * {@code min_amount} / {@code max_amount} limit it to a damage range (in half hearts).
 * <p>
 * Note that the amount is the pre-armor amount from the spell damage event.
 */
public class SpellDamageCondition extends PerkCondition {

    public static final Codec<SpellDamageCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("spell").forGetter(o -> o.spell),
            Codec.INT.optionalFieldOf("min_amount").forGetter(o -> o.minAmount),
            Codec.INT.optionalFieldOf("max_amount").forGetter(o -> o.maxAmount)
    ).apply(instance, SpellDamageCondition::new));

    private final Optional<ResourceLocation> spell;
    private final Optional<Integer> minAmount;
    private final Optional<Integer> maxAmount;

    public SpellDamageCondition(Optional<ResourceLocation> spell, Optional<Integer> minAmount, Optional<Integer> maxAmount) {
        this.spell = spell;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }

    @Override
    public boolean test(PerkConditionContext context) {
        if (context.getAmount().isEmpty()) {
            return false;
        }
        final double amount = context.getAmount().get();
        if (minAmount.isPresent() && amount < minAmount.get()) {
            return false;
        }
        if (maxAmount.isPresent() && amount > maxAmount.get()) {
            return false;
        }
        if (spell.isPresent() && !context.getData().map(spell.get()::equals).orElse(false)) {
            return false;
        }
        return true;
    }

    @Override
    public Component createDescription(RegistryAccess registryAccess) {
        final Component bounds = RGComponentUtils.createBoundsComponent(minAmount.orElse(null), maxAmount.orElse(null));
        if (spell.isPresent()) {
            return Component.translatable(PREFIX + "spell_damage.specific", bounds, SpellCastCondition.spellName(spell.get()));
        }
        return Component.translatable(PREFIX + "spell_damage", bounds);
    }

    @Override
    public Codec<? extends PerkCondition> getCodec() {
        return RGRegistry.PerkConditionReg.SPELL_DAMAGE.get();
    }
}
