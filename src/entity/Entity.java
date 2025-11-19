package entity;

import io.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import physics.AABB;
import physics.Collision;
import render.Animation;
import render.Camera;
import render.Model;
import render.Shader;
import world.World;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;

public class Entity {
    private static Model model;
    private AABB bounding_box;
    private Animation texture;
    protected Transform transform;
    protected float SPEED = 10f;

    public Entity(Animation animation, Transform transform, Vector2f hitbox) {
        this.texture = animation;

        this.transform = transform;

        bounding_box = new AABB(new Vector2f(transform.pos.x, transform.pos.y), hitbox);
    }

    public void move(Vector2f direction) {
        transform.pos.add(direction.x, direction.y, 0);

        bounding_box.getCenter().set(transform.pos.x, transform.pos.y);
    }

    public void update(float delta, Window window, Camera camera, World world) {
        //System.out.println((int)((transform.pos.y) + 1f - (5/2)));

        AABB[] boxes = new AABB[25];
        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 5; j++) {
                boxes[i + j * 5] = world.getTileBoundingBox(
                        (int)(((transform.pos.x / 2) + 1f) - (5/2)) + i,
                        (int)(((-transform.pos.y) + 1f) - (5/2)) + j
                );
            }
        }

        //AABB box = null;
        for(int i = 0; i < boxes.length; i++) {
            if(boxes[i] != null) {
                Collision data = bounding_box.getCollision(boxes[i]);
                if(!data.isIntersecting) { continue; }
                bounding_box.correctPosition(boxes[i], data);
                transform.pos.set(bounding_box.getCenter(), 0);
            }
        }
    }

    public void render(Shader shader, Camera camera, World world) {
        Matrix4f target = camera.getProjection();
        target.mul(world.getWorldMatrix());

        shader.bind();
        shader.setUniform("sampler", 0);
        shader.setUniform("projection", transform.getProjection(target));
        texture.bind(0);
        model.render();
    }

    public static void initAsset() {
        float[] vertices = new float[] {
                -1f, 1f, 0,  // TOP LEFT     0
                1f, 1f, 0,   // TOP RIGHT    1
                1f, -1f, 0,  // BOTTOM RIGHT 2
                -1f, -1f, 0, // BOTTOM LEFT  3
        };

        float[] texture = new float[] {
                0, 0, // TOP LEFT
                1, 0, // TOP RIGHT
                1, 1, // BOTTOM RIGHT
                0, 1  // BOTTOM LEFT
        };

        int[] indices = new int[] {
                0, 1, 2,
                0, 2, 3
        };

        model = new Model(vertices, texture, indices);
    }

    public static void deleteAsset() {
        model = null;
    }

    public float getSpeed() {
        return SPEED;
    }

    public Transform getTransform() {
        return transform;
    }
}
