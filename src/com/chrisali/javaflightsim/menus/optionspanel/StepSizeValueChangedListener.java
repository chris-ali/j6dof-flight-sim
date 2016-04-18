package com.chrisali.javaflightsim.menus.optionspanel;

import java.util.EventListener;

public interface StepSizeValueChangedListener extends EventListener {
	public void valueChanged(int newStepValue);
}
