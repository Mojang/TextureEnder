package com.mojang.minecraft.textureender;

import java.io.File;
import java.util.List;

public interface ConverterTask {
    public List<ConverterTask> run(File folder);
    public String getTaskName();
}
