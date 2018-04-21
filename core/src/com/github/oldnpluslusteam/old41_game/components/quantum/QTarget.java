package com.github.oldnpluslusteam.old41_game.components.quantum;

import com.github.alexeybond.partly_solid_bicycle.game.Component;
import com.github.alexeybond.partly_solid_bicycle.game.Entity;
import com.github.alexeybond.partly_solid_bicycle.game.Game;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.ComponentDeclaration;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.GameDeclaration;
import com.github.alexeybond.partly_solid_bicycle.game.systems.timing.TimingSystem;
import com.github.alexeybond.partly_solid_bicycle.util.event.Event;
import com.github.alexeybond.partly_solid_bicycle.util.event.helpers.Subscription;
import com.github.alexeybond.partly_solid_bicycle.util.event.props.FloatProperty;
import com.github.alexeybond.partly_solid_bicycle.util.event.props.IntProperty;
import com.github.alexeybond.partly_solid_bicycle.util.event.props.ObjectProperty;

public class QTarget implements Component {
    private final float timeout;
    private IntProperty winCondition, winDone;
    private TimingSystem timingSystem;
    private int cnt = 0;

    private Subscription<ObjectProperty<Quant>> triggerSub = new Subscription<ObjectProperty<Quant>>() {
        @Override
        public boolean onTriggered(ObjectProperty<Quant> event) {
            ++cnt;
            if (cnt == 1) {
                winDone.set(winDone.get() + 1);
            }

            timingSystem.scheduleAt(
                    timingSystem.events().<FloatProperty>event("time").get() + timeout,
                    timeoutSub.event());
            return false;
        }
    };

    private Subscription<Event> timeoutSub = new Subscription<Event>() {
        @Override
        public boolean onTriggered(Event event) {
            --cnt;
            if (cnt == 0) {
                winDone.set(winDone.get() - 1);
            }
            return false;
        }
    };

    public QTarget(float timeout) {
        this.timeout = timeout;
    }

    @Override
    public void onConnect(Entity entity) {
        timingSystem = entity.game().systems().get("timing");
        winCondition = entity.game().events().event("winCondition", IntProperty.make());
        winDone = entity.game().events().event("winDone", IntProperty.make());

        triggerSub.set(entity.components().<QuantTrigger>get("qt").trigger);
        timeoutSub.set(Event.makeEvent().create());

        winCondition.set(winCondition.get() + 1);
    }

    @Override
    public void onDisconnect(Entity entity) {
        winCondition.set(winCondition.get() - 1);

        if (cnt > 0) {
            winDone.set(winDone.get() - 1);
        }

        winCondition = null;
        winDone = null;

        triggerSub.clear();
        timeoutSub.clear();
    }

    public static class Decl implements ComponentDeclaration {
        public float timeout = 10;

        @Override
        public Component create(GameDeclaration gameDeclaration, Game game) {
            return new QTarget(timeout);
        }
    }
}
