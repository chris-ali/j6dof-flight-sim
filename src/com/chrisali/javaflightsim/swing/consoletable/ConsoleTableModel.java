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

import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import com.chrisali.javaflightsim.simulation.integration.SimOuts;

public class ConsoleTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 4210192628630933689L;
	
	private List<Map<SimOuts, Double>> logsOut;
	private SimOuts[] columnNames = SimOuts.values();
	
	protected void setData(List<Map<SimOuts, Double>> list) {
		this.logsOut = list;
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return (logsOut == null) ? 0 : logsOut.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		try {
			Map<SimOuts, Double> simOutMap = logsOut.get(row);
			SimOuts simOut = SimOuts.getByIndex(col);
			
			return simOut.getFormat().format(simOutMap.get(simOut));
		} catch (IllegalArgumentException e) {
			return "-";
		}
	}
	
	@Override
	public String getColumnName(int column) {
		return columnNames[column].toString();
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return false;
	}
}
