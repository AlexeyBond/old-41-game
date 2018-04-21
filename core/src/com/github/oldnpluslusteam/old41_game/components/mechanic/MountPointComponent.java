package com.github.oldnpluslusteam.old41_game.components.mechanic;

import com.github.alexeybond.partly_solid_bicycle.game.Component;
import com.github.alexeybond.partly_solid_bicycle.game.Entity;
import com.github.alexeybond.partly_solid_bicycle.game.Game;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.ComponentDeclaration;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.GameDeclaration;

public class MountPointComponent implements Component {
    @Override
    public void onConnect(Entity entity) {

    }

    @Override
    public void onDisconnect(Entity entity) {

    }

    public static class Decl implements ComponentDeclaration {
        @Override
        public Component create(GameDeclaration gameDeclaration, Game game) {
            return new MountPointComponent();
        }
    }
}
