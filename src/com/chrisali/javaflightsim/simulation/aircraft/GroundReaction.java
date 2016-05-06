package com.chrisali.javaflightsim.simulation.aircraft;

public enum GroundReaction {
	NOSE_X  		("nose_x"),
	NOSE_Y  		("nose_y"),
	NOSE_Z  		("nose_z"),
	NOSE_DAMPING 	("nose_damping"),
	NOSE_SPRING 	("nose_spring"),
	LEFT_X  		("left_x"),
	LEFT_Y  		("left_y"),
	LEFT_Z  		("left_z"),
	LEFT_DAMPING 	("left_damping"),
	LEFT_SPRING 	("left_spring"),
	RIGHT_X 		("right_x"),
	RIGHT_Y 		("right_y"),
	RIGHT_Z 		("right_z"),
	RIGHT_DAMPING 	("right_damping"),
	RIGHT_SPRING 	("right_spring"),
	BRAKING_FORCE   ("braking_force");
	
	private String groundReactionParam;
	
	private GroundReaction(String groundReactionParam) {this.groundReactionParam = groundReactionParam;}

	public String toString() {return groundReactionParam;}
}
