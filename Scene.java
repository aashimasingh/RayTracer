package comp557.a4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * Simple scene loader based on XML file format.
 */
public class Scene {
    
    /** List of surfaces in the scene */
    public List<Intersectable> surfaceList = new ArrayList<Intersectable>();
	
	/** All scene lights */
	public Map<String,Light> lights = new HashMap<String,Light>();

    /** Contains information about how to render the scene */
    public Render render;
    
    /** The ambient light colour */
    public Color3f ambient = new Color3f();

    /** 
     * Default constructor.
     */
    public Scene() {
    	this.render = new Render();
    }
    
    /**
     * renders the scene
     */
    public void render(boolean showPanel) {
 
        Camera cam = render.camera; 
        int w = cam.imageSize.width;
        int h = cam.imageSize.height;
        
        render.init(w, h, showPanel);
        
        int depth_max = 2;
        
        for ( int i = 0; i < w && !render.isDone(); i++ ) { //////originally < h
            for ( int j = 0; j < h && !render.isDone(); j++ ) {   //////originally < w
            	
            	int argb = 0;
            	Color3f pixelColor = new Color3f();
            	Color3f bigColor = new Color3f();
            	
            	ArrayList<Double> tvals = new ArrayList<Double>();
            	ArrayList<Double> norx = new ArrayList<Double>();
            	ArrayList<Double> nory = new ArrayList<Double>();
            	ArrayList<Double> norz = new ArrayList<Double>();
            	Point3d perlinpoint = new Point3d();
            	boolean perlinmat = false;
                // TODO: Objective 1: generate a ray (use the generateRay method)
            	render.samples = 8;
            	nextLoop : for ( int num = 0; num < render.samples; num++ ) {
            		double n1 = Math.random();
            		double n2 = Math.random();
            	double[] off = {n1, n2};
            	Ray ray = new Ray();
            	generateRay(i, j, off, cam, ray);
            	
                // TODO: Objective 2: test for intersection with scene surfaces
            	Color3f c = new Color3f(render.bgcolor);
            	int r = (int)(255*c.x);
                int g = (int)(255*c.y);
                int b = (int)(255*c.z);
                int a = 255;
                argb = (a<<24 | r<<16 | g<<8 | b);
                
                
            	IntersectResult result = new IntersectResult();
            	for (int k = 0; k < surfaceList.size(); k++) {
            		
            		if ( surfaceList.get(k) != null ) {
            			surfaceList.get(k).intersect(ray, result);
            		}
            		int depth = 0;
            		double reflection = 1.0;
            		//while ( depth < depth_max ) {
            		if (result.t != Double.POSITIVE_INFINITY ) {
            			while ( depth < depth_max ) {

            				Color3f amb = new Color3f();
            				Color3f lamb = new Color3f();
            				Color3f phong = new Color3f();
            				for (Light light : lights.values()) {
            					////
            					if ( light.type.matches("point") ) {
            					////
            					Ray shadowRay = new Ray();
            					IntersectResult shadowResult = new IntersectResult();
            					
            					if (!inShadow(result, light, surfaceList.get(k), shadowResult, shadowRay, surfaceList, light.from )) {
            					
                        		Vector3d l = new Vector3d(light.from);
                        		l.sub(result.p);
                        		l.normalize();
                        		double mx = Math.max(0, result.n.dot(l));
                      
                        		//lambertian shading
                        		lamb.x += (float)(light.color.x * light.power * result.material.diffuse.x * mx);
                        		lamb.y += (float)(light.color.y * light.power * result.material.diffuse.y * mx);
                        		lamb.z += (float)(light.color.z * light.power * result.material.diffuse.z * mx);
                        		
                        		//phong shading
                        		Vector3d half = new Vector3d();
                        		Vector3d outgoing = new Vector3d(cam.from.x - result.p.x, cam.from.y - result.p.y, cam.from.z - result.p.z);
                        		outgoing.normalize();
                        		half.add(l, outgoing);
                        		half.normalize();
                        		double mx2 = Math.pow(Math.max(0, result.n.dot(half)), result.material.shinyness);
                        		
                        		phong.x += (float)(light.color.x * light.power * result.material.specular.x * mx2);
                        		phong.y += (float)(light.color.y * light.power * result.material.specular.y * mx2);
                        		phong.z += (float)(light.color.z * light.power * result.material.specular.z * mx2);
                        		
            					}
            					amb.x += (float)(result.material.diffuse.x * this.ambient.x);
            					amb.y += (float)(result.material.diffuse.y * this.ambient.y);
            					amb.z += (float)(result.material.diffuse.z * this.ambient.z);
                    		
            					c.x = amb.x + lamb.x + phong.x;
            					c.y = amb.y + lamb.y + phong.y;
            					c.z = amb.z + lamb.z + phong.z;
            					//////
            					}
            					else if ( light.type.matches("sphere") ) {
            						Vector3d dir = new Vector3d();
            						Random sampler = new Random();
            						Vector3d normal = new Vector3d();
            						Point3d samplept = new Point3d();
            						do {
            							double num1 = sampler.nextDouble();
            							double num2 = sampler.nextDouble();
            							Vector3d shadpt = new Vector3d(result.p);
            							
            							samplept = light.getSpherePoint(num1, num2);
            							normal.sub(shadpt, light.from);
            							normal.normalize();
            							
            							dir = new Vector3d();
            							dir.sub(shadpt, samplept);
            							dir.normalize();
            							
            						} while ( dir.dot(normal) < 0 );
            						
            						boolean shadowed = false;
            						if ( dir.dot(normal) < 0 ) shadowed = true;
            						else {
            							Ray shadowRay = new Ray();
            							IntersectResult shadowResult = new IntersectResult();
            							shadowed = inShadow( result, light, surfaceList.get(k), shadowResult, shadowRay, surfaceList, samplept );
            						}
            						if ( !shadowed ) {
            							Vector3d l = new Vector3d(samplept);
                                		l.sub(result.p);
                                		l.normalize();
                                		double mx = Math.max(0, result.n.dot(l));
                              
                                		//lambertian shading
                                		lamb.x += (float)(light.color.x * light.power * result.material.diffuse.x * mx);
                                		lamb.y += (float)(light.color.y * light.power * result.material.diffuse.y * mx);
                                		lamb.z += (float)(light.color.z * light.power * result.material.diffuse.z * mx);
                                		
                                		//phong shading
                                		Vector3d half = new Vector3d();
                                		Vector3d outgoing = new Vector3d(cam.from.x - result.p.x, cam.from.y - result.p.y, cam.from.z - result.p.z);
                                		outgoing.normalize();
                                		half.add(l, outgoing);
                                		half.normalize();
                                		double mx2 = Math.pow(Math.max(0, result.n.dot(half)), result.material.shinyness);
                                		
                                		phong.x += (float)(light.color.x * light.power * result.material.specular.x * mx2);
                                		phong.y += (float)(light.color.y * light.power * result.material.specular.y * mx2);
                                		phong.z += (float)(light.color.z * light.power * result.material.specular.z * mx2);
                                		
                    					}
                    					amb.x += (float)(result.material.diffuse.x * this.ambient.x);
                    					amb.y += (float)(result.material.diffuse.y * this.ambient.y);
                    					amb.z += (float)(result.material.diffuse.z * this.ambient.z);
                            		
                    					c.x = amb.x + lamb.x + phong.x;
                    					c.y = amb.y + lamb.y + phong.y;
                    					c.z = amb.z + lamb.z + phong.z;
            						}
            					
            					//////
            					if ( render.isCartoon ) {
            						Color4f shade = result.material.diffuse;
            						if ( c.x > 0.1 && c.y > 0.1 && c.z > 0.1 )  {
            							c.x = shade.x ; c.y = shade.y; c.z = shade.z; 
            						}
            						else if ( c.x > 0.005 && c.y > 0.005 && c.z > 0.005 ) {
            							c.x = shade.x; c.y = shade.y; c.z = shade.z; c.scale(0.7f);
            						}
            						else if ( c.x > 0.0002 && c.y > 0.0002 && c.z > 0.0002) {
            							c.x = shade.x; c.y = shade.y; c.z = shade.z; c.scale(0.6f);
            						}
            						else {
            							c.x = shade.x; c.y = shade.y; c.z = shade.z; c.scale(0.5f);
            						}
            					}
                    		
                    		if ( result.material.isPerlin ) {
                    		double perlin = PerlinNoise(perlinpoint.x, perlinpoint.y, perlinpoint.z);
                        	c.x *= perlin;
                        	c.y *= perlin;
                        	c.z *= perlin;
                        	c.clamp(0, 1);
                        	}
                    		
            				}  // closes loop over lights
            				//perlinmat = result.material.isPerlin;
            				if ( result.material.isReflective ) {
            					if ( isReflectIntersect( ray, result, surfaceList) ) {
            						depth += 1;
            						c.x += 0.8 * c.x;
            						c.y += 0.8 * c.y;
            						c.z += 0.8 * c.z;
            					}
            					else {
            						break;
            					}
            				}
            				
            				///////////////////////////
            				else if ( result.material.isRefractive ) {
            					depth += 1;
            					double kr = fresnel ( ray, result );
            					boolean outside = ray.viewDirection.dot(result.n) < 0; 
            			        if ( kr < 1 ) {
            			        	if ( outside ) {
            			        		ray.eyePoint.x = result.p.x + 0.00001* result.n.x; 
            			       	   		ray.eyePoint.y = result.p.y + 0.00001* result.n.y;
            			       	   		ray.eyePoint.z = result.p.z + 0.00001* result.n.z;
            			        	}
            			        	else {
            			        		ray.eyePoint.x = result.p.x + 0.00001* result.n.x; 
            			           	   	ray.eyePoint.y = result.p.y + 0.00001* result.n.y;
            			           	   	ray.eyePoint.z = result.p.z + 0.00001* result.n.z;
            			        	}
            			        	if (isRefractiveIntersect( ray, result, surfaceList )) {
            			        		c.x += (1 - kr) * c.x; c.y += (1 - kr) * c.y; c.z += (1 - kr) * c.z;
            			        	}
            			        }
            			        if ( isReflectIntersect(ray, result, surfaceList )) {
            			        	c.x += kr * c.x; c.y += kr * c.y; c.z += kr * c.z;
            			        }
            			        else {
                					break;
                				}
            				}
            				else {
            					depth = 5;
            				}
            				
            				///////////////////////////
            			}
                }
         
                // TODO: Objective 3: compute the shaded result for the intersection point (perhaps requiring shadow rays)

            	// TODO: Objective 8: do antialiasing by sampling more than one ray per pixel
            	
            	// Here is an example of how to calculate the pixel value.
                
                // update the render image

            	c.clamp(0, 1);
            	pixelColor.x += c.x; pixelColor.y += c.y; pixelColor.z += c.z;
            	
            	}
            	tvals.add(result.t);
            	norx.add(result.n.x);
            	nory.add(result.n.y);
            	norz.add(result.n.z);
            	perlinpoint = result.p;
            	
            	IntersectResult sphereRes = new IntersectResult();
                for ( Light light : lights.values() ) {
                	if ( light.isAreaLight ) {
                		light.sphere.intersect(ray, sphereRes);
                		if ( sphereRes.t != Double.POSITIVE_INFINITY ) {
                			pixelColor.x = (float)(light.color.x * light.power * render.samples); pixelColor.y = (float)(light.color.y * light.power * render.samples);
                			pixelColor.z = (float)(light.color.z * light.power * render.samples);
                		}
                	}
                }
            	}
            	//}
            	if ( render.isCartoon ) {
            	double sum = 0;
            	for(Double d : tvals)
            	    sum += d;
            	double avg = sum/tvals.size();
            	double sumDiffsSquared = 0.0;
            	for (Double d : tvals)
            	   {
            	       double diff = d - avg;
            	       diff *= diff;
            	       sumDiffsSquared += diff;
            	   }
            	double variance = sumDiffsSquared/ (tvals.size() - 1);
            	sum = 0;
            	for (Double d : norx) {
            		sum += d;
            	}
            	double avgx = sum/norx.size(); sumDiffsSquared = 0.0;
            	for (Double d : norx)
         	   	{
         	       double diff = d - avgx;
         	       diff *= diff;
         	       sumDiffsSquared += diff;
         	    }
            	double varx = sumDiffsSquared/ (norx.size() - 1);
            	
            	sum = 0;
            	for (Double d : nory) {
            		sum += d;
            	}
            	double avgy = sum/nory.size(); sumDiffsSquared = 0.0;
            	for (Double d : nory)
         	   	{
         	       double diff = d - avgy;
         	       diff *= diff;
         	       sumDiffsSquared += diff;
         	    }
            	double vary = sumDiffsSquared/ (nory.size() - 1);
            	
            	sum = 0;
            	for (Double d : norz) {
            		sum += d;
            	}
            	double avgz = sum/norz.size(); sumDiffsSquared = 0.0;
            	for (Double d : norz)
         	   	{
         	       double diff = d - avgz;
         	       diff *= diff;
         	       sumDiffsSquared += diff;
         	    }
            	double varz = sumDiffsSquared/ (norz.size() - 1);
            	
            	if ( (variance >  0.4 && avg < 50) || (varx > 0.1) || (vary > 0.1) || (varz > 0.1) ) {
            		pixelColor.x = 0; pixelColor.y = 0; pixelColor.z = 0;
            	}
            	}
            	
            	pixelColor.x /= render.samples;
            	pixelColor.y /= render.samples;
            	pixelColor.z /= render.samples;
            	pixelColor.clamp(0, 1);
            	
            	/*double perlin = PerlinNoise(perlinpoint.x, perlinpoint.y, perlinpoint.z);
            	if (perlinmat) {
            	pixelColor.x *= perlin;
            	pixelColor.y *= perlin;
            	pixelColor.z *= perlin;
            	}*/
            	int r = (int)(255*pixelColor.x);
                int g = (int)(255*pixelColor.y);
                int b = (int)(255*pixelColor.z);
                int a = 255;
                argb = (a<<24 | r<<16 | g<<8 | b);
            	
            	render.setPixel(i, j, argb);
            }
        }
        
        // save the final render image
        render.save();
        
        // wait for render viewer to close
        render.waitDone();
        
    }
    
