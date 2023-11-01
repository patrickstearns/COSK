package com.oblong.af.util;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Footprint {

	private Point2D.Double[] points;

	public Footprint(Footprint f){ this(f.getPoints()); }
	public Footprint(Point2D.Double[] points){
		Point2D.Double[] rPoints = new Point2D.Double[points.length];
		for (int i = 0; i < points.length; i++) rPoints[i] = new Point2D.Double(points[i].x, points[i].y);
		setPoints(rPoints);
	}
	public Footprint(double[] xPoints, double[] yPoints){
		if (xPoints.length != yPoints.length) throw new IllegalArgumentException("Coordinate arrays must be the same length.");
		Point2D.Double[] points = new Point2D.Double[xPoints.length];
		for (int i = 0; i < points.length; i++) points[i] = new Point2D.Double(xPoints[i], yPoints[i]);
		setPoints(points);
	}
	
	public Point2D.Double[] getPoints(){ return points; }
	public void setPoints(Point2D.Double[] points){ this.points = points; }

	public int getLength(){ return points.length; }

	public Line2D.Double getSegment(int n){
		if (n > points.length) 
			throw new IllegalArgumentException("Requested segment "+n+" but this footprint only has "+points.length+" segments");
		int n2 = n+1;
		if (n2 >= points.length) n2 = 0;
		return new Line2D.Double(points[n].x, points[n].y, points[n2].x, points[n2].y);
	}
	
	public Line2D.Double[] getSegments(){
		Line2D.Double[] ret = new Line2D.Double[points.length];
		for (int i = 0; i < ret.length; i++) ret[i] = getSegment(i);
		return ret;
	}
	
	public Polygon asPolygon(){
		int[] xs = new int[points.length];
		int[] ys = new int[points.length];
		for (int i = 0; i < points.length; i++){
			xs[i] = (int)points[i].x;
			ys[i] = (int)points[i].y;
		}
		return new Polygon(xs, ys, xs.length);
	}
	
	//this footprint intersects if any of its segments cross any of the other one's segments
	public boolean intersects(Footprint other){
		for (int n = 0; n < getLength(); n++){
			Line2D.Double nSeg = getSegment(n);
			for (int m = 0; m < other.getLength(); m++){
				Line2D.Double mSeg = other.getSegment(m);
				if (nSeg.intersectsLine(mSeg)) return true;
			}
		}				
		
		return false;
	}
	
	public void translate(double x, double y){
		for (Point2D.Double p: points){
			p.x += x;
			p.y += y;
		}
	}
	
	public Rectangle2D.Double getBounds(){
		double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;
		for (int i = 0; i < points.length; i++){
			if (points[i].x < minX) minX = points[i].x;
			if (points[i].x > maxX) maxX = points[i].x;
			if (points[i].y < minY) minY = points[i].y;
			if (points[i].y > maxY) maxY = points[i].y;
		}
		return new Rectangle2D.Double(minX, minY, maxX-minX, maxY-minY);
	}
	
	//cast a ray from px,py in the direction indicated by xa, ya, find point where it intersects the footprint
	public Point2D.Double getEdgePoint(double px, double py, double xa, double ya){
		Point2D.Double p1 = new Point2D.Double(px, py);
		Point2D.Double p2 = new Point2D.Double(px, py);
		if (xa > 0) p2.x = Double.MAX_VALUE;
		if (xa < 0) p2.x = Double.MIN_VALUE;
		if (ya > 0) p2.y = Double.MAX_VALUE;
		if (ya < 0) p2.y = Double.MIN_VALUE;
		Line2D.Double ray = new Line2D.Double(p1.x, p1.y, p2.x, p2.y);
		Point2D.Double intersection = null;
		double distance = Double.MAX_VALUE;
		double mRay = (p2.x-p1.x)/(p2.y-p2.y);
		double bRay = p1.y-mRay*p1.x;
		for (Line2D.Double segment: getSegments())
			if (segment.intersectsLine(ray)){
				//change lines to form y = mx+b
				double m1 = mRay, b1 = bRay;
				double m2 = (segment.x2-segment.x1)/(segment.y2-segment.y1);
				double b2 = segment.y1-m2*segment.x2;
				
				//find intersecting point - m1x+b1 = m2x+b2
				m1 -= m2;
				b2 -= b1;
				b2 /= m1;
				Point2D.Double ip = new Point2D.Double(b2, mRay*b2+bRay);
				double iDist = p1.distance(intersection);
				if (iDist < distance){
					distance = iDist;
					intersection = ip;
				}
			}
		return intersection;
	}
	
	public String toString(){
		String ret = "FP{";
		for (int i = 0; i < points.length; i++){
			ret += "("+points[i].x+","+points[i].y+")";
			if (i != points.length-1) ret+=",";
		}
		ret += "}";
		return ret;
	}
}
