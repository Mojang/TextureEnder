package net.minecraft.client.resources.metadata.animation;

import net.minecraft.client.resources.metadata.MetadataSection;

import java.util.List;

public class AnimationMetadataSection implements MetadataSection {
    public static final int DEFAULT_FRAME_TIME = 1;
    public static final int UNKNOWN_SIZE = -1;

    private final List<AnimationFrame> frames;
    private final int frameWidth;
    private final int frameHeight;
    private final int defaultFrameTime;

    public AnimationMetadataSection(List<AnimationFrame> frames) {
        this(frames, UNKNOWN_SIZE, UNKNOWN_SIZE, DEFAULT_FRAME_TIME);
    }

    public AnimationMetadataSection(List<AnimationFrame> frames, int frameWidth, int frameHeight, int defaultFrameTime) {
        this.frames = frames;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.defaultFrameTime = defaultFrameTime;
    }

    public int getFrameHeight() {
        return frameHeight;
    }

    public int getFrameWidth() {
        return frameWidth;
    }

    public int getFrameCount() {
        return frames.size();
    }

    public int getDefaultFrameTime() {
        return defaultFrameTime;
    }

    public int getFrameTime(int frameIndex) {
        AnimationFrame frame = frames.get(frameIndex);

        if (frame.isTimeUnknown()) {
            return defaultFrameTime;
        } else {
            return frame.getTime();
        }
    }

    public boolean hasCustomFrameTime(int frameIndex) {
        return !frames.get(frameIndex).isTimeUnknown();
    }

    public int getFrameIndex(int frameIndex) {
        return frames.get(frameIndex).getIndex();
    }
}
