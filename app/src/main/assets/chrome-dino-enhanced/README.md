# Chrome Dino enhanced
Chrome Dino, but with enhancement that works for most browsers.

Also check out [Microsoft Edge's *Let's Surf*](https://github.com/yell0wsuit/ms-edge-letssurf), an offline game for Microsoft Edge.

<p align="center">
  <img src="https://i.imgur.com/kIV1YKm.png"/>
  Default night mode for dark-enabled OS
</p>

<p align="center">
  <img src="https://i.imgur.com/krI6KnS.png" />
  Normal day mode
</p>

## Play
Play online: https://yell0wsuit.page/html5-games/games/chrome-dino/

Play offline: download or clone this repo, then open ``index.html`` to play.

## Features / Changes
- Updated the code from the (kinda) latest Chrome version.
   - Restored 5 Olympic sport game modes.  
   (After the birthday event, the Chromium team has decided to [obfuscate new sprites before launch](https://github.com/chromium/chromium/tree/main/components/error_page/common/alt_game_images/README.md). This means you cannot datamine the image files, and if you miss out the event, you won't be able to play the new mode. You can check the [commit history](https://github.com/chromium/chromium/commits/main/components/neterror/resources) for the Dino game to see if they add anything new.)
- Disable "Start slower" toggle as it doesn't work (apparently).
- Preserve highscore. Use ``localStorage`` to save and retrieve high score.
   - The original code uses native code from Chrome to retrieve and store score.
- Reset highscore by clicking/tapping on the HI score twice.
- Arcade mode. The game adapts to the screen width like the Chrome version (chrome://dino). Beneficial to big screens.
- Day & night cycle. Game will start in night cycle if the users choose the dark theme in the OS/browser setting.
   - Dark theme / Night cycle mode uses color value defined in ``--google-grey-900``
   - Fixed the bugs related to invert filter.
   - Also fixed dark mode for Firefox.
- Use other pixelated filters for other browsers so the game looks consistent across browsers.
- Gamepad support.
- PWA support. You can install this game as an app to play offline.
   - This is useful on mobile platforms since you cannot open ``index.html`` directly like PC.  
   On iOS, you need to use Safari and add the page to the homescreen.
- Fix the game not working in iOS/iPadOS Safari browser.
