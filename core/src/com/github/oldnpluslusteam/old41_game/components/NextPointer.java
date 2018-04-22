package com.github.oldnpluslusteam.old41_game.components;

import com.github.alexeybond.partly_solid_bicycle.game.Component;
import com.github.alexeybond.partly_solid_bicycle.game.Entity;
import com.github.alexeybond.partly_solid_bicycle.game.Game;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.ComponentDeclaration;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.GameDeclaration;
import com.github.alexeybond.partly_solid_bicycle.util.event.props.ObjectProperty;

public class NextPointer implements Component {
    @Override
    public void onConnect(Entity entity) {
        entity.events().event("nextLevel", ObjectProperty.<String>make()).use(ObjectProperty.STRING_LOADER);
    }

    @Override
    public void onDisconnect(Entity entity) {
    }

    public static class Decl implements ComponentDeclaration {

        @Override
        public Component create(GameDeclaration gameDeclaration, Game game) {
            return new NextPointer();
        }
    }
}
