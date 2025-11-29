package mchorse.bbs_mod.forms.renderers.utils;

import mchorse.bbs_mod.utils.colors.Color;
import net.minecraft.client.render.VertexConsumer;

/**
 * RecolorVertexSodiumConsumer was disabled in 1.21.1 - the Sodium VertexBufferWriter API
 * has changed significantly. The VertexFormatDescription class no longer exists in the
 * same location (net.caffeinemc.mods.sodium.api.vertex.format.VertexFormatDescription).
 *
 * This functionality will need to be re-implemented when the Sodium API stabilizes.
 * For now, we extend RecolorVertexConsumer without Sodium-specific optimizations.
 */
public class RecolorVertexSodiumConsumer extends RecolorVertexConsumer
{
    public RecolorVertexSodiumConsumer(VertexConsumer consumer, Color color)
    {
        super(consumer, color);

        newColor = color;
    }

    // Sodium VertexBufferWriter interface disabled - API changed in 0.6.0
}
