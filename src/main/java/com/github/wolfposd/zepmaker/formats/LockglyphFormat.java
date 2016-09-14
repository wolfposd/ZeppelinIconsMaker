package com.github.wolfposd.zepmaker.formats;

import java.awt.Color;

public class LockglyphFormat implements IFormat {

    private static int[] sizes = {45, 90, 135};
    private static String[] filenames = {"IdleImage"};
    private String[] fileappends = {"", "@2x", "@3x"};
    private Color[] recolor = {Color.BLACK};

    @Override
    public int[] getSizes() {
        return sizes;
    }

    @Override
    public String[] getFileNames() {
        return filenames;
    }

    @Override
    public String[] getFileappends() {
        return fileappends;
    }

    @Override
    public int getSizeBiggest() {
        return sizes[2];
    }

    @Override
    public int getPaddingForBiggest() {
        return 0;
    }

    @Override
    public Color[] getRecolors() {
        return recolor;
    }

    @Override
    public String getHelpText() {
        return "Converts images for use with the LockGlyph Tweak.\n"
                + "Converted images must be placed in /Library/Application Support/LockGlyph/Themes/<FOLDERNAME>.bundle/\n"
                + "on your iPhone/iPad";
    }

}
