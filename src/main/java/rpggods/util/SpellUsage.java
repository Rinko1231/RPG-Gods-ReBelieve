/**
 * Copyright (c) 2024 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 **/

package rpggods.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

/**
 * Tracks how many times a spell-event perk has granted its effect for a player.
 * <p>
 * Counters are stored in the player's Forge persistent data and are lifetime counters;
 * a condition without {@code maxuses} (or with a value <= 0) is unlimited and does not
 * touch this data.
 */
public final class SpellUsage {

    private static final String KEY = "rpggods:spell_uses";

    private SpellUsage() {
    }

    /**
     * @param player the player
     * @param perkId the perk registry id, may be null
     * @return how many times the perk has been used by this player
     */
    public static int getUses(final ServerPlayer player, @Nullable final ResourceLocation perkId) {
        if (perkId == null) {
            return 0;
        }
        final CompoundTag data = player.getPersistentData();
        if (!data.contains(KEY, Tag.TAG_COMPOUND)) {
            return 0;
        }
        return data.getCompound(KEY).getInt(perkId.toString());
    }

    /**
     * @param player  the player
     * @param perkId  the perk registry id, may be null
     * @param maxUses the maximum number of uses, <= 0 means unlimited
     * @return true if the perk may still be triggered
     */
    public static boolean canUse(final ServerPlayer player, @Nullable final ResourceLocation perkId, final int maxUses) {
        return maxUses <= 0 || getUses(player, perkId) < maxUses;
    }

    /**
     * Adds one use to the perk's counter.
     *
     * @param player the player
     * @param perkId the perk registry id, may be null
     */
    public static void addUse(final ServerPlayer player, @Nullable final ResourceLocation perkId) {
        if (perkId == null) {
            return;
        }
        final CompoundTag data = player.getPersistentData();
        final CompoundTag uses = data.contains(KEY, Tag.TAG_COMPOUND) ? data.getCompound(KEY) : new CompoundTag();
        uses.putInt(perkId.toString(), uses.getInt(perkId.toString()) + 1);
        data.put(KEY, uses);
    }

    /**
     * Clears all spell usage counters for a player.
     *
     * @param player the player
     */
    public static void clear(final ServerPlayer player) {
        player.getPersistentData().remove(KEY);
    }

    /**
     * Copies the spell usage counters from one player to another (used on respawn).
     *
     * @param from the original player
     * @param to   the new player
     */
    public static void copy(final net.minecraft.world.entity.player.Player from, final net.minecraft.world.entity.player.Player to) {
        if (from.getPersistentData().contains(KEY, Tag.TAG_COMPOUND)) {
            to.getPersistentData().put(KEY, from.getPersistentData().getCompound(KEY).copy());
        }
    }
}
