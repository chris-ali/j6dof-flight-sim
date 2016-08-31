package com.chrisali.javaflightsim.otw.particles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.util.vector.Matrix4f;

import com.chrisali.javaflightsim.otw.entities.Camera;
import com.chrisali.javaflightsim.otw.renderengine.Loader;
import com.chrisali.javaflightsim.otw.renderengine.ParticleRenderer;

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
