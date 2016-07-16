package com.chrisali.javaflightsim.consoletable;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import com.chrisali.javaflightsim.utilities.FileUtilities;

public class CSVFileFilter extends FileFilter {

	@Override
	public boolean accept(File file) {
		if (file.isDirectory() || FileUtilities.getFileExtension(file.getName()).contains("csv"))
			return true;
		else
			return false;
	}

	@Override
	public String getDescription() {
		return "Comma-separated values (.csv) file";
	}

}
