"use strict";

//= input_manager.js
//= audio_manager.js
//= enemy_manager.js
//= score_manager.js
//= init.js
//= camera_controls.js
//= camera.js
//= light.js
//= particles.js

//= log_manager.js
let logs = new LogManager();
if(config.logs) {
	logs.enable();
}

//= player_manager.js
let player = new PlayerManager();

//= nature_manager.js
let nature = new NatureManager();

//= load_manager.js
let load_manager = new LoadManager(); // start loading assets ASAP
//= assets.js

//= effects_manager.js
let effects = new EffectsManager();

//= game_manager.js
//= interface_manager.js
let game = new GameManager(new InterfaceManager());
game.init(); // init game & interface ASAP