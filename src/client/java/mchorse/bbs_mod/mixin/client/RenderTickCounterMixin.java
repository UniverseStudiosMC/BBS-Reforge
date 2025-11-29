package mchorse.bbs_mod.mixin.client;

/**
 * RenderTickCounterMixin was disabled in 1.21.1 - the RenderTickCounter class
 * has been significantly refactored. The fields tickDelta, lastFrameDuration, and
 * prevTimeMillis are no longer accessible in the same way, and the beginRenderTick
 * method signature has changed.
 *
 * The video recording functionality that depended on this mixin will need to be
 * re-implemented using the new RenderTickCounter API.
 */
public class RenderTickCounterMixin
{
    // Disabled - RenderTickCounter API changed in 1.21.1
}
