/**
 * GameManager class.
 *
 * @type {EnemyManager}
 */

class GameManager {
	constructor(interface_manager) {
		this.isPlaying = false;
        this.isPaused = false;
        this.isFirstStart = true;
        this.lastTimeDelta = false;

        this.interface = interface_manager;
        this.starter = null;
        this.stats = null;
	}

    init() {
        // init interface
        this.interface.init();

        // hook tab visibility
        visibly.visibilitychange(this.tabVisibilityChanged);

        window.onload = function() {
            // load all assets and start the game
            load_manager.load_all(function() {
                // all assets loaded
                game.interface.other.preloader.classList.add('hidden');

                if(config.debug) {
                    game.interface.btnStartClick();
                } else {
                    game.interface.buttons.start.classList.remove('hidden');
                    game.setStarter();
                }
            }, function() {
                // progress
                let p = load_manager.getLoadPercentage();
                game.interface.indicators.load.classList.add('bar-' + p);
            });
        }

        // debug
        if(config.debug) {
            enemy.config.enable_collisions = false;

            input.addKeyCallback('debug_speedup', 'justPressed', function() {
                enemy.increase_velocity(1);
            });

            enemy.increase_velocity(10);
        }
    }

    setStarter(timeout = 600) {
        if(!this.starter) {
            this.starter = input.addKeyCallback('space', 'justPressed', function() {
                game.starter = null;
                audio.play('jump');

                if(timeout > 0) {
                    game.interface.other.overlay.classList.add('before-start');
                    setTimeout(function() {
                        game.interface.btnStartClick();
                    }, timeout);
                } else {
                    game.interface.btnRestartClick();
                }
            }, 1);
        }
    }

    cancelStarter() {
        if(this.starter) {
            input.removeKeyCallback('space', this.starter);
            this.starter = null;
        }
    }

	async start() {
        if(this.isPlaying) {
            return false;
        }

		this.isPlaying = true;

		// set running speed (def 13)
		enemy.increase_velocity(15, true);

        // init score
        score.set(0);

        // init stuff
        // if(this.isFirstStart) {
        //     // one time inits
        //     this.isFirstStart = false;
        // }

        nature.initGround();
        nature.initEarth();

        // basic landscape
        nature.initGroundDecoration("first", -17.3, nature.cache.earth.box.max.y);
        nature.initGroundDecoration("second", -29.5, nature.cache.earth.box.max.y + 1.6);
        nature.initGroundDecoration("third", -42, nature.cache.earth.box.max.y + (1.6 * 2), false);

        // playground
        nature.ground_chunks_decoration_levels["playground"] = {
          "x": 0,
          "y": nature.cache.ground.box.max.y,
          "box": nature.cache.ground.box
        };

        // water level
        nature.ground_chunks_decoration_levels["water"] = {
          "x": -9,
          "y": nature.cache.earth.box.max.y,
          "box": nature.cache.earth.box
        };

        // water level additional
        nature.ground_chunks_decoration_levels["water2"] = {
          "x": -9,
          "y": nature.cache.earth.box.max.y,
          "box": nature.cache.earth.box
        };

        // water level additional
        nature.ground_chunks_decoration_levels["empty"] = {
          "x": 7,
          "y": nature.cache.earth.box.max.y,
          "box": nature.cache.earth.box
        };



        // var geometry = new THREE.BoxGeometry( .5, .5, .5 );
        // var material = new THREE.MeshBasicMaterial( {color: 0x00ff00} );
        // var cube = new THREE.Mesh( geometry, material );
        // scene.add( cube );
        // window.c = cube;

        // set spawns
        nature.config.levels.first.spawn = load_manager.get_certain_mesh('misc', ['tumbleweed', 'cactus', 'desert_skull', 'scorpion', 'rocks', 'flowers'], 'misc_type', true);
        nature.config.levels.second.spawn = load_manager.get_certain_mesh('misc', ['tumbleweed', 'desert_skull', 'scorpion', 'rocks', 'flowers', 'trees'], 'misc_type', true);
        nature.config.levels.third.spawn = load_manager.get_certain_mesh('misc', ['tumbleweed', 'trees'], 'misc_type', true);

        nature.config.levels.playground.spawn = load_manager.get_certain_mesh('misc', ['desert_skull', 'rocks', 'flowers'], 'misc_type', true);

        nature.config.levels.water.spawn = load_manager.get_certain_mesh('misc', ['fish'], 'misc_type', true);
        nature.config.levels.water2.spawn = load_manager.get_certain_mesh('misc', ['seaweed', 'rocks'], 'misc_type', true);

        nature.config.levels.empty.spawn = load_manager.get_certain_mesh('misc', ['desert_skull', 'flowers', 'rocks', 'tumbleweed'], 'misc_type', true);



        nature.initWater();
        // nature.initRocks();
        // nature.initFlowers(load_manager.get_vox('flowers'));
        await nature.initMisc();

        player.init();
        enemy.init();

        audio.play('bg');

        // cancel starters
        this.cancelStarter();

        // run the loop
        clock.getDelta(); // drop delta
        this.render(); // render first frame, then loop
        this.loop();

        // check if tab is hidden
        if(visibly.hidden()) {
            this.pause();
        }
	}

    stop() {
        if(!this.isPlaying) {return false;}

        // stop the loop
    	this.isPlaying = false;

		// remove dust particles
		dynoDustEmitter.removeAllParticles();
		dynoDustEmitter.stopEmit();
		dynoDustEmitter.dead = true;

        // stop stuff
        audio.stop('bg');

        // show restart button
        this.interface.buttons.restart.classList.remove('hidden');

        // play kill sound & frame
        player.deathFrame();
        audio.play('killed');

        // set starters
        this.setStarter(0);
    }

    pause() {
        if(!this.isPlaying) {return false;}

        this.isPaused = true;
        this.isPlaying = false;
        audio.pause('bg');
    }

    resume() {
        if(!this.isPaused) {return false;}

        this.isPlaying = true;
        this.isPaused = false;
        audio.resume('bg');

        clock.getDelta(); // drop delta
        this.render();
        this.loop();
    }

    reset() {
        // reset running speed (def 13)
        enemy.increase_velocity(13, true);

        // reset stuff
        enemy.reset();
        nature.reset();
        score.reset();
        player.reset();
        effects.reset();

        // redraw to remove objects from scene
        this.render();
    }

    restart() {
        if(this.isPlaying) {
            this.stop();
        }

        this.reset();
        this.start();
    }

    render() {
        let timeDelta = clock.getDelta();

        if(timeDelta > 0.15) {
            timeDelta = 0.15;
        }

        if(config.camera.controls) {
            controls.update();}

        player.update(timeDelta);
        enemy.update(timeDelta);
        nature.update(timeDelta);
        input.update();
        effects.update(timeDelta);
        nebulaSystem.update();

        if(config.renderer.postprocessing.enable) {
            // postprocessing
            composer.render(timeDelta);
        } else {
            // standart
            renderer.render( scene, camera );
        }

        score.update(timeDelta);
    }

    tabVisibilityChanged(state) {
        if(state == 'visible') {
            // resume
            logs.log('GAME RESUME');
            if(game.isPaused) {
                game.resume();
                effects.resume();
            }
        } else {
            // pause
            logs.log('GAME PAUSE');
            if(game.isPlaying) {
                game.pause();
                effects.pause();
            }
        }
    }

    loop() {
        if(!this.isPlaying) {
            // stop the loop if necessary
            return false;
        }

        requestAnimationFrame(function() {
            game.loop();
        });

        this.render();
    }
}