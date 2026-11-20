/**
 * Copyright (c) 2024 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 **/

package rpggods.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import rpggods.RGRegistry;
import rpggods.RPGGods;
import rpggods.data.deity.Altar;
import rpggods.entity.AltarEntity;

import javax.annotation.Nullable;
import java.util.List;

public class AltarItem extends Item {

    public static final String KEY_ALTAR = "altar";
    public static final String KEY_DEITY = "deity";

    public AltarItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        final String sDeityId = stack.getOrCreateTag().getString(KEY_DEITY);
        ResourceLocation deityId = sDeityId.isEmpty() ? null : ResourceLocation.tryParse(sDeityId);
        // fall back to the altar registry when the NBT does not contain a deity
        if (deityId == null) {
            final String sAltarId = stack.getOrCreateTag().getString(KEY_ALTAR);
            final ResourceLocation altarId = sAltarId.isEmpty() ? null : ResourceLocation.tryParse(sAltarId);
            final net.minecraft.core.RegistryAccess registryAccess = RPGGods.getRegistryAccess();
            if (altarId != null && registryAccess != null) {
                final Altar altar = Altar.getRegistry(registryAccess).get(altarId);
                if (altar != null && altar.getDeity().isPresent()) {
                    deityId = altar.getDeity().get();
                }
            }
        }
        // create translation key using deity information
        if(deityId != null) {
            return Component.translatable("item.rpggods.altar_x",
                    Component.translatable(Altar.createTranslationKey(deityId)));
        }
        // fallback when no altar information provided
        return Component.translatable(this.getDescriptionId(stack));
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        String sAltarId = stack.getOrCreateTag().getString(KEY_ALTAR);
        // create translation key using altar information
        if(!sAltarId.isEmpty() && worldIn != null) {
            ResourceLocation altarId = ResourceLocation.tryParse(sAltarId);
            if(altarId != null) {
                // determine if altar is not a deity but has a name
                final Altar altar = Altar.getRegistry(worldIn.registryAccess()).get(altarId);
                if(altar != null && altar.getDeity().isEmpty() && altar.getName().isPresent()) {
                    tooltip.add(Component.literal(altar.getName().get()));
                }
            }
        }
    }

    public static void addAltarItems(final List<ItemStack> items, final RegistryAccess registryAccess) {
        // add altar item stacks for each registered altar
        final List<ResourceLocation> altarList = new java.util.ArrayList<>(Altar.getRegistry(registryAccess).keySet());
        altarList.sort(ResourceLocation::compareNamespaced);
        for(ResourceLocation altarId : altarList) {
            final Altar altar = Altar.getRegistry(registryAccess).get(altarId);
            final ItemStack itemStack = new ItemStack(RGRegistry.ItemReg.ALTAR.get());
            itemStack.getOrCreateTag().putString(AltarItem.KEY_ALTAR, altarId.toString());
            if(altar != null && altar.getDeity().isPresent()) {
                itemStack.getOrCreateTag().putString(AltarItem.KEY_DEITY, altar.getDeity().get().toString());
            }
            items.add(itemStack);
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Direction direction = context.getClickedFace();
        if (direction == Direction.DOWN) {
            return InteractionResult.FAIL;
        } else {
            Level world = context.getLevel();
            BlockPlaceContext blockitemusecontext = new BlockPlaceContext(context);
            BlockPos blockpos = blockitemusecontext.getClickedPos();
            ItemStack itemstack = context.getItemInHand();
            Vec3 vector3d = Vec3.atBottomCenterOf(blockpos);
            AABB axisalignedbb = RGRegistry.EntityReg.ALTAR.get().getDimensions().makeBoundingBox(vector3d.x(), vector3d.y(), vector3d.z());
            if (world.noCollision(null, axisalignedbb) && world.getEntities(null, axisalignedbb).isEmpty()) {
                if (world instanceof ServerLevel) {
                    ServerLevel serverworld = (ServerLevel)world;
                    // determine altar properties to apply
                    String sAltarId = context.getItemInHand().getOrCreateTag().getString(KEY_ALTAR);
                    ResourceLocation altarId = ResourceLocation.tryParse(sAltarId);
                    // crate altar entity
                    AltarEntity altarEntity = AltarEntity.createAltar(world, blockpos, context.getHorizontalDirection().getOpposite(), altarId);
                    if (altarEntity == null) {
                        return InteractionResult.FAIL;
                    }
                    serverworld.addFreshEntity(altarEntity);
                    world.playSound(null, altarEntity.getX(), altarEntity.getY(), altarEntity.getZ(), SoundEvents.ARMOR_STAND_PLACE, SoundSource.BLOCKS, 0.75F, 0.8F);
                }

                itemstack.shrink(1);
                return InteractionResult.sidedSuccess(world.isClientSide);
            } else {
                return InteractionResult.FAIL;
            }
        }
    }

}
