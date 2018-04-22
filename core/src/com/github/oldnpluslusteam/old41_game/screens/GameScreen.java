package com.github.oldnpluslusteam.old41_game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.github.alexeybond.partly_solid_bicycle.application.Layer;
import com.github.alexeybond.partly_solid_bicycle.application.Screen;
import com.github.alexeybond.partly_solid_bicycle.application.impl.DefaultScreen;
import com.github.alexeybond.partly_solid_bicycle.application.impl.layers.GameLayer;
import com.github.alexeybond.partly_solid_bicycle.application.impl.layers.GameLayerWith2DPhysicalGame;
import com.github.alexeybond.partly_solid_bicycle.application.impl.layers.StageLayer;
import com.github.alexeybond.partly_solid_bicycle.application.util.ScreenUtils;
import com.github.alexeybond.partly_solid_bicycle.drawing.Technique;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.GameDeclaration;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.visitor.impl.ApplyGameDeclarationVisitor;
import com.github.alexeybond.partly_solid_bicycle.game.systems.box2d_physics.PhysicsSystem;
import com.github.alexeybond.partly_solid_bicycle.game.systems.tagging.TaggingSystem;
import com.github.alexeybond.partly_solid_bicycle.ioc.IoC;
import com.github.alexeybond.partly_solid_bicycle.util.event.EventListener;
import com.github.alexeybond.partly_solid_bicycle.util.event.props.BooleanProperty;
import com.github.alexeybond.partly_solid_bicycle.util.event.props.IntProperty;
import com.github.alexeybond.partly_solid_bicycle.util.event.props.ObjectProperty;
import com.github.alexeybond.partly_solid_bicycle.util.parts.AParts;
import com.github.oldnpluslusteam.old41_game.components.quantum.QuantumSystem;

public class GameScreen extends DefaultScreen {
    public static String INITIAL_LEVEL = "level0.json";

    private final String levelName;

    public GameScreen(String levelName) {
        this.levelName = levelName;
    }

    @Override
    protected Technique createTechnique() {
        return new GameScreenTechnique();
    }

    @Override
    protected void createLayers(AParts<Screen, Layer> layers) {
        super.createLayers(layers);

        ScreenUtils.enableToggleDebug(this, false);

        GameLayer gameLayer = layers.add("game", new GameLayerWith2DPhysicalGame());
        StageLayer stageLayer = layers.add("ui", new StageLayer("ui-main"));

        // game setup

        gameLayer.game().systems().add("q", new QuantumSystem());

        gameLayer.game().systems().<PhysicsSystem>get("physics").world()
                .setGravity(new Vector2(0, -100));

        GameDeclaration gameDeclaration = IoC.resolve(
                "load game declaration",
                Gdx.files.internal(levelName));

        new ApplyGameDeclarationVisitor().doVisit(gameDeclaration, gameLayer.game());

        //

        Skin skin = IoC.resolve("load skin", "ui/uiskin.json");

        final Label progressLabel = new Label("", skin);
        final ProgressBar progressBar = new ProgressBar(0, 1, 1, false, skin);
//        final TextButton button = new TextButton("Next", skin);
        final ImageButton button = new ImageButton(
                new TextureRegionDrawable(
                        IoC.<TextureRegion>resolve("get texture region", "sprites/done-button-o")),
                new TextureRegionDrawable(
                        IoC.<TextureRegion>resolve("get texture region", "sprites/done-button-h"))
        );

        button.setDisabled(true);
        button.setVisible(false);

        VerticalGroup group = new VerticalGroup();
        group.addActor(progressLabel);
        group.addActor(progressBar);
        group.addActor(button);
        group.setFillParent(true);

        stageLayer.stage().addActor(group);

        // wincondition setup

        final IntProperty wc = gameLayer.game().events().event("winCondition", IntProperty.make());
        final IntProperty wd = gameLayer.game().events().event("winDone", IntProperty.make());

        EventListener<IntProperty> winListener = new EventListener<IntProperty>() {
            @Override
            public boolean onTriggered(IntProperty event) {
                progressLabel.setText("Done " + wd.get() + " of " + wc.get());
                progressBar.setRange(0, wc.get());
                progressBar.setValue(wd.get());
                if (wd.get() != 0 && wd.get() == wc.get()) {
                    // win
                    button.setDisabled(false);
                    button.setVisible(true);
                }
                return false;
            }
        };

        wc.subscribe(winListener);
        wd.subscribe(winListener);
        wd.trigger();

        Gdx.gl.glClearColor(0.2f, 0.2f, 0.24f, 0.0f);

        try {
            final String nextLevel = gameLayer.game().systems().<TaggingSystem>get("tagging")
                    .group("next_pointer").getOnly()
                    .events().<ObjectProperty<String>>event("nextLevel").get();

            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    next(new GameScreen(nextLevel));
                }
            });
        } catch (RuntimeException e) {
//            button.setText("Exit");
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Gdx.app.exit();
                }
            });
        }

        input().keyEvent(Input.Keys.ESCAPE).subscribe(new EventListener<BooleanProperty>() {
            @Override
            public boolean onTriggered(BooleanProperty event) {
                if (!event.get()) {
                    Gdx.app.exit();
                }
                return false;
            }
        });
    }
}
