package net.minecraft.client.resources.metadata.animation;

import com.google.common.collect.Lists;
import com.google.gson.*;
import net.minecraft.client.resources.metadata.BaseMetadataSectionSerializer;
import net.minecraft.client.resources.metadata.MetadataSectionSerializer;
import net.minecraft.client.resources.metadata.font.FontMetadataSection;

import java.lang.reflect.Type;
import java.util.List;

public class AnimationMetadataSectionSerializer extends BaseMetadataSectionSerializer<AnimationMetadataSection> implements JsonSerializer<AnimationMetadataSection> {
    @Override
    public AnimationMetadataSection deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        List<AnimationFrame> frames = Lists.newArrayList();
        JsonObject object = (JsonObject) json;
        int defaultFrameTime = getInt(object.get("frametime"), "frametime", AnimationMetadataSection.DEFAULT_FRAME_TIME, 1, Integer.MAX_VALUE);

        if (object.has("frames")) {
            try {
                JsonArray array = object.getAsJsonArray("frames");

                for (int i = 0; i < array.size(); i++) {
                    JsonElement element = array.get(i);

                    frames.add(getFrame(i, element));
                }
            } catch (ClassCastException ex) {
                throw new JsonParseException("Invalid animation->frames: expected array, was " + object.get("frames"), ex);
            }
        }

        int width = getInt(object.get("width"), "width", AnimationMetadataSection.UNKNOWN_SIZE, 1, Integer.MAX_VALUE);
        int height = getInt(object.get("height"), "height", AnimationMetadataSection.UNKNOWN_SIZE, 1, Integer.MAX_VALUE);

        return new AnimationMetadataSection(frames, width, height, defaultFrameTime);
    }

    private AnimationFrame getFrame(int index, JsonElement element) {
        if (element.isJsonPrimitive()) {
            try {
                return new AnimationFrame(element.getAsInt());
            } catch (NumberFormatException ex) {
                throw new JsonParseException("Invalid animation->frames->" + index + ": expected number, was " + element, ex);
            }
        } else if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            int time = getInt(object.get("time"), "frames->" + index + "->time", AnimationMetadataSection.UNKNOWN_SIZE, 1, Integer.MAX_VALUE);
            int frame = getInt(object.get("index"), "frames->" + index + "->index", null, 0, Integer.MAX_VALUE);
            return new AnimationFrame(frame, time);
        } else {
            throw new JsonParseException("Invalid animation->frames->" + index + ": unexpected " + element);
        }
    }

    @Override
    public JsonElement serialize(AnimationMetadataSection src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();

        result.addProperty("frametime", src.getDefaultFrameTime());
        if (src.getFrameWidth() != AnimationMetadataSection.UNKNOWN_SIZE) result.addProperty("width", src.getFrameWidth());
        if (src.getFrameHeight() != AnimationMetadataSection.UNKNOWN_SIZE) result.addProperty("height", src.getFrameHeight());

        if (src.getFrameCount() > 0) {
            JsonArray frames = new JsonArray();
            for (int i = 0; i < src.getFrameCount(); i++) {
                if (src.hasCustomFrameTime(i)) {
                    JsonObject frame = new JsonObject();

                    frame.addProperty("index", src.getFrameIndex(i));
                    frame.addProperty("time", src.getFrameTime(i));

                    frames.add(frame);
                } else {
                    frames.add(new JsonPrimitive(src.getFrameIndex(i)));
                }
            }
            result.add("frames", frames);
        }

        return result;
    }

    @Override
    public String getMetadataSectionName() {
        return "animation";
    }
}
