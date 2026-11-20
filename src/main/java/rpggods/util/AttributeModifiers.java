/**
 * Copyright (c) 2024 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 **/

package rpggods.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.ForgeRegistries;
import rpggods.RPGGods;
import rpggods.data.deity.Deity;
import rpggods.data.deity.DeityContainer;
import rpggods.data.favor.FavorLevel;
import rpggods.data.favor.IFavor;
import rpggods.data.perk.Perk;
import rpggods.data.perk.action.AttributeAction;
import rpggods.data.perk.action.PerkAction;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Applies and removes the attribute modifiers declared by {@link AttributeAction} perks.
 * <p>
 * Attribute actions are passive: a perk contributes its modifier while the player's favor
 * level is inside the perk's range. The modifiers are reconciled whenever favor levels
 * change and whenever the player joins, respawns, changes dimension or data packs reload.
 * <p>
 * Modifiers use {@link AttributeModifier#addTransientModifier} style transient modifiers, so
 * they are never serialized into player data and cannot become permanent buffs.
 */
public final class AttributeModifiers {

    private static final String MODIFIER_PREFIX = "rpggods:attr/";

    /** Applied modifiers per player, used to remove them on the next reconciliation */
    private static final Map<UUID, List<Applied>> APPLIED = new HashMap<>();

    private AttributeModifiers() {
    }

    private record Key(ResourceLocation deity, ResourceLocation attribute, AttributeModifier.Operation operation) {
    }

    private record Applied(ResourceLocation attribute, UUID id) {
    }

    /**
     * Reconciles the attribute modifiers of every online player.
     *
     * @param server the server
     */
    public static void reconcileAll(final MinecraftServer server) {
        if (server == null) {
            return;
        }
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            reconcile(player);
        }
    }

    /**
     * Reconciles the attribute modifiers of a single player.
     *
     * @param player the player
     */
    public static void reconcile(final ServerPlayer player) {
        if (player.level().isClientSide()) {
            return;
        }
        reconcile(player, RPGGods.getFavor(player).orElse(null));
    }

    /**
     * Reconciles the attribute modifiers of a single player using the given favor.
     *
     * @param player the player
     * @param favor  the player's favor, or null
     */
    public static void reconcile(final ServerPlayer player, @Nullable final IFavor favor) {
        // remove previously applied modifiers first
        final List<Applied> previous = APPLIED.remove(player.getUUID());
        if (previous != null) {
            for (Applied applied : previous) {
                removeModifier(player, applied.attribute(), applied.id());
            }
        }
        // no favor means no modifiers
        if (favor == null || !favor.isEnabled()) {
            return;
        }
        // gather the desired modifier amount for each deity / attribute / operation combination
        final Map<Key, Double> desired = new LinkedHashMap<>();
        for (DeityContainer container : DeityContainer.getRegistry(false).values()) {
            final Deity deity = container.getDeity();
            if (deity == null || !deity.isEnabled()) {
                continue;
            }
            final FavorLevel favorLevel = favor.getFavor(container.getId());
            if (!favorLevel.isEnabled()) {
                continue;
            }
            final int level = favorLevel.getLevel();
            for (Perk perk : container.getPerks().values()) {
                if (!perk.getRange().isInRange(level)) {
                    continue;
                }
                for (PerkAction action : perk.getActions()) {
                    if (action instanceof AttributeAction attributeAction) {
                        final Key key = new Key(container.getId(), attributeAction.getAttribute(), attributeAction.getOperation());
                        desired.merge(key, attributeAction.getAmount(), Double::sum);
                    }
                }
            }
        }
        // apply the desired modifiers
        final List<Applied> applied = new ArrayList<>();
        for (Map.Entry<Key, Double> entry : desired.entrySet()) {
            // resolve the attribute, silently skipping attributes from mods that are not installed
            final Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(entry.getKey().attribute());
            if (attribute == null || entry.getValue() == 0.0D) {
                continue;
            }
            final AttributeInstance instance = player.getAttribute(attribute);
            if (instance == null) {
                // the player entity does not have this attribute
                continue;
            }
            final UUID id = modifierId(entry.getKey());
            // never stack duplicates
            instance.removeModifier(id);
            final AttributeModifier modifier = new AttributeModifier(id, MODIFIER_PREFIX + entry.getKey().deity() + "/" + entry.getKey().attribute(),
                    entry.getValue(), entry.getKey().operation());
            instance.addTransientModifier(modifier);
            applied.add(new Applied(entry.getKey().attribute(), id));
        }
        if (!applied.isEmpty()) {
            APPLIED.put(player.getUUID(), applied);
        }
    }

    /**
     * Forgets the modifiers of a player that is leaving the server.
     *
     * @param player the player
     */
    public static void clear(final ServerPlayer player) {
        APPLIED.remove(player.getUUID());
    }

    private static void removeModifier(final ServerPlayer player, final ResourceLocation attributeId, final UUID id) {
        final Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(attributeId);
        if (attribute == null) {
            return;
        }
        final AttributeInstance instance = player.getAttribute(attribute);
        if (instance != null) {
            instance.removeModifier(id);
        }
    }

    private static UUID modifierId(final Key key) {
        final String name = MODIFIER_PREFIX + key.deity() + "/" + key.attribute() + "/" + AttributeAction.operationName(key.operation());
        return UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
    }
}
