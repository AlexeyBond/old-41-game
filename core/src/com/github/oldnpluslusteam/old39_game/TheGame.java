package com.github.oldnpluslusteam.old39_game;

import com.github.alexeybond.partly_solid_bicycle.application.Application;
import com.github.alexeybond.partly_solid_bicycle.application.modules.DefaultLoadingScreenModule;
import com.github.alexeybond.partly_solid_bicycle.drawing.modules.GlobalDrawingState;
import com.github.alexeybond.partly_solid_bicycle.drawing.modules.GlobalParticlePool;
import com.github.alexeybond.partly_solid_bicycle.drawing.modules.ShaderLoader;
import com.github.alexeybond.partly_solid_bicycle.game.modules.CommonComponents;
import com.github.alexeybond.partly_solid_bicycle.game.modules.GameSerialization;
import com.github.alexeybond.partly_solid_bicycle.game.systems.box2d_physics.modules.PhysicsComponentDeclarations;
import com.github.alexeybond.partly_solid_bicycle.game.systems.input.modules.InputComponentsDeclarations;
import com.github.alexeybond.partly_solid_bicycle.game.systems.render.modules.RenderComponentsDeclarations;
import com.github.alexeybond.partly_solid_bicycle.game.systems.render.modules.ZoomFunctionsModule;
import com.github.alexeybond.partly_solid_bicycle.game.systems.tagging.modules.TaggingComponentsDeclarations;
import com.github.alexeybond.partly_solid_bicycle.ioc.modules.Modules;
import com.github.alexeybond.partly_solid_bicycle.music.modules.GlobalMusicPlayer;
import com.github.alexeybond.partly_solid_bicycle.resource_management.modules.ResourceManagement;
import com.github.oldnpluslusteam.old39_game.modules.StartupScreenModule;

public class TheGame extends Application {
	@Override
	protected Modules setupModules(Modules modules) {
		modules = super.setupModules(modules);

		modules.add(new ResourceManagement());
		modules.add(new GlobalDrawingState());
		modules.add(new GlobalParticlePool());
		modules.add(new ShaderLoader());
		modules.add(new GameSerialization());
		modules.add(new DefaultLoadingScreenModule());
		modules.add(new GlobalMusicPlayer());

		modules.add(new PhysicsComponentDeclarations());
		modules.add(new InputComponentsDeclarations());
		modules.add(new RenderComponentsDeclarations());
		modules.add(new ZoomFunctionsModule());
		modules.add(new TaggingComponentsDeclarations());
		modules.add(new CommonComponents());

		modules.add(new StartupScreenModule());

		return modules;
	}
}
