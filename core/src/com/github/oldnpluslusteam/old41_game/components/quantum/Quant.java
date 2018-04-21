package com.github.oldnpluslusteam.old41_game.components.quantum;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Queue;
import com.github.alexeybond.partly_solid_bicycle.drawing.DrawingContext;
import com.github.alexeybond.partly_solid_bicycle.game.Component;
import com.github.alexeybond.partly_solid_bicycle.game.Entity;
import com.github.alexeybond.partly_solid_bicycle.game.Game;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.ComponentDeclaration;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.GameDeclaration;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.util.DeclUtils;
import com.github.alexeybond.partly_solid_bicycle.game.systems.render.RenderSystem;
import com.github.alexeybond.partly_solid_bicycle.game.systems.render.interfaces.RenderComponent;
import com.github.alexeybond.partly_solid_bicycle.game.systems.timing.TimingSystem;
import com.github.alexeybond.partly_solid_bicycle.util.event.helpers.Subscription;
import com.github.alexeybond.partly_solid_bicycle.util.event.props.FloatProperty;
import com.github.alexeybond.partly_solid_bicycle.util.event.props.Vec2Property;

import static java.lang.Math.exp;

public class Quant implements Component, RenderComponent {
    private Vec2Property position;
    private RenderSystem renderSystem;
    private float initialLength, targetLength, phase, frequency;
    private boolean isDisposing = false;
    private Vector2 velocity = new Vector2();

    private Vector2 tmp = new Vector2();

    private Quant(float initialLength, float targetLength, float phase, float frequency, Vector2 velocity) {
        this.initialLength = initialLength;
        this.targetLength = targetLength;
        this.frequency = frequency;
        this.phase = phase;
        this.velocity.set(velocity);
    }

    private float phaseEnergy(float phase) {
        return (0.5f * MathUtils.sin(MathUtils.PI * phase * frequency) + 0.5f);
    }

    private Queue<Segment> segments = new Queue<Segment>();

    private final Subscription<FloatProperty> dtSub = new Subscription<FloatProperty>() {
        @Override
        public boolean onTriggered(FloatProperty event) {
            if (0 == segments.size) {
                if (isDisposing) { /* ... */ }

                Segment segment = new Segment();
                segment.head.set(position.ref());
                segment.tail.set(velocity).setLength(initialLength).scl(-1).add(segment.head);

                segments.addFirst(segment);

                return false;
            }

            float dt = event.get();

            if (isDisposing) { /* ... */ }

            Segment first = segments.first();

            tmp.set(velocity).scl(dt);

            phase += tmp.len();

            first.head.add(tmp);

            position.set(first.head);

            float cutLength = -targetLength;

            for (Segment segment : segments) cutLength += segment.head.dst(segment.tail);

            while (cutLength > 0) {
                Segment last = segments.last();

                float l = last.head.dst(last.tail);

                if (l <= cutLength) {
                    segments.removeLast();
                    cutLength -= l;
                    continue;
                }

                tmp.set(last.head).sub(last.tail).nor().scl(cutLength);

                last.tail.add(tmp);
                break;
            }

            return false;
        }
    };

    private class Segment {
        Vector2 head = new Vector2();
        Vector2 tail = new Vector2();
    }

    @Override
    public void onConnect(Entity entity) {
        renderSystem = entity.game().systems().get("render");
        renderSystem.addToPass("q", this);

        position = entity.events().event("position", Vec2Property.make());
        dtSub.set(entity.game().systems().<TimingSystem>get("timing").events()
                .<FloatProperty>event("deltaTime"));

        segments.clear();
    }

    @Override
    public void onDisconnect(Entity entity) {
        dtSub.clear();
        renderSystem.removeFromPass("q", this);
    }

    @Override
    public void draw(DrawingContext context) {
        ShapeRenderer sr = context.state().beginFilled();

        float phase = this.phase;

        Vector2 tmp = this.tmp;


        float prevA = 0;
        float prevX = position.ref().x;
        float prevY = position.ref().y;

        for (Segment segment : segments) {
            tmp.set(segment.tail).sub(segment.head);
            float l = tmp.len();
            float step = 1.0f / l;
            tmp.scl(step).rotate90(1);

            if (l < 1.0f) continue;

            for (float f = 0; f < 1.0f; f += step) {
                float a0 = 10f;
                float a1 = phaseEnergy(phase);

                float p = (this.phase - phase) * (100f / targetLength);
                float a2 = (float) exp(-(p*0.1f)) * p * p * 0.02f;

                float a = a0 * a1 * a2;

                float x = MathUtils.lerp(segment.head.x, segment.tail.x, f);
                float y = MathUtils.lerp(segment.head.y, segment.tail.y, f);

                sr.triangle(
                        x - tmp.x * a,
                        y - tmp.y * a,
                        x + tmp.x * a,
                        y + tmp.y * a,
                        prevX - tmp.x * prevA,
                        prevY - tmp.y * prevA
                );

                sr.triangle(
                        prevX + tmp.x * prevA,
                        prevY + tmp.y * prevA,
                        x + tmp.x * a,
                        y + tmp.y * a,
                        prevX - tmp.x * prevA,
                        prevY - tmp.y * prevA
                );

                prevA = a;
                prevX = x;
                prevY = y;

                phase -= 1.0f;
            }
        }
    }

    public static class Decl implements ComponentDeclaration {
        public float initialLength, targetLength, phase, frequency = 0.1f;
        public float[] velocity = null;
        public float vx = 1, vy;
        private Vector2 vv;

        @Override
        public Component create(GameDeclaration gameDeclaration, Game game) {
            return new Quant(
                    initialLength,
                    targetLength,
                    phase,
                    frequency,
                    vv = DeclUtils.readVector(vv, velocity, vx, vy)
            );
        }
    }
}
