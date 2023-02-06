/**
 * Nature class v2.
 * 
 * @type {NatureManager}
 */

class NatureManager {

  constructor() {
    this.config = {
      "remove_z": {
        "ground": 50,
        "earth": 250
      },
      "levels": {
        "playground": {
          "max_amount": 20,
          "z_distance": 5,
          "z_distance_rand": [1, 3],
          "x_random_range": [-2.5, 2.5],
          "remove_z": 20,
          "spawn": null
        },
        "first": {
          "max_amount": 20, // 25 for z_distance = 4 is optimal
          "z_distance": 5,
          "z_distance_rand": [1, 4],
          "remove_z": 20,
          "spawn": null
        },
        "second": {
          "max_amount": 20,
          "z_distance": 10,
          "z_distance_rand": [1, 4],
          "remove_z": 20,
          "spawn": null
        },
        "third": {
          "max_amount": 10,
          "z_distance": 30,
          "z_distance_rand": [1, 7],
          "remove_z": 20,
          "spawn": null
        },
        "water": {
          "max_amount": 10,
          "z_distance": 20,
          "z_distance_rand": [1, 4],
          "remove_z": 20,
          "spawn": null // will be set at game start
        },
        "water2": {
          "max_amount": 20,
          "z_distance": 10,
          "z_distance_rand": [1, 2],
          "remove_z": 20,
          "spawn": null // will be set at game start
        },
        "empty": {
          "max_amount": 20,
          "z_distance": 10,
          "z_distance_rand": [1, 4],
          "remove_z": 20,
          "spawn": null // will be set at game start
        },
      },
      "misc_items": {
        "PalmTree": {
          "rescale_rand": [2, 3],
          "x_random_range": [-3, 3]
        },
        "tumbleweed": {
          "rescale_rand": [.6, .8],
          "x_random_range": [-3, 3],
          "random_rotate_vel": [0.01, 0.1],
          "y_rotate": -(Math.PI / 2),
          "rotate_direction": 'z',
          "behavior": 'roll'
        },
        "cactus": {
          "rescale_rand": [.6, 1.2],
          "x_random_range": [-3, 3],
          "y_random_rotate": [-80, 80]
        },
        "desert_skull": {
          "rescale_rand": [.15, .3],
          "x_random_range": [-3, 3],
          "z_random_rotate": [-60, 60],
          "y_random_rotate": [-30, 30]
        },
        "scorpion": {
          "rescale_rand": [.3, .7], // [.3, .7]
          "x_random_range": [-3, 3],
          "y_random_rotate": [-40, 40]
        },
        "rocks": {
          "rescale_rand": [.5, 3],
          "x_random_range": [-3, 3],
        },
        "flowers": {
          "rescale_rand": [.7, 1.3],
          "x_random_range": [-3, 3],
        },
        "trees": {
          "rescale_rand": [.8, 3],
          "x_random_range": [-3, 3],
          "y_random_rotate": [-80, 80]
        },
        "fish": {
          "rescale_rand": [.1, .4],
          "x_random_range": [-2.5, 2.5],
          "y_random_rotate": [-60, 60]
        },
        "seaweed": {
          "rescale_rand": [.3, 1],
          "x_random_range": [-2.5, 2.5],
          "y_random_rotate": [-60, 60]
        }
      }
    }

    this.earth_chunks = [];
    this.ground_chunks = [];
    this.ground_chunks_decoration = [];
    this.ground_chunks_decoration_levels = [];
    this.water = null;
    this.rocks = [];
    this.flowers = [];
    this.misc = {};

    this.cache = {
      "earth": {
        "box": null,
        "geometry": null,
        "material": null,
        "texture": null
      },
      "ground": {
        "box": null,
        "geometry": null,
        "material": null
      },
      "ground_decoration": {
        "box": null,
        "geometry": null,
        "material": null
      },
      "water": {
        "geometry": null,
        "material": null
      },
      "rocks": {
        "geometry": null,
        "material": null
      },
      "flowers": {
        "geometry": null,
        "material": null
      },
      "misc": {
        "geometry": null,
        "material": null
      }
    };
  }

