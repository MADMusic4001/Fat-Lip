/*
 Excerpted from "OpenGL ES for Android",
 published by The Pragmatic Bookshelf.
 Copyrights apply to this code. It may not be used to create training material,
 courses, books, articles, and the like. Contact us if you are in doubt.
 We make no guarantees that this code is fit for any purpose.
 Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 */
package com.madinnovations.fatlip.view.utils;

@SuppressWarnings("WeakerAccess, unused")
public class Geometry {
	private static final String TAG = "Geometry";
	public static class Point {
        public final float x, y, z;

        public Point(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }   
        
        public Point translateY(float distance) {
            return new Point(x, y + distance, z);
        }
             
        public Point translate(Vector vector) {
            return new Point(
                x + vector.x, 
                y + vector.y, 
                z + vector.z);
        }        
    }
    
    public static class Vector  {
        public final float x, y, z;

        public Vector(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public float length() {
            return (float)Math.sqrt(
                x * x 
              + y * y 
              + z * z);
        }
        
        // http://en.wikipedia.org/wiki/Cross_product        
        public Vector crossProduct(Vector other) {
            return new Vector(
                (y * other.z) - (z * other.y), 
                (z * other.x) - (x * other.z), 
                (x * other.y) - (y * other.x));
        }

        // http://en.wikipedia.org/wiki/Dot_product
        public float dotProduct(Vector other) {
            return x * other.x 
                 + y * other.y 
                 + z * other.z;
        }
        
        public Vector scale(float f) {
            return new Vector(
                x * f, 
                y * f, 
                z * f);
        }     
    }    

    public static class Ray {
        public final Point point;
        public final Vector vector;

        public Ray(Point point, Vector vector) {
            this.point = point;
            this.vector = vector;
        }        
    }

	public static class Circle {
        public final Point center;
        public final float radius;

        public Circle(Point center, float radius) {
            this.center = center;
            this.radius = radius;
        }                      
        
        public Circle scale(float scale) {
            return new Circle(center, radius * scale);
        }
    }
    
    public static class Cylinder {
        public final Point center;
        public final float radius;
        public final float height;
        
        public Cylinder(Point center, float radius, float height) {        
            this.center = center;
            this.radius = radius;
            this.height = height;
        }                                    
    }
    
    public static class Sphere {
        public final Point center;
        public final float radius;

        public Sphere(Point center, float radius) {
            this.center = center;
            this.radius = radius;
        }
    }

	public static class Plane {
        public final Point point;
        public final Vector normal;

        public Plane(Point point, Vector normal) {                        
            this.point = point;
            this.normal = normal;
        }
    }

    public static Vector vectorBetween(Point from, Point to) {
        return new Vector(
            to.x - from.x, 
            to.y - from.y, 
            to.z - from.z);
    }
  
    public static boolean intersects(Sphere sphere, Ray ray) {
        return distanceBetween(sphere.center, ray) < sphere.radius;
    }
    
    // http://mathworld.wolfram.com/Point-LineDistance3-Dimensional.html
    // Note that this formula treats Ray as if it extended infinitely past
    // either point.
    public static float distanceBetween(Point point, Ray ray) {
        Vector p1ToPoint = vectorBetween(ray.point, point);
        Vector p2ToPoint = vectorBetween(ray.point.translate(ray.vector), point);

        // The length of the cross product gives the area of an imaginary
        // parallelogram having the two vectors as sides. A parallelogram can be
        // thought of as consisting of two triangles, so this is the same as
        // twice the area of the triangle defined by the two vectors.
        // http://en.wikipedia.org/wiki/Cross_product#Geometric_meaning
        float areaOfTriangleTimesTwo 
            = p1ToPoint.crossProduct(p2ToPoint).length();
        float lengthOfBase = ray.vector.length();

        // The area of a triangle is also equal to (base * height) / 2. In
        // other words, the height is equal to (area * 2) / base. The height
        // of this triangle is the distance from the point to the ray.
		return areaOfTriangleTimesTwo / lengthOfBase;
    }
        
    // http://en.wikipedia.org/wiki/Line-plane_intersection
    // This also treats rays as if they were infinite. It will return a
    // point full of NaNs if there is no intersection point.
    public static Point intersectionPoint(Ray ray, Plane plane) {        
        Vector rayToPlaneVector = vectorBetween(ray.point, plane.point);
        
        float scaleFactor = rayToPlaneVector.dotProduct(plane.normal)
                          / ray.vector.dotProduct(plane.normal);

		return ray.point.translate(ray.vector.scale(scaleFactor));
    }

    public static String printMatrix(float[] matrix, int decimalDigits) {
    	float min = getMatrixMin(matrix);
    	float max = getMatrixMax(matrix);
    	float absMax = Math.max(Math.abs(min), Math.abs(max));
		int integerDigits = min < 0 ? 2 : 1;
    	do {
    		absMax /= 10;
    		integerDigits++;
		} while (absMax >= 1.0f);
		String formatString = "%" + (integerDigits + decimalDigits) + "." + decimalDigits + "f";
    	return "\n[" +
					String.format(formatString, matrix[0]) + " " +
					String.format(formatString, matrix[1]) + " " +
					String.format(formatString, matrix[2]) + " " +
					String.format(formatString, matrix[3]) +
				"]\n" +
				"[" +
					String.format(formatString, matrix[4]) + " " +
					String.format(formatString, matrix[5]) + " " +
					String.format(formatString, matrix[6]) + " " +
					String.format(formatString, matrix[7]) +
				"]\n" +
				"[" +
					String.format(formatString, matrix[8]) + " " +
					String.format(formatString, matrix[9]) + " " +
					String.format(formatString, matrix[10]) + " " +
					String.format(formatString, matrix[11]) +
				"]\n" +
				"[" +
					String.format(formatString, matrix[12]) + " " +
					String.format(formatString, matrix[13]) + " " +
					String.format(formatString, matrix[14]) + " " +
					String.format(formatString, matrix[15]) +
				"]\n";
	}

	private static float getMatrixMin(float[] matrix) {
    	float min = Float.MAX_VALUE;
    	for(float value : matrix) {
    		if(value < min) {
    			min = value;
			}
		}
		return min;
	}

	private static float getMatrixMax(float[] matrix) {
		float max = Float.MIN_VALUE;
		for(float value : matrix) {
			if(value > max) {
				max = value;
			}
		}
		return max;
	}
}
