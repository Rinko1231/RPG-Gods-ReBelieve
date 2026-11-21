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
import rpggods.RGRegistry;
import rpggods.util.RGComponentUtils;
import rpggods.util.SpellUsage;

import java.util.Optional;

/**
 * Matches when a spell heals. {@code min_amount} / {@code max_amount} limit the condition to
 * a healing range, and {@code maxuses} limits how many times the perk can trigger for a
 * player (omitted or <= 0 is unlimited).
 */
public class SpellHealCondition extends PerkCondition {

    public static final Codec<SpellHealCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("min_amount").forGetter(o -> o.minAmount),
            Codec.INT.optionalFieldOf("max_amount").forGetter(o -> o.maxAmount),
            Codec.INT.optionalFieldOf("maxuses").forGetter(o -> o.maxUses)
    ).apply(instance, SpellHealCondition::new));

    private final Optional<Integer> minAmount;
    private final Optional<Integer> maxAmount;
    private final Optional<Integer> maxUses;

    public SpellHealCondition(Optional<Integer> minAmount, Optional<Integer> maxAmount, Optional<Integer> maxUses) {
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.maxUses = maxUses;
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
        if (maxUses.isPresent() && maxUses.get() > 0) {
            if (!SpellUsage.canUse(context.getPlayer(), context.getPerkId().orElse(null), maxUses.get())) {
                return false;
            }
            SpellUsage.addUse(context.getPlayer(), context.getPerkId().orElse(null));
        }
        return true;
    }

    @Override
    public Component createDescription(RegistryAccess registryAccess) {
        final Component bounds = RGComponentUtils.createBoundsComponent(minAmount.orElse(null), maxAmount.orElse(null));
        return Component.translatable(PREFIX + "spell_heal", bounds);
    }

    @Override
    public Codec<? extends PerkCondition> getCodec() {
        return RGRegistry.PerkConditionReg.SPELL_HEAL.get();
    }
}