    /**
     * Generate a ray through pixel (i,j).
     * 
     * @param i The pixel row.
     * @param j The pixel column.
     * @param offset The offset from the center of the pixel, in the range [-0.5,+0.5] for each coordinate. 
     * @param cam The camera.
     * @param ray Contains the generated ray.
     */
	public static void generateRay(final int i, final int j, final double[] offset, final Camera cam, Ray ray) {
		
		// TODO: Objective 1: generate rays given the provided parmeters

		/*double dist = Math.abs(cam.from.z - cam.to.z);
		
		double t = Math.tan(Math.toRadians(cam.fovy)/2) * dist;
		double b = -t;
		double asp = cam.imageSize.width / cam.imageSize.height;
		double r = t*asp;
		double l = -r;
		
		double u = l + (r-l)*(i+offset[0])/cam.imageSize.width;
		double v = b + (t-b)*(j+offset[1])/cam.imageSize.height;
		
		////// implement u, v and w vectors along with below later too?
		
		Vector3d uvec = new Vector3d();
		Vector3d wvec = new Vector3d(0, 0, cam.from.z - cam.to.z);
		wvec.normalize();
		uvec.cross(cam.up, wvec);
		uvec.normalize();
		Vector3d vvec = new Vector3d();
		vvec.cross(uvec, wvec);
		vvec.normalize();
		
		Vector3d e = new Vector3d(cam.from.x, cam.from.y, cam.from.z);
		Vector3d s = new Vector3d( (u*uvec.x) + (v*vvec.x) - (dist*wvec.x) + e.x, (u*uvec.y) +
				(v*vvec.y) - (dist*wvec.y) + e.y, (u*uvec.z) + (v*vvec.z) - (dist*wvec.z) + e.z);
		Point3d p = new Point3d(e.x, e.y, e.z);
		Vector3d d = new Vector3d(s.x - e.x, s.y - e.y, s.z - e.z);
		d.normalize();
		
		ray.viewDirection = d;
		ray.eyePoint = p;*/
		
		double distance = cam.from.distance(cam.to);
		double h = distance * Math.tan(Math.toRadians(cam.fovy/2));
		double asp =cam.imageSize.width/cam.imageSize.height;
		double w = h * asp;
		double u = -w + (2*w)*(i + offset[0])/cam.imageSize.width;
		double v = -h +(2*h)*(j + offset[1])/(cam.imageSize.height);
		
		
		Vector3d u2 = new Vector3d();
		Vector3d w2 = new Vector3d();
		w2.sub(cam.from, cam.to);
		w2.normalize();
		
		u2.cross(cam.up, w2);
		u2.normalize();
		
		Vector3d v2 = new Vector3d();
		
		v2.cross(u2, w2);
		v2.normalize();
		
		Vector3d eye = new Vector3d(cam.from.x, cam.from.y, cam.from.z);
		Vector3d other = new Vector3d(	u*u2.x + v*v2.x - distance*w2.x ,
										u*u2.y + v*v2.y - distance*w2.y ,
										u*u2.z + u*u2.z - distance*w2.z );
		
		Point3d point = new Point3d(eye.x, eye.y, eye.z);
		Vector3d direction = new Vector3d(other.x - eye.x, other.y - eye.y, other.z - eye.z);
		direction.normalize();
		other.normalize();
		
		ray.viewDirection = other;
		ray.eyePoint = point;
	}

