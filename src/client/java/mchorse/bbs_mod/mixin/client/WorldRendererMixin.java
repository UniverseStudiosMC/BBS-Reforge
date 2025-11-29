package mchorse.bbs_mod.mixin.client;

/**
 * WorldRendererMixin was disabled in 1.21.1 - the renderSky method signature changed
 * from renderSky(MatrixStack, Matrix4f, float, Camera, boolean, Runnable) to a different
 * signature.
 *
 * The chroma sky and render layer functionality that depended on this mixin will need
 * to be re-implemented using the new WorldRenderer API.
 */
public class WorldRendererMixin
{
    // Disabled - WorldRenderer.renderSky signature changed in 1.21.1
}