  initEarth(chunks = 3) {
    // earth
    if(!this.cache.earth.geometry) {

      this.cache.earth.texture = load_manager.get_texture('t_ground').top;
      this.cache.earth.texture.wrapS = this.cache.earth.texture.wrapT = THREE.RepeatWrapping;
      this.cache.earth.texture.offset.set( 0, 0 );
      this.cache.earth.texture.repeat.set( 100 / 8, 250 / 16 );

      this.cache.earth.geometry = new THREE.BoxGeometry( 100, 0, 250 );
      this.cache.earth.material = new THREE.MeshLambertMaterial( {map: this.cache.earth.texture} ); // 0xD6B161
    }

    for(let i = 0; i < chunks; i++) {
      let chunk = new THREE.Mesh(this.cache.earth.geometry, this.cache.earth.material);
      chunk.receiveShadow = true;

      chunk.position.x = 0;
      chunk.position.y = nature.cache.ground.box.min.y - .5;

      if(i > 0) {
        // reposition
        let lChunk = this.earth_chunks[this.earth_chunks.length-1];
        chunk.position.z = this.earth_chunks[this.earth_chunks.length-1].position.z - (250 * lChunk.scale.z);
      } else {
        // first
        chunk.position.z = -20;
      }

      if(!this.cache.earth.box) {
        this.cache.earth.box = new THREE.Box3().setFromObject(chunk);
      }

      this.earth_chunks.push(chunk)

      scene.add( chunk );
    }

    // set leader
    this.earth_chunks.leader = this.earth_chunks.length - 1;
  }

  moveEarth(timeDelta) {
    for(let i = 0; i < this.earth_chunks.length; i++) {
      if(this.earth_chunks[i].position.z > this.config.remove_z.earth) {
        // re move
        let lChunk = this.earth_chunks[this.earth_chunks.leader];
        this.earth_chunks[i].position.z = lChunk.position.z - (250 * lChunk.scale.z);
        this.earth_chunks.leader = i;
      }

      // move
      this.earth_chunks[i].position.z += enemy.config.vel * timeDelta;
    }
  }

  initWater() {
    if(this.cache.water.geometry === null) {
      // set cache
      this.cache.water.geometry = new THREE.BoxGeometry( 8, 1, 250 );
      this.cache.water.material = new THREE.MeshLambertMaterial( {color: 0x6EDFFF, transparent: true, opacity: .85} );
    }

    this.water = new THREE.Mesh( this.cache.water.geometry, this.cache.water.material );
    scene.add( this.water );

    this.water.position.z = -75;
    this.water.position.x = -7;
    this.water.position.y = nature.cache.earth.box.max.y + .5;
  }

  initGround(chunks = 15) {
    // get vox
    let vox = load_manager.get_vox('ground');

    // set cache
    this.cache.ground = {
      "geometry": vox.geometry,
      "material": vox.material
    };

    // spawn runner ground chunks
    for(let i = 0; i < chunks; i++) {
      let chunk = new THREE.Mesh( this.cache.ground.geometry, this.cache.ground.material );
      chunk.receiveShadow = true;
      // chunk.castShadow = true;

      chunk.position.y = -2.5;
      chunk.scale.set(1.5, 1.5, 1.5);

      if(i > 0) {
        // reposition
        let lChunk = this.ground_chunks[this.ground_chunks.length-1];
        chunk.position.z = this.ground_chunks[this.ground_chunks.length-1].position.z - (10 * lChunk.scale.z);
      } else {
        // first
        chunk.position.z = 15;

        if(!this.cache.ground.box) {
          this.cache.ground.box = new THREE.Box3().setFromObject(chunk);
        }
      }

      // push chunk to pool
      this.ground_chunks.push(chunk);

      // spawn chunk
      scene.add(chunk);
    }

    // set leader
    this.ground_chunks.leader = this.ground_chunks.length - 1;
  }

  moveGround(timeDelta) {
    for(let i = 0; i < this.ground_chunks.length; i++) {
      if(this.ground_chunks[i].position.z > this.config.remove_z.ground) {
        // re move
        let lChunk = this.ground_chunks[this.ground_chunks.leader];
        this.ground_chunks[i].position.z = lChunk.position.z - (10 * lChunk.scale.z);
        this.ground_chunks.leader = i;
      }

      // move
      this.ground_chunks[i].position.z += enemy.config.vel * timeDelta;
    }
  }

