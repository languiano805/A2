package code;

import java.nio.*;
import javax.swing.*;
import java.lang.Math;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.util.*;
import com.jogamp.common.nio.Buffers;

import org.joml.*;
import org.joml.Vector3f;
import org.joml.Matrix4f;

public class FirstPersonCamera {
    private Vector3f position;
    private Vector3f orientation;

    private float yaw = 0.0f;
    private float pitch = 0.0f;

    public FirstPersonCamera(float u, float v, float n) {
        position = new Vector3f(u, v, n);
        orientation = new Vector3f(0, 1, 0);
    }

    public void translate(float u, float v, float n) {
        position.x += u;
        position.y += v;
        position.z += n;
    }

    public void setLook(float x, float y) {
        yaw = x;
        pitch = y;
    }

    public Matrix4f getViewMatrix() {
        Vector3f lookPoint = new Vector3f(0, 0, -1);
        lookPoint.rotateX((float) Math.toRadians(pitch), lookPoint);
        lookPoint.rotateY((float) Math.toRadians(yaw), lookPoint);

        lookPoint.add(position, lookPoint);

        Matrix4f view = new Matrix4f();
        view.lookAt(position, lookPoint, orientation, view);
        return view;
    }

}
