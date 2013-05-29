package net.minecraft.client.resources.metadata;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

public interface MetadataSectionSerializer<T extends MetadataSection> extends JsonDeserializer<T> {
    String getMetadataSectionName();
}
