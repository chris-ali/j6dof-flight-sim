# j6dof-flight-sim 
JavaFlightSim - A Six Degree of Freedom (6DOF) Flight Simulator Written in Java by Chris Ali

This program was written to apply my knowledge and background in Flight Dynamics to build my Java language skills. It can run as a simple analysis tool for flight dynamics, as well as a real time simulation for pilot-in-the-loop use. When configured as a pilot in the loop simulation, a Swing instrument panel and out-the-window display open, and the user can control the simulation using a joystick.

One day I'd like to tie objectives or a scoring element into the simulation to make it a game as well.

##Assumptions and Simplifications
-JavaFlightSim currently assumes a non-rotating Earth.
 
-The aircraft is a rigid body, with a constant mass.

-A simple ground reaction model is implemented, but is unstable while on the ground stationary and on hills 

-Only a simple propeller engine model is implemented for now. No engine effects (fuel burn, propeller effects) are modeled yet

##Future Ideas
-Selectable starting locations (in air or on ground)

-Configurable weather and time of day

-Use of USAF Digital DATCOM to calculate stability derivatives for creating custom and/or more accurate aircraft

##Libraries
-This program makes use of the Apache Commons Math libraries to do the numerical integration (Runge-Kutta) necessary to make the program tick.

-In addition, the jFreeChart libraries are used to graph the simulation states after the simulation stops.

-jinput is used for joystick, mouse and keyboard support

-Instrument panel gauges were created using Gerrit Grunwald's SteelSeries Swing port: 
https://github.com/HanSolo/SteelSeries-Swing 

-LWJGL is used to create the out the window display using OpenGL

-OpenAL generates the sounds for the simulation 

##Reference
-The 6DOF state equations come from *Small Unmanned Aircraft: Theory and Practice by Beard, R.W. and McLain, T.W.*

-Trimming method and other simulation strategies come from *Principles of Flight Simulation, Allerton David* 