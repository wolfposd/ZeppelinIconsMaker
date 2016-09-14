package com.github.wolfposd.zepmaker.formats;

import java.awt.Color;

public class ZeppelinFormat implements IFormat {

    // black.png -> white
    // etched.png -> white (usually with black etches)
    // logo.png -> black
    // silver.png -> black

    public static final int SIZE_3X_PADDING = 8;
    public static final int SIZE_3X = 48;
    public static final int SIZE_2X = 32;
    public static final int SIZE_1X = 16;
    private static int[] sizes = {SIZE_1X, SIZE_2X, SIZE_3X};
    private static String[] filenames = {"black", "etched", "logo", "silver"};
    private static String[] fileappends = {"", "@2x", "@3x"};
    private static Color[] recolors = {Color.WHITE, Color.WHITE, Color.BLACK, Color.BLACK};

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
    public int getPaddingForBiggest() {
        return SIZE_3X_PADDING;
    }

    @Override
    public int getSizeBiggest() {
        return SIZE_3X;
    }

    @Override
    public Color[] getRecolors() {
        return recolors;
    }

    @Override
    public String getHelpText() {
        return "Converts images for use with the Zeppelin Tweak.\n"
                + "Converted images must be placed in /Library/Zeppelin/<FOLDERNAME>/\n" + "on your iPhone/iPad";
    }

}
