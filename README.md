# j6dof-flight-sim 
JavaFlightSim - A Six Degree of Freedom (6DOF) Flight Simulator Written in Java
Chris Ali

This program was written to apply my knowledge and background in Flight Dynamics to build my Java language skills. I hope for it to run as a simple analysis tool for flight dynamics, as well as a real time simulation for pilot in the loop use. One day I'd like to tie objectives or a scoring element into the simulation to make it a game as well.

Until I learn OpenGL or Java3D, this program will send/receive data to/from an existing flight simulator such as FlightGear or X-Plane.

##Assumptions and Simplifications
-JavaFlightSim currently assumes a flat, non-rotating Earth.
 
-The aircraft is a rigid body, with a constant mass.

-No ground reaction model has been included yet, so landing is not possible. 

-All aircraft stability derivatives are constants for now, so no stall modeling has been implemented.

-Only a simple (single) engine model is implemented for now. No engine effects (fuel burn, propeller effects) are modeled yet

-No pause, reset or trim methods are implemented yet.

##Future Ideas
-Include different aircraft, defined from a text file, selectable from a GUI

-Selectable starting locations (in air or on ground)

-Create an instrument panel GUI to run either as a Swing window, or as an Android app

-Moving map support in a Swing GUI or Android app

##Libraries
-This program makes use of the Apache Commons Math libraries to do the numerical integration (Runge-Kutta) necessary to make the program tick.

-In addition, the jFreeChart libraries are used to graph the simulation states after the simulation stops.

##Reference
-The 6DOF state equations come from *Small Unmanned Aircraft: Theory and Practice by Beard, R.W. and McLain, T.W.*

-