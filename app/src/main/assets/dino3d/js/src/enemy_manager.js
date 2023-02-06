/**
 * Enemy class v4.
 * This enemy manager gererates N number of mesh groups(!) and puts them to pool.
 * And only N of them will be randomly rendered within the buffer.
 * Also, materials & geometry is cached.
 * @type {EnemyManager}
 */

class EnemyPool {

	constructor() {
		this.items = [];
		this.keys = [];
	}

	addItem(item) {
		this.items.push(item);
		this.keys.push(this.items.length-1);
	}

	getItem(k) {
		return this.items[k];
	}

	getRandomKey() {
		if(!this.keys.length) {
			return false;
		}

		let i = Math.floor(Math.random() * this.keys.length);
		let k = this.keys.splice(i, 1)[0];
		return k;
	}

	returnKey(k) {
		this.keys.push(k);
	}

	reset() {
		this.items = [];
		this.keys = [];
	}
}

class EnemyManager {
	constructor() {
		this.pool = new EnemyPool();
		this.buffer = [];
		this.clock = new THREE.Clock();
		this.config = {
			"enable_collisions": true,
			"max_amount": {
				"pool": {
					"cactus": 50,
					"ptero": 15
				},
				"buffer": 10
			}, // max ammount of enemy groups
			"vel": 0, // overall speed of all enemies and other moving elements in-game
			"initial_z": -50,
			"remove_z": 25, // z offset when enemy will be removed
			"z_distance": {
				"cactus": 20,
				"ptero": 40
			}, // z distance between enemies
			"z_distance_rand": {
				"cactus": [.9, 2.5],
				"ptero": [.7, 1.5]
			}, // z distance random rescale range
			"rescale_rand": {
				"cactus": [.6, 1.2]
			}, // random rescale range
			"y_random_rotate": {
				"cactus": [-60, 60]
			},
			"x_random_range": {
				"cactus": [-.5, .5]
			},
			"chance_to_spawn_tail": [100, 25], // tails spawn chances
			"tail_rescale_rand": [[.6, .9], [.4, .7]], // tails rescale rand

			"ptero_anim_speed": 0.10, // lower is faster
			"ptero_y_rand": [0, 1.3, 3.5], // random ptero y positions

			"ptero_z_speedup": -35
		}

		this.cache = {
			"cactus": {
				"material": [],
				"geometry": []
			},
			"ptero": {
				"material": [],
				"geometry": []
			},
		}
	}

	hasDuplicates(array) {
	    return (new Set(array)).size !== array.length;
	}

	async init() {
		// set cache
		this.cache.cactus = {
			"geometry": await load_manager.get_mesh_geometry('cactus'),
			"material": await load_manager.get_mesh_material('cactus')
		};

		this.cache.ptero = {
			"geometry": await load_manager.get_mesh_geometry('ptero'),
			"material": await load_manager.get_mesh_material('ptero')
		};

		// fill the pool
		if(!this.pool.items.length) {
			for(let i = 0; i < this.config.max_amount.pool.cactus; i++) {
				this.pool.addItem(this.createEnemy('cactus'));
			}
		}

		// initial buffer fill
		for(let i = 0; i < this.config.max_amount.buffer; i++) {
			this.buffer.push(this.spawn());
		}
	}

	createEnemy(type = 'cactus', tail = false, tail_number = 0) {
		// get random mesh (within given type)
		let rand = Math.floor(Math.random() * load_manager.assets[type].mesh.length);
		let mesh = new THREE.Mesh(
			this.cache[type].geometry[rand],
			this.cache[type].material[rand]
		);

		// xbox
		// mesh.xbox = new THREE.BoxHelper( mesh, 0xffff00 );
		// scene.add(mesh.xbox);

		// basic mesh setup
		mesh.enemy_type = type;
		mesh.castShadow = true;
		if(type == 'cactus') {
			mesh.rotation.y = -(Math.PI / 2);
		} else {
			// ptero
			mesh.current_frame = rand;
		}
		let enemiesGroup = [mesh];

		if(type == 'cactus') {
			// randomly generate cactus tails
			if(tail) {
				// return tail
				return enemiesGroup[0];
			} else {
				if(Math.floor(Math.random() * 100) < this.config.chance_to_spawn_tail[0])
				{
					// first tail
					enemiesGroup.push(this.createEnemy('cactus', true, 0));

					if(Math.floor(Math.random() * 100) < this.config.chance_to_spawn_tail[1])
					{
						// second tail
						enemiesGroup.push(this.createEnemy('cactus', true, 1));
					}
				}
			}
		}

		// render to scene
		for(let e = 0; e < enemiesGroup.length; e++) {
			enemiesGroup[e].visible = false;
			scene.add(enemiesGroup[e]);
		}

		// return
		return enemiesGroup;
	}

