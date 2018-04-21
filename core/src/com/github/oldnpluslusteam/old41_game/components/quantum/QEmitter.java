package com.github.oldnpluslusteam.old41_game.components.quantum;

import com.github.alexeybond.partly_solid_bicycle.game.Component;
import com.github.alexeybond.partly_solid_bicycle.game.Entity;
import com.github.alexeybond.partly_solid_bicycle.game.Game;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.ComponentDeclaration;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.EntityDeclaration;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.GameDeclaration;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.visitor.impl.ApplyEntityDeclarationVisitor;
import com.github.alexeybond.partly_solid_bicycle.game.systems.timing.TimingSystem;
import com.github.alexeybond.partly_solid_bicycle.util.event.Event;
import com.github.alexeybond.partly_solid_bicycle.util.event.helpers.Subscription;
import com.github.alexeybond.partly_solid_bicycle.util.event.props.FloatProperty;
import com.github.alexeybond.partly_solid_bicycle.util.event.props.Vec2Property;

public class QEmitter implements Component {
    private final EntityDeclaration quantDeclaration;
    private final GameDeclaration gameDeclaration;
    private final float period;

    private Game game;
    private TimingSystem timingSystem;
    private FloatProperty rotation;
    private Vec2Property position;

    private Event fire = Event.makeEvent().create();

    private final Subscription sub = new Subscription() {
        @Override
        public boolean onTriggered(Event event) {
            Entity entity = new ApplyEntityDeclarationVisitor().doVisit(
                    quantDeclaration, gameDeclaration, new Entity(game)
            );

            entity.events().event("position", Vec2Property.make()).set(position.ref());
            entity.events().event("rotation", FloatProperty.make()).set(rotation.get());

            reschedule();
            return false;
        }
    };

    public QEmitter(EntityDeclaration quantDeclaration, GameDeclaration gameDeclaration, float period) {
        this.quantDeclaration = quantDeclaration;
        this.gameDeclaration = gameDeclaration;
        this.period = period;
    }

    private void reschedule() {
        timingSystem.scheduleAt(
                timingSystem.events().<FloatProperty>event("time").get() + period, fire);
    }

    @Override
    public void onConnect(Entity entity) {
        rotation = entity.events().event("rotation", FloatProperty.make());
        position = entity.events().event("position", Vec2Property.make());

        this.game = entity.game();
        timingSystem = entity.game().systems().get("timing");

        sub.set(fire);

        reschedule();
    }

    @Override
    public void onDisconnect(Entity entity) {
        sub.set(fire);
    }

    public static class Decl implements ComponentDeclaration {
        public EntityDeclaration quant;
        public float period;

        @Override
        public Component create(GameDeclaration gameDeclaration, Game game) {
            return new QEmitter(quant, gameDeclaration, period);
        }
    }
}
