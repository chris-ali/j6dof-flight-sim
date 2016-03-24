package com.chrisali.javaflightsim.menus;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;

public class ResizingCardLayout extends CardLayout {

	private static final long serialVersionUID = 4014980659323192230L;

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		Component current = findCurrentComponent(parent);
		
		if (current != null) {
			Insets insets = parent.getInsets();
			Dimension preferred = current.getPreferredSize();
			
			preferred.width += insets.left + insets.right;
			preferred.height += insets.top + insets.bottom;
			
			return preferred;
		}

		return super.preferredLayoutSize(parent);
	}

	public Component findCurrentComponent(Container parent) {
		for (Component comp : parent.getComponents()) {
			if (comp.isVisible())
				return comp;
		}
		return null;
	}
	
}
