package com.mojang.minecraft.textureender.tasks;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import com.mojang.minecraft.textureender.ConverterTask;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static com.mojang.minecraft.textureender.ConverterGui.logLine;
import static com.mojang.minecraft.textureender.ConverterGui.relativize;

public class PopulateDefaultMcMetaTask implements ConverterTask {
    private static final Splitter KEY_VALUE_SPLITTER = Splitter.on('=').omitEmptyStrings().trimResults().limit(2);
    private final Map<String, String> mcmetaPresets = Maps.newHashMap();
    private final String taskName;

    public PopulateDefaultMcMetaTask(String taskName, InputStream mapSource) throws IOException {
        this.taskName = taskName;
        List<String> lines = IOUtils.readLines(mapSource);

        for (String line : lines) {
            if (!Strings.isNullOrEmpty(line) && !line.startsWith("#")) {
                String[] split = Iterables.toArray(KEY_VALUE_SPLITTER.split(line), String.class);

                if (Iterables.size(KEY_VALUE_SPLITTER.split(line)) == 2) {
                    mcmetaPresets.put(split[0], split[1]);
                }
            }
        }
    }

    @Override
    public List<ConverterTask> run(File folder) {
        for (String key : mcmetaPresets.keySet()) {
            File original = new File(folder, key);
            File metadata = new File(original.getAbsolutePath() + ".mcmeta");

            if (original.isFile()) {
                if (metadata.isFile()) {
                    logLine("Skipped populating mcmeta of " + relativize(folder, original) + " - mcmeta already exists!");
                } else {
                    try {
                        FileUtils.writeStringToFile(metadata, mcmetaPresets.get(key));
                        logLine("Created preset mcmeta for " + relativize(folder, original));
                    } catch (IOException e) {
                        logLine("Couldn't create preset mcmeta of " + relativize(folder, original), e);
                    }
                }
            } else {
                logLine("Skipped populating mcmeta of " + relativize(folder, original) + " - couldn't find it!");
            }
        }

        return null;
    }

    @Override
    public String getTaskName() {
        return taskName;
    }
}