	/**
	 * Shoot a shadow ray in the scene and get the result.
	 * 
	 * @param result Intersection result from raytracing. 
	 * @param light The light to check for visibility.
	 * @param root The scene node.
	 * @param shadowResult Contains the result of a shadow ray test.
	 * @param shadowRay Contains the shadow ray used to test for visibility.
	 * 
	 * @return True if a point is in shadow, false otherwise. 
	 */
	public static boolean inShadow(final IntersectResult result, final Light light, final Intersectable root, IntersectResult shadowResult, Ray shadowRay, List<Intersectable> surfaceList, Point3d samplept) {
		
		// TODO: Objective 5: check for shadows and use it in your lighting computation
		if ( !light.isAreaLight ) {
		Vector3d dir = new Vector3d();
		 result.p.x = 0.00001 * result.n.x + result.p.x;
         result.p.y = 0.00001 * result.n.y + result.p.y;
         result.p.z = 0.00001 * result.n.z + result.p.z;
		dir.sub(light.from, result.p);
		//dir.normalize();
		shadowRay.set(result.p, dir);
		}
		else {
			Vector3d dir = new Vector3d();
			dir.sub(samplept, result.p);
			dir.normalize();
			shadowRay.set(result.p, dir);
		}
		
		//for (Intersectable object :  root.children) {
		for ( int i = 0; i < surfaceList.size(); i++ ) {
			//if ( root != surfaceList.get(i)) {
			surfaceList.get(i).intersect(shadowRay, shadowResult);
			if ( shadowResult.t != Double.POSITIVE_INFINITY ) {
				return true;
			//}
			}
		} 
		return false;
	}    
	
