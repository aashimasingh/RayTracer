# RayTracer

![image from RayTracer](https://github.com/aashimasingh/RayTracer/blob/master/EndResult.png)

Put all the .java files in comp557/a4/ folder structure

1. Sphere.xml and Sphere.png 
2. Plane.xml and Plane.png, Plane2.xml and Plane2.png
3. TwoSpheresPlane.xml and TwoSpheresPlane.png: demonstrates shadows
4. BoxRGBLights.xml and BoxRGBLights.png: demonstrates box
5. BoxStacks.xml and BoxStacks.png: demonstrate hierarchy and instances
6. AACheckerPlane.xml and AACheckerPlane.png and AACheckerPlane32samples.png: shows the difference between 
	1 sample vs 32 samples. I have hard coded render.samples to 8 for other files in Scene.java and hence to test this
	objective, the statement render.samples = 8 has to be commented out.
7. TorusMesh.xml and TorusMesh.png: (Meshes)
   CowMesh.png: was obtained by replacing the filename used in TorusMesh.xml from torus.obj to cow.obj
8. novelScene.xml and soccer_in_the_city.png: demonstrate ( boys with soccer in city) includes Perlin noise and area lights

Bonuses:
1. Perlin noise: demonstrated by PerlinTwoSpheresPlane.xml and PerlinTwoSpheresPlane.png where the red sphere 
	has Perlin noise but others do not. One can change this by changing the boolean isPerlin associated with the material.
2. Cartoonify image: demonstrated by CartoonBoxStacks.png and CartoonSphere.png. To turn this effect on, please change the isCartoon parameter in Render.java
	to true. This is an implementation of edge detection and discretising the shades of colors.
3. Quadrics: demonstrated by Cone.png and Cone.xml
4. Mirror reflection: demonstrated by ReflectionTwoSpheresPlane.xml and ReflectionTwoSpheresPlane.png
5. Fresnel Reflection and refraction: demonstrated by RefractionSpherePlane.xml
6. Area Lights: demonstrated by softShadow.png and softShadow.xml
7. Acceleration technique on meshes: used a single bounding box around meshes so that checks mesh-ray intersection only when ray 
	intersects box first
