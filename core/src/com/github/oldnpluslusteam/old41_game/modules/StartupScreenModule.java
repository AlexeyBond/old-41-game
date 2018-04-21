package com.github.oldnpluslusteam.old41_game.modules;

import com.github.alexeybond.partly_solid_bicycle.ioc.IoC;
import com.github.alexeybond.partly_solid_bicycle.ioc.IoCStrategy;
import com.github.alexeybond.partly_solid_bicycle.ioc.modules.Module;
import com.github.oldnpluslusteam.old41_game.components.quantum.Quant;
import com.github.oldnpluslusteam.old41_game.screens.GameScreen;

import java.util.Map;

public class StartupScreenModule implements Module {
    @Override
    public void init() {
        IoC.register("initial screen", new IoCStrategy() {
            @Override
            public Object resolve(Object... args) {
                return new GameScreen(GameScreen.INITIAL_LEVEL);
            }
        });

        Map<String, Class> map = IoC.resolve("component type aliases");
        map.put("q", Quant.Decl.class);
    }

    @Override
    public void shutdown() {

    }
}
