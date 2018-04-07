package ray.renderer;

import ray.material.Material;
import ray.math.Geometry;
import ray.math.Point2;
import ray.math.Vector3;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.LuminaireSamplingRecord;
import ray.misc.Ray;
import ray.misc.Scene;
import ray.sampling.SampleGenerator;
import java.util.Random;

public abstract class PathTracer extends DirectOnlyRenderer {

    protected int depthLimit = 5;
    protected int backgroundIllumination = 0;

    

    private IntersectionRecord sampleIRec = new IntersectionRecord();
    
    
    private Ray sampleRay = new Ray();
    

    
    private Color sampleIrradiance = new Color();
    private LuminaireSamplingRecord lRec = new LuminaireSamplingRecord();
    
    


    public void setDepthLimit(int depthLimit) { this.depthLimit = depthLimit; }
    public void setBackgroundIllumination(int backgroundIllumination) { this.backgroundIllumination = backgroundIllumination; }

    @Override
    public void rayRadiance(Scene scene, Ray ray, SampleGenerator sampler, int sampleIndex, Color outColor) {
    
        rayRadianceRecursive(scene, ray, sampler, sampleIndex, 0, outColor);
    }

    protected abstract void rayRadianceRecursive(Scene scene, Ray ray, SampleGenerator sampler, int sampleIndex, int level, Color outColor);

    //I did this homework before lec14 so the structure is not the same as what the professor suggested us to do,
	//but the basic idea should be the same.
    //Please note that I changed gatherIllumination to a function.
    public Color gatherIllumination(Scene scene, Vector3 incDir, 
            IntersectionRecord iRec, SampleGenerator sampler, 	  
            int sampleIndex, int level) {

    	Color outColor = new Color();							  //Use stack data structure
        Color tColor = new Color(); 
        Random random = new Random();
        Point2 sampleSeed = new Point2();
        Color BRDFColor = new Color();    
        Vector3 outDir = new Vector3();
    	Material mat = iRec.surface.getMaterial();



		tColor.set(0);
		sampleSeed.set(random.nextDouble(),random.nextDouble());  //I am not sure why but if I use sampler.sample, the weird stains appear in "cbox".
		//sampler.sample(2 * level + 1, sampleIndex, sampleSeed); 

		Geometry.squareToPSAHemisphere(sampleSeed, outDir);       //Transform 2D square sample to 3D hemisphere sample with PSA

		iRec.frame.frameToCanonical(outDir);					  //Transform local direction to global direction
			
    	outDir.normalize();
    	
    	sampleRay.set(iRec.frame.o, outDir);				      //Generate a new sampling ray
    	sampleRay.makeOffsetRay();

		if (scene.getFirstIntersection(sampleIRec, sampleRay)) {							//If the ray intersects with something
			mat.getBRDF(iRec).evaluate(iRec.frame, incDir, outDir, BRDFColor);

			if (sampleIRec.surface.getMaterial().isEmitter()) {       			
    			lRec.set(sampleIRec);
    			lRec.emitDir.set(sampleRay.direction);
    			lRec.emitDir.scale(-1);
    		
    			sampleIRec.surface.getMaterial().emittedRadiance(lRec, sampleIrradiance);
    		

    			tColor.set(Math.PI);
    			tColor.scale(sampleIrradiance);
    			tColor.scale(BRDFColor);
    			outColor.add(tColor);														//Rendering equation

			}
		

			if (level < depthLimit - 1) {													//Check if recursive level goes beyond limit
			
    			lRec.set(sampleIRec);
    			lRec.emitDir.set(sampleRay.direction);
    			lRec.emitDir.scale(-1);

    			sampleSeed.set(random.nextDouble(),random.nextDouble());					//Same as line 63
    			//sampler.sample(2 * level + 2, sampleIndex, sampleSeed); 
    			Geometry.squareToPSAHemisphere(sampleSeed, outDir);

    			iRec.frame.frameToCanonical(outDir);
        		outDir.normalize();

    			sampleIrradiance.set(gatherIllumination(scene, outDir, sampleIRec,sampler,sampleIndex, level + 1));
    			//Recursive process start, note "level + 1" so that the process will finally come to an end
    			
    			tColor.set(Math.PI);
    			tColor.scale(sampleIrradiance);
    			tColor.scale(BRDFColor); 
    			outColor.add(tColor);														//Rendering equation
			}	
		} else {
			outColor.add(backgroundIllumination);		
			//I am not sure how to use this backgroundIllumination. It seems like an integer.
			//If I just leave it as the original value 1, the scene will be like all white (which is rather easy to understand).
			//Thus I change this value to 0 in .xml file.
		}

 	
		return outColor;
    	
    	
    }
}
