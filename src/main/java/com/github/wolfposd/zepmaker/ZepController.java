/*
 * Copyright (c) 2016 wolfposd
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.wolfposd.zepmaker;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ZepController {

    // black.png -> white
    // etched.png -> white (usually with black etches)
    // logo.png -> black
    // silver.png -> black

    public static final int SIZE_3X_PADDING = 8;
    public static final int SIZE_3X = 42;
    public static final int SIZE_2X = 28;
    public static final int SIZE_1X = 14;
    private int[] sizes = { SIZE_1X, SIZE_2X, SIZE_3X };
    private String[] filenames = { "black", "etched", "logo", "silver" };
    private String[] fileappends = { "", "@2x", "@3x" };
    private File lastFile = null;

    private ZepUI zepui;

    public ZepController() {

        zepui = new ZepUI();

        zepui.createButton.addActionListener(e -> createAction());
        zepui.loadImageButton.addActionListener(e -> loadAction());
        zepui.imageLoadedPreview.setTransferHandler(new SingleFileTransferHandler(f -> {
            if (f != null) {
                String extension = getFileExtension(f).toLowerCase();
                if ("png".equals(extension)) {
                    loadImageIntoPreview(f);
                } else {
                    zepui.setErrorText("Can only import PNG");
                }
            } else {
                zepui.setErrorText("Can only import one file");
            }
        }));

        zepui.setVisible(true);

    }

    public void createAction() {

        zepui.processing.setText("Starting Conversion...");
        zepui.processing.setForeground(Color.BLACK);

        SwingWorker<Integer, Object> w = new SwingWorker<Integer, Object>() {
            protected Integer doInBackground() throws Exception {
                return convertAndSaveImages();
            }
        };

        w.execute();
        w.addPropertyChangeListener(evt -> {
            if (evt.getNewValue() == SwingWorker.StateValue.DONE) {
                try {
                    int result = w.get();
                    switch (result) {
                    case 1:
                        zepui.setSuccessText("All done! :-)");
                        break;
                    case -1:
                        zepui.setErrorText("Couldn't create outputfolder :-(");
                        break;
                    case -2:
                        zepui.setErrorText("Couldn't create outputfolder :-(");
                        break;
                    }
                } catch (InterruptedException | ExecutionException e) {
                    zepui.setErrorText("Some shit happened :-(");
                    e.printStackTrace();
                }
            }
        });
    }

    private String getFileExtension(File f) {
        return f.getName().substring(f.getName().lastIndexOf(".") + 1);
    }

    private int convertAndSaveImages() {
        Image image = ((ImageIcon) zepui.imageLoadedPreview.getIcon()).getImage();

        Image[] images = preparedImages(image, zepui.keepColors.isSelected(), zepui.paddingEnabled.isSelected());

        String fileExtension = getFileExtension(lastFile);
        File newFolder = new File(lastFile.getParentFile(), lastFile.getName().substring(0, lastFile.getName().lastIndexOf(".")));

        if (!newFolder.exists()) {
            newFolder.mkdir();
        }

        if (newFolder.isDirectory() && newFolder.canWrite()) {
            for (int i = 0; i < images.length; i++) {
                Image curIm = images[i];
                int w = curIm.getWidth(null);
                int h = curIm.getHeight(null);

                for (int j = 0; j < sizes.length; j++) {
                    int size = sizes[j];

                    float scale = h / size;
                    int newW = (int) (w / scale);
                    int newH = (int) (h / scale);

                    Image newImg = curIm.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);

                    File destinationFile = new File(newFolder, filenames[i] + "" + fileappends[j] + "." + fileExtension);

                    try {
                        ImageIO.write(toBufferedImage(newImg), fileExtension, destinationFile);
                    } catch (IOException e) {
                        System.out.println("failed creating image");
                        e.printStackTrace();
                        return -2;
                    }
                }

            }
        } else {
            return -1;
        }
        return 1;
    }

    public void loadAction() {

        JFileChooser jf = new JFileChooser();
        jf.setFileFilter(new FileNameExtensionFilter("Only Imagefiles (png)", "png"));
        jf.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int res = jf.showOpenDialog(zepui.frame);
        File f = jf.getSelectedFile();

        if (res == JFileChooser.APPROVE_OPTION && f != null) {
            loadImageIntoPreview(f);
        }
    }

    public void loadImageIntoPreview(File f) {
        lastFile = f;
        ImageIcon icon = new StretchIcon(f.getAbsolutePath());
        zepui.imageLoadedPreview.setIcon(icon);
        zepui.imageLoadedPreview.setText("");
        zepui.setSuccessText("Loaded image.");
    }

    public Image[] preparedImages(Image image, boolean leavecolors, boolean padding) {
        Image[] result = new Image[4];

        // 1=white, 2=white, 3=black, 4=black
        if (!leavecolors) {
            result[0] = recolorImage(image, Color.WHITE.getRGB(), padding);
            result[1] = recolorImage(image, Color.WHITE.getRGB(), padding);
            result[2] = recolorImage(image, Color.BLACK.getRGB(), padding);
            result[3] = recolorImage(image, Color.BLACK.getRGB(), padding);
        } else {
            result[0] = padding ? padImage(image) : image;
            result[1] = padding ? padImage(image) : image;
            result[2] = padding ? padImage(image) : image;
            result[3] = padding ? padImage(image) : image;
        }

        return result;
    }

    public static Image padImage(Image origImage) {
        int width = origImage.getWidth(null);
        float scale = width / SIZE_3X;
        int padding = Math.round(SIZE_3X_PADDING * scale);

        BufferedImage bufimage = new BufferedImage(padding + origImage.getWidth(null), origImage.getHeight(null),
                BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g = bufimage.getGraphics();

        g.drawImage(origImage, padding, 0, null);

        return bufimage;

    }

    public static Image recolorImage(Image origImage, int newColor, boolean paddingEnabled) {

        int padding = 0;
        if (paddingEnabled) {
            int width = origImage.getWidth(null);
            float scale = width / SIZE_3X;
            padding = Math.round(SIZE_3X_PADDING * scale);
        }

        BufferedImage bufimage = new BufferedImage(padding + origImage.getWidth(null), origImage.getHeight(null),
                BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g = bufimage.getGraphics();

        g.drawImage(origImage, padding, 0, null);

        for (int x = padding; x < bufimage.getWidth(); x++) {
            for (int y = 0; y < bufimage.getHeight(); y++) {
                int rgb = bufimage.getRGB(x, y);
                Color c = new Color(rgb, true);
                if (c.getAlpha() > 0) {
                    bufimage.setRGB(x, y, newColor);
                }
            }
        }

        return bufimage;
    }

    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }
}
