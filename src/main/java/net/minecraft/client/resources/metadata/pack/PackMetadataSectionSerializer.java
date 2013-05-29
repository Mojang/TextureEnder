package net.minecraft.client.resources.metadata.pack;

import com.google.gson.*;
import net.minecraft.client.resources.metadata.BaseMetadataSectionSerializer;
import net.minecraft.client.resources.metadata.MetadataSectionSerializer;

import java.lang.reflect.Type;

public class PackMetadataSectionSerializer extends BaseMetadataSectionSerializer<PackMetadataSection> implements JsonSerializer<PackMetadataSection> {
    @Override
    public PackMetadataSection deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        String desc = getString(object.get("description"), "description", null, 1, Integer.MAX_VALUE);
        int format = getInt(object.get("pack_format"), "pack_format", null, 1, Integer.MAX_VALUE);
        return new PackMetadataSection(desc, format);
    }

    @Override
    public JsonElement serialize(PackMetadataSection src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();

        result.addProperty("pack_format", src.getPackFormat());
        result.addProperty("description", src.getDescription());

        return result;
    }

    @Override
    public String getMetadataSectionName() {
        return "pack";
    }
}
