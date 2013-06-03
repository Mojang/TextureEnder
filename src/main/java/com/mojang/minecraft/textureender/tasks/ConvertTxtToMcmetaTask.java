package com.mojang.minecraft.textureender.tasks;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.minecraft.textureender.ConverterGui;
import com.mojang.minecraft.textureender.ConverterTask;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSectionSerializer;
import net.minecraft.client.resources.metadata.pack.PackMetadataSection;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import static com.mojang.minecraft.textureender.ConverterGui.*;

public class ConvertTxtToMcmetaTask implements ConverterTask {
    private static final Splitter COMMA_SPLITTER = Splitter.on(',').omitEmptyStrings();
    private final Gson gson;

    public ConvertTxtToMcmetaTask() {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.registerTypeAdapter(AnimationMetadataSection.class, new AnimationMetadataSectionSerializer());
        this.gson = builder.create();
    }

    @Override
    public List<ConverterTask> run(File folder) {
        Iterator<File> iterator = FileUtils.iterateFiles(folder, new String[]{"txt"}, true);
        List<ConverterTask> result = Lists.newArrayList();

        while (iterator.hasNext()) {
            File file = iterator.next();
            convertFileMetadata(folder, file);

            if (file.equals(new File(folder, "pack.txt"))) {
                InputStream stream = getClass().getResourceAsStream("/rename_lists/texpack_to_respack.txt");

                if (stream != null) {
                    try {
                        result.add(new RenameFilesTask("Renaming legacy texpack files", stream));
                    } catch (IOException e) {
                        logLine("Couldn't read list for legacy file renames", e);
                    }
                } else {
                    logLine("Couldn't find list for legacy file renames");
                }
            }
        }

        return result;
    }

    @Override
    public String getTaskName() {
        return "Legacy .txt to .mcmeta conversion";
    }

    private void convertFileMetadata(File root, File txtFile) {
        File sourceFile = new File(FilenameUtils.removeExtension(txtFile.getAbsolutePath()) + ".png");
        File metadataFile = new File(sourceFile.getAbsolutePath() + ".mcmeta");
        boolean converted = false;

        if (metadataFile.exists()) {
            logLine("Ignoring " + relativize(root, txtFile) + " for metadata conversion as " + relativize(root, metadataFile) + " already exists...");
            return;
        }

        if (txtFile.equals(new File(root, "pack.txt"))) {
            // It's the pack information file
            logLine("Found pack metadata to convert: " + relativize(root, txtFile));

            List<String> lines;
            try {
                lines = FileUtils.readLines(txtFile);
            } catch (IOException e) {
                ConverterGui.logLine("Couldn't read " + relativize(root, txtFile) + " for pack metadata conversion", e);
                return;
            }

            if (lines.isEmpty()) {
                ConverterGui.logLine("Pack description is empty, cannot convert! At " + relativize(root, txtFile));
                return;
            }

            PackMetadataSection metadata = new PackMetadataSection(lines.get(0), 1);
            try {
                JsonElement pack = gson.toJsonTree(metadata);
                JsonObject json = new JsonObject();
                json.add("pack", pack);
                FileUtils.writeStringToFile(metadataFile, gson.toJson(json));
            } catch (Exception e) {
                logLine("Couldn't save new pack metadata to " + relativize(root, metadataFile), e);
                return;
            }

            converted = true;
        } else if (sourceFile.isFile()) {
            // It's an image metadata
            logLine("Found animation metadata to convert: " + relativize(root, txtFile));
            AnimationMetadataSection metadata = getAnimationMetadata(root, txtFile);

            if (metadata != null) {
                try {
                    JsonElement animation = gson.toJsonTree(metadata);
                    JsonObject json = new JsonObject();
                    json.add("animation", animation);
                    FileUtils.writeStringToFile(metadataFile, gson.toJson(json));
                } catch (Exception e) {
                    logLine("Couldn't save new animation metadata to " + relativize(root, metadataFile), e);
                    return;
                }
            }

            converted = true;
        }

        if (converted) {
            if (!FileUtils.deleteQuietly(txtFile)) {
                logLine("Couldn't delete legacy animation metadata " + relativize(root, txtFile));
            }
        }
    }

    private AnimationMetadataSection getAnimationMetadata(File root, File file) {
        try {
            List<String> text = FileUtils.readLines(file);
            List<AnimationFrame> frames = Lists.newArrayList();

            for (String line : text) {
                for (String token : COMMA_SPLITTER.split(line)) {
                    AnimationFrame frame;
                    int index = token.indexOf('*');

                    if (index > 0) {
                        int frameIndex = Integer.valueOf(token.substring(0, index));
                        int time = Integer.valueOf(token.substring(index + 1));
                        frame = new AnimationFrame(frameIndex, time);
                    } else {
                        frame = new AnimationFrame(Integer.valueOf(token));
                    }

                    if (frame.getIndex() < 0) {
                        logLine("Invalid negative frame-index in legacy metadata " + relativize(root, file) + ": " + token);
                    } else if (frame.getTime() <= 0 && !frame.isTimeUnknown()) {
                        logLine("Invalid non-positive frame time in legacy metadata " + relativize(root, file) + ": " + token);
                    } else {
                        frames.add(frame);
                    }
                }
            }

            return new AnimationMetadataSection(frames, AnimationMetadataSection.UNKNOWN_SIZE, AnimationMetadataSection.UNKNOWN_SIZE, AnimationMetadataSection.DEFAULT_FRAME_TIME);
        } catch (Exception e) {
            logLine("Couldn't read " + relativize(root, file) + " for legacy animation metadata parsing", e);
            e.printStackTrace();
            return null;
        }
    }
}
