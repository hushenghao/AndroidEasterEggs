/**
 * InterfaceManager class.
 * @type {InterfaceManager}
 */

class InterfaceManager {
    constructor() {
    	this.buttons = {
    		"start": document.getElementById('game-start'),
    		"restart": document.getElementById('game-restart')
    	};

    	this.indicators = {
    		"load": document.getElementById('game-load-progress')
    	};

        this.other = {
            "preloader": document.getElementById('preloader'),
            "overlay": document.getElementById('chrome-no-internet')
        }
    }

    init() {
    	// hook buttons
    	this.buttons.start.addEventListener('click', this.btnStartClick);
    	this.buttons.restart.addEventListener('click', this.btnRestartClick);
    }

    btnStartClick(e) {
    	game.interface.buttons.start.display = 'none'; //hide
   		document.body.classList.add('game-started');

   		game.start();
    }

    btnRestartClick(e) {
    	game.interface.buttons.restart.classList.add('hidden');

   		game.restart();
    }
}