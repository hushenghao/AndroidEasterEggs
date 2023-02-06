/**
 * Audio class.
 * @type {AudioManager}
 */

class AudioManager {
    constructor() {
      this.base_path = config.base_path + 'sound/';
      this.sounds = {
        "score": new Howl({
          src: [this.base_path + 'Pickup_Coin103.wav'],
          preload: true,
          autoplay: false,
          loop: false,
          volume: .3
        }),
        "highest_score": new Howl({
          src: [this.base_path + 'Powerup33.wav'],
          preload: true,
          autoplay: false,
          loop: false,
          volume: .4
        }),
        "jump": new Howl({
          src: [this.base_path + 'Jump24.wav'],
          preload: true,
          autoplay: false,
          loop: false,
          volume: .15
        }),
        "killed": new Howl({
          src: [this.base_path + 'Randomize62.wav'],
          preload: true,
          autoplay: false,
          loop: false,
          volume: .15
        }),
        "bg": new Howl({
          src: [this.base_path + 'ingame/Reloaded Games - Music.ogg'],
          preload: true,
          autoplay: false,
          loop: true,
          volume: .75
        })
      }

      // detect any user interaction
      // window.addEventListener('mousemove', this.autoplay);
      // window.addEventListener('scroll', this.autoplay);
      // window.addEventListener('keydown', this.autoplay);
      // window.addEventListener('click', this.autoplay);
      // window.addEventListener('touchstart', this.autoplay);
    }

    autoplay() {
      if(!this.sounds['bg'].playing()) {
        this.play('bg');
      }
    }

    play(what) {
      this.sounds[what].stop();
      this.sounds[what].play();
    }

    stop(what) {
      this.sounds[what].stop();
    }

    pause(what) {
      this.sounds[what].pause();
    }

    resume(what) {
      this.sounds[what].play();
    }
  }