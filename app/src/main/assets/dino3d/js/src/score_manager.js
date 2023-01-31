/**
 * Score class.
 * @type {ScoreManager}
 */

class ScoreManager {
    constructor() {
      this.score = 0;
      this.highest_score = 0;
      this.highest_alert = false;
      this.zero_padding = 5;
      this.config = {}
      this.timer = null;
      this.add_vel = 10; // scores to be added per second
      this.step = 100;
      this.is_flashing = false;
      this.lvl = 0;
      this.clock = new THREE.Clock();
      this.last_flash_score = 0;

      Number.prototype.pad = function(size) {
          var s = String(this);
          while (s.length < (size || 2)) {s = "0" + s;}
          return s;
      }

      {
        this.canvas = document.createElement('canvas');
        this.canvas.id = 'score-counter';
        this.canvas.width = 450;
        this.canvas.height = 60;
        document.body.appendChild(this.canvas);

        this.ctx = this.canvas.getContext('2d');
      }

      // TEMP GLITCHED SCORES FIX
      if(!localStorage.getItem('highest_score___GLITCH_FIX')) {
        localStorage.setItem('highest_score', 0);
        localStorage.setItem('highest_score___GLITCH_FIX', true);
      }
    }

    set(points) {
      this.score = points;

      // get last highest score
      this.highest_score = localStorage.getItem('highest_score');

      if(this.highest_score < 25) {
        this.highest_alert = true;
      } else {
        this.highest_alert = false;
      }
    }

    add(points) {
        this.score += points;

        if(this.score > this.highest_score) {
          localStorage.setItem('highest_score', this.score);
          this.highest_score = this.score;

          if(!this.highest_alert) {
            audio.play('highest_score');
            this.highest_alert = true;
          }
        }

        if(this.score != 0 && Math.trunc(this.score) % this.step == 0 && Math.trunc(this.score) != this.last_flash_score) {
          // flash
          this.last_flash_score = Math.trunc(this.score);
          this.flash();
        }
    }

    flash() {
      this.clock.stop();
      this.clock.elapsedTime = 0;
      this.clock.start();
      this.is_flashing = true;

      audio.play('score');
      enemy.increase_velocity();

      if(this.score >= 400 && this.lvl == 0) {
        // inc lvl
        this.lvl = 1;
        enemy.spawnPteros();
        logs.log('Pterodactyls started to spawn');
      } else if(this.score >= 1000 && this.lvl == 1) {
        // inc lvl
        this.lvl = 2;
        this.add_vel = 20; //twice the score gain speed
        logs.log('Score level 2');
      } else if(this.score >= 3000 && this.lvl == 2) {
        // inc lvl
        this.lvl = 3;
        this.add_vel = 40; //twice the score gain speed
        logs.log('Score level 3');
      }
    }

    reset() {
      this.clock = new THREE.Clock();
      this.lvl = 0;
      this.add_vel = 10;
    }

    update(timeDelta) {
      this.add(this.add_vel * timeDelta);

      let text = '';
      if(this.highest_score > 9999) {
        text = 'HI ' + (this.highest_score / 1000).toFixed(1) +'K';
      } else {
        text = 'HI ' + Math.trunc(this.highest_score).pad(this.zero_padding);
      }

      if(this.is_flashing) {
        if(Math.trunc(this.clock.getElapsedTime() * 4) % 2) {
          text = text + ' ' +  Math.trunc(this.score).pad(this.zero_padding);

          if(this.clock.getElapsedTime() > 1) {this.is_flashing = false;}
        }
      } else {
        text = text + ' ' +  Math.trunc(this.score).pad(this.zero_padding);
      }

      this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);

      this.ctx.font = '28px "Press Start 2P"';
      this.ctx.fillStyle = 'rgba(106,133,145,1)';
      this.ctx.fillText(text, 0, 60);
    }
  }