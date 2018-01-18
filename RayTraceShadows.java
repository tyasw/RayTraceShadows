/**
 * RayTraceShadows illustrates some basics of Java 2D.
 * This version is compliant with Java 1.2 Beta 3, Jun 1998.
 * Please refer to: <BR>
 * http://www.javaworld.com/javaworld/jw-07-1998/jw-07-media.html
 * <P>
 * @author Bill Day <bill.day@javaworld.com>
 * @version 1.0
 * @see java.awt.Graphics2D
**/

/**
Geoffrey Matthews modified this code to show how to make
an image pixel by pixel.
13 April 2017
**/

/**
William Tyas modified this code to implement a ray tracer.
9 August 2017
**/

import java.util.*;
import java.awt.*;
import java.awt.event.*;

public class RayTraceShadows extends Frame {
	public static final int WIDTH = 512;
	public static final int HEIGHT = 512;
	public static final Color BACKGROUND = new Color(0.4f, 0.6f, 0.8f);
	public static final nTuple LIGHT = new nTuple(1.0f, 1.0f, 1.0f).normalize();
	public static final nTuple LIGHT_BASIS_2 = new nTuple(5.0f, -3.0f, -2.0f).normalize();
	public static final nTuple LIGHT_BASIS_3 = new nTuple(1.0f, 7.0f, -8.0f).normalize();
	public static final float IMG_PLANE_SZ = 10.0f;
	public static final float CAM_Z = 20.0f;
	public static Quadtree tree;
	public static Quadtree shadowTree;

	/*
	 * Main entry point
	 */
	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		int numSpheres = howManySpheres(input);
		int treeDepth = howDeep(input);
		tree = new Quadtree(-IMG_PLANE_SZ,
							-IMG_PLANE_SZ,
							IMG_PLANE_SZ,
							IMG_PLANE_SZ,
							treeDepth,
							CAM_Z);
		shadowTree = new Quadtree(-IMG_PLANE_SZ * 5,
									-IMG_PLANE_SZ * 5,
									IMG_PLANE_SZ * 5,
									IMG_PLANE_SZ * 5,
									treeDepth,
									CAM_Z);
		ArrayList<Sphere> spheres = new ArrayList<Sphere>();

		for (int i = 0; i < numSpheres; i++) {
			Sphere s = randSphere(LIGHT, LIGHT_BASIS_2, LIGHT_BASIS_3);
			tree.addSphere(s);
			shadowTree.addShadowSphere(s);
			spheres.add(s);
		}

		Statistics stats = new Statistics(spheres);
		stats.generateUsefulInfo();
		new RayTraceShadows();
	}

	public static int howManySpheres(Scanner input) {
		System.out.print("How many spheres do you want drawn? ");
		return input.nextInt();
	}

	public static int howDeep(Scanner input) {
		System.out.print("How deep do you want the quadtree to be? (Less than 10 recommended)? ");
		return input.nextInt();
	}

	public static Sphere randSphere(nTuple u1, nTuple u2, nTuple u3) {
		float x = (float) Math.random() * 16.0f - 8.0f;
		float y = (float) Math.random() * 16.0f - 8.0f;
		float z = (float) Math.random() * 16.0f - 8.0f;
		float radius = (float) Math.random() * 0.1f + 0.05f;
		float r = (float) Math.random();
		float g = (float) Math.random();
		float b = (float) Math.random();
		return new Sphere(x, y, z, radius, r, g, b, u1, u2, u3);
	}

	public Color getColor(int x, int y) {
		nTuple p = new nTuple(0.0f, 0.0f, CAM_Z);	// camera point
		nTuple q = imagePlaneCoord(x, y);			// point on image plane
		ray ray = new ray(p, q.subtract(p));
		double closestHit = Double.POSITIVE_INFINITY;
		float intersection = 0.0f;	// t-value for ray to intersect sphere
		Sphere closestSphere = null;
		SphereList intersectSpheres = tree.getSpheres(q.getX(), q.getY());

		while (intersectSpheres != null) { // Find closest sphere
			Sphere next = intersectSpheres.getSphere();
			intersection = ray.intersectSphere(next);
			if (intersection > 0.01f && intersection < closestHit) {
				closestHit = intersection;
				closestSphere = next;
			}
			intersectSpheres = intersectSpheres.getNext();
		}

		if (closestSphere != null) {
			nTuple IntPt = ray.pointAlongRay((float) closestHit);
			boolean inShadow = inShadow(IntPt);
			return closestSphere.shadeSphere(IntPt, LIGHT, inShadow);
		} else {
			return BACKGROUND;
		}
	}

	// Check if a point on a sphere is in shadow
	public boolean inShadow(nTuple point) {
		nTuple coords = new nTuple(point.coordChange(LIGHT,
														LIGHT_BASIS_2,
														LIGHT_BASIS_3,
														point));
		SphereList shadowIntersect = shadowTree.getSpheres(coords.getY(), coords.getZ()); 
		ray shadowRay = new ray(new nTuple(), LIGHT);	// terminal at origin makes math easier
		boolean inShadow = false;
		while (!inShadow && (shadowIntersect != null)) {
			Sphere next = new Sphere(shadowIntersect.getSphere());
			next.setCenter(next.getCenter().subtract(point));
			float intersection = shadowRay.intersectSphere(next);
			if (intersection > 0.0f) {
				inShadow = true;
			}
			shadowIntersect = shadowIntersect.getNext();
		}
		return inShadow;
	}

	public nTuple imagePlaneCoord(float u, float v) {
		return new nTuple(this.IMG_PLANE_SZ * (2*u/(float)WIDTH - 1),
							-1.0f * this.IMG_PLANE_SZ * (2*v/(float)HEIGHT - 1),
							0.0f);
	}

    /**
     * Our RayTraceShadows constructor sets the frame's size, adds the
     * visual components, and then makes them visible to the user.
     * It uses an adapter class to deal with the user closing
     * the frame.
     **/
    public RayTraceShadows() {
        super("RayTracer");
        setSize(WIDTH, HEIGHT);
        setVisible(true);
        addWindowListener(new WindowAdapter()
                          {public void windowClosing(WindowEvent e)
                          {dispose(); System.exit(0);}
                          }
        );
    }

    public void paint(Graphics g) {
		for (int u = 0; u < WIDTH; u++) {
			for (int v = 0; v < HEIGHT; v++) {
        		g.setColor(getColor(u, v));
        		g.drawLine(u, v, u, v);
			}
		}
    }
}
