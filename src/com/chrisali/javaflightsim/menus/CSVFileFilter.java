package com.chrisali.javaflightsim.menus;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import com.chrisali.javaflightsim.utilities.Utilities;

public class CSVFileFilter extends FileFilter {

	@Override
	public boolean accept(File file) {
		if (file.isDirectory() || Utilities.getFileExtension(file.getName()).contains("csv"))
			return true;
		else
			return false;
	}

	@Override
	public String getDescription() {
		return "Comma-separated values (.csv) file";
	}

}
