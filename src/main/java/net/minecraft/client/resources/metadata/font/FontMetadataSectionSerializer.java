package net.minecraft.client.resources.metadata.font;

import com.google.gson.*;
import net.minecraft.client.resources.metadata.BaseMetadataSectionSerializer;
import net.minecraft.client.resources.metadata.MetadataSectionSerializer;

import java.lang.reflect.Type;

public class FontMetadataSectionSerializer extends BaseMetadataSectionSerializer<FontMetadataSection> {
    @Override
    public FontMetadataSection deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        float[] widths = new float[FontMetadataSection.NUMBER_OF_CHARS];
        float[] spacings = new float[FontMetadataSection.NUMBER_OF_CHARS];
        float[] lefts = new float[FontMetadataSection.NUMBER_OF_CHARS];
        float defaultWidth = FontMetadataSection.DEFAULT_WIDTH;
        float defaultSpacing = FontMetadataSection.DEFAULT_SPACING;
        float defaultLeft = FontMetadataSection.DEFAULT_LEFT;

        if (object.has("characters")) {
            if (!object.get("characters").isJsonObject()) {
                throw new JsonParseException("Invalid font->characters: expected object, was " + object.get("characters"));
            }

            JsonObject characters = object.getAsJsonObject("characters");

            if (characters.has("default")) {
                if (!characters.get("default").isJsonObject()) {
                    throw new JsonParseException("Invalid font->characters->default: expected object, was " + characters.get("default"));
                }
                JsonObject def = characters.getAsJsonObject("default");

                defaultWidth = getFloat(def.get("width"), "characters->default->width", defaultWidth, 0, Integer.MAX_VALUE);
                defaultSpacing = getFloat(def.get("spacing"), "characters->default->spacing", defaultSpacing, 0, Integer.MAX_VALUE);
                defaultLeft = getFloat(def.get("left"), "characters->default->left", defaultLeft, 0, Integer.MAX_VALUE);
            }

            for (int i = 0; i < FontMetadataSection.NUMBER_OF_CHARS; i++) {
                JsonElement element = characters.get(Integer.toString(i));
                float width = defaultWidth;
                float spacing = defaultSpacing;
                float left = defaultLeft;

                if (element != null) {
                    if (element.isJsonObject()) {
                        JsonObject character = element.getAsJsonObject();
                        width = getFloat(character.get("width"), "characters->" + i + "->width", width, 0, Integer.MAX_VALUE);
                        spacing = getFloat(character.get("spacing"), "characters->" + i + "->spacing", spacing, 0, Integer.MAX_VALUE);
                        left = getFloat(character.get("left"), "characters->" + i + "->left", left, 0, Integer.MAX_VALUE);
                    } else {
                        throw new JsonParseException("Invalid font->characters->" + i + ": expected object, was " + element);
                    }
                }

                widths[i] = width;
                spacings[i] = spacing;
                lefts[i] = left;
            }
        }

        return new FontMetadataSection(widths, lefts, spacings);
    }

    @Override
    public String getMetadataSectionName() {
        return "font";
    }
}
