package entity;

import assets.Assets;
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

public abstract class Entity {
    protected AABB bounding_box;
    protected Animation[] animations;
    private int use_animation;

    protected Transform transform;
    protected float SPEED = 10f;

    public Entity(int max_animations, Transform transform, Vector2f hitbox) {
        this.animations = new Animation[max_animations];
        this.use_animation = 0;

        this.transform = transform;

        bounding_box = new AABB(new Vector2f(transform.pos.x, transform.pos.y), hitbox);
    }

    protected void setAnimation(int index, Animation animation) {
        if (index >= this.animations.length) {
            throw new ArrayIndexOutOfBoundsException("setAnimation index [" + index + "] is out of Bounds");
        }
        animations[index] = animation;
    }

    public void useAnimation(int index) {
        if (index >= this.animations.length) {
            throw new ArrayIndexOutOfBoundsException("useAnimation index [" + index + "] is out of Bounds");
        }
        this.use_animation = index;
    }

    public void move(Vector2f direction) {
        transform.pos.add(direction.x, direction.y, 0);

        bounding_box.getCenter().set(transform.pos.x, transform.pos.y);
    }

    public void collideWithWorld(World world) {
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

        //System.out.println("Player pos: [" + transform.pos.x + ", " + transform.pos.y + "]");
        //System.out.println("Hitbox pos: [" + bounding_box.getCenter().x + ", " + bounding_box.getCenter().y + "]");
    }

    public void collideWithEntity(Entity entity) {
        Collision collision = bounding_box.getCollision(entity.bounding_box);
        if (!collision.isIntersecting) {return;}

        collision.distance.div(2);

        bounding_box.correctPosition(entity.bounding_box, collision);
        transform.pos.set(bounding_box.getCenter(), 0);

        entity.bounding_box.correctPosition(bounding_box, collision);
        entity.transform.pos.set(entity.bounding_box.getCenter(), 0);
    }

    public abstract void update(float delta, Window window, Camera camera, World world);

    public void render(Shader shader, Camera camera, World world) {
        Matrix4f target = camera.getProjection(); // Projection Matrix
        target.mul(world.getWorldMatrix()); // View Matrix

        shader.bind();
        shader.setUniform("sampler", 0);
        shader.setUniform("projection", transform.getProjection(target)); // Projection * View * Model
        animations[use_animation].bind(0);
        Assets.getModel().render();
    }

    public float getSpeed() {
        return SPEED;
    }

    public Transform getTransform() {
        return transform;
    }
}
