package ray.renderer;

import ray.math.Vector3;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.LuminaireSamplingRecord;
import ray.misc.Ray;
import ray.misc.Scene;
import ray.sampling.SampleGenerator;

public class BruteForcePathTracer extends PathTracer {
    /**
     * @param scene
     * @param ray
     * @param sampler
     * @param sampleIndex
     * @param outColor
     */
    protected void rayRadianceRecursive(Scene scene, Ray ray, 
            SampleGenerator sampler, int sampleIndex, int level, Color outColor) {
    	
    	
    	// W4160 TODO (B)
    	//
        // Find the visible surface along the ray, then add emitted and reflected radiance
        // to get the resulting color.
    	//
    	// If the ray depth is less than the limit (depthLimit), you need
    	// 1) compute the emitted light radiance from the current surface if the surface is a light surface
    	// 2) reflected radiance from other lights and objects. You need recursively compute the radiance
    	//    hint: You need to call gatherIllumination(...) method.
    	
    	
    	
    	//I did this homework before lec14 so the structure is not the same as what the professor suggested us to do,
    	//but the basic idea should be the same.
    	
    	Color eRadiance = new Color();
    	Color dRadiance = new Color();
    	Vector3 incDir = new Vector3();

    	IntersectionRecord iRec = new IntersectionRecord();

    	if (scene.getFirstIntersection(iRec, ray)) {
    		
    		incDir.set(ray.direction);		
    		
    		dRadiance = gatherIllumination(scene, incDir, iRec,sampler,sampleIndex, 0);//Please note that I changed gatherIllumination to a function.
    		
    		outColor.set(dRadiance);												//Diffused radiance
    		
    		
			emittedRadiance(iRec, ray.direction, eRadiance);
			outColor.add(eRadiance);   												//Emitted radiance
    	} else {
 			scene.getBackground().evaluate(ray.direction, outColor);				//If the "eye ray" do not intersect with anything just set the color to uniform color.
 		}
    	
    }
    
    protected void emittedRadiance(IntersectionRecord iRec, Vector3 dir, Color outColor) {
    	if (iRec.surface.getMaterial().isEmitter()) {
    		LuminaireSamplingRecord lRec = new LuminaireSamplingRecord();
    		lRec.set(iRec);
    		lRec.emitDir.set(dir);
    		lRec.emitDir.scale(-1);
    		iRec.surface.getMaterial().emittedRadiance(lRec, outColor);
    	} else {
    		outColor.set(0);
    	}
    }
    

}
