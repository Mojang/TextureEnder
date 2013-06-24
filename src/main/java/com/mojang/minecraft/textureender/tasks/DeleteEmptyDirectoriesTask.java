package com.mojang.minecraft.textureender.tasks;

import com.google.common.collect.Lists;
import com.mojang.minecraft.textureender.ConverterGui;
import com.mojang.minecraft.textureender.ConverterTask;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.*;

import java.io.File;
import java.util.Collection;
import java.util.List;

public class DeleteEmptyDirectoriesTask implements ConverterTask {
    @Override
    public List<ConverterTask> run(File root) {
        while (true) {
            Collection<File> dirs = listDirectories(root, EmptyFileFilter.EMPTY, TrueFileFilter.TRUE);

            if (dirs.isEmpty()) {
                break;
            } else {
                for (File dir : dirs) {
                    ConverterGui.logLine("Deleting empty directory " + ConverterGui.relativize(root, dir));
                    FileUtils.deleteQuietly(dir);
                }
            }
        }

        return null;
    }

    protected Collection<File> listDirectories(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter) {
        List<File> files = Lists.newArrayList();
        File[] found = directory.listFiles();

        if (found != null) {
            for (File file : found) {
                if (file.isDirectory() && fileFilter.accept(file)) {
                    files.add(file);
                }

                if (file.isDirectory() && dirFilter.accept(file)) {
                    files.addAll(listDirectories(file, fileFilter, dirFilter));
                }
            }
        }

        return files;
    }

    @Override
    public String getTaskName() {
        return "Delete empty directories";
    }
}
