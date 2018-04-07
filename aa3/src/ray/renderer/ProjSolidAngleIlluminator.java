package ray.renderer;

import ray.material.Material;
import ray.math.Geometry;
import ray.math.Point2;
import ray.math.Vector3;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.LuminaireSamplingRecord;
import ray.misc.Scene;
import ray.misc.Ray;


/**
 * This class computes direct illumination at a surface by the simplest possible approach: it estimates
 * the integral of incident direct radiance using Monte Carlo integration with a uniform sampling
 * distribution.
 * 
 * The class has two purposes: it is an example to serve as a starting point for other methods, and it
 * is a useful base class because it contains the generally useful <incidentRadiance> function.
 * 
 * @author srm, Changxi Zheng (at Columbia)
 */
public class ProjSolidAngleIlluminator extends DirectIlluminator {
   
    
    
    public void directIllumination(Scene scene, Vector3 incDir, Vector3 outDir, 
            IntersectionRecord iRec, Point2 seed, Color outColor) {
    	
    	Ray sampleRay = new Ray();
    	IntersectionRecord sampleIRec = new IntersectionRecord();
    	LuminaireSamplingRecord lRec = new LuminaireSamplingRecord();
    	Color BRDFColor = new Color();
    	Color sampleRadiance = new Color();

		Material mat = iRec.surface.getMaterial();                 //Get the material of the surface at this point(iRec)											  
    	
    	Geometry.squareToPSAHemisphere(seed, outDir);			   //Use the seed to generate a sample ray direction(local)
    	iRec.frame.frameToCanonical(outDir);					   //Transform local direction to global direction
    	outDir.normalize();
    	
    	sampleRay.set(iRec.frame.o, outDir);					   //Generate the sample ray
    	sampleRay.makeOffsetRay();


    	
    	if (scene.getFirstIntersection(sampleIRec, sampleRay) && sampleIRec.surface.getMaterial().isEmitter()) { //Make sure the ray reaches something and it is an emitter
    		

    		mat.getBRDF(iRec).evaluate(iRec.frame, incDir, outDir, BRDFColor);					//Get BRDF
    		
    		lRec.set(sampleIRec);																
    		lRec.emitDir.set(sampleRay.direction);
    		lRec.emitDir.scale(-1);																//The light direction is out coming
    		sampleIRec.surface.getMaterial().emittedRadiance(lRec, sampleRadiance);			    //Get the radiance of the emitter
    																		
    		outColor.set(Math.PI);																//Rendering equation wrt. PSA
    		outColor.scale(sampleRadiance);
    		outColor.scale(BRDFColor);
    	}

    }
    
}
