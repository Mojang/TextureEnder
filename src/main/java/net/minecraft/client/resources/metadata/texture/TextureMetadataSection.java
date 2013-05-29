package net.minecraft.client.resources.metadata.texture;

import net.minecraft.client.resources.metadata.MetadataSection;

public class TextureMetadataSection implements MetadataSection {
    public static final boolean DEFAULT_BLUR = false;
    public static final boolean DEFAULT_CLAMP = false;

    private final boolean blur;
    private final boolean clamp;

    public TextureMetadataSection(boolean blur, boolean clamp) {
        this.blur = blur;
        this.clamp = clamp;
    }

    public boolean isBlur() {
        return blur;
    }

    public boolean isClamp() {
        return clamp;
    }
}
