package comp557.a4;

import javax.vecmath.Color4f;
import javax.vecmath.Point3d;

public class Light {
	
	/** Light name */
    public String name = "";
    
    /** Light colour, default is white */
    public Color4f color = new Color4f(1,1,1,1);
    
    /** Light position, default is the origin */
    public Point3d from = new Point3d(0,0,0);
    
    /** Light intensity, I, combined with colour is used in shading */
    public double power = 1.0;
    
    /** Type of light, default is a point light */
    public String type = "point";
    
    public Sphere sphere = new Sphere();
    
    public boolean isAreaLight = false;
    
    public double radius = 1.0;

    /**
     * Default constructor 
     */
    public Light() {
    	// do nothing
    }
    
    public void createAreaLight() {
    	sphere.center = this.from;
    	sphere.radius = this.radius;
    	this.isAreaLight = true;
    }
    
    public Point3d getSpherePoint(double num1, double num2) {
    	double ang1 = 2* Math.PI * num1;
		double ang2 = Math.acos(1 - 2* num2);
		double xpt = Math.sin(ang2) * Math.cos(ang1) * this.radius;
		double ypt = Math.sin(ang2) * Math.sin(ang1) * this.radius;
		double zpt = Math.cos(ang2) * this.radius;
		
		Point3d resultpt = new Point3d( this.from.x + xpt, this.from.y + ypt, this.from.z + zpt );
		return resultpt;
    }
}
