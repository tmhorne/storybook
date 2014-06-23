package ch.intertec.storybook.toolkit.filefilter;

import java.io.File;

public class TXTFileFilter extends javax.swing.filechooser.FileFilter {
    public boolean accept(File file) {
		if (file.isDirectory()) {
			return true;
		}
        String filename = file.getName();
        return filename.endsWith(".txt");
    }
    public String getDescription() {
        return "*.txt";
    }
}
