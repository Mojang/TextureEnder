package net.minecraft.client.resources.metadata;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public abstract class BaseMetadataSectionSerializer<T extends MetadataSection> implements MetadataSectionSerializer<T> {
    protected float getFloat(JsonElement element, String name, Float def, float min, float max) {
        name = getMetadataSectionName() + "->" + name;

        if (element == null) {
            if (def == null) {
                throw new JsonParseException("Missing " + name + ": expected float");
            } else {
                return def;
            }
        }

        if (!element.isJsonPrimitive()) {
            throw new JsonParseException("Invalid " + name + ": expected float, was " + element);
        }

        try {
            float result = element.getAsFloat();

            if (result < min) {
                throw new JsonParseException("Invalid " + name + ": expected float >= " + min + ", was " + result);
            } else if (result > max) {
                throw new JsonParseException("Invalid " + name + ": expected float <= " + max + ", was " + result);
            }

            return result;
        } catch (NumberFormatException ex) {
            throw new JsonParseException("Invalid " + name + ": expected float, was " + element, ex);
        }
    }

    protected int getInt(JsonElement element, String name, Integer def, int min, int max) {
        name = getMetadataSectionName() + "->" + name;

        if (element == null) {
            if (def == null) {
                throw new JsonParseException("Missing " + name + ": expected int");
            } else {
                return def;
            }
        }

        if (!element.isJsonPrimitive()) {
            throw new JsonParseException("Invalid " + name + ": expected int, was " + element);
        }

        try {
            int result = element.getAsInt();

            if (result < min) {
                throw new JsonParseException("Invalid " + name + ": expected int >= " + min + ", was " + result);
            } else if (result > max) {
                throw new JsonParseException("Invalid " + name + ": expected int <= " + max + ", was " + result);
            }

            return result;
        } catch (NumberFormatException ex) {
            throw new JsonParseException("Invalid " + name + ": expected int, was " + element, ex);
        }
    }

    protected String getString(JsonElement element, String name, String def, int minLength, int maxLength) {
        name = getMetadataSectionName() + "->" + name;

        if (element == null) {
            if (def == null) {
                throw new JsonParseException("Missing " + name + ": expected string");
            } else {
                return def;
            }
        }

        if (!element.isJsonPrimitive()) {
            throw new JsonParseException("Invalid " + name + ": expected string, was " + element);
        }

        String result = element.getAsString();

        if (result.length() < minLength) {
            throw new JsonParseException("Invalid " + name + ": expected string length >= " + minLength + ", was " + result);
        } else if (result.length() > maxLength) {
            throw new JsonParseException("Invalid " + name + ": expected string length <= " + maxLength + ", was " + result);
        }

        return result;
    }

    protected boolean getBoolean(JsonElement element, String name, Boolean def) {
        name = getMetadataSectionName() + "->" + name;

        if (element == null) {
            if (def == null) {
                throw new JsonParseException("Missing " + name + ": expected boolean");
            } else {
                return def;
            }
        }

        if (!element.isJsonPrimitive()) {
            throw new JsonParseException("Invalid " + name + ": expected boolean, was " + element);
        }

        boolean result = element.getAsBoolean();
        return result;
    }
}
