/*******************************************************************************
 * Copyright (C) 2016-2017 Christopher Ali
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *  If you have any questions about this project, you can visit
 *  the project's GitHub repository at: http://github.com/chris-ali/j6dof-flight-sim/
 ******************************************************************************/
package com.chrisali.javaflightsim.plotting;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class ProgressDialog extends JDialog {

	private static final long serialVersionUID = 4882434290876879153L;
	
	private JButton cancelButton;
	private JProgressBar progressBar;
	private ProgressDialogListener listener;
	
	
	public ProgressDialog(Window parent, String title) {
		super(parent, title);
		
		setLayout(new BorderLayout());
		
		//---------------- Cancel Button -------------------------
		
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(listener != null)
					listener.ProgressDialogCancelled();
			}
		});
		add(cancelButton, BorderLayout.SOUTH);
		
		//---------------- Progress Bar -------------------------
		
		progressBar = new JProgressBar();
		progressBar.setMaximum(10);
		progressBar.setStringPainted(true);
		progressBar.setString("Refreshing Plots...");
		add(progressBar, BorderLayout.CENTER);
				
		Dimension size = cancelButton.getPreferredSize();
		size.width = 400;
		progressBar.setPreferredSize(size);
		
		//================= Window Settings =======================
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(listener != null)
					listener.ProgressDialogCancelled();
			}
		});
		
		setSize(300, 100);
		setLocationRelativeTo(parent);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}
	
	public void setMaximum(int count) {
		progressBar.setMaximum(count);
	}
	
	public void setValue(int count) {
		progressBar.setString(String.format("%d%% Complete", (100*count)/progressBar.getMaximum()));
		progressBar.setValue(count);
	}
	
	public void setProgressDialogListener(ProgressDialogListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void setVisible(final boolean visible) {
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if(!visible) {
					try {Thread.sleep(62);} 
					catch (InterruptedException e) {}
				} else {
					progressBar.setValue(0);
				}
				
				if(visible) 
					setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				else
					setCursor(Cursor.getDefaultCursor());
				
				ProgressDialog.super.setVisible(visible);
			}
		});
	}

}
