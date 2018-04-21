package com.github.oldnpluslusteam.old41_game.components.quantum;

import com.badlogic.gdx.math.Vector2;
import com.github.alexeybond.partly_solid_bicycle.game.Component;
import com.github.alexeybond.partly_solid_bicycle.game.Entity;
import com.github.alexeybond.partly_solid_bicycle.game.Game;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.ComponentDeclaration;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.GameDeclaration;
import com.github.alexeybond.partly_solid_bicycle.util.event.helpers.Subscription;
import com.github.alexeybond.partly_solid_bicycle.util.event.props.ObjectProperty;

public class QEffectDispose implements Component {
    private Vector2 tmp = new Vector2();

    private Subscription<ObjectProperty<Quant>> sub = new Subscription<ObjectProperty<Quant>>() {
        @Override
        public boolean onTriggered(ObjectProperty<Quant> event) {
            Quant quant = event.get();

            quant.dispose();

            return false;
        }
    };

    @Override
    public void onConnect(Entity entity) {
        sub.set(entity.components().<QuantTrigger>get("qt").trigger);
    }

    @Override
    public void onDisconnect(Entity entity) {
        sub.clear();
    }

    public static class Decl implements ComponentDeclaration {

        @Override
        public Component create(GameDeclaration gameDeclaration, Game game) {
            return new QEffectDispose();
        }
    }
}
