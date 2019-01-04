package comp557.a4;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * Class for a plane at y=0.
 * 
 * This surface can have two materials.  If both are defined, a 1x1 tile checker 
 * board pattern should be generated on the plane using the two materials.
 */
public class Plane extends Intersectable {
    
	/** The second material, if non-null is used to produce a checker board pattern. */
	Material material2;
	
	/** The plane normal is the y direction */
	public static final Vector3d n = new Vector3d( 0, 1, 0 );
    
    /**
     * Default constructor
     */
    public Plane() {
    	super();
    }

        
    @Override
    public void intersect( Ray ray, IntersectResult res ) {
    
        // TODO: Objective 4: intersection of ray with plane
    	Vector3d tmp = new Vector3d(ray.eyePoint.x, ray.eyePoint.y, ray.eyePoint.z);
    	double d2 = n.dot(ray.viewDirection);
    	if ( Math.abs(d2) < 0.00000001 ) {
    		return;
    	}
    	double d1 = -n.dot(tmp);
    	if ( d1/d2 < res.t && d1/d2 > 0 ) {
    		res.t = d1/d2;
    	Point3d intersec =new Point3d();
    	ray.getPoint(res.t, intersec);
    	res.p =  intersec;
    	res.n = n;
    	
    	boolean isMat = true;
    	if (this.material2 == null) {
    		res.material = this.material;
    	}
    	else {
    		if ((Math.abs(res.p.x) % 2 < 1 && Math.abs(res.p.z) % 2 < 1) || (Math.abs(res.p.x) % 2 >= 1 && Math.abs(res.p.z) % 2 >= 1) ) {
    			isMat = true;
    		}
    		else {
    			isMat = false;
    		}
    		if ((res.p.x < 0  && res.p.z >= 0) || (res.p.x >= 0 && res.p.z < 0 )) {
    			isMat = !isMat;
    		}
    		res.material = isMat? this.material : this.material2;
    		
    	}
    }
    
    else {
		return;
	}}
    
}
