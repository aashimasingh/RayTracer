package comp557.a4;

import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class Mesh extends Intersectable {
	
	/** Static map storing all meshes by name */
	public static Map<String,Mesh> meshMap = new HashMap<String,Mesh>();
	
	/**  Name for this mesh, to allow re-use of a polygon soup across Mesh objects */
	public String name = "";
	
	/**
	 * The polygon soup.
	 */
	public PolygonSoup soup;
	
	public Box BoundingBox = new Box();

	public Mesh() {
		super();
		this.soup = null;
	}			
		
	@Override
	public void intersect(Ray ray, IntersectResult result) {
		
		// TODO: Objective 9: ray triangle intersection for meshes
		IntersectResult boxres = new IntersectResult();
		BoundingBox.intersect(ray, boxres);
		if ( boxres.t != Double.POSITIVE_INFINITY) {
		
		for ( int[] face : soup.faceList ) {
			double t = Double.POSITIVE_INFINITY;
			Point3d p0 = soup.vertexList.get(face[0]).p;
			Point3d p1 = soup.vertexList.get(face[1]).p;
			Point3d p2 = soup.vertexList.get(face[2]).p;
			
			Vector3d ba = new Vector3d(p1.x - p0.x, p1.y - p0.y, p1.z - p0.z);
			Vector3d cb = new Vector3d(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z);
			
			Vector3d n = new Vector3d();
			n.cross(ba, cb);
			n.normalize();
			
			Vector3d a = new Vector3d(p0.x - ray.eyePoint.x, p0.y - ray.eyePoint.y, p0.z - ray.eyePoint.z);
			t = a.dot(n)/(ray.viewDirection.dot(n));
			Point3d intersect = new Point3d();
			ray.getPoint(t, intersect);
			
			Vector3d xa = new Vector3d(intersect.x - p0.x, intersect.y - p0.y, intersect.z - p0.z);
			Vector3d cross1 = new Vector3d();
			cross1.cross(ba, xa);

			Vector3d xb = new Vector3d(intersect.x - p1.x, intersect.y - p1.y, intersect.z - p1.z);
			Vector3d cross2 = new Vector3d();
			cross2.cross(cb, xb);
				
			Vector3d ac = new Vector3d(p0.x - p2.x, p0.y - p2.y, p0.z - p2.z);
			Vector3d xc = new Vector3d(intersect.x - p2.x, intersect.y - p2.y, intersect.z - p2.z);
			Vector3d cross3 = new Vector3d();
			cross3.cross(ac, xc);
			//System.out.println(cross2.dot(n));
			if ( (cross1.dot(n) > 0) && (cross2.dot(n) > 0) && (cross3.dot(n) > 0) ) {
					if ( t < result.t && t > 0 ) {
					result.t = t;
					result.p = intersect;
					result.n = n;
					result.material = this.material;
					}
				}
				else {
					
				}
		    }
		}
	}
	

	public void setBB () {
		double xmin = soup.vertexList.get(soup.faceList.get(0)[0]).p.x;
		double xmax = xmin;
		double ymin = soup.vertexList.get(soup.faceList.get(0)[0]).p.y;
		double ymax = ymin;
		double zmin = soup.vertexList.get(soup.faceList.get(0)[0]).p.z;
		double zmax = zmin;
		
		for ( int[] face : soup.faceList ) {
			for ( int i = 0; i < 3; i ++) {
				Point3d p0 = soup.vertexList.get(face[i]).p;
				if ( p0.x < xmin ) xmin = p0.x;
				if ( p0.x > xmax ) xmax = p0.x;
				if ( p0.y < ymin ) ymin = p0.y;
				if ( p0.y > ymax ) ymax = p0.y;
				if ( p0.z < zmin ) zmin = p0.z;
				if ( p0.z > zmax ) zmax = p0.z;
			}
			
		}
		
		BoundingBox.min = new Point3d( xmin, ymin, zmin );
		BoundingBox.max = new Point3d( xmax, ymax, zmax );
	}
}
