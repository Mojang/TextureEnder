package net.minecraft.client.resources.metadata.font;

import net.minecraft.client.resources.metadata.MetadataSection;

public class FontMetadataSection implements MetadataSection {
    public static final float DEFAULT_WIDTH = 1;
    public static final float DEFAULT_SPACING = 0;
    public static final float DEFAULT_LEFT = 0;
    public static final int NUMBER_OF_CHARS = 16 * 16;

    private final float[] widths;
    private final float[] lefts;
    private final float[] spacings;

    public FontMetadataSection(float[] widths, float[] lefts, float[] spacings) {
        this.widths = widths;
        this.lefts = lefts;
        this.spacings = spacings;
    }

    public float getWidth(int pos) {
        return widths[pos];
    }

    public float getLeft(int pos) {
        return lefts[pos];
    }

    public float getSpacing(int pos) {
        return spacings[pos];
    }
}
