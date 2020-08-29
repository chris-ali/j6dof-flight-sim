# Java Flight Simulator 
*A Six Degree of Freedom (6DOF) Flight Simulator Written in Java by Chris Ali*

This program was written to apply my knowledge and background in Flight Dynamics to build my Java language skills. It can run as a simple analysis tool for flight dynamics, as well as a real time simulation for pilot-in-the-loop use. When configured as a pilot in the loop simulation, a LWJGL out-the-window display opens and the user can control the simulation using a joystick or keyboard.

One day I'd like to tie objectives or a scoring element into the simulation to make it a game as well.

![flight1](https://user-images.githubusercontent.com/15899769/41509534-5f47bc40-7223-11e8-8ef0-eb4707c23bf7.PNG)

![flight2](https://user-images.githubusercontent.com/15899769/41509536-637cc350-7223-11e8-8c19-971704a33a87.PNG)

![flight3](https://user-images.githubusercontent.com/15899769/41509537-66a0b2b2-7223-11e8-9ce8-05e963e096f6.PNG)

![analysis](https://user-images.githubusercontent.com/15899769/41509538-6cc8fdd4-7223-11e8-8b62-106062c8bda4.PNG)

## Assumptions and Simplifications
- JavaFlightSim assumes a non-rotating Earth.
 
- The aircraft is a rigid body, with a constant mass.

- A simple ground reaction model is implemented, but is unstable while on the ground stationary and on hills 

- Only a simple propeller engine model is implemented for now. No engine effects (fuel burn, propeller effects) are modeled yet

## Future Ideas
- Selectable starting locations (in air or on ground)

- Configurable weather and time of day

- Use of USAF Digital DATCOM to calculate stability derivatives for creating custom and/or more accurate aircraft

## Libraries
- The Apache Commons Math libraries perform the numerical integration (Runge-Kutta) necessary to make the program tick.

- jFreeChart libraries are used to graph the aircraft's response.

- LWJGL is used to create the out the window display and sounds using OpenGL through GLFW and OpenAL

## Reference
- The 6DOF state equations come from *Small Unmanned Aircraft: Theory and Practice by Beard, R.W. and McLain, T.W.*

- Trimming method and other simulation strategies come from *Principles of Flight Simulation, Allerton David* 

## Building and Development
- Maven is required to build project and generate natives

- Currently being developed with Java 11, but should be compatible with Java 14 and Java 8 

- Running the build script ./buildJavaFlightSim.sh will execute the Maven package task and package all required dependencies and natives

- buildJavaFlightSim.sh should work in Windows if run through WSL shell or Cygwin
