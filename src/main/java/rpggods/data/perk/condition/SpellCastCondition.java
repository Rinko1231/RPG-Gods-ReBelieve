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

import java.util.Optional;

/**
 * Matches when a spell is cast. The optional {@code spell} field limits the condition to a
 * single spell id, for example {@code irons_spellbooks:firebolt}.
 */
public class SpellCastCondition extends PerkCondition {

    public static final Codec<SpellCastCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("spell").forGetter(o -> o.spell)
    ).apply(instance, SpellCastCondition::new));

    private final Optional<ResourceLocation> spell;

    public SpellCastCondition(Optional<ResourceLocation> spell) {
        this.spell = spell;
    }

    @Override
    public boolean test(PerkConditionContext context) {
        if (spell.isEmpty()) {
            return true;
        }
        return context.getData().map(spell.get()::equals).orElse(false);
    }

    @Override
    public Component createDescription(RegistryAccess registryAccess) {
        if (spell.isPresent()) {
            return Component.translatable(PREFIX + "spell_cast.specific", spellName(spell.get()));
        }
        return Component.translatable(PREFIX + "spell_cast");
    }

    @Override
    public Codec<? extends PerkCondition> getCodec() {
        return RGRegistry.PerkConditionReg.SPELL_CAST.get();
    }

    public Optional<ResourceLocation> getSpell() {
        return spell;
    }

    static Component spellName(final ResourceLocation spell) {
        return Component.translatable("spell." + spell.getNamespace() + "." + spell.getPath());
    }
}
