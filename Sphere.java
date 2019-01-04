package comp557.a4;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * A simple sphere class.
 */
public class Sphere extends Intersectable {
    
	/** Radius of the sphere. */
	public double radius = 1;
    
	/** Location of the sphere center. */
	public Point3d center = new Point3d( 0, 0, 0 );
    
    /**
     * Default constructor
     */
    public Sphere() {
    	super();
    }
    
    /**
     * Creates a sphere with the request radius and center. 
     * 
     * @param radius
     * @param center
     * @param material
     */
    public Sphere( double radius, Point3d center, Material material ) {
    	super();
    	this.radius = radius;
    	this.center = center;
    	this.material = material;
    }
    
    @Override
    public void intersect( Ray ray, IntersectResult result ) {
    
        // TODO: Objective 2: intersection of ray with sphere
    	Vector3d p = new Vector3d(ray.eyePoint.x - center.x, ray.eyePoint.y - center.y, ray.eyePoint.z - center.z); 
    	Vector3d d = ray.viewDirection;
    	double t1 = 0; double t2 = 0;
    	t1 = (-d.dot(p) + Math.sqrt(Math.pow(d.dot(p), 2) - (d.dot(d))*(p.dot(p) - Math.pow(radius, 2)) ))/ d.dot(d) ;
    	t2 = (-d.dot(p) - Math.sqrt(Math.pow(d.dot(p), 2) - (d.dot(d))*(p.dot(p) - Math.pow(radius, 2)) ))/ d.dot(d) ;

    	if (Math.pow(d.dot(p), 2) - (d.dot(d))*(p.dot(p) - 1) < 0) {
    		return;
    	}
    	
    	if ( (t1 < t2) && (t1 >= 0) && (t1 < result.t) ) {
    		result.t = t1;
    		result.material = this.material;
            
            Point3d intersect = new Point3d();
            ray.getPoint(result.t, intersect);
            result.p = intersect;
            Vector3d nor = new Vector3d(intersect);
            nor.sub(center);
            nor.scale(1/radius);
            result.n = nor;
            
            result.p.x = 0.00001 * result.n.x + result.p.x;
            result.p.y = 0.00001 * result.n.y + result.p.y;
            result.p.z = 0.00001 * result.n.z + result.p.z;
    	}
    	else if ( !(t1<t2) && (t2 >= 0) && (t2 < result.t) ) {
    		result.t = t2;
    		result.material = this.material;
            
            Point3d intersect = new Point3d();
            ray.getPoint(result.t, intersect);
            result.p = intersect;
            Vector3d nor = new Vector3d(intersect);
            nor.sub(center);
            nor.scale(1/radius);
            result.n = nor;
            result.p.x = 0.00001 * result.n.x + result.p.x;
            result.p.y = 0.00001 * result.n.y + result.p.y;
            result.p.z = 0.00001 * result.n.z + result.p.z;
    	}
    	else if ( (t1 < t2) && (t1 < 0) && (t2 >= 0) && (t2 < result.t)) {
    		result.t = t2;
    		result.material = this.material;
            
            Point3d intersect = new Point3d();
            ray.getPoint(result.t, intersect);
            result.p = intersect;
            Vector3d nor = new Vector3d(intersect);
            nor.sub(center);
            nor.scale(1/radius);
            result.n = nor;
            result.p.x = 0.00001 * result.n.x + result.p.x;
            result.p.y = 0.00001 * result.n.y + result.p.y;
            result.p.z = 0.00001 * result.n.z + result.p.z;
    	}
    	else if ( !(t1 < t2) && (t2 < 0) && (t1 >= 0) && (t1 < result.t) ) {
    		result.t = t1;
    		result.material = this.material;
            
            Point3d intersect = new Point3d();
            ray.getPoint(result.t, intersect);
            result.p = intersect;
            Vector3d nor = new Vector3d(intersect);
            nor.sub(center);
            nor.scale(1/radius);
            result.n = nor;
            result.p.x = 0.00001 * result.n.x + result.p.x;
            result.p.y = 0.00001 * result.n.y + result.p.y;
            result.p.z = 0.00001 * result.n.z + result.p.z;
    	}
    	else {
    		return;
    	}
    	/*result.t = Math.min(t1, t2);
    	result.material = this.material;
        
        Point3d intersect = new Point3d();
        ray.getPoint(result.t, intersect);
        result.p = intersect;
        Vector3d nor = new Vector3d(intersect);
        nor.sub(center);
        nor.scale(1/radius);
        result.n = nor;*/
        
    }
    
}
