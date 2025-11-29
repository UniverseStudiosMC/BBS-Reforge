package mchorse.bbs_mod.client;

import mchorse.bbs_mod.forms.entities.IEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LightBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages dynamic lighting for BBS entities holding light-emitting items.
 * Uses actual Minecraft light blocks for perfect lighting compatibility with shaders.
 */
public class DynamicLightManager
{
    private static final DynamicLightManager INSTANCE = new DynamicLightManager();

    /**
     * Map of entity IDs to their current light block position
     */
    private final Map<Integer, LightSourceData> activeLights = new ConcurrentHashMap<>();

    /**
     * Set of positions where we placed light blocks (to restore original state)
     */
    private final Map<BlockPos, BlockState> originalStates = new ConcurrentHashMap<>();

    /**
     * Tracks which light sources were updated this frame
     */
    private final Set<Integer> updatedThisFrame = new HashSet<>();

    /**
     * Whether the dynamic light system is enabled
     */
    private boolean enabled = true;

    /**
     * Counter for generating unique entity IDs for BBS entities
     */
    private int entityIdCounter = 0;

    public static DynamicLightManager getInstance()
    {
        return INSTANCE;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        if (!enabled && this.enabled)
        {
            // Turning off - remove all lights
            clearAllLights();
        }
        this.enabled = enabled;
    }

    /**
     * Called at the start of each render frame to prepare for updates.
     */
    public void beginFrame()
    {
        updatedThisFrame.clear();
    }

    /**
     * Called at the end of each render frame to remove stale lights.
     */
    public void endFrame()
    {
        if (!enabled) return;

        // Remove lights for entities that weren't updated this frame
        Iterator<Map.Entry<Integer, LightSourceData>> iter = activeLights.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry<Integer, LightSourceData> entry = iter.next();
            if (!updatedThisFrame.contains(entry.getKey()))
            {
                removeLightBlock(entry.getValue().currentPos);
                iter.remove();
            }
        }
    }

    /**
     * Update or create a light source for a BBS entity.
     *
     * @param entityId Unique identifier for this entity
     * @param entity The BBS entity
     * @param x World X position
     * @param y World Y position
     * @param z World Z position
     */
    public void updateEntityLight(int entityId, IEntity entity, double x, double y, double z)
    {
        if (!enabled) return;

        int lightLevel = getEntityLightLevel(entity);
        updatedThisFrame.add(entityId);

        if (lightLevel <= 0)
        {
            // Entity no longer emitting light - remove existing light
            LightSourceData existing = activeLights.remove(entityId);
            if (existing != null)
            {
                removeLightBlock(existing.currentPos);
            }
            return;
        }

        BlockPos newPos = BlockPos.ofFloored(x, y + 0.5, z);
        LightSourceData existing = activeLights.get(entityId);

        if (existing != null)
        {
            // Check if position changed
            if (!existing.currentPos.equals(newPos) || existing.lightLevel != lightLevel)
            {
                // Remove old light block
                removeLightBlock(existing.currentPos);

                // Place new light block
                placeLightBlock(newPos, lightLevel);
                existing.currentPos = newPos;
                existing.lightLevel = lightLevel;
            }
        }
        else
        {
            // New light source
            placeLightBlock(newPos, lightLevel);
            activeLights.put(entityId, new LightSourceData(newPos, lightLevel));
        }
    }

    /**
     * Generate a unique entity ID for BBS entities that don't have one.
     */
    public int generateEntityId()
    {
        return --entityIdCounter; // Negative to avoid collision with real entity IDs
    }

    /**
     * Remove all dynamic lights and restore original block states.
     */
    public void clearAllLights()
    {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) return;

        for (Map.Entry<BlockPos, BlockState> entry : originalStates.entrySet())
        {
            BlockPos pos = entry.getKey();
            BlockState original = entry.getValue();

            // Only restore if current block is still a light block
            BlockState current = world.getBlockState(pos);
            if (current.getBlock() == Blocks.LIGHT)
            {
                world.setBlockState(pos, original, Block.NOTIFY_ALL | Block.FORCE_STATE);
            }
        }

        activeLights.clear();
        originalStates.clear();
        updatedThisFrame.clear();
    }

    /**
     * Place a light block at the specified position.
     */
    private void placeLightBlock(BlockPos pos, int lightLevel)
    {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) return;

        BlockState currentState = world.getBlockState(pos);

        // Don't place light blocks in solid blocks
        if (currentState.isOpaque() || currentState.getBlock() == Blocks.WATER)
        {
            return;
        }

        // Save original state if we haven't already
        if (!originalStates.containsKey(pos))
        {
            originalStates.put(pos.toImmutable(), currentState);
        }

        // Create light block with appropriate level (clamped to 1-15)
        int level = Math.max(1, Math.min(15, lightLevel));
        BlockState lightState = Blocks.LIGHT.getDefaultState().with(LightBlock.LEVEL_15, level);

        // Place the light block (client-side only)
        world.setBlockState(pos, lightState, Block.NOTIFY_ALL | Block.FORCE_STATE);
    }

    /**
     * Remove a light block and restore the original state.
     */
    private void removeLightBlock(BlockPos pos)
    {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) return;

        BlockState original = originalStates.remove(pos);
        if (original != null)
        {
            // Only restore if current block is still a light block we placed
            BlockState current = world.getBlockState(pos);
            if (current.getBlock() == Blocks.LIGHT)
            {
                world.setBlockState(pos, original, Block.NOTIFY_ALL | Block.FORCE_STATE);
            }
        }
    }

    /**
     * Get the light level emitted by an entity based on held items.
     */
    public int getEntityLightLevel(IEntity entity)
    {
        int maxLight = 0;

        // Check main hand
        ItemStack mainHand = entity.getEquipmentStack(EquipmentSlot.MAINHAND);
        if (mainHand != null && !mainHand.isEmpty())
        {
            maxLight = Math.max(maxLight, getItemLuminance(mainHand));
        }

        // Check off hand
        ItemStack offHand = entity.getEquipmentStack(EquipmentSlot.OFFHAND);
        if (offHand != null && !offHand.isEmpty())
        {
            maxLight = Math.max(maxLight, getItemLuminance(offHand));
        }

        return maxLight;
    }

    /**
     * Get the luminance value of an item stack.
     */
    public static int getItemLuminance(ItemStack stack)
    {
        if (stack.getItem() instanceof BlockItem blockItem)
        {
            Block block = blockItem.getBlock();
            return block.getDefaultState().getLuminance();
        }

        return 0;
    }

    // ==================== Legacy methods for mixin compatibility ====================

    /**
     * @deprecated Use updateEntityLight with entityId instead
     */
    @Deprecated
    public void updateEntityLight(IEntity entity, double x, double y, double z)
    {
        // Generate a hash-based ID for backward compatibility
        int id = System.identityHashCode(entity);
        updateEntityLight(id, entity, x, y, z);
    }

    public void cleanup()
    {
        // Legacy method - now handled by beginFrame/endFrame
    }

    public int getDynamicLightAt(BlockPos pos)
    {
        // No longer needed - light blocks handle this natively
        return 0;
    }

    public int applyDynamicLight(int originalLight, BlockPos pos)
    {
        // No longer needed - light blocks handle this natively
        return originalLight;
    }

    /**
     * Internal class to store light source data.
     */
    private static class LightSourceData
    {
        BlockPos currentPos;
        int lightLevel;

        LightSourceData(BlockPos pos, int lightLevel)
        {
            this.currentPos = pos;
            this.lightLevel = lightLevel;
        }
    }
}
