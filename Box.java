package comp557.a4;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * A simple box class. A box is defined by it's lower (@see min) and upper (@see max) corner. 
 */
public class Box extends Intersectable {

	public Point3d max;
	public Point3d min;
	
    /**
     * Default constructor. Creates a 2x2x2 box centered at (0,0,0)
     */
    public Box() {
    	super();
    	this.max = new Point3d( 1, 1, 1 );
    	this.min = new Point3d( -1, -1, -1 );
    }	

	@Override
	public void intersect(Ray ray, IntersectResult result) {
		// TODO: Objective 6: intersection of Ray with axis aligned box
		//result.t = Double.POSITIVE_INFINITY;
		//Vector3d invdir = new Vector3d(1/ray.viewDirection.x, 1/ray.viewDirection.y, 1/ray.viewDirection.z);
		//int sign[] = {(invdir.x < 0) ? 1 : 0, (invdir.y < 0) ? 1 : 0, (invdir.z < 0) ? 1 : 0 };
		double tmin, tmax, tymin, tymax, tzmin, tzmax; 
		 
		if ( ray.viewDirection.x > 0 ) {
			tmin = (this.min.x - ray.eyePoint.x) / ray.viewDirection.x; 
			tmax = (this.max.x - ray.eyePoint.x) / ray.viewDirection.x;
		}
		else {
			tmin = (this.max.x - ray.eyePoint.x) / ray.viewDirection.x;
			tmax = (this.min.x - ray.eyePoint.x) / ray.viewDirection.x;
		}
		
		if ( ray.viewDirection.y > 0 ) {
			tymin = (this.min.y - ray.eyePoint.y) / ray.viewDirection.y;
			tymax = (this.max.y - ray.eyePoint.y) / ray.viewDirection.y;
		}
		else {
			tymin = (this.max.y - ray.eyePoint.y) / ray.viewDirection.y;
			tymax = (this.min.y - ray.eyePoint.y) / ray.viewDirection.y;
		}

		if ((tmin > tymax) || (tymin > tmax)) 
	        return; 
	    if (tymin > tmin) 
	        tmin = tymin; 
	    if (tymax < tmax) 
	        tmax = tymax; 
	    
	    if ( ray.viewDirection.z > 0 ) {
	    	tzmin = (this.min.z - ray.eyePoint.z) / ray.viewDirection.z;
	    	tzmax = (this.max.z - ray.eyePoint.z) / ray.viewDirection.z;
	    }
	    else {
	    	tzmin = (this.max.z - ray.eyePoint.z) / ray.viewDirection.z;
	    	tzmax = (this.min.z - ray.eyePoint.z) / ray.viewDirection.z;
	    }
	    
	    if ((tmin > tzmax) || (tzmin > tmax)) {
	        return ; 
	    }

	    if (tzmin > tmin) 
	        tmin = tzmin; 
	    if (tzmax < tmax) 
	        tmax = tzmax;

	    if ( tmin < 0 ) {
	    	return;
	    }
	    if ( tmin < result.t ) {
	    result.t = tmin;
		/*if (Math.abs(ray.viewDirection.x) < 1e-2 && Math.abs(ray.viewDirection.y) < 1e-2) {
		System.out.print("here");
	}*/
	   
	    result.material = this.material;
	    Point3d intersect = new Point3d();
	    ray.getPoint(result.t, intersect);
	    result.p = intersect;

	    if (tmin == tymin) result.n = new Vector3d( 0,(ray.viewDirection.y >= 0 ? -1 : 1), 0);
	    else if (tmin == tzmin) result.n = new Vector3d( 0, 0, (ray.viewDirection.z >= 0 ? -1 : 1));
	    else result.n = new Vector3d( (ray.viewDirection.x >= 0 ? -1 : 1), 0, 0);
	    }
	}	

}
