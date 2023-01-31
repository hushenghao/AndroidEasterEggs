/**
 * Game main loop.
 */







if(config.renderer.interval === false) {
    // no fps cap

} else {
    // with fps cap
    var frameCount = 0;
    var fps, fpsInterval, startTime, now, then, elapsed;

    let timeDelta = clock.getDelta();
    function loop() {

        // request another frame
        requestAnimationFrame(loop);

        // calc elapsed time since last loop
        now = performance.now();
        elapsed = now - then;

        // if enough time has elapsed, draw the next frame
        if (elapsed > fpsInterval) {

            // Get ready for next frame by setting then=now, but...
            // Also, adjust for fpsInterval not being multiple of 16.67
            then = now - (elapsed % fpsInterval);

            // draw stuff here
            if(config.camera.controls) {controls.update();}
            player.update(timeDelta);
            enemy.update(timeDelta);
            nature.update(timeDelta);
            input.update();
            nebulaSystem.update();
            if(config.renderer.postprocessing.enable) {
                composer.render(timeDelta);
            } else {
                renderer.render( scene, camera );
            }
            score.update(timeDelta);

            // TESTING...Report #seconds since start and achieved fps.
            if(config.renderer.fps_counter) {
                var sinceStart = now - startTime;
                var currentFps = Math.round(1000 / (sinceStart / ++frameCount) * 100) / 100;
                fc.innerHTML = currentFps;

                if(currentFps < (config.renderer.interval - (config.renderer.interval / 4)) ) {
                    fc.className = 'fps-status-bad';
                } else {
                    fc.className = 'fps-status-good';
                }
            }
        }
    }

    // load all assets & run
    function startAnimating(fps) {
        fpsInterval = 1000 / fps;
        then = performance.now();
        startTime = then;
        loop();
    }

    load_manager.load_all(function() {
        enemy.init();

        loop();
    });
}