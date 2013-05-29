package net.minecraft.client.resources.metadata.texture;

import com.google.gson.*;
import net.minecraft.client.resources.metadata.BaseMetadataSectionSerializer;
import net.minecraft.client.resources.metadata.MetadataSectionSerializer;

import java.lang.reflect.Type;

public class TextureMetadataSectionSerializer extends BaseMetadataSectionSerializer<TextureMetadataSection> {
    @Override
    public TextureMetadataSection deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        boolean blur = getBoolean(object.get("blur"), "blur", TextureMetadataSection.DEFAULT_BLUR);
        boolean clamp = getBoolean(object.get("clamp"), "clamp", TextureMetadataSection.DEFAULT_CLAMP);
        return new TextureMetadataSection(blur, clamp);
    }

    @Override
    public String getMetadataSectionName() {
        return "texture";
    }
}
