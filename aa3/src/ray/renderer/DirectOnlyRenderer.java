package ray.renderer;


import ray.math.Point2;
import ray.math.Vector3;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.LuminaireSamplingRecord;
import ray.misc.Ray;
import ray.misc.Scene;
import ray.sampling.SampleGenerator;

/**
 * A renderer that computes radiance due to emitted and directly reflected light only.
 * 
 * @author cxz (at Columbia)
 */
public class DirectOnlyRenderer implements Renderer {
	
    
    /**
     * This is the object that is responsible for computing direct illumination.
     */
    DirectIlluminator direct = null;
        
    /**
     * The default is to compute using uninformed sampling wrt. projected solid angle over the hemisphere.
     */
    public DirectOnlyRenderer() {
        this.direct = new ProjSolidAngleIlluminator();
    }
    
    
    /**
     * This allows the rendering algorithm to be selected from the input file by substituting an instance
     * of a different class of DirectIlluminator.
     * @param direct  the object that will be used to compute direct illumination
     */
    public void setDirectIlluminator(DirectIlluminator direct) {
        this.direct = direct;
    }

    
    public void rayRadiance(Scene scene, Ray ray, SampleGenerator sampler, int sampleIndex, Color outColor) {
    	Color eRadiance = new Color();							//Emitted radiance
    	Color dRadiance = new Color();							//Diffused radiance
    	Vector3 incDir = new Vector3();
    	Vector3 outDir = new Vector3();

    	
    	
    	IntersectionRecord iRec = new IntersectionRecord();

    	if (scene.getFirstIntersection(iRec, ray)) {										//Make sure our "eye ray" reaches something
    		
    		Point2 directSeed = new Point2();
    		incDir.set(ray.direction);														//Incoming direction is our "eye ray" direction
    		sampler.sample(1, sampleIndex, directSeed); 									//Generate a sample seed
    		direct.directIllumination(scene, incDir, outDir, iRec, directSeed, dRadiance);	//Calculate diffused radiance
    		outColor.set(dRadiance);
			emittedRadiance(iRec, ray.direction, eRadiance);								//Calculate emitted radiance
			outColor.add(eRadiance);
    	} else {
			scene.getBackground().evaluate(ray.direction, outColor);	//If the "eye ray" cannot reach anything then simply set the output color as background color
		}
    }

    
    /**
     * Compute the radiance emitted by a surface.
     * @param iRec      Information about the surface point being shaded
     * @param dir          The exitant direction (surface coordinates)
     * @param outColor  The emitted radiance is written to this color
     */
    protected void emittedRadiance(IntersectionRecord iRec, Vector3 dir, Color outColor) {
    	if (iRec.surface.getMaterial().isEmitter()) {
    		LuminaireSamplingRecord lRec = new LuminaireSamplingRecord();
    		lRec.set(iRec);
    		lRec.emitDir.set(dir);
    		lRec.emitDir.scale(-1);															//The light direction is out coming
    		iRec.surface.getMaterial().emittedRadiance(lRec, outColor);						//Make sure the material is emitter and calculate the emitted radiance
    	} else {
    		outColor.set(0);
    	}
    }
}
