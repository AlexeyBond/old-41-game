package com.github.oldnpluslusteam.old41_game.components;

import com.github.alexeybond.partly_solid_bicycle.drawing.sprite.SpriteTemplate;
import com.github.alexeybond.partly_solid_bicycle.drawing.sprite.renderer.StandardSpriteInstance;
import com.github.alexeybond.partly_solid_bicycle.game.systems.render.components.SpriteComponent;

public class SwitchSpriteComponent extends SpriteComponent {
    private SpriteTemplate template;

    public SwitchSpriteComponent(SpriteTemplate template, float scale) {
        super("game-objects", new StandardSpriteInstance(), scale);
        this.template = template;
    }

    @Override
    protected SpriteTemplate getTemplate() {
        return template;
    }

    public void setTemplate(SpriteTemplate template) {
        this.template = template;
    }
}
