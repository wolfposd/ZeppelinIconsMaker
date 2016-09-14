package com.github.wolfposd.zepmaker.formats;

import java.awt.Color;

public interface IFormat {

    /**
     * @return size of the images
     */
    public int[] getSizes();

    /**
     * @return base names of all files
     */
    public String[] getFileNames();

    /**
     * @return what is to be appended to the sizes @2x and so on
     */
    public String[] getFileappends();
    
    public int getSizeBiggest();
    
    public int getPaddingForBiggest();
    
    public Color[] getRecolors();
    
    public String getHelpText();

}