	spawn() {
		let rand = this.pool.getRandomKey();

		if(rand !== false) {
			let enemiesGroup = this.pool.getItem(rand);

			for(let i = 0; i < enemiesGroup.length; i++) {
				
				// position Y
				enemiesGroup[i].position.y = nature.cache.ground.box.max.y + -nature.cache.ground.box.min.y - 2.5;

				if(enemiesGroup[i].enemy_type == 'cactus') {

					// random scale
					let rescaleRand = 1;
					if(i > 0) {
						// tail
						rescaleRand = this.random(this.config.tail_rescale_rand[i-1][0], this.config.tail_rescale_rand[i-1][1]);
					} else {
						rescaleRand = this.get_rr('cactus');
					}
					enemiesGroup[i].scale.set(rescaleRand, rescaleRand, rescaleRand);

					// random x position
		            enemiesGroup[i].position.x = this.random(
		              this.config.x_random_range.cactus[1],
		              this.config.x_random_range.cactus[0]
		            );

		            // random y rotation
					let yRandomRotate = this.random(this.config.y_random_rotate.cactus[0], this.config.y_random_rotate.cactus[1]);
					enemiesGroup[i].rotateY(THREE.Math.degToRad(yRandomRotate));

					// position Z
					let zRand = this.get_z('cactus');
					if(i > 0) {
						// tail
						if(i == 1) {
							enemiesGroup[i].position.z = -(-enemiesGroup[i-1].position.z + (rescaleRand * 1.7));
						} else {
							enemiesGroup[i].position.z = -(-enemiesGroup[i-1].position.z + (rescaleRand * 1.9));
						}
					} else {
						// head
						if(!this.buffer.length) {
							// first
							enemiesGroup[i].position.z = this.config.initial_z;
						} else {
							// chain
							if(this.pool.getItem( this.buffer[ this.buffer.indexOf(this.buffer.leader) ] )[0].enemy_type == 'ptero') {
								// after ptero
								let last = this.pool.getItem(this.buffer.leader);
								zRand = this.get_z('ptero');
								enemiesGroup[i].position.z = -(-last[last.length - 1].position.z + zRand);
							} else {
								// after cactus
								let last = this.pool.getItem(this.buffer.leader);
								enemiesGroup[i].position.z = -(-last[last.length - 1].position.z + zRand);
							}
						}
					}

				} else {
					
					// ptero
					enemiesGroup[0].position.x = 0;
					enemiesGroup[0].position.y = this.get_ptero_y();

					let zRand = this.get_z('ptero');
					if(this.buffer.length) {
						// chain
						let last = this.pool.getItem(this.buffer.leader);
						enemiesGroup[i].position.z = -(-last[last.length - 1].position.z + zRand);
					} else {
						// first
						enemiesGroup[0].position.z = -(zRand * 2);
					}

				}

				// make visible
				enemiesGroup[i].visible = true;
			}

			// push to buffer
			this.buffer.leader = rand;
			return rand;
		}
	}

	despawn(k = false) {

		// identify key
		let key = null;

		if(k !== false) {
			// key = this.buffer.splice(this.buffer.indexOf(k), 1)[0];
			key = this.buffer[this.buffer.indexOf(k)];
		} else {
			// key = this.buffer.splice(0, 1)[0];
			key = this.buffer[0];
		}

		// hide mesh
		let enemiesGroup = this.pool.getItem(key);

		for(let e = 0; e < enemiesGroup.length; e++ ) {
			enemiesGroup[e].position.z = this.config.remove_z * 2;
			enemiesGroup[e].visible = false;
		}

		// push key back
		this.pool.returnKey(key);
	}

	move(timeDelta) {
		// now do the check
		for(let i = 0; i < this.buffer.length; i++) {
			let e = this.pool.getItem(this.buffer[i]);

			// respawn, if required
			if(e[0].position.z > this.config.remove_z) {
				let newEnemy = this.spawn();
				this.despawn(this.buffer[i]);
				this.buffer[i] = newEnemy; // just replace the key

				continue;
			}

			// move by Z & detect collisions
			for(let j = 0; j < e.length; j++) {
				if(e[j].enemy_type == 'ptero') {
					// ptero
					if(e[j].position.z > this.config.ptero_z_speedup) {
						e[j].position.z += (this.config.vel * 1.7) * timeDelta;
					} else {
						e[j].position.z += this.config.vel * timeDelta;
					}
				} else {
					// cactus
					e[j].position.z += this.config.vel * timeDelta;
				}

				// xbox
				// e[j].xbox.update();

				/**
				 * @TODO
				 * Optimization can be done.
				 */
				if(this.config.enable_collisions) {
					// check collision with player
					let eBox = this.box3 = new THREE.Box3(new THREE.Vector3(), new THREE.Vector3());
					eBox.setFromObject(e[j]);

					let pBox = new THREE.Box3(new THREE.Vector3(), new THREE.Vector3());
					pBox.setFromObject(player.collisionBox);

					if(eBox.intersectsBox(pBox) && e[j].visible) {
						game.stop();
						return;
					}
				}
			}
		}
	}

