package com.chrisali.javaflightsim.datatransfer;

import java.util.EventListener;

public interface EnvironmentDataListener extends EventListener {
	public void onEnvironmentDataReceived(EnvironmentData environmentData);
}