  initGroundDecoration(level_name, x, y, receiveShadow = true, spawn = 'all', chunks = 11) {
    // get vox
    let vox = load_manager.get_vox('ground_bg');

    // set cache
    this.cache.ground_decoration = {
      "geometry": vox.geometry,
      "material": vox.material
    };

    // create pool
    let pool = [];

    // spawn runner ground chunks
    for(let i = 0; i < chunks; i++) {
      let chunk = new THREE.Mesh( this.cache.ground_decoration.geometry, this.cache.ground_decoration.material );
      
      chunk.scale.set(3, 2, 3);
      chunk.position.x = x;
      chunk.position.y = y;
      chunk.receiveShadow = receiveShadow;
      // chunk.castShadow = true;

      if(i > 0) {
        // reposition
        let lChunk = pool[pool.length-1];
        chunk.position.z = lChunk.position.z - (10 * lChunk.scale.z);
      } else {
        // first
        chunk.position.z = 15;
        this.cache.ground_decoration.box = new THREE.Box3().setFromObject(chunk);
      }

      // save level position
      this.ground_chunks_decoration_levels[level_name] = {
        "x": x,
        "y": y,
        "spawn": spawn,
        "box": new THREE.Box3().setFromObject(chunk)
      };

      // push chunk to pool
      pool.push(chunk);

      // spawn chunk
      scene.add(chunk);
    }

    // set pool leader
    pool.leader = pool.length - 1;

    // pull pool to chunks pool
    this.ground_chunks_decoration.push(pool);

    // add custom locations
    // this.ground_chunks_decoration_levels.push({
    //   "x": -9,
    //   "y": nature.cache.earth.box.max.y,
    //   "box": new THREE.Box3().setFromObject(this.earth)
    // });

    // this.ground_chunks_decoration_levels.push({
    //   "x": 8,
    //   "y": nature.cache.earth.box.max.y,
    //   "box": new THREE.Box3().setFromObject(this.earth)
    // });
  }

  moveGroundDecoration(timeDelta) {
    for(let i = 0; i < this.ground_chunks_decoration.length; i++) {
      // pools
      for(let j = 0; j < this.ground_chunks_decoration[i].length; j++) {
        // chunks
        if(this.ground_chunks_decoration[i][j].position.z > this.config.remove_z.ground) {
          // re move
          let lChunk = this.ground_chunks_decoration[i][this.ground_chunks_decoration[i].leader];
          this.ground_chunks_decoration[i][j].position.z = lChunk.position.z - (10 * lChunk.scale.z);
          this.ground_chunks_decoration[i].leader = j;
        }

        // move
        this.ground_chunks_decoration[i][j].position.z += enemy.config.vel * timeDelta;
      }
    }
  }

