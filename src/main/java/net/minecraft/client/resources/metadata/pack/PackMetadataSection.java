package net.minecraft.client.resources.metadata.pack;

import net.minecraft.client.resources.metadata.MetadataSection;

public class PackMetadataSection implements MetadataSection {
    private final String description;
    private final int packFormat;

    public PackMetadataSection(String description, int packFormat) {
        this.description = description;
        this.packFormat = packFormat;
    }

    public String getDescription() {
        return description;
    }

    public int getPackFormat() {
        return packFormat;
    }
}
