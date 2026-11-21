/**
 * Copyright (c) 2024 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 **/

package rpggods.data.perk.condition;

import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import rpggods.data.deity.Deity;
import rpggods.data.favor.IFavor;
import rpggods.data.perk.Perk;

import javax.annotation.concurrent.Immutable;
import java.util.Optional;

@Immutable
public final class PerkConditionContext {

    /** The deity associated with this {@link Perk} **/
    private final ResourceLocation deity;
    /** The server player **/
    private final ServerPlayer player;
    /** The player favor **/
    private final IFavor favor;
    /** Some arbitrary data as an ID **/
    private final Optional<ResourceLocation> data;
    /** The entity, if there is an entity associated with the {@link Perk} **/
    private final Optional<Entity> entity;
    /** The item stack, if there is an item stack associated with the {@link Perk} **/
    private final Optional<ItemStack> itemStack;
    /** The block state, if there is a block state associated with the {@link Perk} **/
    private final Optional<BlockState> blockState;
    /** A numeric amount associated with the event, if any (spell damage, healing, ...) **/
    private final Optional<Double> amount;
    /** The registry id of the perk being tested, if known **/
    private final Optional<ResourceLocation> perkId;

    /**
     * Constructs a new {@link PerkConditionContext} with the specified parameters.
     * @param deity the deity associated with this {@link Perk}
     * @param player the player
     * @param favor the player favor
     * @param data some arbitrary data as an ID
     * @param entity the entity, if there is an entity associated with the {@link Perk}
     * @param itemStack the item stack, if there is an item stack associated with the {@link Perk}
     * @param blockState the block state, if there is a block state associated with the {@link Perk}
     **/
    public PerkConditionContext(ResourceLocation deity, ServerPlayer player, IFavor favor,
                                Optional<ResourceLocation> data, Optional<Entity> entity,
                                Optional<ItemStack> itemStack, Optional<BlockState> blockState) {
        this(deity, player, favor, data, entity, itemStack, blockState, Optional.empty());
    }

    /**
     * Constructs a new {@link PerkConditionContext} with a numeric event amount.
     * @param deity the deity associated with this {@link Perk}
     * @param player the player
     * @param favor the player favor
     * @param data some arbitrary data as an ID
     * @param entity the entity, if there is an entity associated with the {@link Perk}
     * @param itemStack the item stack, if there is an item stack associated with the {@link Perk}
     * @param blockState the block state, if there is a block state associated with the {@link Perk}
     * @param amount a numeric amount associated with the event, if any
     **/
    public PerkConditionContext(ResourceLocation deity, ServerPlayer player, IFavor favor,
                                Optional<ResourceLocation> data, Optional<Entity> entity,
                                Optional<ItemStack> itemStack, Optional<BlockState> blockState,
                                Optional<Double> amount) {
        this(deity, player, favor, data, entity, itemStack, blockState, amount, Optional.empty());
    }

    /**
     * Constructs a new {@link PerkConditionContext} with a numeric event amount and perk id.
     * @param deity the deity associated with this {@link Perk}
     * @param player the player
     * @param favor the player favor
     * @param data some arbitrary data as an ID
     * @param entity the entity, if there is an entity associated with the {@link Perk}
     * @param itemStack the item stack, if there is an item stack associated with the {@link Perk}
     * @param blockState the block state, if there is a block state associated with the {@link Perk}
     * @param amount a numeric amount associated with the event, if any
     * @param perkId the registry id of the perk being tested, if known
     **/
    public PerkConditionContext(ResourceLocation deity, ServerPlayer player, IFavor favor,
                                Optional<ResourceLocation> data, Optional<Entity> entity,
                                Optional<ItemStack> itemStack, Optional<BlockState> blockState,
                                Optional<Double> amount, Optional<ResourceLocation> perkId) {
        this.deity = deity;
        this.player = player;
        this.favor = favor;
        this.data = data;
        this.entity = entity;
        this.itemStack = itemStack;
        this.blockState = blockState;
        this.amount = amount;
        this.perkId = perkId;
    }

    /** The deity ID associated with this {@link Perk} **/
    public ResourceLocation getDeityId() {
        return deity;
    }

    /** The deity associated with this {@link Perk} **/
    public Deity getDeity() {
        return Deity.getRegistry(getRegistryAccess()).get(deity);
    }

    /** @return The server player **/
    public ServerPlayer getPlayer() {
        return player;
    }

    /** @return The server player {@link RandomSource} **/
    public RandomSource getRandom() {
        return player.getRandom();
    }

    /** @return The server player player **/
    public ServerLevel getLevel() {
        return player.serverLevel();
    }

    /** @return The player favor **/
    public IFavor getFavor() {
        return favor;
    }

    /** @return Some arbitrary data as an ID **/
    public Optional<ResourceLocation> getData() {
        return data;
    }

    /** @return The entity, if there is an entity associated with the {@link Perk} **/
    public Optional<Entity> getEntity() {
        return entity;
    }

    /** @return The item stack, if there is an item stack associated with the {@link Perk} **/
    public Optional<ItemStack> getItemStack() {
        return itemStack;
    }

    /** @return the block state, if there is a block state associated with the {@link Perk} **/
    public Optional<BlockState> getBlockState() {
        return blockState;
    }

    /** @return a numeric amount associated with the event, if any **/
    public Optional<Double> getAmount() {
        return amount;
    }

    /** @return the registry id of the perk being tested, if known **/
    public Optional<ResourceLocation> getPerkId() {
        return perkId;
    }

    /** @return the registry access of the player level **/
    public RegistryAccess getRegistryAccess() {
        return player.level().registryAccess();
    }

    /** @return the block position of the player **/
    public BlockPos getPos() {
        return player.blockPosition();
    }
}