  async initMisc() {
    // get vox
    let vox = load_manager.get_vox('misc');

    // set cache
    this.cache.misc = {
      "geometry": await load_manager.get_mesh_geometry('misc'),
      "material": await load_manager.get_mesh_material('misc')
    };

    for(let l in this.config.levels) {
      let level = this.config.levels[l];
      let randLevel = this.ground_chunks_decoration_levels[l];

      if(!level.spawn) {
        delete this.config.levels[l];
        continue;
      }

      // spawn misc according to level
      for(let i = 0; i < level.max_amount; i++) {

        // get misc
        let rand
        let misc = null;
        if(level.spawn == '*') {
          // any from all
          rand = Math.floor(Math.random() * load_manager.assets['misc'].mesh.length);
          misc = new THREE.Mesh(
            this.cache.misc.geometry[rand],
            this.cache.misc.material[rand]
          );
        } else {
          // any from given list
          rand = level.spawn[Math.floor(Math.random() * level.spawn.length)];
          misc = new THREE.Mesh(
            this.cache.misc.geometry[rand],
            this.cache.misc.material[rand]
          );
        }

        // basic misc setup
        misc.misc_type = vox[rand].misc_type;
        let misc_type = misc.misc_type.split('/')[0]; // local
        misc.castShadow = true;
        misc.receiveShadow = true;
        misc.randLevel = randLevel;

        // set X position according to level
        if( "x_random_range" in level ) {
          // level override
          if( Array.isArray(level.x_random_range) ) {
            // all
            misc.position.x = this.random(
              randLevel.x + level.x_random_range[1],
              randLevel.x + level.x_random_range[0]
            );
          } else {
            // declared
            misc.position.x = this.random(
              randLevel.x + level.x_random_range[misc_type][1],
              randLevel.x + level.x_random_range[misc_type][0]
            );
          }
        } else {
          // misc config
          misc.position.x = this.random(
            randLevel.x + this.config.misc_items[misc_type].x_random_range[1],
            randLevel.x + this.config.misc_items[misc_type].x_random_range[0]
          );
        }

        misc.init_x = misc.position.x;

        // Other positioning (init)
        if("behavior" in this.config.misc_items[misc_type]) {
          // Special behavior
          if(this.config.misc_items[misc_type].behavior == 'roll') {
            // roll behavior
            misc.geometry.center();
            misc.position.y = randLevel.box.max.y + 0.6;
            misc.position.z = randLevel.box.max.y;

            misc.rotation.y = this.config.misc_items[misc_type].y_rotate;
            misc.rotate_vel = this.random(this.config.misc_items[misc_type].random_rotate_vel[0], this.config.misc_items[misc_type].random_rotate_vel[1]);
          } else if(this.config.misc_items[misc_type].behavior == 'move') {
            // walk behavior
            misc.position.y = randLevel.box.max.y;

            // Z random rotate
            if(typeof(this.config.misc_items[misc_type].z_random_rotate) !== 'undefined') {
              let zRandomRotate = this.random(this.config.misc_items[misc_type].z_random_rotate[0], this.config.misc_items[misc_type].z_random_rotate[1]);
              misc.rotateZ(THREE.Math.degToRad(zRandomRotate));
            }

            // Y random rotate
            if(typeof(this.config.misc_items[misc_type].y_random_rotate) !== 'undefined') {
              let yRandomRotate = this.random(this.config.misc_items[misc_type].y_random_rotate[0], this.config.misc_items[misc_type].y_random_rotate[1]);
              misc.rotateY(THREE.Math.degToRad(yRandomRotate));
            }
          }
        } else {
          // all other mesh types
          misc.position.y = randLevel.box.max.y;

          // Z random rotate
          if(typeof(this.config.misc_items[misc_type].z_random_rotate) !== 'undefined') {
            let zRandomRotate = this.random(this.config.misc_items[misc_type].z_random_rotate[0], this.config.misc_items[misc_type].z_random_rotate[1]);
            misc.rotateZ(THREE.Math.degToRad(zRandomRotate));
          }

          // Y random rotate
          if(typeof(this.config.misc_items[misc_type].y_random_rotate) !== 'undefined') {
            let yRandomRotate = this.random(this.config.misc_items[misc_type].y_random_rotate[0], this.config.misc_items[misc_type].y_random_rotate[1]);
            misc.rotateY(THREE.Math.degToRad(yRandomRotate));
          }

          // Y add
          if(typeof(this.config.misc_items[misc_type].y_add) !== 'undefined') {
            misc.position.y += this.config.misc_items[misc_type].y_add;
          }
        }

        // Rescale mesh
        let rescaleRand = this.random(this.config.misc_items[misc_type].rescale_rand[0], this.config.misc_items[misc_type].rescale_rand[1]);
        misc.scale.set(rescaleRand, rescaleRand, rescaleRand);

        // Set Z initial position
        let zRand = this.get_z('misc', l);
        if((l in this.misc) && this.misc[l].length) {
          // tail z
          misc.position.z = -(-this.misc[l][this.misc[l].length-1].position.z + zRand);
        } else {
          // first z
          misc.position.z = zRand;
        }

        // add to level pool
        if(!(l in this.misc)) {
          this.misc[l] = [];
        }

        this.misc[l].push(misc);

        // add to scene
        scene.add(misc);
      }

      // set last mesh index
      this.misc[l].leader = level.max_amount - 1;
    }
  }

