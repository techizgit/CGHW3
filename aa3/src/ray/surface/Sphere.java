package ray.surface;

import ray.accel.AxisAlignedBoundingBox;
import ray.material.Material;
import ray.math.Geometry;
import ray.math.Point2;
import ray.math.Point3;
import ray.math.Vector3;
import ray.misc.IntersectionRecord;
import ray.misc.LuminaireSamplingRecord;
import ray.misc.Ray;

/**
 * Represents a sphere as a center and a radius.
 *
 * @author ags
 */
public class Sphere extends Surface {
    
    /** Material for this sphere. */
    protected Material material = Material.DEFAULT_MATERIAL;
    
    /** The center of the sphere. */
    protected final Point3 center = new Point3();
    
    /** The radius of the sphere. */
    protected double radius = 1.0;
    
    /**
     * Default constructor, creates a sphere at the origin with radius 1.0
     */
    public Sphere() {        
    }
    
    /**
     * The explicit constructor. This is the only constructor with any real code
     * in it. Values should be set here, and any variables that need to be
     * calculated should be done here.
     *
     * @param newCenter The center of the new sphere.
     * @param newRadius The radius of the new sphere.
     * @param newMaterial The material of the new sphere.
     */
    public Sphere(Vector3 newCenter, double newRadius, Material newMaterial) {        
        material = newMaterial;
        center.set(newCenter);
        radius = newRadius;
        updateArea();
    }
    
    public void updateArea() {
    	area = 4 * Math.PI * radius*radius;
    	oneOverArea = 1. / area;
    }
    
    /**
     * @see ray1.surface.Surface#getMaterial()
     */
    public Material getMaterial() {
        return material;
    }
    
    /**
     * @see ray1.surface.Surface#setMaterial(ray1.material.Material)
     */
    public void setMaterial(Material material) {
        this.material = material;
    }
    
    /**
     * Returns the center of the sphere in the input Point3
     * @param outPoint output space
     */
    public void getCenter(Point3 outPoint) {        
        outPoint.set(center);        
    }
    
    /**
     * @param center The center to set.
     */
    public void setCenter(Point3 center) {        
        this.center.set(center);        
    }
    
    /**
     * @return Returns the radius.
     */
    public double getRadius() {
        return radius;
    }
    
    /**
     * @param radius The radius to set.
     */
    public void setRadius(double radius) {
        this.radius = radius;
        updateArea();
    }
    
    public boolean chooseSamplePoint(Point3 p, Point2 seed, LuminaireSamplingRecord lRec) {
        Geometry.squareToSphere(seed, lRec.frame.w);
        lRec.frame.o.set(center);
        lRec.frame.o.scaleAdd(radius, lRec.frame.w);
        lRec.frame.initFromW();
        lRec.pdf = oneOverArea;
        lRec.emitDir.sub(p, lRec.frame.o);
        return true;
    }
        
    /**
     * @see ray1.surface.Surface#intersect(ray1.misc.IntersectionRecord,
     *      ray1.misc.Ray)
     */
    public boolean intersect(IntersectionRecord outRecord, Ray ray) {
    	//about this part please see the details in my report
    	
    	Vector3 v = new Vector3();
    	v.sub(ray.origin, this.center);
    	Vector3 d = ray.direction;
    	double r = this.radius;
    	double a = d.dot(d);
    	double b = 2 * d.dot(v);
    	double c = v.dot(v) - r * r;
    	double delta = b * b - 4 * a * c;
    	if (delta < 0) {
    		return false;
    	}
    	double t = 0;
    	double t1 = (-b + Math.sqrt(delta)) / (2 * a);
    	double t2 = (-b - Math.sqrt(delta)) / (2 * a);
    	
    	//ray.start is not 0 but EPSILON=1e-6 here
    	if ((t1 >= ray.start && t1 <= ray.end) && (t2 >= ray.start && t2 <= ray.end)){
    		t = t1 < t2 ? t1 : t2; 		
    	}
    	else if (t1 >= ray.start && t1 <= ray.end){
    		t = t1;
    	}
    	else if (t2 >= ray.start && t2 <= ray.end){
    		t = t2;
    	}else return false;
    	
    	outRecord.t = t;
    	ray.evaluate(outRecord.frame.o, t);
    	outRecord.frame.w.sub(outRecord.frame.o, this.center); //Generate correct normal from the center of the sphere and the surface point
		outRecord.frame.w.normalize();
		outRecord.frame.initFromW();						   //Generate frame
		outRecord.surface = this;
		return true;	
    }
    

    
    /**
     * @see Object#toString()
     */
    public String toString() {
        return "sphere " + center + " " + radius + " " + material + " end";
    }
    
    /**
     * @see ray1.surface.Surface#addToBoundingBox(ray1.surface.accel.AxisAlignedBoundingBox)
     */
    public void addToBoundingBox(AxisAlignedBoundingBox inBox) {
        inBox.add(center.x - radius, center.y - radius, center.z - radius);
        inBox.add(center.x + radius, center.y + radius, center.z + radius);        
    }
    
}
