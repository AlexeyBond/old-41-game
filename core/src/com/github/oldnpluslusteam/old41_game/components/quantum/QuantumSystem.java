package com.github.oldnpluslusteam.old41_game.components.quantum;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.github.alexeybond.partly_solid_bicycle.game.Game;
import com.github.alexeybond.partly_solid_bicycle.game.GameSystem;

import java.util.ArrayList;
import java.util.List;

public class QuantumSystem implements GameSystem {
    final List<QuantTrigger> triggers = new ArrayList<QuantTrigger>();

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void onConnect(Game game) {

    }

    @Override
    public void onDisconnect(Game game) {

    }

    private final Vector2 tmp = new Vector2();

    public void testQuant(Vector2 prevPos, Vector2 nextPos, Quant quant) {
        for (QuantTrigger trigger : triggers) {
            if (Intersector.intersectSegments(
                prevPos, nextPos,
                    trigger.transformedP1, trigger.transformedP2,
                    tmp
            )) {
                trigger.trigger.setSilently(quant);
                trigger.trigger.trigger();
            }
        }
    }
}
