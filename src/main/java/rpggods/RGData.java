/**
 * Copyright (c) 2024 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 **/

package rpggods;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;
import rpggods.data.favor.Favor;
import rpggods.data.favor.FavorCommand;
import rpggods.data.favor.IFavor;
import rpggods.data.tameable.ITameable;
import rpggods.data.tameable.Tameable;
import rpggods.entity.AltarEntity;
import rpggods.network.SUpdateAltarPacket;

public final class RGData {

    @SubscribeEvent
    public static void onAddCommands(final RegisterCommandsEvent event) {
        FavorCommand.register(event.getDispatcher());
    }

    /**
     * Rebuilds the {@link rpggods.data.deity.DeityContainer} cache when the server starts.
     * @param event the server started event
     */
    @SubscribeEvent
    public static void onServerStarted(final net.minecraftforge.event.server.ServerStartedEvent event) {
        rpggods.data.deity.DeityContainer.clearCache();
        final net.minecraft.core.RegistryAccess registryAccess = event.getServer().registryAccess();
        rpggods.data.deity.DeityContainer.populate(registryAccess);
        logRegistrySummary(registryAccess);
    }

    private static void logRegistrySummary(final net.minecraft.core.RegistryAccess registryAccess) {
        RPGGods.LOGGER.info("Loaded %s deities, %s altars, %s offerings, %s sacrifices, %s perks from data packs",
                registryAccess.registryOrThrow(rpggods.RGRegistry.Keys.DEITIES).size(),
                registryAccess.registryOrThrow(rpggods.RGRegistry.Keys.ALTARS).size(),
                registryAccess.registryOrThrow(rpggods.RGRegistry.Keys.OFFERINGS).size(),
                registryAccess.registryOrThrow(rpggods.RGRegistry.Keys.SACRIFICES).size(),
                registryAccess.registryOrThrow(rpggods.RGRegistry.Keys.PERKS).size());
    }

    /**
     * Rebuilds the {@link rpggods.data.deity.DeityContainer} cache whenever
     * datapack registries are (re)synced, for example on player join or /reload.
     * @param event the datapack sync event
     */
    @SubscribeEvent
    public static void onDatapackSync(final net.minecraftforge.event.OnDatapackSyncEvent event) {
        if (event.getPlayerList().getServer() != null) {
            rpggods.data.deity.DeityContainer.clearCache();
            final net.minecraft.core.RegistryAccess registryAccess = event.getPlayerList().getServer().registryAccess();
            rpggods.data.deity.DeityContainer.populate(registryAccess);
            logRegistrySummary(registryAccess);
            // data packs may have changed attribute perks
            rpggods.util.AttributeModifiers.reconcileAll(event.getPlayerList().getServer());
        }
    }

    /**
     * Used to sync datapack info when resources are reloaded
     * @param event the reload listener event
     **/
    @SubscribeEvent
    public static void onReloadListeners(final AddReloadListenerEvent event) {
        // Datapack registries are synced automatically, so no custom reload listeners are required
    }

    /**
     * Used to attach Favor to players
     * @param event the capability attach event (Entity)
     */
    @SubscribeEvent
    public static void onAttachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof Player) {
            event.addCapability(IFavor.REGISTRY_NAME, new Favor.Provider((Player)event.getObject()));
        } else if(event.getObject() instanceof Mob
                && !(event.getObject() instanceof TamableAnimal)
                && !(event.getObject() instanceof AbstractHorse)) {
            event.addCapability(ITameable.REGISTRY_NAME, new Tameable.Provider(event.getObject()));
        }
    }

    /**
     * Used to ensure that favor persists across deaths
     * @param event the player clone event
     */
    @SubscribeEvent
    public static void onPlayerClone(final PlayerEvent.Clone event) {
        // revive capabilities in order to copy to the clone
        event.getOriginal().reviveCaps();
        LazyOptional<IFavor> original = event.getOriginal().getCapability(RPGGods.FAVOR);
        LazyOptional<IFavor> copy = event.getEntity().getCapability(RPGGods.FAVOR);
        if(original.isPresent() && copy.isPresent()) {
            copy.ifPresent(f -> f.deserializeNBT(original.orElse(Favor.EMPTY).serializeNBT()));
        }
    }

    /**
     * Used to ensure that
     * 1. AltarEntity syncs inventory to client and
     * 2. Tameable entities sync sitting state to client
     * @param event the Player Start Tracking Entity event
     */
    @SubscribeEvent
    public static void onStartTracking(final PlayerEvent.StartTracking event) {
        if(event.getEntity().isAlive() && !event.getEntity().level().isClientSide) {
            // sync altar entity
            if(event.getTarget() instanceof AltarEntity) {
                int entityId = event.getTarget().getId();
                ItemStack block = ((AltarEntity)event.getTarget()).getBlockBySlot();
                RPGGods.CHANNEL.send(PacketDistributor.ALL.noArg(), new SUpdateAltarPacket(entityId, block));
            }
            // sync tameable entity
            LazyOptional<ITameable> tameable = event.getTarget().getCapability(RPGGods.TAMEABLE);
            if(tameable.isPresent()) {
                ITameable t = tameable.orElse(Tameable.EMPTY);
                t.setSittingWithUpdate(event.getTarget(), t.isSitting());
            }
        }
    }
}
