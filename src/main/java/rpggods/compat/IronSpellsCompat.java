/**
 * Copyright (c) 2024 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 **/

package rpggods.compat;

import io.redspace.ironsspellbooks.api.events.SpellDamageEvent;
import io.redspace.ironsspellbooks.api.events.SpellHealEvent;
import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import rpggods.RPGGods;
import rpggods.data.deity.Deity;
import rpggods.data.favor.FavorLevel;
import rpggods.data.favor.IFavor;
import rpggods.util.FavorChangedEvent;

/**
 * Optional compatibility with Iron's Spellbooks.
 * <p>
 * This class is only loaded when {@code irons_spellbooks} is present, so the mod still
 * runs when Iron's Spellbooks is not installed.
 * <p>
 * Casting a spell, dealing spell damage and spell healing all grant favor with the test
 * deity {@code iss:iron}. If that deity is not registered the events are ignored.
 */
public final class IronSpellsCompat {

    /** The deity that gains favor from spell casting */
    public static final ResourceLocation DEITY = new ResourceLocation("iss", "iron");

    private static final long CAST_FAVOR = 10L;
    private static final long DAMAGE_FAVOR = 4L;
    private static final long HEAL_FAVOR = 8L;

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
        if (event.getEntity() instanceof ServerPlayer player) {
            addFavor(player, CAST_FAVOR);
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
            addFavor(player, DAMAGE_FAVOR);
        }
    }

    @SubscribeEvent
    public static void onSpellHeal(final SpellHealEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            addFavor(player, HEAL_FAVOR);
        }
    }

    private static void addFavor(final ServerPlayer player, final long amount) {
        if (player.level().isClientSide() || player.isSpectator() || player.isCreative()) {
            return;
        }
        // only grant favor when the deity exists and is enabled
        final Deity deity = Deity.getRegistry(player.level().registryAccess()).get(DEITY);
        if (deity == null || !deity.isEnabled() || !deity.isUnlocked()) {
            return;
        }
        final IFavor favor = RPGGods.getFavor(player).orElse(null);
        if (favor == null || !favor.isEnabled()) {
            return;
        }
        final FavorLevel favorLevel = favor.getFavor(DEITY);
        if (!favorLevel.isEnabled()) {
            return;
        }
        favorLevel.addFavor(player, DEITY, amount, FavorChangedEvent.Source.PERK);
    }
}
