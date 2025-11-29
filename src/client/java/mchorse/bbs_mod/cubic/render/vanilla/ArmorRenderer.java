package mchorse.bbs_mod.cubic.render.vanilla;

import com.google.common.collect.Maps;
import mchorse.bbs_mod.cubic.model.ArmorType;
import mchorse.bbs_mod.forms.entities.IEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.util.Identifier;

import java.util.Map;

public class ArmorRenderer
{
    private static final Map<String, Identifier> ARMOR_TEXTURE_CACHE = Maps.newHashMap();
    private final BipedEntityModel innerModel;
    private final BipedEntityModel outerModel;
    private final SpriteAtlasTexture armorTrimsAtlas;

    public ArmorRenderer(BipedEntityModel innerModel, BipedEntityModel outerModel, BakedModelManager bakery)
    {
        this.innerModel = innerModel;
        this.outerModel = outerModel;
        this.armorTrimsAtlas = bakery.getAtlas(TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE);
    }

    public void renderArmorSlot(MatrixStack matrices, VertexConsumerProvider vertexConsumers, IEntity entity, EquipmentSlot armorSlot, ArmorType type, int light)
    {
        ItemStack itemStack = entity.getEquipmentStack(armorSlot);
        Item item = itemStack.getItem();

        if (item instanceof ArmorItem armorItem)
        {
            if (armorItem.getSlotType() == armorSlot)
            {
                boolean innerModel = this.usesInnerModel(armorSlot);
                BipedEntityModel bipedModel = this.getModel(armorSlot);
                ModelPart part = this.getPart(bipedModel, type);

                bipedModel.setVisible(true);

                part.pivotX = part.pivotY = part.pivotZ = 0F;
                part.pitch = part.yaw = part.roll = 0F;
                part.xScale = part.yScale = part.zScale = 1F;

                // In 1.21.1, dyeable items use data components instead of DyeableArmorItem
                DyedColorComponent dyedColor = itemStack.get(DataComponentTypes.DYED_COLOR);
                if (dyedColor != null)
                {
                    int color = dyedColor.rgb();
                    float r = (float)(color >> 16 & 255) / 255.0F;
                    float g = (float)(color >> 8 & 255) / 255.0F;
                    float b = (float)(color & 255) / 255.0F;

                    this.renderArmorParts(part, matrices, vertexConsumers, light, armorItem, innerModel, r, g, b, null);
                    this.renderArmorParts(part, matrices, vertexConsumers, light, armorItem, innerModel, 1F, 1F, 1F, "overlay");
                }
                else
                {
                    this.renderArmorParts(part, matrices, vertexConsumers, light, armorItem, innerModel, 1F, 1F, 1F, null);
                }

                // TODO: In 1.21.1, armor trims should be accessed via data components
                // However, DataComponentTypes.TRIM may not be available in current mappings
                // Temporarily disabled until mappings are updated or alternative solution found
                // ArmorTrim trim = itemStack.get(DataComponentTypes.TRIM);
                // if (trim != null)
                // {
                //     this.renderTrim(part, armorItem.getMaterial(), matrices, vertexConsumers, light, trim, innerModel);
                // }

                if (itemStack.hasGlint())
                {
                    this.renderGlint(part, matrices, vertexConsumers, light);
                }
            }
        }
    }

    private ModelPart getPart(BipedEntityModel bipedModel, ArmorType type)
    {
        switch (type)
        {
            case HELMET -> {
                return bipedModel.head;
            }
            case CHEST, LEGGINGS -> {
                return bipedModel.body;
            }
            case LEFT_ARM -> {
                return bipedModel.leftArm;
            }
            case RIGHT_ARM -> {
                return bipedModel.rightArm;
            }
            case LEFT_LEG, LEFT_BOOT -> {
                return bipedModel.leftLeg;
            }
            case RIGHT_LEG, RIGHT_BOOT -> {
                return bipedModel.rightLeg;
            }
        }

        return bipedModel.head;
    }

    private void renderArmorParts(ModelPart part, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorItem item, boolean secondTextureLayer, float red, float green, float blue, String overlay)
    {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getArmorCutoutNoCull(this.getArmorTexture(item, secondTextureLayer, overlay)));

        // In 1.21.1, we need to wrap the vertex consumer to apply color
        if (red != 1.0F || green != 1.0F || blue != 1.0F)
        {
            vertexConsumer = new ColoredVertexConsumer(vertexConsumer, red, green, blue);
        }

        part.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
    }

    // TODO: ArmorTrim rendering disabled in 1.21.1 - API changed significantly
    // The trim methods now require different parameter types and the whole trim system
    // needs to be reimplemented to work with the new registry-based armor material system
    private void renderTrim(ModelPart part, RegistryEntry<ArmorMaterial> material, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorTrim trim, boolean leggings)
    {
        // Disabled - needs reimplementation for 1.21.1
    }

    private void renderGlint(ModelPart part, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light)
    {
        part.render(matrices, vertexConsumers.getBuffer(RenderLayer.getArmorEntityGlint()), light, OverlayTexture.DEFAULT_UV);
    }

    private BipedEntityModel getModel(EquipmentSlot slot)
    {
        return this.usesInnerModel(slot) ? this.innerModel : this.outerModel;
    }

    private boolean usesInnerModel(EquipmentSlot slot)
    {
        return slot == EquipmentSlot.LEGS;
    }

    private Identifier getArmorTexture(ArmorItem item, boolean secondLayer, String overlay)
    {
        // In 1.21.1, getMaterial() returns RegistryEntry<ArmorMaterial>
        RegistryEntry<ArmorMaterial> material = item.getMaterial();

        // Get the material name from the registry key
        String materialName = material.getKey()
            .map(key -> key.getValue().getPath())
            .orElse("unknown");

        String id = "textures/models/armor/" + materialName + "_layer_" + (secondLayer ? 2 : 1) + (overlay == null ? "" : "_" + overlay) + ".png";

        return ARMOR_TEXTURE_CACHE.computeIfAbsent(id, s -> Identifier.tryParse(s));
    }

    /**
     * Helper class to apply color multiplication to vertex consumers in 1.21.1
     * Extends VertexConsumer to properly implement the interface
     */
    private static class ColoredVertexConsumer implements VertexConsumer
    {
        private final VertexConsumer delegate;
        private final float red;
        private final float green;
        private final float blue;

        public ColoredVertexConsumer(VertexConsumer delegate, float red, float green, float blue)
        {
            this.delegate = delegate;
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        @Override
        public VertexConsumer vertex(float x, float y, float z)
        {
            this.delegate.vertex(x, y, z);
            return this;
        }

        @Override
        public VertexConsumer color(int red, int green, int blue, int alpha)
        {
            int r = (int)(red * this.red);
            int g = (int)(green * this.green);
            int b = (int)(blue * this.blue);
            this.delegate.color(r, g, b, alpha);
            return this;
        }

        @Override
        public VertexConsumer texture(float u, float v)
        {
            this.delegate.texture(u, v);
            return this;
        }

        @Override
        public VertexConsumer overlay(int u, int v)
        {
            this.delegate.overlay(u, v);
            return this;
        }

        @Override
        public VertexConsumer light(int u, int v)
        {
            this.delegate.light(u, v);
            return this;
        }

        @Override
        public VertexConsumer normal(float x, float y, float z)
        {
            this.delegate.normal(x, y, z);
            return this;
        }
    }
}