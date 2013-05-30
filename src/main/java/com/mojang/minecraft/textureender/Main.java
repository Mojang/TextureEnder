package com.mojang.minecraft.textureender;

import com.google.common.collect.Lists;
import com.mojang.minecraft.textureender.tasks.ConvertTxtToMcmetaTask;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Main {
    private static final List<ConverterTask> TASKS_TO_RUN = Lists.newArrayList();

    public static void main(String[] args) {
        ConverterGui gui = new ConverterGui();

        TASKS_TO_RUN.add(new ConvertTxtToMcmetaTask());

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Throwable e) {
        }

        JFrame frame = new JFrame("Minecraft Texture Ender");
        frame.add(gui);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });

        List<File> filesToConvert = Lists.newArrayList();

        if (args.length == 0) {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select texture/resource pack(s) to convert.");
            chooser.setMultiSelectionEnabled(true);
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            chooser.setFileFilter(new FileNameExtensionFilter("Zip texture packs", "zip"));

            if (chooser.showDialog(gui, "Convert packs") == JFileChooser.APPROVE_OPTION) {
                File[] files = chooser.getSelectedFiles();
                Collections.addAll(filesToConvert, files);
            }
        } else {
            for (String arg : args) {
                filesToConvert.add(new File(arg));
            }
        }

        ConverterGui.logLine("Selected " + filesToConvert.size() + " files/folders to convert...");
        runConversions(filesToConvert);
    }

    public static void runConversions(File folder) {
        if (!folder.isDirectory()) return;

        ConverterGui.logLine("Starting conversion on " + folder.getAbsolutePath());
        List<ConverterTask> tasks = Lists.newArrayList(TASKS_TO_RUN);

        for (int i = 0; i < tasks.size(); i++) {
            ConverterTask task = tasks.get(i);
            ConverterGui.logLine("Starting task '" + task.getTaskName() + "'");
            List<ConverterTask> extraTasks = task.run(folder);

            if (extraTasks != null) {
                tasks.addAll(i + 1, extraTasks);
            }
        }

        ConverterGui.logLine("Finished converting " + folder.getAbsolutePath());
    }

    public static void runConversions(List<File> files) {
        for (File file : files) {
            if (file.isFile()) {
                if (FilenameUtils.isExtension(file.getAbsolutePath(), "zip")) {
                    File copy = new File(FilenameUtils.removeExtension(file.getAbsolutePath()) + "-converted-" + System.currentTimeMillis());

                    try {
                        ConverterGui.logLine("Extracting " + file);
                        extractZip(file, copy);
                    } catch (IOException e) {
                        ConverterGui.logLine("Couldn't copy " + file + " to " + copy, e);
                        continue;
                    }

                    runConversions(copy);
                } else {
                    ConverterGui.logLine("Skipping " + file + " because I don't know what it is");
                }
            } else if (file.isDirectory()) {
                File copy = new File(file.getAbsolutePath() + "-converted-" + System.currentTimeMillis());

                try {
                    ConverterGui.logLine("Making a copy of " + file);
                    FileUtils.copyDirectory(file, copy);
                } catch (IOException e) {
                    ConverterGui.logLine("Couldn't copy " + file + " to " + copy, e);
                    continue;
                }

                runConversions(copy);
            } else {
                ConverterGui.logLine("Skipping " + file + " because I don't know what it is");
            }
        }

        ConverterGui.logLine("All done!");
    }

    private static void extractZip(File source, File output) throws IOException {
        output.mkdirs();

        ZipFile zip = new ZipFile(source);
        Enumeration<? extends ZipEntry> entries = zip.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String name = entry.getName();
            File dest = new File(output, name);

            if (dest.getParentFile() != null) dest.getParentFile().mkdirs();

            if (!entry.isDirectory()) {
                FileUtils.copyInputStreamToFile(zip.getInputStream(entry), dest);
            }
        }

        IOUtils.closeQuietly(zip);
    }
}
