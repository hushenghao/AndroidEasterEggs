/**
 * Effects class.
 * Rain, day/night, etc.
 * @type {EffectsManager}
 */

class EffectsManager {
    constructor() {

      // day/night circle
      this.daytime = {
        "is_day": true,
        "duration": {
          "day": 60, // sec
          "night": 20, // sec
        },
        "transition": {
          "active": false,
          "duration": 5, // sec
          "step": 1 / 30, // times/sec
          "clock": new THREE.Clock()
        },
        "intensity": {
          "day": {
            "ambient": ALight.intensity,
            "direct": DLight.intensity,
            "shadow_radius": 1
          },
          "night": {
            "ambient": 0,
            "direct": .1,
            "shadow_radius": 10
          }
        },
        "fog": {
          "day": {
            "color": [.91, .70, .32]
          },
          "night": {
            "color": [.24, .40, .55]
          },
          "diff_cache": null
        },
        "background": {
          "day": {
            "color": [.91, .70, .32]
          },
          "night": {
            "color": [.24, .40, .55]
          },
          "diff_cache": null
        },
        "clock": new THREE.Clock()
      }

      if(!config.renderer.effects) {
        this.update = function() {};
      }
    }

    changeDaytime(daytime = 'day') {
      this.daytime.is_day = daytime == 'day';

      // drop clock
      this.daytime.clock.stop();
      this.daytime.clock.elapsedTime = 0;
      this.daytime.clock.start();

      // reset values
      this.stepTransition(!this.daytime.is_day, 1, 1);
    }

    stepTransition(darken = true, step, total) {
      let inc = step / total;

      if(darken) {
        // to night
        if(inc === 1) {
          // set
          ALight.intensity = this.daytime.intensity.night.ambient;
          DLight.intensity = this.daytime.intensity.night.direct;

          scene.fog.color.setRGB(this.daytime.fog.night.color[0], this.daytime.fog.night.color[1], this.daytime.fog.night.color[2]);
          scene.background.setRGB(this.daytime.background.night.color[0], this.daytime.background.night.color[1], this.daytime.background.night.color[2]);
        
          DLight.shadow.radius = this.daytime.intensity.night.shadow_radius;
        } else {
          // step
          ALight.intensity = parseFloat((ALight.intensity - (this.daytime.intensity.day.ambient - this.daytime.intensity.night.ambient) * inc).toFixed(5));
          DLight.intensity = parseFloat((DLight.intensity - (this.daytime.intensity.day.direct - this.daytime.intensity.night.direct) * inc).toFixed(5));

          scene.fog.color.sub(this.daytime.fog.diff_cache);
          scene.background.sub(this.daytime.background.diff_cache);

          DLight.shadow.radius = parseFloat((DLight.shadow.radius - (this.daytime.intensity.night.shadow_radius - this.daytime.intensity.day.shadow_radius) * inc).toFixed(5));
        }
      } else {
        // to day
        if(inc === 1) {
          // set
          ALight.intensity = this.daytime.intensity.day.ambient;
          DLight.intensity = this.daytime.intensity.day.direct;

          scene.fog.color.setRGB(this.daytime.fog.day.color[0], this.daytime.fog.day.color[1], this.daytime.fog.day.color[2]);
          scene.background.setRGB(this.daytime.background.day.color[0], this.daytime.background.day.color[1], this.daytime.background.day.color[2]);
        
          DLight.shadow.radius = this.daytime.intensity.day.shadow_radius;
        } else {
          // inc
          ALight.intensity = parseFloat((ALight.intensity + (this.daytime.intensity.day.ambient - this.daytime.intensity.night.ambient) * inc).toFixed(5));
          DLight.intensity = parseFloat((DLight.intensity + (this.daytime.intensity.day.direct - this.daytime.intensity.night.direct) * inc).toFixed(5));

          scene.fog.color.add(this.daytime.fog.diff_cache);
          scene.background.add(this.daytime.background.diff_cache);
        
          DLight.shadow.radius = parseFloat((DLight.shadow.radius + (this.daytime.intensity.night.shadow_radius - this.daytime.intensity.day.shadow_radius) * inc).toFixed(5));
        }
      }

      this.daytime.transition.steps_done = parseFloat((this.daytime.transition.steps_done + step).toFixed(5));
    }

