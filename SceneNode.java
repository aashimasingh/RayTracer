package comp557.a4;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.vecmath.Matrix4d;

import comp557.a4.IntersectResult;
import comp557.a4.Intersectable;
import comp557.a4.Ray;

/**
 * The scene is constructed from a hierarchy of nodes, where each node
 * contains a transform, a material definition, some amount of geometry, 
 * and some number of children nodes.  Each node has a unique name so that
 * it can be instanced elsewhere in the hierarchy (provided it does not 
 * make loops. 
 * 
 * Note that if the material (inherited from Intersectable) for a scene 
 * node is non-null, it should override the material of any child.
 * 
 */
public class SceneNode extends Intersectable {
	
	/** Static map for accessing scene nodes by name, to perform instancing */
	public static Map<String,SceneNode> nodeMap = new HashMap<String,SceneNode>();
	
    public String name;
   
    /** Matrix transform for this node */
    public Matrix4d M;
    
    /** Inverse matrix transform for this node */
    public Matrix4d Minv;
    
    /** Child nodes */
    public List<Intersectable> children;
    
    /**
     * Default constructor.
     * Note that all nodes must have a unique name, so that they can used as an instance later on.
     */
    public SceneNode() {
    	super();
    	this.name = "";
    	this.M = new Matrix4d();
    	this.Minv = new Matrix4d();
    	this.children = new LinkedList<Intersectable>();
    }
           
    @Override
    public void intersect(Ray ray, IntersectResult result) {

    	// TODO: Objective 7: implement hierarchy with instances
    	Minv.transform(ray.eyePoint);
    	Minv.transform(ray.viewDirection);

    	double closest_t = Double.POSITIVE_INFINITY;
    	for ( Intersectable object : children ) {
    		IntersectResult childResult = new IntersectResult();
    	// this is not going to work, but might help you get
    	// started with some scenes...
    		
    		object.intersect( ray, childResult );
    		
    		Matrix4d newMat = new Matrix4d();
    		newMat.transpose(Minv);
    		newMat.transform(childResult.n);
    		
    		M.transform(childResult.p);
    		if ( closest_t > childResult.t ) {
    			closest_t = childResult.t;
    			result.t = closest_t;
    		result.p = childResult.p;
    		result.n = childResult.n;
    		//result.t = childResult.t;
    		result.material = childResult.material;
    		result.n.normalize();
    		}
    	}
    	/*if (this.material != null)
    		result.material = this.material;*/
    	
    	M.transform(ray.eyePoint);
    	M.transform(ray.viewDirection);
    }
    
}
