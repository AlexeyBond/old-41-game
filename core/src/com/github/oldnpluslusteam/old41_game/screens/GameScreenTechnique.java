package com.github.oldnpluslusteam.old41_game.screens;

import com.github.alexeybond.partly_solid_bicycle.drawing.Pass;
import com.github.alexeybond.partly_solid_bicycle.drawing.TargetSlot;
import com.github.alexeybond.partly_solid_bicycle.drawing.tech.PlainTechnique;

public class GameScreenTechnique extends PlainTechnique {
    private Pass setupCameraPass, backgroundPass, qPass, objectsPass, debugPass, uiPass;
    private TargetSlot qTarget;

    @Override
    protected void setup() {
        setupCameraPass = newPass("setup-main-camera");
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
        doPass(setupCameraPass);
        doPass(backgroundPass);
        screenQuad(qTarget.get().asColorTexture(), true);
        doPass(objectsPass);
        doPass(debugPass);
        doPass(uiPass);
    }
}
