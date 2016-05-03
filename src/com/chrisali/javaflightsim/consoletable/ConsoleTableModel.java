package com.chrisali.javaflightsim.consoletable;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import com.chrisali.javaflightsim.simulation.integration.SimOuts;

public class ConsoleTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 4210192628630933689L;
	
	private List<Map<SimOuts, Double>> logsOut;
	private Map<SimOuts, Double> simOut;
	private SimOuts[] columnNames = SimOuts.values();
	
	protected void setData(List<Map<SimOuts, Double>> list) {
		this.logsOut = list;
	}

	@Override
	public int getColumnCount() {
		return 72;
	}

	@Override
	public int getRowCount() {
		return (logsOut == null) ? 0 : logsOut.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		simOut = logsOut.get(row);
		
		DecimalFormat df6 = new DecimalFormat("#.######");
		DecimalFormat df4 = new DecimalFormat("#.####");
		DecimalFormat df2 = new DecimalFormat("#.##");
		DecimalFormat df1 = new DecimalFormat("#.#");
		
		try {
		switch (col) {
		case 0:
			return df2.format(simOut.get(SimOuts.TIME));
		case 1:
			return df4.format(simOut.get(SimOuts.U));
		case 2:
			return df4.format(simOut.get(SimOuts.U_DOT));
		case 3:
			return df4.format(simOut.get(SimOuts.V));
		case 4:
			return df4.format(simOut.get(SimOuts.V_DOT));
		case 5:
			return df4.format(simOut.get(SimOuts.W));
		case 6:
			return df4.format(simOut.get(SimOuts.W_DOT));
		case 7:
			return df1.format(simOut.get(SimOuts.NORTH));
		case 8:
			return df4.format(simOut.get(SimOuts.NORTH_DOT));
		case 9:
			return df1.format(simOut.get(SimOuts.EAST));
		case 10:
			return df4.format(simOut.get(SimOuts.EAST_DOT));
		case 11:
			return df1.format(simOut.get(SimOuts.ALT));
		case 12:
			return df2.format(simOut.get(SimOuts.ALT_DOT));
		case 13:
			return df4.format(simOut.get(SimOuts.PHI));
		case 14:
			return df4.format(simOut.get(SimOuts.PHI_DOT));
		case 15:
			return df4.format(simOut.get(SimOuts.THETA));
		case 16:
			return df4.format(simOut.get(SimOuts.THETA_DOT));
		case 17:
			return df4.format(simOut.get(SimOuts.PSI));
		case 18:
			return df4.format(simOut.get(SimOuts.PSI_DOT));
		case 19:
			return df4.format(simOut.get(SimOuts.P));
		case 20:
			return df4.format(simOut.get(SimOuts.P_DOT));
		case 21:
			return df4.format(simOut.get(SimOuts.Q));
		case 22:
			return df4.format(simOut.get(SimOuts.Q_DOT));
		case 23:
			return df4.format(simOut.get(SimOuts.R));
		case 24:
			return df4.format(simOut.get(SimOuts.R_DOT));
		case 25:
			return df2.format(simOut.get(SimOuts.TAS));
		case 26:
			return df4.format(simOut.get(SimOuts.BETA));
		case 27:
			return df4.format(simOut.get(SimOuts.ALPHA));
		case 28:
			return df4.format(simOut.get(SimOuts.ALPHA_DOT));
		case 29:
			return df4.format(simOut.get(SimOuts.MACH));
		case 30:
			return df4.format(simOut.get(SimOuts.LAT));
		case 31:
			return df6.format(simOut.get(SimOuts.LAT_DOT));
		case 32:
			return df4.format(simOut.get(SimOuts.LON));
		case 33:
			return df6.format(simOut.get(SimOuts.LON_DOT));
		case 34:
			return df4.format(simOut.get(SimOuts.A_X));
		case 35:
			return df4.format(simOut.get(SimOuts.AN_X));
		case 36:
			return df4.format(simOut.get(SimOuts.A_Y));
		case 37:
			return df4.format(simOut.get(SimOuts.AN_Y));
		case 38:
			return df4.format(simOut.get(SimOuts.A_Z));
		case 39:
			return df4.format(simOut.get(SimOuts.AN_Z));
		case 40:
			return df4.format(simOut.get(SimOuts.L));
		case 41:
			return df4.format(simOut.get(SimOuts.M));
		case 42:
			return df4.format(simOut.get(SimOuts.N));
		case 43:
			return df2.format(simOut.get(SimOuts.THRUST_1));
		case 44:
			return df2.format(simOut.get(SimOuts.RPM_1));
		case 45:
			return df2.format(simOut.get(SimOuts.FUEL_FLOW_1));
		case 46:
			return df2.format(simOut.get(SimOuts.THRUST_2));
		case 47:
			return df2.format(simOut.get(SimOuts.RPM_2));
		case 48:
			return df2.format(simOut.get(SimOuts.FUEL_FLOW_2));
		case 49:
			return df2.format(simOut.get(SimOuts.THRUST_3));
		case 50:
			return df2.format(simOut.get(SimOuts.RPM_3));
		case 51:
			return df2.format(simOut.get(SimOuts.FUEL_FLOW_3));
		case 52:
			return df2.format(simOut.get(SimOuts.THRUST_4));
		case 53:
			return df2.format(simOut.get(SimOuts.RPM_4));
		case 54:
			return df2.format(simOut.get(SimOuts.FUEL_FLOW_4));
		case 55:
			return df2.format(simOut.get(SimOuts.ELEVATOR));
		case 56:
			return df2.format(simOut.get(SimOuts.AILERON));
		case 57:
			return df2.format(simOut.get(SimOuts.RUDDER));
		case 58:
			return df1.format(simOut.get(SimOuts.THROTTLE_1));
		case 59:
			return df1.format(simOut.get(SimOuts.THROTTLE_2));
		case 60:
			return df1.format(simOut.get(SimOuts.THROTTLE_3));
		case 61:
			return df1.format(simOut.get(SimOuts.THROTTLE_4));
		case 62:
			return df1.format(simOut.get(SimOuts.PROPELLER_1));
		case 63:
			return df1.format(simOut.get(SimOuts.PROPELLER_2));
		case 64:
			return df1.format(simOut.get(SimOuts.PROPELLER_3));
		case 65:
			return df1.format(simOut.get(SimOuts.PROPELLER_4));
		case 66:
			return df1.format(simOut.get(SimOuts.MIXTURE_1));
		case 67:
			return df1.format(simOut.get(SimOuts.MIXTURE_2));
		case 68:
			return df1.format(simOut.get(SimOuts.MIXTURE_3));
		case 69:
			return df1.format(simOut.get(SimOuts.MIXTURE_4));
		case 70:
			return df1.format(simOut.get(SimOuts.GEAR));
		case 71:
			return df1.format(simOut.get(SimOuts.FLAPS));
		}
		} catch (IllegalArgumentException e) {return "-";}
		
		return null;
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
