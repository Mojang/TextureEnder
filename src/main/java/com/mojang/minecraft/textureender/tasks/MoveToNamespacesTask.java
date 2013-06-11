package com.mojang.minecraft.textureender.tasks;

import com.mojang.minecraft.textureender.ConverterTask;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.mojang.minecraft.textureender.ConverterGui.logLine;
import static com.mojang.minecraft.textureender.ConverterGui.relativize;

public class MoveToNamespacesTask implements ConverterTask {
    @Override
    public List<ConverterTask> run(File root) {
        File targetDir = new File(root, "assets/minecraft/");

        root.listFiles()
        for (File file : FileUtils.listFilesAndDirs(root, TrueFileFilter.TRUE, TrueFileFilter.TRUE)) {
            if (!file.equals(new File(root, "pack.mcmeta")) && !file.equals(new File(root, "pack.png")) && !file.equals(root)) {
                try {
                    FileUtils.moveToDirectory(file, targetDir, true);
                    logLine("Moved " + relativize(root, file) + " to " + relativize(root, targetDir));
                } catch (IOException e) {
                    logLine("Couldn't move " + relativize(root, file) + " to " + relativize(root, targetDir), e);
                }
            }
        }

        return null;
    }

    @Override
    public String getTaskName() {
        return "Move from root to vanilla namespace";
    }
}