  moveMisc(timeDelta) {
    for(let l in this.config.levels) {
      let level = this.config.levels[l];
      let randLevel = this.ground_chunks_decoration_levels[l];

      if(!(l in this.misc)) {
        continue;
      }

      for(let i = 0; i < this.misc[l].length; i++) {
        let misc_type = this.misc[l][i].misc_type.split('/')[0];

        // reposition, if required
        if(this.misc[l][i].position.z > level.remove_z) {
          // random rescale
          let rescaleRand = this.random(
            this.config.misc_items[misc_type].rescale_rand[0],
            this.config.misc_items[misc_type].rescale_rand[1]
          );

          this.misc[l][i].scale.set(rescaleRand, rescaleRand, rescaleRand);

          // new Z position
          let zRand = this.get_z('misc', l);
          this.misc[l][i].position.z = -(-this.misc[l][ this.misc[l].leader ].position.z + zRand);
          this.misc[l].leader = i;

          // other stuff
          if("behavior" in this.config.misc_items[misc_type]) {
            if(this.config.misc_items[misc_type].behavior == "roll") {
              // roll behavior
              // misc.position.y = randLevel.box.max.y + 0.6;

              this.misc[l][i].rotation.y = this.config.misc_items[misc_type].y_rotate;
              // misc.rotate_vel = this.random(
              //   this.config.misc_items[misc_type].random_rotate_vel[0],
              //   this.config.misc_items[misc_type].random_rotate_vel[1]
              // );
            } else if(this.config.misc_items[misc_type].behavior == "move") {
              this.misc[l][i].position.x = misc.init_x;
            }
          } else {
            // any other mesh
            // misc.position.y = randLevel.box.max.y;
            // if(typeof(this.config.misc_items[misc_type].y_add) !== 'undefined') {
            //   // Y add
            //   misc.position.y += this.config.misc_items[misc_type].y_add;
            // }

            // Z random rotate
            if(typeof(this.config.misc_items[misc_type].z_random_rotate) !== 'undefined') {
              let zRandomRotate = this.random(this.config.misc_items[misc_type].z_random_rotate[0], this.config.misc_items[misc_type].z_random_rotate[1]);
              this.misc[l][i].rotateZ(THREE.Math.degToRad(zRandomRotate));
            }

            // Y random rotate
            if(typeof(this.config.misc_items[misc_type].y_random_rotate) !== 'undefined') {
              let yRandomRotate = this.random(this.config.misc_items[misc_type].y_random_rotate[0], this.config.misc_items[misc_type].y_random_rotate[1]);
              this.misc[l][i].rotateY(THREE.Math.degToRad(yRandomRotate));
            }
          }

          continue;
        }

        // move
        if("behavior" in this.config.misc_items[misc_type]) {
          if( this.config.misc_items[misc_type].behavior == 'roll' ) {
            // roll behavior
            this.misc[l][i].rotation[this.config.misc_items[misc_type].rotate_direction] -= this.misc[l][i].rotate_vel;
            this.misc[l][i].position.z += (enemy.config.vel * 1 + (this.misc[l][i].rotate_vel * 20)) * timeDelta;
          } else if(this.config.misc_items[misc_type].behavior == "move") {
            this.misc[l][i].position.x -= (this.config.misc_items[misc_type].move_speed / 2) * -this.misc[l][i].rotation.y;

            this.misc[l][i].position.z += enemy.config.vel * timeDelta;
          }
        } else {
          // any other mesh movement
          this.misc[l][i].position.z += enemy.config.vel * timeDelta;
        }
      }
    }
  }

  random(from, to, float = true) {
    if(float) {
      return (Math.random() * (to - from) + from).toFixed(4)
    } else {
      return Math.floor(Math.random() * to) + from;
    }
  }

  get_z(type, level) {
    // according to level
    let zrr = this.random(
      this.config.levels[level].z_distance_rand[0],
      this.config.levels[level].z_distance_rand[1],
    );

    return this.config.levels[level].z_distance * zrr;
  }

  reset() {
    // remove misc
    for(let l in this.config.levels) {
      for(let i = 0; i < this.misc[l].length; i++) {
        scene.remove(this.misc[l][i]);
      }
    }

    // remove earth chunks
    for(let i = 0; i < this.earth_chunks.length; i++) {
      scene.remove(this.earth_chunks[i]);
    }

    // remove ground chunks
    for(let i = 0; i < this.ground_chunks.length; i++) {
      scene.remove(this.ground_chunks[i]);
    }

    // remove all ground chunks decoration
    for(let i = 0; i < this.ground_chunks_decoration.length; i++) {
      for(let j = 0; j < this.ground_chunks_decoration[i].length; j++) {
        scene.remove(this.ground_chunks_decoration[i][j]);
      }
    }

    // remove water
    scene.remove(this.water);

    // clear arrays
    this.misc = [];
    this.earth_chunks = [];
    this.ground_chunks = [];
    this.ground_chunks_decoration = [];
    this.ground_chunks_decoration_levels = [];
    this.water = null;
  }

  update(timeDelta) {
    this.moveEarth(timeDelta);
    this.moveGround(timeDelta);
    this.moveGroundDecoration(timeDelta);

    this.moveMisc(timeDelta);
  }

}