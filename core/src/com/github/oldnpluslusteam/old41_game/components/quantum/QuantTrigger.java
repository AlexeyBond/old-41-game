package com.github.oldnpluslusteam.old41_game.components.quantum;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.github.alexeybond.partly_solid_bicycle.drawing.DrawingContext;
import com.github.alexeybond.partly_solid_bicycle.game.Component;
import com.github.alexeybond.partly_solid_bicycle.game.Entity;
import com.github.alexeybond.partly_solid_bicycle.game.Game;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.ComponentDeclaration;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.GameDeclaration;
import com.github.alexeybond.partly_solid_bicycle.game.systems.render.RenderSystem;
import com.github.alexeybond.partly_solid_bicycle.game.systems.render.interfaces.RenderComponent;
import com.github.alexeybond.partly_solid_bicycle.util.event.Event;
import com.github.alexeybond.partly_solid_bicycle.util.event.helpers.Subscription;
import com.github.alexeybond.partly_solid_bicycle.util.event.props.FloatProperty;
import com.github.alexeybond.partly_solid_bicycle.util.event.props.ObjectProperty;
import com.github.alexeybond.partly_solid_bicycle.util.event.props.Vec2Property;

public class QuantTrigger implements Component, RenderComponent {
    private final Vector2 originalP1, originalP2;
    final Vector2 transformedP1 = new Vector2(), transformedP2 = new Vector2();

    private FloatProperty rotation;
    private Vec2Property position;
    private QuantumSystem system;
    private RenderSystem renderSystem;

    ObjectProperty<Quant> trigger = ObjectProperty.<Quant>make().create();

    private final Subscription positionSub = new Subscription() {
        @Override
        public boolean onTriggered(Event event) {
            transformedP1.set(originalP1).rotate(rotation.get()).add(position.ref());
            transformedP2.set(originalP2).rotate(rotation.get()).add(position.ref());

            return false;
        }
    };

    private final Subscription rotationSub = new Subscription() {
        @Override
        public boolean onTriggered(Event event) {
            transformedP1.set(originalP1).rotate(rotation.get()).add(position.ref());
            transformedP2.set(originalP2).rotate(rotation.get()).add(position.ref());

            return false;
        }
    };

    public QuantTrigger(Vector2 originalP1, Vector2 originalP2) {
        this.originalP1 = originalP1;
        this.originalP2 = originalP2;
    }

    @Override
    public void onConnect(Entity entity) {
        rotation = entity.events().event("rotation", FloatProperty.make());
        position = entity.events().event("position", Vec2Property.make());

        positionSub.set(position);
        rotationSub.set(rotation);

        system = entity.game().systems().get("q");
        system.triggers.add(this);

        renderSystem = entity.game().systems().get("render");
        renderSystem.addToPass("game-debug", this);
    }

    @Override
    public void onDisconnect(Entity entity) {
        positionSub.clear();
        rotationSub.clear();
        system.triggers.remove(this);

        renderSystem.removeFromPass("game-debug", this);
    }

    @Override
    public void draw(DrawingContext context) {
        ShapeRenderer sr = context.state().beginLines();

        sr.line(transformedP1, transformedP2);
    }

    public static class Decl implements ComponentDeclaration {
        public float[] points;

        @Override
        public Component create(GameDeclaration gameDeclaration, Game game) {
            return new QuantTrigger(
                    new Vector2(points[0], points[1]),
                    new Vector2(points[2], points[3])
            );
        }
    }
}
