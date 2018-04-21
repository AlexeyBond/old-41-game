package com.github.oldnpluslusteam.old41_game.screens;

import com.badlogic.gdx.math.Matrix4;
import com.github.alexeybond.partly_solid_bicycle.drawing.Pass;
import com.github.alexeybond.partly_solid_bicycle.drawing.ProjectionMode;
import com.github.alexeybond.partly_solid_bicycle.drawing.TargetSlot;
import com.github.alexeybond.partly_solid_bicycle.drawing.tech.PlainTechnique;

public class GameScreenTechnique extends PlainTechnique {
    private Pass setupCameraPass, backgroundPass, qPass, objectsPass, debugPass, uiPass;
    private Pass backloopPass;
    private TargetSlot qTarget;

    private Matrix4 projBg = new Matrix4().setToOrtho2D(0, 0,1,1);

    @Override
    protected void setup() {
        setupCameraPass = newPass("setup-main-camera");
        backloopPass = newPass("game-background-loop");
        backgroundPass = newPass("game-background");
        qPass = newPass("q");
        objectsPass = newPass("game-objects");
        debugPass = newPass("game-debug");
        uiPass = newPass("ui-main");

        qTarget = scene().context().getSlot("q");
    }

    @Override
    protected void draw() {
        ensureMatchingFBO(qTarget, scene().context().getOutputTarget());

        toTarget(qTarget);

        gl.glClearColor(0,0,0,0);
        clear();
        doPass(setupCameraPass);
        doPass(qPass);

        toOutput();

        clear();
        state().setProjection(projBg);
        doPass(backloopPass);
        doPass(setupCameraPass);
        doPass(backgroundPass);
        screenQuad(qTarget.get().asColorTexture(), true);
        doPass(setupCameraPass);
        doPass(objectsPass);
        doPass(debugPass);
        doPass(uiPass);
    }
}
