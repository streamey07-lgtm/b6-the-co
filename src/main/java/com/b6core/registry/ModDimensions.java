package com.b6core.registry;

import com.b6core.B6Mod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public final class ModDimensions {
    /** Minecraft's core. Defined by data/b6core/dimension/core.json (basic flat checker world for now). */
    public static final ResourceKey<Level> CORE_LEVEL =
            ResourceKey.create(Registries.DIMENSION, new ResourceLocation(B6Mod.MOD_ID, "core"));

    private ModDimensions() {}
}