	reset() {
		for(let i = 0; i < this.buffer.length; i++) {
			this.despawn();
		}

		for(let i = 0; i < this.pool.items.length; i++) {
			for(let j = 0; j < this.pool.items[i].length; j++) {
				scene.remove(this.pool.items[i][j]);
			}
		}

		this.pool.reset();
		this.buffer = [];
		delete this.buffer.leader;
	}

	spawnPteros() {
		for(let i = 0; i < this.config.max_amount.pool.ptero; i++) {
			this.pool.addItem(this.createEnemy('ptero'));
		}
	}

	random(from, to, float = true) {
		if(float) {
			return (Math.random() * (to - from) + from).toFixed(4)
		} else {
			return Math.floor(Math.random() * to) + from;
		}
	}

	get_rr(type) {
		return this.random(this.config.rescale_rand[type][0], this.config.rescale_rand[type][1]);
	}

	get_z(type) {
		let zrr = this.random(this.config.z_distance_rand[type][0], this.config.z_distance_rand[type][1]);
		return this.config.z_distance[type] * zrr;
	}

	get_ptero_y() {
		return (nature.cache.ground.box.max.y + -nature.cache.ground.box.min.y - 2.5) + this.config.ptero_y_rand[this.random(0, this.config.ptero_y_rand.length, false)];
	}

    increase_velocity(add = 1, init = false) {
        if(this.config.vel >= 35 && !init)
            {return;}

        if(init) {
        	// set
        	this.config.vel = add;
        } else {
        	// add
        	this.config.vel += add;
        }

        if(this.config.vel < 10) {
            player.setVelocity(15);
                player.setVelocity(1.1, true);

            player.setGravity(37);
                player.setGravity(30, true);

            logs.log('Speed level 1');
        }
        else if(this.config.vel >= 10 && this.config.vel < 20 && (player.jump.vel == 15 || init)) {
            player.setVelocity(19);
                player.setVelocity(1.1, true);

            player.setGravity(60);
                player.setGravity(40, true);

            // speedup dust particles
            dynoDustEmitter.removeAllParticles();
            dynoDustEmitter.stopEmit();
            // nebulaSystem.removeEmitter(dynoDustEmitter);
            dynoDustEmitter = nebulaCreateDynoDustEmitter(7);
            nebulaSystem.addEmitter(dynoDustEmitter);

            logs.log('Speed level 2');
        }
        else if(this.config.vel >= 20 && this.config.vel < 30 && (player.jump.vel == 19 || init)) {
            player.setVelocity(25);
                player.setVelocity(1.3, true);

            player.setGravity(100);
                player.setGravity(70, true);

            // speedup dust particles
            dynoDustEmitter.removeAllParticles();
            dynoDustEmitter.stopEmit();
            // nebulaSystem.removeEmitter(dynoDustEmitter);
            dynoDustEmitter = nebulaCreateDynoDustEmitter(10);
            nebulaSystem.addEmitter(dynoDustEmitter);

            logs.log('Speed level 3');
        }
        else if(this.config.vel >= 30 && (player.jump.vel == 25 || init)) {
            player.setVelocity(30);
                player.setVelocity(1.5, true);

            player.setGravity(150);
                player.setGravity(70, true);

            // remove dust particles
            dynoDustEmitter.removeAllParticles();
            dynoDustEmitter.stopEmit();
            dynoDustEmitter.dead = true;

            logs.log('Speed level 4');
        }
    }

    pteroNextFrame() {
        for(let i = 0; i < this.buffer.length; i++) {
        	let e = this.pool.getItem(this.buffer[i])[0];

        	if(e.enemy_type == 'ptero') {
        		// animate
        		e.current_frame++;

        		if(e.current_frame > this.cache.ptero.geometry.length - 1) {
        			e.current_frame = 0;
        		}

        		e.geometry = this.cache.ptero.geometry[e.current_frame];
        	}
        }
    }

    update(timeDelta) {
    	this.move(timeDelta);

	    // draw ptero frames
	    if( this.clock.getElapsedTime() > this.config.ptero_anim_speed ) {
	        this.clock.elapsedTime = 0;
	        this.pteroNextFrame();
	    }
    }
}