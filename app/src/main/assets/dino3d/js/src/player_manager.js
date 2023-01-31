/**
 * Player class.
 * @type {PlayerManager}
 */

  class PlayerManager {
    constructor() {
      this.frames = null;
      this.frames_band = null;
      this.frames_death = null;
      this.frame = null;
      this.collisionBox = null;
      this.currentFrame = 0;
      this.clock = new THREE.Clock();
      this.anim_speed = 0.10; // lower is faster
      this.block_fall_fast = false;
      this.jump = {
          "is_active": false,
          "vel": 15,
          "gravity": -37,
          "boost": {
            "vel": 1.1, // mult
            "gravity": -30 // new g
          }
      }
    }

    init() {
        // init position
        for(let i in this.frames) {
            this.frames[i].position.y = nature.cache.ground.box.max.y + 0.05;
            this.frames[i].position.z = 15;
            this.frames[i].rotation.y = Math.PI / 2;

            this.frames[i].init_y = this.frames[i].position.y;
        }

        for(let i in this.frames_band) {
            this.frames_band[i].position.y = nature.cache.ground.box.max.y + 0.05;
            this.frames_band[i].position.z = 15;
            this.frames_band[i].rotation.y = Math.PI / 2;

            this.frames_band[i].init_y = this.frames_band[i].position.y;
        }

        for(let i in this.frames_death) {
            this.frames_death[i].position.y = nature.cache.ground.box.max.y + 0.05;
            this.frames_death[i].position.z = 15;
            this.frames_death[i].rotation.y = Math.PI / 2;
        }
    }

    getVelocity(boost = false) {
        if(boost) {return this.jump.boost.vel;}
        else {return this.jump.vel;}
    }

    setVelocity(v = 15, boost = false) {
        if(boost) {this.jump.boost.vel = v;}
        else {this.jump.vel = v;}
    }

    getGravity(boost = false) {
        if(boost) {return -(this.jump.boost.gravity);}
        else {return -(this.jump.gravity);}
    }

    setGravity(g = 37, boost = false) {
        if(boost) {this.jump.boost.gravity = -g;}
        else {this.jump.gravity = -g;}
    }

    setPlayerDeathFrames(frames) {
        this.frames_death = frames;
    }

    setPlayerFrames(frames, band_down = false) {
        if(!band_down)
        {
            // stance
            this.frames = frames;
            this.frame = this.frames[this.currentFrame];
            this.frame.init_y = this.frame.position.y;

            scene.add(this.frame);

            // set collision box
            let geometry = new THREE.BoxGeometry( .5, 1.7, .7 );
            let material = new THREE.MeshBasicMaterial( {color: 0x00ff00} );
            this.collisionBox = new THREE.Mesh( geometry, material );
            this.collisionBox.position.x = this.frame.position.x;
            this.collisionBox.position.y = this.frame.position.y + 1.4;
            this.collisionBox.position.z = this.frame.position.z;
            scene.add( this.collisionBox );

            this.collisionBox.visible = false;
        } else
        {
            // band down
            this.frames_band = frames;
        }
    }

    nextFrame(ignore_jump = false) {
        if(!ignore_jump && this.jump.is_active)
            return;

        this.currentFrame++;

        if( this.currentFrame > this.frames.length - 1 )
            this.currentFrame = 0;
        
        // console.log("FRAME: " + this.currentFrame);

        if(!input.keys.down.down) {
            // stance
            this.frame.geometry = this.frames[this.currentFrame].geometry;
            this.collisionBox.scale.y = 1;
            this.collisionBox.scale.z = 1;
            this.collisionBox.position.z = this.frame.position.z;
            this.collisionBox.position.y = this.frame.position.y + 1.4;
        } else {
            // band down
            this.frame.geometry = this.frames_band[this.currentFrame].geometry;
            this.collisionBox.scale.y = 0.5;
            this.collisionBox.scale.z = 2.5;
            this.collisionBox.position.z = this.frame.position.z - .5;
            this.collisionBox.position.y = this.frame.position.y + 0.7;
        }
    }

    deathFrame() {
        if(!input.keys.down.down) {
            // stance
            this.frame.geometry = this.frames_death['wow'].geometry;
        } else {
            // band down
            this.frame.geometry = this.frames_death['wow-down'].geometry;
        }
    }

    getY() {
        return this.frame.position.y;
        // return this.frames[0].position.y;
    }

    setY(y) {
        this.frame.position.y = y;
        // this.frames.forEach(function(f) {
        //     f.position.y = y;
        // });
    }

    initJump(timeDelta) {
        this.jump.is_active = true;
        this.jump.falling = false;
        this.frame.vel = this.jump.vel;
        this.frame.gravity = this.jump.gravity;
        this.frame.boost = false;
        this.nextFrame(true);

        audio.play('jump');

        if( !dynoDustEmitter.dead ) {
            dynoDustEmitter.stopEmit();
        }

        if(input.keys.down.down) {
            this.block_fall_fast = true;
        }
    }

    doJump(timeDelta) {

        if((input.keys.space.justPressed) && !this.jump.is_active && !input.keys.down.down) {
            this.initJump(timeDelta);
        }

        if(this.jump.is_active) {

            input.keys.space.clock.getElapsedTime();
            if( !this.frame.boost && input.keys.space.down && input.keys.space.clock.getElapsedTime() > 0.20 ) {
                // this.frame.vel = this.frame.vel + this.jump.vel / 8;
                // this.frame.gravity = this.jump.gravity / 1.5;

                this.frame.vel = this.frame.vel * this.jump.boost.vel;
                this.frame.gravity = this.jump.boost.gravity;
                this.frame.boost = true;
            }

            if(input.keys.down.justReleased) {
                this.block_fall_fast = false;
            }

            if(input.keys.down.down && !this.block_fall_fast) {
                // fall fast
                this.frame.gravity = this.frame.gravity * 1.1;
                this.frame.geometry = this.frames_band[this.currentFrame].geometry;
                this.collisionBox.scale.y = 0.5;
                this.collisionBox.scale.z = 2.5;
                this.collisionBox.position.z = this.frame.position.z - .5;
                this.collisionBox.position.y = this.frame.position.y - 2;
            }

            this.frame.position.y = this.frame.position.y + this.frame.vel * timeDelta;
            if(input.keys.down.down && !this.block_fall_fast) {
                // fall fast
                this.collisionBox.position.y = this.frame.position.y + .8;
            } else {
                this.collisionBox.position.y = this.frame.position.y + 1.4;
            }
            this.frame.vel = this.frame.vel + this.frame.gravity * timeDelta;

            if(this.frame.position.y <= this.frame.init_y) {
                if(!input.keys.space.down) {
                    // simple fall
                    this.jump.is_active = false;
                    if( !dynoDustEmitter.dead ) {
                        dynoDustEmitter.emit();
                    }
                } else if(!input.keys.down.down) {
                    // space is down, continue to jump
                    this.initJump(timeDelta);
                } else {
                    // simple fall
                    this.jump.is_active = false;
                    if( !dynoDustEmitter.dead ) {
                        dynoDustEmitter.emit();
                    }
                }

                this.frame.position.y = this.frame.init_y;
                this.collisionBox.position.y = this.frame.position.y + 1.4;
                input.keys.space.clock.elapsedTime = 0;
            }
        }
    }

    reset() {
        this.currentFrame = 0;
        this.nextFrame();
    }

    update(timeDelta) {
        if( this.frames ) {
            this.anim_speed = 0.18 / (enemy.config.vel / 2);
            this.doJump(timeDelta);

            // draw frames
            if( this.clock.getElapsedTime() > this.anim_speed ) {
                this.clock.elapsedTime = 0;
                this.nextFrame();
            }
        }
    }
  }