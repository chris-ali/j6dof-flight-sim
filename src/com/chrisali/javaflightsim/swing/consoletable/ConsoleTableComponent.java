/*******************************************************************************
 * Copyright (C) 2016-2020 Christopher Ali
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
package com.chrisali.javaflightsim.swing.consoletable;

import java.awt.BorderLayout;

import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;

import com.chrisali.javaflightsim.simulation.integration.SimOuts;

public class ConsoleTableComponent extends JComponent {

    private static final long serialVersionUID = 1L;

    private JTable table;
    private ConsoleTableModel consoleTableModel;
    private SwingWorker<Void, Integer> tableRefreshWorker;

    public ConsoleTableComponent(List<Map<SimOuts, Double>> logsOut) {
        consoleTableModel = new ConsoleTableModel();
		consoleTableModel.setData(logsOut);
		
		setLayout(new BorderLayout());
        
		table = new JTable(consoleTableModel);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setColumnSelectionAllowed(true);
        table.setRowSelectionAllowed(true);

        tableRefreshWorker = new SwingWorker<Void, Integer>() {
			@Override
			protected void done() {}

			@Override
			protected Void doInBackground() throws Exception {
				while (true) {
					consoleTableModel.fireTableDataChanged();
					Thread.sleep(100);
				}
			}
		};
        
        setPreferredSize(getToolkit().getScreenSize());
    
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public void startTableRefresh() {
		tableRefreshWorker.execute();
	}

	public void stopTableRefresh() {
		tableRefreshWorker.cancel(true);
	}
}