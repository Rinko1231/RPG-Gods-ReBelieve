/**
 * Copyright (c) 2024 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 **/

package rpggods.compat;

import com.mojang.serialization.Codec;
import io.redspace.ironsspellbooks.api.events.SpellDamageEvent;
import io.redspace.ironsspellbooks.api.events.SpellHealEvent;
import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import rpggods.PerkDispatcher;
import rpggods.RGRegistry;
import rpggods.RPGGods;
import rpggods.data.favor.IFavor;
import rpggods.data.perk.condition.PerkCondition;

/**
 * Optional compatibility with Iron's Spellbooks.
 * <p>
 * This class is only loaded when {@code irons_spellbooks} is present, so the mod still
 * runs when Iron's Spellbooks is not installed.
 * <p>
 * Spell events are turned into data pack perks: the event dispatches perks whose
 * condition is {@code rpggods:spell_cast}, {@code rpggods:spell_damage} or
 * {@code rpggods:spell_heal}. The perk itself decides what happens (for example a
 * {@code rpggods:favor} action), so favor gain, cooldowns and thresholds are fully
 * data-driven and visible in the favor GUI.
 */
public final class IronSpellsCompat {

    private IronSpellsCompat() {
    }

    /**
     * Registers the event handlers. Must only be called when Iron's Spellbooks is loaded.
     */
    public static void register() {
        MinecraftForge.EVENT_BUS.register(IronSpellsCompat.class);
    }

    @SubscribeEvent
    public static void onSpellCast(final SpellOnCastEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        final ResourceLocation spell = ResourceLocation.tryParse(event.getSpellId());
        if (spell != null) {
            dispatch(player, spell, 0.0D, RGRegistry.PerkConditionReg.SPELL_CAST.get());
        }
    }

    @SubscribeEvent
    public static void onSpellDamage(final SpellDamageEvent event) {
        if (event.isCanceled()) {
            return;
        }
        Entity caster = event.getSpellDamageSource().getEntity();
        if (!(caster instanceof ServerPlayer)) {
            caster = event.getSpellDamageSource().getDirectEntity();
        }
        if (caster instanceof ServerPlayer player) {
            dispatch(player, event.getSpellDamageSource().spell().getSpellResource(), event.getAmount(),
                    RGRegistry.PerkConditionReg.SPELL_DAMAGE.get());
        }
    }

    @SubscribeEvent
    public static void onSpellHeal(final SpellHealEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            dispatch(player, null, event.getHealAmount(), RGRegistry.PerkConditionReg.SPELL_HEAL.get());
        }
    }

    /**
     * Runs the perks with the given condition for this player.
     *
     * @param player    the server player
     * @param spell     the spell id, or null when the event does not expose one
     * @param amount    the event amount (damage, healing, ...)
     * @param condition the perk condition codec to dispatch
     */
    private static void dispatch(final ServerPlayer player, final ResourceLocation spell, final double amount,
                                 final Codec<? extends PerkCondition> condition) {
        if (player.level().isClientSide()) {
            return;
        }
        final IFavor favor = RPGGods.getFavor(player).orElse(null);
        if (favor == null || !favor.isEnabled()) {
            return;
        }
        final PerkDispatcher.Invoker invoker = PerkDispatcher.invoke(player, favor).withAmount(amount);
        if (spell != null) {
            invoker.withData(spell);
        }
        invoker.runForCondition(condition);
    }
}
