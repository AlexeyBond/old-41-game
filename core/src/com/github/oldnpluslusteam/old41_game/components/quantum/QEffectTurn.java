package com.github.oldnpluslusteam.old41_game.components.quantum;

import com.badlogic.gdx.math.Vector2;
import com.github.alexeybond.partly_solid_bicycle.game.Component;
import com.github.alexeybond.partly_solid_bicycle.game.Entity;
import com.github.alexeybond.partly_solid_bicycle.game.Game;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.ComponentDeclaration;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.GameDeclaration;
import com.github.alexeybond.partly_solid_bicycle.util.event.helpers.Subscription;
import com.github.alexeybond.partly_solid_bicycle.util.event.props.ObjectProperty;
import com.github.alexeybond.partly_solid_bicycle.util.event.props.Vec2Property;

public class QEffectTurn implements Component {
    private final String trigger;
    private final int dir;
    private Vec2Property position;
    private Vector2 tmp = new Vector2();

    private Subscription<ObjectProperty<Quant>> sub = new Subscription<ObjectProperty<Quant>>() {
        @Override
        public boolean onTriggered(ObjectProperty<Quant> event) {
            Quant quant = event.get();

            quant.redirect(tmp.set(quant.getVelocity()).rotate90(dir), position.ref());

            return false;
        }
    };

    public QEffectTurn(String trigger, int dir) {
        this.trigger = trigger;
        this.dir = dir;
    }

    @Override
    public void onConnect(Entity entity) {
        position = entity.events().event("position", Vec2Property.make());
        sub.set(entity.components().<QuantTrigger>get(trigger).trigger);
    }

    @Override
    public void onDisconnect(Entity entity) {
        sub.clear();
    }

    public static class Decl implements ComponentDeclaration {
        public String trigger = "qt";
        public int direction = 1;

        @Override
        public Component create(GameDeclaration gameDeclaration, Game game) {
            return new QEffectTurn(trigger, direction);
        }
    }
}
