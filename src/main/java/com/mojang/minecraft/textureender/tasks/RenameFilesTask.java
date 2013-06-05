package com.mojang.minecraft.textureender.tasks;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.minecraft.textureender.ConverterGui;
import com.mojang.minecraft.textureender.ConverterTask;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.pack.PackMetadataSection;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static com.mojang.minecraft.textureender.ConverterGui.logLine;
import static com.mojang.minecraft.textureender.ConverterGui.relativize;

public class RenameFilesTask implements ConverterTask {
    private static final Splitter KEY_VALUE_SPLITTER = Splitter.on('=').omitEmptyStrings().trimResults().limit(2);
    private final Multimap<String, String> renames = HashMultimap.create();
    private final String taskName;

    public RenameFilesTask(String taskName, InputStream mapSource) throws IOException {
        this.taskName = taskName;
        List<String> lines = IOUtils.readLines(mapSource);

        for (String line : lines) {
            if (!Strings.isNullOrEmpty(line) && !line.startsWith("#")) {
                String[] split = Iterables.toArray(KEY_VALUE_SPLITTER.split(line), String.class);

                if (Iterables.size(KEY_VALUE_SPLITTER.split(line)) == 2) {
                    renames.put(split[0], split[1]);
                }
            }
        }
    }

    @Override
    public List<ConverterTask> run(File folder) {
        for (String key : renames.keySet()) {
            File original = new File(folder, key);
            File originalMetadata = new File(folder, key + ".mcmeta");

            if (original.isFile()) {
                boolean deleteFile = true;

                for (String renameTo : this.renames.get(key)) {
                    File newFile = new File(folder, renameTo);

                    if (renameTo.startsWith("purged/")) {
                        logLine("Deleted " + relativize(folder, original));
                        continue;
                    } else if (newFile.equals(original)) {
                        deleteFile = false;
                        continue;
                    }

                    try {
                        FileUtils.copyFile(original, newFile);
                    } catch (IOException e) {
                        logLine("Couldn't copy " + relativize(folder, original) + " to " + relativize(folder, newFile), e);
                        continue;
                    }

                    if (originalMetadata.isFile()) {
                        File newMetadata = new File(newFile.getAbsolutePath() + ".mcmeta");

                        try {
                            FileUtils.copyFile(originalMetadata, newMetadata);
                        } catch (IOException e) {
                            logLine("Couldn't copy " + relativize(folder, originalMetadata) + " to " + relativize(folder, newMetadata), e);
                            continue;
                        }
                    }

                    logLine("Copied " + relativize(folder, original) + " to " + relativize(folder, newFile));
                }

                if (deleteFile) {
                    FileUtils.deleteQuietly(original);
                    FileUtils.deleteQuietly(originalMetadata);
                }
            } else {
                logLine("Skipped renaming " + relativize(folder, original) + " - couldn't find it!");
            }
        }

        return null;
    }

    @Override
    public String getTaskName() {
        return taskName;
    }
}
