package com.mojang.minecraft.textureender;

import java.io.File;

public interface ConverterTask {
    public void run(File folder);
    public String getTaskName();
}
