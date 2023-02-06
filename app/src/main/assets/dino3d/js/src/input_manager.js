  // Keeps the state of keys/buttons
  //
  // You can check
  //
  //   inputManager.keys.left.down
  //
  // to see if the left key is currently held down
  // and you can check
  //
  //   inputManager.keys.left.justPressed
  //
  // To see if the left key was pressed this frame
  //

  class InputManager {
    constructor() {
      this.keys = {};
      this.callbacks = [];
      this.callbacks_i = 0;
      const keyMap = new Map();

      const setKey = (keyName, pressed) => {
        const keyState = this.keys[keyName];
        keyState.justPressed = pressed && !keyState.down;
        keyState.down = pressed;
        keyState.justReleased = !keyState.down && !pressed && !keyState.justReleased;

        // callbacks
        if(keyState.justPressed && this.callbacks[keyName].length) {
          for(let i in this.callbacks[keyName]) {
            if(this.callbacks[keyName][i].actionType == 'justPressed') {
              this.callbacks[keyName][i].callback();

              if(this.callbacks[keyName][i].maxCalls) {
                this.callbacks[keyName][i].totalCalls++;
                if(this.callbacks[keyName][i].totalCalls >= this.callbacks[keyName][i].maxCalls) {
                  this.callbacks[keyName].splice(i, 1);
                }
              }
            }
          }
        }
      };

      const addKey = (keyCode, name) => {
        this.keys[name] = { down: false, justPressed: false, justReleased: false, clock: new THREE.Clock() };
        this.callbacks[name] = [];
        keyMap.set(keyCode, name);
      };

      const setKeyFromKeyCode = (keyCode, pressed) => {
        const keyName = keyMap.get(keyCode);
        if (!keyName) {
          return;
        }
        setKey(keyName, pressed);
      };

      this.addKeyCallback = (keyName, actionType, callback, calls = false) => {
        this.callbacks_i++;
        this.callbacks[keyName][this.callbacks_i] = {
          "actionType": actionType,
          "callback": callback,
          "maxCalls": calls,
          "totalCalls": 0
        };

        return this.callbacks_i;
      }

      this.removeKeyCallback = (keyName, callback_i) => {
        if(this.callbacks[keyName][callback_i]) {
          this.callbacks[keyName].splice(callback_i, 1);
        }
      }

      // addKey(37, 'left');
      // addKey(39, 'right');
      // addKey(38, 'up');
      addKey(40, 'down'); // down
      // addKey(83, 'down'); // s
      addKey(17, 'down'); // Ctrl

      // addKey(87, 'space'); // w
      addKey(38, 'space'); // up
      addKey(32, 'space'); // space

      addKey(81, 'debug_speedup'); // q

      window.addEventListener('keydown', (e) => {
        // console.log(e.keyCode);
        setKeyFromKeyCode(e.keyCode, true);
      });
 
      window.addEventListener('keyup', (e) => {
        setKeyFromKeyCode(e.keyCode, false);
      });
    }

    update() {
      for (const keyState of Object.values(this.keys)) {
        if (keyState.justPressed) {
          keyState.clock.start();
          keyState.justPressed = false;
        }

        if (keyState.justReleased) {
          keyState.clock.stop();
          keyState.clock.elapsedTime = 0;
          keyState.justReleased = false;
        }
      }
    }
  }