	public double PerlinNoise( double x, double y, double z ) {
		int p[] = new int[512];
		int permutation[] = { 151,160,137,91,90,15,
				   131,13,201,95,96,53,194,233,7,225,140,36,103,30,69,142,8,99,37,240,21,10,23,
				   190, 6,148,247,120,234,75,0,26,197,62,94,252,219,203,117,35,11,32,57,177,33,
				   88,237,149,56,87,174,20,125,136,171,168, 68,175,74,165,71,134,139,48,27,166,
				   77,146,158,231,83,111,229,122,60,211,133,230,220,105,92,41,55,46,245,40,244,
				   102,143,54, 65,25,63,161, 1,216,80,73,209,76,132,187,208, 89,18,169,200,196,
				   135,130,116,188,159,86,164,100,109,198,173,186, 3,64,52,217,226,250,124,123,
				   5,202,38,147,118,126,255,82,85,212,207,206,59,227,47,16,58,17,182,189,28,42,
				   223,183,170,213,119,248,152, 2,44,154,163, 70,221,153,101,155,167, 43,172,9,
				   129,22,39,253, 19,98,108,110,79,113,224,232,178,185, 112,104,218,246,97,228,
				   251,34,242,193,238,210,144,12,191,179,162,241, 81,51,145,235,249,14,239,107,
				   49,192,214, 31,181,199,106,157,184, 84,204,176,115,121,50,45,127, 4,150,254,
				   138,236,205,93,222,114,67,29,24,72,243,141,128,195,78,66,215,61,156,180
				   };
			for (int i=0; i < 256 ; i++) p[256+i] = p[i] = permutation[i]; 
		 int X = (int)Math.floor(x) & 255,                  // FIND UNIT CUBE THAT
		          Y = (int)Math.floor(y) & 255,                  // CONTAINS POINT.
		          Z = (int)Math.floor(z) & 255;
		      x -= Math.floor(x);                                // FIND RELATIVE X,Y,Z
		      y -= Math.floor(y);                                // OF POINT IN CUBE.
		      z -= Math.floor(z);
		      double u = fade(x),                                // COMPUTE FADE CURVES
		             v = fade(y),                                // FOR EACH OF X,Y,Z.
		             w = fade(z);
		      int A = p[X  ]+Y, AA = p[A]+Z, AB = p[A+1]+Z,      // HASH COORDINATES OF
		          B = p[X+1]+Y, BA = p[B]+Z, BB = p[B+1]+Z;      // THE 8 CUBE CORNERS,
		 
		      return (lerp(w, lerp(v, lerp(u, grad(p[AA  ], x  , y  , z   ),  // AND ADD
		                                     grad(p[BA  ], x-1, y  , z   )), // BLENDED
		                             lerp(u, grad(p[AB  ], x  , y-1, z   ),  // RESULTS
		                                     grad(p[BB  ], x-1, y-1, z   ))),// FROM  8
		                     lerp(v, lerp(u, grad(p[AA+1], x  , y  , z-1 ),  // CORNERS
		                                     grad(p[BA+1], x-1, y  , z-1 )), // OF CUBE
		                             lerp(u, grad(p[AB+1], x  , y-1, z-1 ),
		                                     grad(p[BB+1], x-1, y-1, z-1 ))))+1)/2;
	}
	
