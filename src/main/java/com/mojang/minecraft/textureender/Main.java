package com.mojang.minecraft.textureender;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {
    public static void main(String[] args) {
        ConverterGui gui = new ConverterGui();

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
    }
}
