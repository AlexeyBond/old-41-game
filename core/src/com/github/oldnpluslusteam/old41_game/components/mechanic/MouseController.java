package com.github.oldnpluslusteam.old41_game.components.mechanic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.github.alexeybond.partly_solid_bicycle.game.Component;
import com.github.alexeybond.partly_solid_bicycle.game.Entity;
import com.github.alexeybond.partly_solid_bicycle.game.Game;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.ComponentDeclaration;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.GameDeclaration;
import com.github.alexeybond.partly_solid_bicycle.game.systems.box2d_physics.components.MouseJointComponent;
import com.github.alexeybond.partly_solid_bicycle.game.systems.box2d_physics.interfaces.APhysicsSystem;
import com.github.alexeybond.partly_solid_bicycle.game.systems.box2d_physics.interfaces.CollidablePhysicsComponent;
import com.github.alexeybond.partly_solid_bicycle.game.systems.box2d_physics.interfaces.UpdatablePhysicsComponent;
import com.github.alexeybond.partly_solid_bicycle.game.systems.input.InputSystem;
import com.github.alexeybond.partly_solid_bicycle.util.event.Event;
import com.github.alexeybond.partly_solid_bicycle.util.event.helpers.Subscription;
import com.github.alexeybond.partly_solid_bicycle.util.event.props.BooleanProperty;
import com.github.alexeybond.partly_solid_bicycle.util.event.props.ObjectProperty;
import com.github.alexeybond.partly_solid_bicycle.util.event.props.Vec2Property;

public class MouseController implements Component, UpdatablePhysicsComponent {
    private final String cameraName;
    private final float forcePerMass, damping, frequency, tolerance;

    private final MouseJointDef jointDef = new MouseJointDef();

    private InputSystem inputSystem;
    private APhysicsSystem physicsSystem;
    private Vec2Property mousePosProperty;

    private ObjectProperty<Camera> cameraProperty;

    private boolean alive;

    private Body hitBody = null;

    private final QueryCallback queryCallback = new QueryCallback() {
        @Override
        public boolean reportFixture(Fixture fixture) {
            Body body = fixture.getBody();

            if (body.getType() != BodyDef.BodyType.DynamicBody) {
                return true;
            }

            hitBody = body;

            return false;
        }
    };

    private final Subscription<BooleanProperty> mousePressSub
            = new Subscription<BooleanProperty>() {
        @Override
        public boolean onTriggered(BooleanProperty event) {
            if (null == joint && event.get()) {
                Vector2 t = jointDef.target.set(unprojectMousePos(jointDef.target));
                hitBody = null;
                physicsSystem.world().QueryAABB(
                        queryCallback,
                        t.x - tolerance, t.y - tolerance,
                        t.x + tolerance, t.y + tolerance
                );

                if (null == hitBody) return false;

                try {
                    Entity grabbed = ((CollidablePhysicsComponent) hitBody.getUserData()).entity();
                    grabbed.events().event("grab", Event.makeEvent()).trigger();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                jointDef.bodyB = hitBody;
                jointDef.maxForce = hitBody.getMass() * forcePerMass;
                hitBody.setAwake(true);

                joint = (MouseJoint) physicsSystem.world().createJoint(jointDef);

                return true;
            }

            if (null != joint && !event.get()) {
                try {
                    Entity grabbed = ((CollidablePhysicsComponent) joint.getBodyB().getUserData()).entity();
                    grabbed.events().event("ungrab", Event.makeEvent()).trigger();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                physicsSystem.world().destroyJoint(joint);
                joint = null;

                return true;
            }

            return false;
        }
    };

    private MouseJoint joint;

    public MouseController(
            String cameraName, float forcePerMass, float damping, float frequency, float tolerance) {
        this.cameraName = cameraName;
        this.forcePerMass = forcePerMass;
        this.damping = damping;
        this.frequency = frequency;
        this.tolerance = tolerance;
    }

    @Override
    public void onConnect(Entity entity) {
        physicsSystem = entity.game().systems().get("physics");
        inputSystem = entity.game().systems().get("input");

        cameraProperty = entity.game().events()
                .event(cameraName, ObjectProperty.<Camera>make());

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        jointDef.bodyA = physicsSystem.world().createBody(bodyDef);
        jointDef.dampingRatio = damping;
        jointDef.frequencyHz = frequency;
        mousePressSub.set(inputSystem.input().events()
                .event("mouseDown", BooleanProperty.make()));
        mousePosProperty = inputSystem.input().events()
                .event("mousePos", Vec2Property.make());

        alive = true;
        physicsSystem.registerComponent(this);
    }

    @Override
    public void onDisconnect(Entity entity) {
        mousePressSub.clear();

        if (null != joint) physicsSystem.world().destroyJoint(joint);

        joint = null;
        alive = false;
    }

    @Override
    public void update() {
        if (null != joint) {
            joint.setTarget(unprojectMousePos(joint.getTarget()));
        }
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    private final Vector3 tmp = new Vector3();

    private Vector2 unprojectMousePos(Vector2 dst) {
        tmp.set(mousePosProperty.ref(), 0);
        tmp.scl(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 1);
        tmp.set(cameraProperty.get().unproject(
                tmp,
                0,0,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight()
        ));
        dst.set(tmp.x, tmp.y);
        return dst;
    }

    public static class Decl implements ComponentDeclaration {
        public String camera = "mainCamera";

        public float force = 1000;

        public float damping = 1f;

        public float frequency = 100;

        public float tolerance = 1;

        @Override
        public Component create(GameDeclaration gameDeclaration, Game game) {
            return new MouseController(
                    camera,
                    force,
                    damping,
                    frequency,
                    tolerance
            );
        }
    }
}
