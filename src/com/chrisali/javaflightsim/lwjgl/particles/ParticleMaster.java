/*******************************************************************************
 * Copyright (C) 2016-2017 Christopher Ali
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
package com.chrisali.javaflightsim.lwjgl.particles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.util.vector.Matrix4f;

import com.chrisali.javaflightsim.lwjgl.entities.Camera;
import com.chrisali.javaflightsim.lwjgl.renderengine.Loader;
import com.chrisali.javaflightsim.lwjgl.renderengine.ParticleRenderer;

public class ParticleMaster {
	
	private static Map<ParticleTexture, List<Particle>> particles = new HashMap<>();
	private static ParticleRenderer renderer;
	
	public static void init(Loader loader, Matrix4f projectionMatrix) {
		particles.clear();
		renderer = new ParticleRenderer(loader, projectionMatrix);
	}
	
	public static void update(Camera camera) {
		Iterator<Entry<ParticleTexture, List<Particle>>> mapIterator = particles.entrySet().iterator();
		
		while(mapIterator.hasNext()) {
			Entry<ParticleTexture, List<Particle>> entry = mapIterator.next();
			List<Particle> list = entry.getValue();
			
			Iterator<Particle> listIterator = list.iterator();
		
			while(listIterator.hasNext()) {
				Particle particle = listIterator.next();
				boolean alive = particle.update(camera);
				
				if(!alive) {
					listIterator.remove();
					
					if(list.isEmpty())
						mapIterator.remove();
				}
			}
			
			if(!entry.getKey().usesAdditiveBlending())
				particleInsertionSort(list);
		}
	}
	
	public static void renderParticles(Camera camera) {
		renderer.render(particles, camera);
	}
	
	public static void cleanUp() {
		renderer.cleanUp();
	}
	
	public static void addParticle(Particle particle) {
		List<Particle> list = particles.get(particle.getTexture());
		
		if (list == null) {
			list = new ArrayList<>();
			particles.put(particle.getTexture(), list);
		}
		
		list.add(particle);
	}
	
	/**
     * Sorts a list of particles so that the particles with the highest distance
     * from the camera are first, and the particles with the shortest distance
     * are last.
     * 
     * @param list
     *            - the list of particles needing sorting.
     */
    public static void particleInsertionSort(List<Particle> list) {
        for (int i = 1; i < list.size(); i++) {
            Particle item = list.get(i);
            
            if (item.getDistaceFromCamera() > list.get(i - 1).getDistaceFromCamera()) {
                int attemptPos = i - 1;
                
                while (attemptPos != 0 && list.get(attemptPos - 1).getDistaceFromCamera() < item.getDistaceFromCamera())
                    attemptPos--;

                list.remove(i);
                list.add(attemptPos, item);
            }
        }
    }
}
