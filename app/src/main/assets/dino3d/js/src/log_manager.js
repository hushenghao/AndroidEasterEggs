/**
 * Log class.
 * @type {LogManager}
 */

class LogManager {
    constructor() {
      this.is_active = false;
    }

    enable() {
      this.is_active = true;
    }

    disable() {
      this.is_active = false;
    }

    // levels are
    // 0 - info
    // 1 - warning
    // 2 - fatal
    log(message, level = 0) {
      if(level == 0)
        console.log('[INFO] ' + message);

      else if(level == 1)
        console.log('[WARNING] ' + message)

      else if(level == 2)
        console.log(['[FATAL] ' + message])
    }
  }