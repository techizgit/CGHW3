package ray.light;

import ray.math.Point3;
import ray.misc.Color;

public class PointLight {
	
	public final Point3 location = new Point3();
	
	public final Color diffuse = new Color(0.01,0.01,0.01);	// diffuse component of the point light source
	
	public final Color specular = new Color(4.,4.,4.);	// specular component of the point light source
	
	public PointLight() { }
	
	/**
	 * Used internally by the parser to set the position of the point light source. 
	 *
	 * @deprecated
	 */
	public void setLocation(Point3 l) {
		location.set(l);
	}
	
	/**
	 * Used internally by the parser to set the diffuse component of the point light source. 
	 *
	 * @deprecated
	 */
	public void setDiffuse(Color s) {
		diffuse.set(s);
	}
	
	/**
	 * Used internally by the parser to set the specular component of the point light source. 
	 *
	 * @deprecated
	 */
	public void setSpecular(Color s) {
		specular.set(s);
	}
}
