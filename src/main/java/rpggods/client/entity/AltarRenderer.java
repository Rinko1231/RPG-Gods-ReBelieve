/**
 * Copyright (c) 2024 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 **/

package rpggods.client.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.MinecraftForge;
import rpggods.RPGGods;
import rpggods.client.screen.AltarScreen;
import rpggods.entity.AltarEntity;
import rpggods.util.altar.AltarPose;
import rpggods.util.altar.HumanoidPart;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class AltarRenderer extends LivingEntityRenderer<AltarEntity, AltarModel> {

    protected static final ResourceLocation STEVE_TEXTURE = new ResourceLocation(RPGGods.MODID, "textures/altar/steve.png");
    protected static final ResourceLocation ALEX_TEXTURE = new ResourceLocation(RPGGods.MODID, "textures/altar/alex.png");

    public static final ModelLayerLocation ALTAR_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(RPGGods.MODID, "altar"), "altar");
    public static final ModelLayerLocation ALTAR_INNER_ARMOR_RESOURCE = new ModelLayerLocation(new ResourceLocation(RPGGods.MODID, "altar"), "inner_armor");
    public static final ModelLayerLocation ALTAR_OUTER_ARMOR_RESOURCE = new ModelLayerLocation(new ResourceLocation(RPGGods.MODID, "altar"), "outer_armor");

    private final Map<ResourceLocation, ResourceLocation> DEITY_TEXTURES = new HashMap<>();
    private final Map<String, ResourceLocation> SKIN_TEXTURES = new HashMap<>();
    private final Set<String> PENDING_SKINS = new HashSet<>();

    public AltarRenderer(final EntityRendererProvider.Context context) {
        super(context, new AltarModel(context.bakeLayer(ALTAR_MODEL_RESOURCE)), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this,
                new AltarArmorModel(context.bakeLayer(ALTAR_INNER_ARMOR_RESOURCE)),
                new AltarArmorModel(context.bakeLayer(ALTAR_OUTER_ARMOR_RESOURCE)), context.getModelManager()));
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
        this.addLayer(new ElytraLayer<>(this, context.getModelSet()));
        this.addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getItemInHandRenderer()));
    }

    @Override
    public void render(AltarEntity entityIn, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource bufferIn, int packedLightIn) {
        // intentional omission of super call
        // pre-render event
        if (MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Pre<>(entityIn, this, partialTicks, poseStack, bufferIn, packedLightIn))) {
            return;
        }
        poseStack.pushPose();
        // rotate around entity body rotation
        poseStack.mulPose(Axis.YN.rotationDegrees(entityIn.yBodyRot));

        // render base
        float baseHeight = -0.5F;
        ItemStack blockItem = entityIn.getBlockBySlot();
        if(!blockItem.isEmpty()) {
            final Block block = Block.byItem(blockItem.getItem());
            if(block != null) {
                baseHeight = 0.0F;
                poseStack.pushPose();
                poseStack.translate(-0.5D, 0.0D, -0.5D);
                Minecraft.getInstance().getBlockRenderer().renderSingleBlock(block.defaultBlockState(),
                        poseStack, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
                poseStack.popPose();
            }
        }

        // prepare to render model
        AltarPose pose = entityIn.getAltarPose();
        getModel().young = false;
        getModel().setupAnim(entityIn, entityIn.walkAnimation.position(partialTicks), entityIn.walkAnimation.speed(partialTicks), entityIn.tickCount, entityIn.getYHeadRot(), entityIn.getViewXRot(partialTicks));

        // determine render type
        Minecraft minecraft = Minecraft.getInstance();
        boolean flag = this.isBodyVisible(entityIn);
        boolean flag1 = !flag && !entityIn.isInvisibleTo(minecraft.player);
        boolean flag2 = minecraft.shouldEntityAppearGlowing(entityIn);
        RenderType rendertype = this.getRenderType(entityIn, flag, flag1, flag2);

        // rotate around body and translate according to pose offsets
        getModel().translateRotateAroundBody(pose.get(HumanoidPart.ROOT), pose.get(HumanoidPart.BODY), poseStack, partialTicks);
        // translate and rotate so the model is not upside-down
        // the small model is scaled by 0.5; this offset puts its feet exactly on the ground
        poseStack.translate(0.0F, 2.0F + baseHeight - (entityIn.isSmall() ? 0.75F : 0.0F), 0.0F);
        // the small statue uses a baby-sized model (half scale)
        if (entityIn.isSmall()) {
            poseStack.scale(0.5F, 0.5F, 0.5F);
        }
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        if (rendertype != null) {
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(rendertype);
            getModel().render(entityIn, poseStack, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY,
                    entityIn.isFemale(), entityIn.isSlim(), entityIn.isSmall(), 1.0F, 1.0F, 1.0F, 1.0F);
        }

        // render layers
        if (!entityIn.isSpectator()) {
            for(var layerrenderer : this.layers) {
                layerrenderer.render(poseStack, bufferIn, packedLightIn, entityIn, entityIn.walkAnimation.position(partialTicks), entityIn.walkAnimation.speed(partialTicks), partialTicks, entityIn.tickCount, entityIn.getYHeadRot(), entityIn.getViewXRot(partialTicks));
            }
        }
        poseStack.popPose();

        // render nametag
        net.minecraftforge.client.event.RenderNameTagEvent renderNameTagEvent = new net.minecraftforge.client.event.RenderNameTagEvent(entityIn, entityIn.getDisplayName(), this, poseStack, bufferIn, packedLightIn, partialTicks);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(renderNameTagEvent);
        if (renderNameTagEvent.getResult() != net.minecraftforge.eventbus.api.Event.Result.DENY && (renderNameTagEvent.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW || this.shouldShowName(entityIn))) {
            this.renderNameTag(entityIn, renderNameTagEvent.getContent(), poseStack, bufferIn, packedLightIn);
        }

        // post-render event
        MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Post<>(entityIn, this, partialTicks, poseStack, bufferIn, packedLightIn));
    }

    @Override
    public boolean shouldShowName(final AltarEntity entityIn) {
        final Minecraft mc = Minecraft.getInstance();
        if(mc.screen instanceof AltarScreen) {
            return false;
        }
        final Vec3 pos = entityIn.position().add(0, entityIn.getType().getDimensions().height / 2D, 0);
        return super.shouldShowName(entityIn) && mc.crosshairPickEntity == entityIn;
    }

    @Override
    public ResourceLocation getTextureLocation(final AltarEntity entity) {
        // return deity texture
        if(entity.getDeity().isPresent() && !entity.getDeity().get().toString().isEmpty()) {
            return DEITY_TEXTURES.computeIfAbsent(entity.getDeity().get(),
                    deity -> new ResourceLocation(deity.getNamespace(), "textures/altar/" + deity.getPath() + ".png"));
        }
        // return player texture
        final GameProfile gameProfile = entity.getPlayerProfile();
        if(gameProfile != null && gameProfile.getName() != null && !gameProfile.getName().isEmpty()) {
            Minecraft minecraft = Minecraft.getInstance();
            final String skinKey = gameProfile.getName().toLowerCase(Locale.ROOT);
            final ResourceLocation cached = SKIN_TEXTURES.get(skinKey);
            if (cached != null) {
                return cached;
            }
            // CustomSkinLoader patches this method to resolve skins by name, even when the
            // profile has no textures property yet. Do not gate this behind that property.
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = minecraft.getSkinManager().getInsecureSkinInformation(gameProfile);
            if(map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                final MinecraftProfileTexture texture = map.get(MinecraftProfileTexture.Type.SKIN);
                final ResourceLocation location = minecraft.getSkinManager().registerTexture(texture, MinecraftProfileTexture.Type.SKIN);
                SKIN_TEXTURES.put(skinKey, location);
                return location;
            }
            // the server resolves the profile UUID asynchronously; fetch its textures once complete
            if(gameProfile.getId() != null && PENDING_SKINS.add(skinKey)) {
                try {
                    minecraft.getSkinManager().registerSkins(gameProfile, (type, location, texture) -> {
                        if (type == MinecraftProfileTexture.Type.SKIN) {
                            SKIN_TEXTURES.put(skinKey, location);
                            PENDING_SKINS.remove(skinKey);
                        }
                    }, false);
                } catch (Exception exception) {
                    PENDING_SKINS.remove(skinKey);
                }
            }
        }
        // return default texture
        // TODO: return dynamic texture based on material
        return entity.isSlim() ? ALEX_TEXTURE : STEVE_TEXTURE;
    }

    @Override
    protected RenderType getRenderType(final AltarEntity entityIn, boolean isVisible, boolean isVisibleToPlayer, boolean isGlowing) {
        // TODO: optimize, allow for player skins, etc.
        return super.getRenderType(entityIn, isVisible, isVisibleToPlayer, isGlowing);
    }
}
