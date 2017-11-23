package com.gzsr.misc;

public class Vector2f {
    public float x;
    public float y;
   
    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }
   
    public Vector2f(double x, double y) {
        this.x = (float) x;
        this.y = (float) y;
    }
   
    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }
   
    public void set(Vector2f v) {
        set(v.x, v.y);
    }
   
    public float length() {
        return (float) Math.sqrt(dot(this, this));
    }
   
    public static Vector2f sub(Vector2f a, Vector2f b) {
        return new Vector2f(a.x - b.x, a.y - b.y);
    }
   
    public static float dot(Vector2f a, Vector2f b) {
        return a.x * b.x + a.y * b.y;
    }
   
    public static Vector2f normalize(Vector2f v) {
        float length = v.length();
        return new Vector2f(v.x / length, v.y / length);
    }
}