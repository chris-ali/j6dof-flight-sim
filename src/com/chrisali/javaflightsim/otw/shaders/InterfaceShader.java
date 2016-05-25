package com.chrisali.javaflightsim.otw.shaders;

import org.lwjgl.util.vector.Matrix4f;

public class InterfaceShader extends ShaderProgram {
	
	private static final String VERTEX_FILE = SHADER_ROOT_PATH + "interfaceVertexShader.txt";
	private static final String FRAGMENT_FILE = SHADER_ROOT_PATH + "interfaceFragmentShader.txt";
     
    private int location_transformationMatrix;
 
    public InterfaceShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }
     
    public void loadTransformation(Matrix4f matrix){
        super.loadMatrix(location_transformationMatrix, matrix);
    }
 
    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
    }
 
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
}