	static double fade(double t) { return t * t * t * (t * (t * 6 - 15) + 10); }
	   static double lerp(double t, double a, double b) { return a + t * (b - a); }
	   static double grad(int hash, double x, double y, double z) {
	      int h = hash & 15;                      // CONVERT LO 4 BITS OF HASH CODE
	      double u = h<8 ? x : y,                 // INTO 12 GRADIENT DIRECTIONS.
	             v = h<4 ? y : h==12||h==14 ? x : z;
	      return ((h&1) == 0 ? u : -u) + ((h&2) == 0 ? v : -v);
	   }
	   
	   public static boolean isReflectIntersect( Ray ray, IntersectResult result, List<Intersectable> surfaceList ) {
		   	ray.eyePoint.x = result.p.x + 0.00001* result.n.x; 
       		ray.eyePoint.y = result.p.y + 0.00001* result.n.y;
       		ray.eyePoint.z = result.p.z + 0.00001* result.n.z;
       		double newx = ray.viewDirection.x - 2*ray.viewDirection.dot(result.n)*result.n.x;
       		double newy = ray.viewDirection.y - 2*ray.viewDirection.dot(result.n)*result.n.y;
       		double newz = ray.viewDirection.z - 2*ray.viewDirection.dot(result.n)*result.n.z;
       		ray.viewDirection.x = newx; ray.viewDirection.y = newy; ray.viewDirection.z = newz;
       		
       		for ( int i = 0; i < surfaceList.size(); i++ ) {
    			surfaceList.get(i).intersect(ray, result);
    			if ( result.t != Double.POSITIVE_INFINITY ) {
    				return true;
    			}
    		} 
    		return false;
		   
	   }
	   
