package qinomed.qinartificery.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import qinomed.qinartificery.QinArtificery;
import qinomed.qinartificery.entity.projectile.NephriteBombProjectile;

@OnlyIn(Dist.CLIENT)
public class NephriteBombRenderer extends EntityRenderer<NephriteBombProjectile> {
    private final ItemRenderer itemRenderer;
    private final ItemModelShaper itemModelShaper;

    private static final ResourceLocation TEXTURE_LOCATION = QinArtificery.modPath("textures/item/nephrite_bomb_2.png");

    public NephriteBombRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.itemRenderer = pContext.getItemRenderer();
        this.itemModelShaper = itemRenderer.getItemModelShaper();
    }

    @Override
    public void render(NephriteBombProjectile entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (entity.tickCount >= 2 || !(this.entityRenderDispatcher.camera.getEntity().distanceToSqr(entity) < 12.25D)) {
            poseStack.pushPose();
            poseStack.scale(1, 1, 1);
            poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));

            BakedModel bakedModel = this.itemModelShaper.getItemModel(entity.getItem());
            this.itemRenderer.render(entity.getItem(), ItemTransforms.TransformType.GROUND, false, poseStack, bufferSource, packedLight, packedLight, bakedModel);

            poseStack.popPose();

            super.render(entity, yaw, partialTick, poseStack, bufferSource, packedLight);
        }
    }

    @Override
    protected int getBlockLightLevel(NephriteBombProjectile pEntity, BlockPos pPos) {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(NephriteBombProjectile pEntity) {
        return TEXTURE_LOCATION;
    }
}
