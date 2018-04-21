package com.github.oldnpluslusteam.old41_game.components.mechanic;

import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.github.alexeybond.partly_solid_bicycle.game.Component;
import com.github.alexeybond.partly_solid_bicycle.game.Entity;
import com.github.alexeybond.partly_solid_bicycle.game.Game;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.ComponentDeclaration;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.GameDeclaration;
import com.github.alexeybond.partly_solid_bicycle.game.systems.box2d_physics.CollisionData;
import com.github.alexeybond.partly_solid_bicycle.game.systems.box2d_physics.PhysicsSystem;
import com.github.alexeybond.partly_solid_bicycle.game.systems.box2d_physics.interfaces.BodyPhysicsComponent;
import com.github.alexeybond.partly_solid_bicycle.game.systems.box2d_physics.interfaces.CollidablePhysicsComponent;
import com.github.alexeybond.partly_solid_bicycle.game.systems.box2d_physics.interfaces.CreatablePhysicsComponent;
import com.github.alexeybond.partly_solid_bicycle.util.event.Event;
import com.github.alexeybond.partly_solid_bicycle.util.event.helpers.Subscription;
import com.github.alexeybond.partly_solid_bicycle.util.event.props.ObjectProperty;

import java.util.NoSuchElementException;

public class MountableComponent implements Component {
    private PhysicsSystem physicsSystem;
    private RevoluteJointDef jointDef = new RevoluteJointDef();
    private BodyPhysicsComponent body;

    private Joint joint;

    private boolean grabbed;

    private Entity lastMountpoint;

    private void disjoint() {
        if (joint != null) {
            physicsSystem.world().destroyJoint(joint);
            joint = null;
        }
    }

    private void joint() {
        jointDef.bodyA = body.body();
        jointDef.bodyB = lastMountpoint.components().<BodyPhysicsComponent>get("body").body();
        jointDef.localAnchorA.set(0, 0);
        jointDef.localAnchorB.set(0, 0);
        jointDef.referenceAngle = 0;

        physicsSystem.enqueueCreatable(new CreatablePhysicsComponent() {
            @Override
            public void create() {
                if (null == joint) joint = physicsSystem.world().createJoint(jointDef);
            }

            @Override
            public void onConnect(Entity entity) {

            }

            @Override
            public void onDisconnect(Entity entity) {

            }
        });
    }

    private Subscription<Event> grabSub = new Subscription<Event>() {
        @Override
        public boolean onTriggered(Event event) {
            disjoint();
            grabbed = true;
            return false;
        }
    };
    private Subscription<Event> ungrabSub = new Subscription<Event>() {
        @Override
        public boolean onTriggered(Event event) {
            grabbed = false;
            if (lastMountpoint != null) {
                joint();
            }
            return false;
        }
    };
    private Subscription<ObjectProperty<CollisionData>> collisionBeginSub
            = new Subscription<ObjectProperty<CollisionData>>() {
        @Override
        public boolean onTriggered(ObjectProperty<CollisionData> event) {
            try {
                Entity entity = ((CollidablePhysicsComponent)
                        event.get().otherFixture().getBody().getUserData()).entity();

                try {
                    entity.components().get("mmp");
                } catch (NoSuchElementException e) {
                    return false;
                }

                lastMountpoint = entity;
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!grabbed) joint();

            return false;
        }
    };
    private Subscription<ObjectProperty<CollisionData>> collisionEndSub
            = new Subscription<ObjectProperty<CollisionData>>() {
        @Override
        public boolean onTriggered(ObjectProperty<CollisionData> event) {
            try {
                Entity entity = ((CollidablePhysicsComponent)
                        event.get().otherFixture().getBody().getUserData()).entity();

                if (entity == lastMountpoint) {
                    lastMountpoint = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    };

    @Override
    public void onConnect(Entity entity) {
        physicsSystem = entity.game().systems().get("physics");
        grabSub.set(entity.events().event("grab", Event.makeEvent()));
        ungrabSub.set(entity.events().event("ungrab", Event.makeEvent()));

        collisionBeginSub.set(entity.events()
                .event("collisionBegin", ObjectProperty.<CollisionData>make()));
        collisionEndSub.set(entity.events()
                .event("collisionEnd", ObjectProperty.<CollisionData>make()));

        body = entity.components().get("body");
    }

    @Override
    public void onDisconnect(Entity entity) {
        grabSub.clear();
        ungrabSub.clear();
        collisionBeginSub.clear();
        collisionEndSub.clear();
    }

    public static class Decl implements ComponentDeclaration {

        @Override
        public Component create(GameDeclaration gameDeclaration, Game game) {
            return new MountableComponent();
        }
    }
}
