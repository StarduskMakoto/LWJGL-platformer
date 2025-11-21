package entity;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transform {
    public Vector3f pos;
    public Vector3f scale;

    public Transform() {
        pos = new Vector3f();
        scale = new Vector3f(1);
    }

    public Matrix4f getProjection(Matrix4f target) {
        //target.translate(pos);
        target.mul(new Matrix4f().translate(pos.mul(1, 2, 1, new Vector3f())));
        target.scale(scale);

        //System.out.println(target.toString());

        return target;
    }
}
