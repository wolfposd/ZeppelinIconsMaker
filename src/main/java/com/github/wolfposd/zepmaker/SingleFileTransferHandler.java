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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.TransferHandler;

public class SingleFileTransferHandler extends TransferHandler {

    public interface FileDropHandler {
        /**
         * a File or <code>null</code> if multiple/none
         * 
         * @param f
         */
        public void fileHasBeenDropped(File f);
    }

    private static final long serialVersionUID = 1L;
    private FileDropHandler dropHandler;

    public SingleFileTransferHandler(FileDropHandler dp) {
        this.dropHandler = dp;
    }

    public boolean canImport(TransferHandler.TransferSupport support) {
        if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public boolean importData(TransferHandler.TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }
        try {
            Transferable t = support.getTransferable();
            List<File> l = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
            if (l.size() == 1) {
                dropHandler.fileHasBeenDropped(l.get(0));
            } else {
                dropHandler.fileHasBeenDropped(null);
            }

        } catch (UnsupportedFlavorException | IOException e) {
            return false;
        }

        return true;
    }
}