    startTransition(step, total) {
      let inc = step / total;

      this.daytime.transition.active = true; // begin transition
      this.daytime.transition.clock.elapsedTime = 0;
      this.daytime.transition.clock.start();
      this.daytime.transition.steps_done = 0;


      // cache sub & add colors
      this.daytime.fog.diff_cache = new THREE.Color();
      this.daytime.fog.diff_cache.setRGB(
        parseFloat((this.daytime.fog.day.color[0] - this.daytime.fog.night.color[0]) * inc),
        parseFloat((this.daytime.fog.day.color[1] - this.daytime.fog.night.color[1]) * inc),
        parseFloat((this.daytime.fog.day.color[2] - this.daytime.fog.night.color[2]) * inc)
      );

      this.daytime.background.diff_cache = new THREE.Color();
      this.daytime.background.diff_cache.setRGB(
        parseFloat((this.daytime.background.day.color[0] - this.daytime.background.night.color[0]) * inc),
        parseFloat((this.daytime.background.day.color[1] - this.daytime.background.night.color[1]) * inc),
        parseFloat((this.daytime.background.day.color[2] - this.daytime.background.night.color[2]) * inc)
      );
    }

    stopTransition() {
      this.daytime.transition.active = false; // end transition
      this.daytime.transition.clock.stop();
      this.daytime.transition.clock.elapsedTime = 0;
      this.daytime.transition.steps_done = 0;
    }

    reset() {
      this.stopTransition();
      this.changeDaytime('day');
    }

    pause() {
      this.pause_time = this.daytime.clock.getElapsedTime();
      this.daytime.clock.stop();

      if( this.daytime.transition.active ) {
        this.pause_transition_time = this.daytime.transition.clock.getElapsedTime();
        this.daytime.transition.clock.stop();
      }
    }

    resume() {
      this.daytime.clock.start();
      this.daytime.clock.elapsedTime = this.pause_time;

      if( this.daytime.transition.active ) {
        this.daytime.transition.clock.start();
        this.daytime.transition.clock.elapsedTime = this.pause_transition_time;
      }
    }

    update(timeDelta) {
      if(this.daytime.is_day) {
        // day
        if(!this.daytime.transition.active) {
          // wait until night
          if(this.daytime.clock.getElapsedTime() > this.daytime.duration.day) {
            this.startTransition(this.daytime.transition.step, this.daytime.transition.duration);
          }
        } else {
          // transition to night
          if(this.daytime.transition.steps_done < this.daytime.transition.duration) {
            // step
            if(this.daytime.transition.clock.getElapsedTime() > (this.daytime.transition.step + this.daytime.transition.steps_done)) {
              this.stepTransition(true, this.daytime.transition.step, this.daytime.transition.duration);
            }
          } else {
            // end
            this.stopTransition();
            this.changeDaytime('night');
          }
        }
      } else {
        // night
        if(!this.daytime.transition.active) {
          // wait until day
          if(this.daytime.clock.getElapsedTime() > this.daytime.duration.night) {
            this.startTransition(this.daytime.transition.step, this.daytime.transition.duration);
          }
        } else {
          // transition to day
          if(this.daytime.transition.steps_done < this.daytime.transition.duration) {
            // step
            if(this.daytime.transition.clock.getElapsedTime() > (this.daytime.transition.step + this.daytime.transition.steps_done)) {
              this.stepTransition(false, this.daytime.transition.step, this.daytime.transition.duration);
            }
          } else {
            // end
            this.stopTransition();
            this.changeDaytime('day');
          }
        }
      }

      
    }
  }