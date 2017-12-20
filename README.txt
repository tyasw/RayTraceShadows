Project: Ray Trace Shadows
Author: William Tyas
Last updated: 9/18/17

Ray Trace Shadows is a simple ray tracer that draws random spheres on
the screen. The Java Graphics package is used for drawing pixels. The 
ray tracing process is quite inefficient, so a quadtree has been used 
to speed up the drawing of the spheres. In a tradtional ray tracer, 
for every pixel on the screen, the ray tracer checks each sphere to see 
if it intersects the pixel. This is very inefficient. To speed up the 
process, prior to drawing pixels on the screen, the spheres are all put 
in a multilevel quadtree, based on where their location on the screen
is. Then, when it comes time to draw pixels, the ray tracer only has to
look at the spheres at the current node of the quadtree.

Unlike the ray tracer I wrote for school, this one actually
works, and I have added shadows as well.

How to run:
To compile Ray Trace Shadows, run the following command:

	javac RayTraceShadows.java

To run Ray Trace Shadows, run the following command:

	java RayTraceShadows

The program will then prompt the user for the number of spheres they want
drawn on the screen. Once the user lets the ray tracer know how many
spheres they want drawn, the ray tracer will draw the spheres.
