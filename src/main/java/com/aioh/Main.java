package com.aioh;

import com.aioh.graphics.AiohWindow;
import com.aioh.text.AiohFreeGlyphAtlas;

public class Main {


    public static void main(String[] args) throws Exception {

        var window = new AiohWindow("Aioh", 960, 540);
        window.init();
        var face = AiohUtils.initFace();
        var atlas = new AiohFreeGlyphAtlas(face);
        var listener = new AiohEventListener();
        listener.init();

        while (!window.shouldClose()) {
            window.update();
            listener.display();
        }

        window.destroy();

    }
}
