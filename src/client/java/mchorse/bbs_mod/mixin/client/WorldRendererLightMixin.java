package mchorse.bbs_mod.mixin.client;

import mchorse.bbs_mod.client.DynamicLightManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.world.BlockRenderView;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to add dynamic lighting from BBS entities to the world renderer.
 * This intercepts the lightmap coordinate calculation and adds light from
 * BBS entities holding light-emitting items.
 */
@Mixin(WorldRenderer.class)
public class WorldRendererLightMixin
{
    @Inject(method = "getLightmapCoordinates(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/util/math/BlockPos;)I",
            at = @At("RETURN"),
            cancellable = true)
    private static void onGetLightmapCoordinates(BlockRenderView world, BlockPos pos, CallbackInfoReturnable<Integer> cir)
    {
        int originalLight = cir.getReturnValue();
        int modifiedLight = DynamicLightManager.getInstance().applyDynamicLight(originalLight, pos);

        if (modifiedLight != originalLight)
        {
            cir.setReturnValue(modifiedLight);
        }
    }
}
