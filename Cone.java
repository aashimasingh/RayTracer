package comp557.a4;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * A simple cone class.
 */
public class Cone extends Intersectable {
    
	/** Radius of the cone. */
	public double radius = 1;
	
	public double height = 1;
    
	/** Location of the cone center. */
	public Point3d center = new Point3d( 0, 0, 0 );
    
    /**
     * Default constructor
     */
    public Cone() {
    	super();
    }
    
    public Cone( double radius, double height, Point3d center, Material material ) {
    	super();
    	this.radius = radius;
    	this.height = height;
    	this.center = center;
    	this.material = material;
    }
    
    @Override
    public void intersect( Ray ray, IntersectResult result ) {
    	double A = ray.eyePoint.x - center.x;
        double B = ray.eyePoint.z - center.z;
        double D = height - ray.eyePoint.y + center.y;
        
        double tan = (radius / height) * (radius / height);
        
        double a = (ray.viewDirection.x * ray.viewDirection.x) + (ray.viewDirection.z * ray.viewDirection.z) - (tan*(ray.viewDirection.y * ray.viewDirection.y));
        double b = (2*A*ray.viewDirection.x) + (2*B*ray.viewDirection.z) + (2*tan*D*ray.viewDirection.y);
        double c = (A*A) + (B*B) - (tan*(D*D));
        
        double delta = b*b - 4*(a*c);
    	if(Math.abs(delta) < 0.001) return; 
        if(delta < 0.0) return;
        
        double t1 = (-b - Math.sqrt(delta))/(2*a);
        double t2 = (-b + Math.sqrt(delta))/(2*a);
        double t;
        
        if (t1>t2) t = t2;
        else t = t1;
        
        double r = ray.eyePoint.y + t*ray.viewDirection.y;
        
        if ((r > center.y) && (r < center.y + height)) {
        	result.t = t;
        	Point3d intersect = new Point3d();
        	ray.getPoint(t, intersect);
        	result.p = intersect;
        	result.material = this.material;
        	
        	double ra = Math.sqrt((result.p.x-center.x)*(result.p.x-center.x) + (result.p.z-center.z)*(result.p.z-center.z));
            Vector3d n = new Vector3d(result.p.x-center.x, ra*(radius/height), result.p.z-center.z);
            n.normalize();
            result.n = n;
        }
        else return;
    }
}