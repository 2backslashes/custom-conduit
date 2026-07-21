package net.backslashes.customconduit.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.backslashes.customconduit.CustomConduit;
import net.minecraft.client.Camera;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static net.backslashes.customconduit.MathUtil.lerpf;

@OnlyIn(Dist.CLIENT)
public class EffectConduitRenderer implements BlockEntityRenderer<EffectConduitBlockEntity> {
    public static final Material SHELL_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.fromNamespaceAndPath(CustomConduit.MODID, "entity/conduit/base"));
    public static final Material ACTIVE_SHELL_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.fromNamespaceAndPath(CustomConduit.MODID, "entity/conduit/cage"));
    public static final Material WIND_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.fromNamespaceAndPath(CustomConduit.MODID, "entity/conduit/wind"));
    public static final Material EYE_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("entity/conduit/open_eye"));
    private final ModelPart eye;
    private final ModelPart wind;
    private final ModelPart shell;
    private final ModelPart cage;
    private final BlockEntityRenderDispatcher renderer;

    public EffectConduitRenderer(BlockEntityRendererProvider.Context context) {
        this.renderer = context.getBlockEntityRenderDispatcher();
        this.eye = context.bakeLayer(ModelLayers.CONDUIT_EYE);
        this.wind = context.bakeLayer(ModelLayers.CONDUIT_WIND);
        this.shell = context.bakeLayer(ModelLayers.CONDUIT_SHELL);
        this.cage = context.bakeLayer(ModelLayers.CONDUIT_CAGE);
    }

    public void render(EffectConduitBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        float f = (float)blockEntity.tickCount + partialTick;
        if (!blockEntity.isActive()) {
            VertexConsumer vertexconsumer1 = SHELL_TEXTURE.buffer(bufferSource, RenderType::entitySolid);
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.5F, 0.5F);
//            poseStack.mulPose(new Quaternionf().rotationY(f * (float) (Math.PI / 180.0)));
            poseStack.scale(2.0f, 2.0f, 2.0f);
            this.shell.render(poseStack, vertexconsumer1, packedLight, packedOverlay);
            poseStack.popPose();
        } else {
            // Cage.
            poseStack.pushPose();
            float factor = (blockEntity.getActiveLevel() - 1) / 3.0f;
            float wobble = lerpf(0.005f,  0.03f, factor);
            float wobbleSpeed = lerpf(0.1f,  2.0f, factor);
            poseStack.translate(0.5F + Math.sin(f * wobbleSpeed) * wobble, 0.5F + Math.cos(f * 1.3 * wobbleSpeed) * wobble, 0.5F + Math.cos(f * 1.7 * wobbleSpeed) * wobble);
            poseStack.scale(2.0f, 2.0f, 2.0f);
            Vector3f vector3f = new Vector3f(0.5F, 1.0F, 0.5F).normalize();
            float spinSpeed = lerpf(1.0f, 3.0f, factor);
            poseStack.mulPose(new Quaternionf().rotationAxis(f * spinSpeed * (float) (Math.PI / 180.0), vector3f));
            this.cage.render(poseStack, ACTIVE_SHELL_TEXTURE.buffer(bufferSource, RenderType::entityCutoutNoCull), packedLight, packedOverlay);
            poseStack.popPose();

            // Wind 1.
            int i = blockEntity.tickCount / 66 % 3;
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.5F, 0.5F);
            if (i == 1) {
                poseStack.mulPose(new Quaternionf().rotationX((float) (Math.PI / 2)));
            } else if (i == 2) {
                poseStack.mulPose(new Quaternionf().rotationZ((float) (Math.PI / 2)));
            }
            poseStack.scale(2.5F, 2.5F, 2.5F);
            VertexConsumer vertexconsumer = WIND_TEXTURE.buffer(bufferSource, RenderType::entityCutoutNoCull);
            this.wind.render(poseStack, vertexconsumer, packedLight, packedOverlay, blockEntity.color);
            poseStack.popPose();

            // Wind 2.
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.5F, 0.5F);
            poseStack.scale(2.0F, 2.0F, 2.0F);
            poseStack.mulPose(new Quaternionf().rotationXYZ((float) Math.PI, 0.0F, (float) Math.PI));
            this.wind.render(poseStack, vertexconsumer, packedLight, packedOverlay, blockEntity.color);
            poseStack.popPose();

            // Eye.
            Camera camera = this.renderer.camera;
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.5F + Math.sin(f * 0.1) * 0.1, 0.5F);
            poseStack.scale(0.5F, 0.5F, 0.5F);
            float f3 = -camera.getYRot();
            poseStack.mulPose(new Quaternionf().rotationYXZ(f3 * (float) (Math.PI / 180.0), camera.getXRot() * (float) (Math.PI / 180.0), (float) Math.PI));
            float f4 = 1.3333334F;
            poseStack.scale(1.3333334F, 1.3333334F, 1.3333334F);
            this.eye
                    .render(
                            poseStack,
                            EYE_TEXTURE.buffer(bufferSource, RenderType::entityCutoutNoCull),
                            packedLight,
                            packedOverlay
                    );
            poseStack.popPose();
        }
    }

    @Override
    public net.minecraft.world.phys.AABB getRenderBoundingBox(EffectConduitBlockEntity blockEntity) {
        net.minecraft.core.BlockPos pos = blockEntity.getBlockPos();
        return new net.minecraft.world.phys.AABB(pos.getX(), pos.getY() - .25, pos.getZ(), pos.getX() + 1.0, pos.getY() + 1.25, pos.getZ() + 1.0);
    }
}
