package ray.renderer;

import ray.light.PointLight;
import ray.math.Vector3;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.Ray;
import ray.misc.Scene;
import ray.sampling.SampleGenerator;

public class PhongShader implements Renderer {
    
    private double phongCoeff = 2.5;
    
    public PhongShader() { }
    
    public void setAlpha(double a) {
        phongCoeff = a;
    }
    
    @Override
    public void rayRadiance(Scene scene, Ray ray, SampleGenerator sampler,
            int sampleIndex, Color outColor) {
        // W4160 TODO (A)
        // Here you need to implement the basic phong reflection model to calculate
        // the color value (radiance) along the given ray. The output color value 
        // is stored in outColor. 
        // 
        // For such a simple rendering algorithm, you might not need Monte Carlo integration
        // In this case, you can ignore the input variable, sampler and sampleIndex.
    	Vector3 normal = new Vector3();
    	Vector3 vDir = new Vector3();
    	Vector3 oDir = new Vector3();
    	Vector3 sDir = new Vector3();
    	Color diffColor = new Color();
    	Color specColor = new Color();
    	
    	IntersectionRecord iRec = new IntersectionRecord();
    	if (scene.getFirstIntersection(iRec, ray)) {

    		normal.set(iRec.frame.w);
    		normal.normalize();
    		vDir.set(ray.direction);
    		vDir.normalize();

    		
    		outColor.set(0);
    		for (PointLight pLight : scene.getPointLights()) {
    			oDir.sub(pLight.location, iRec.frame.o);
    			if (oDir.dot(normal) > 0) {
    				
    				iRec.surface.getMaterial().getBRDF(iRec).evaluate(iRec.frame, oDir, vDir, diffColor);
    				diffColor.scale(oDir.dot(normal));
    				diffColor.scale(pLight.diffuse);
    				outColor.add(diffColor);
    				
    				sDir.set(normal);
    				sDir.scale(2 * oDir.dot(normal));
    				sDir.sub(oDir);
    				sDir.normalize();
    				
    				iRec.surface.getMaterial().getBRDF(iRec).evaluate(iRec.frame, oDir, vDir, specColor);
    				
       	    		vDir.scale(-1);
    				specColor.scale(Math.pow(sDir.dot(vDir), phongCoeff));
    				specColor.scale(pLight.specular);
					outColor.add(specColor);
    			}
    		}
    	} else {
			/* otherwise, just compute background color */
			scene.getBackground().evaluate(ray.direction, outColor);
		}
    	
    }
}