	   public static boolean isRefractiveIntersect( Ray ray, IntersectResult result, List<Intersectable> surfaceList ) {
		   double cosi = ray.viewDirection.dot(result.n);
		   cosi = clamp(cosi, -1, 1);
		   double etai = 1; double etat = 1.5; double tmp = 0;
		   Vector3d n = new Vector3d( result.n.x, result.n.y, result.n.z);
		   if (cosi < 0) { cosi = -cosi; }
		   else { 
			   tmp = etai;
			   etai = etat;
			   etat = tmp;
			   n.x= -result.n.x; n.y = -result.n.y; n.z = -result.n.z;
		   }
		   double eta = etai / etat; 
		   double k = 1 - eta * eta * (1 - cosi * cosi); 
		   
		   double newx = k < 0 ? 0 : eta * ray.viewDirection.x + (eta * cosi - Math.sqrt(k)) * result.n.x; 
		   double newy = k < 0 ? 0 : eta * ray.viewDirection.y + (eta * cosi - Math.sqrt(k)) * result.n.y;
		   double newz = k < 0 ? 0 : eta * ray.viewDirection.z + (eta * cosi - Math.sqrt(k)) * result.n.z;
      	   
      	   ray.viewDirection.x = newx; ray.viewDirection.y = newy; ray.viewDirection.z = newz;
      	   for ( int i = 0; i < surfaceList.size(); i++ ) {
      		   surfaceList.get(i).intersect(ray, result);
      		   if ( result.t != Double.POSITIVE_INFINITY ) {
      			   return true;
      		   }
      	   } 
      	   return false;
		   
	   }
	   public static double clamp(double value, double min, double max) { 
	        return value < min ? min : (value > max ? max : value); 
	    } 
	   
	   public static double fresnel ( Ray ray, IntersectResult result ) {
		   
		   	double kr = 0;
		    double cosi = ray.viewDirection.dot(result.n);
		    cosi = clamp( cosi, -1, 1 );
		    double etai = 1, etat = 1.5, tmp = 0; 
		    if (cosi > 0) { 
		    	tmp = etai;
		    	etai = etat;
		    	etat = tmp;
		    } 
		    // Compute sini using Snell's law
		    double sint = etai / etat * Math.sqrt(Math.max(0, 1 - cosi * cosi)); 
		    // Total internal reflection
		    if (sint >= 1) { 
		        kr = 1; 
		    } 
		    else { 
		        double cost = Math.sqrt(Math.max(0, 1 - sint * sint)); 
		        cosi = Math.abs(cosi); 
		        double Rs = ((etat * cosi) - (etai * cost)) / ((etat * cosi) + (etai * cost)); 
		        double Rp = ((etai * cosi) - (etat * cost)) / ((etai * cosi) + (etat * cost)); 
		        kr = (Rs * Rs + Rp * Rp) / 2; 
		    } 
		   return kr;
	   }
}
