# Contributing

## Reporting issues

If you find an issue in the client, you can use our [Issue
Tracker](https://github.com/hushenghao/AndroidEasterEggs/issues). Make sure that it hasn't yet been reported by searching first.

Remember to include the following information:

* Android version
* Device model
* Easter Eggs version
* Steps to reproduce the issue

Optional:

* Logcat - see [instructions](https://developer.android.com/tools/logcat)


## Translating

The strings are translated using [Crowdin](https://crowdin.com/). Follow [these instructions](https://crowdin.com/project/easter-eggs) if you would like to contribute.

Please *do not* send merge requests or patches modifying the translations. Use Crowdin instead - it applies a series of fixes and suggestions, plus it keeps track of modifications and fuzzy translations. Applying translations manually
skips all of the fixes and checks, and overrides the fuzzy state of strings.

Note that you cannot change the English strings on Crowdin. If you have any
suggestions on how to improve them, open an issue or merge request like you
would if you were making code changes. This way the changes can be reviewed
before the source strings on Crowdin are changed.

## Code Style

We follow the default Android Studio code formatter (e.g. `Ctrl-Alt-L`).  This
should be more or less the same as [Kotlin style guide](https://developer.android.com/kotlin/style-guide), [Java code style](https://source.android.com/source/code-style). Some key points:

* Four space indentation
* UTF-8 source files
* One statement per line
* K&R spacings with braces and parenthesis
* Commented fallthroughs
* Braces are always used after if, for and while

The current code base doesn't follow it entirely, but new code should follow it.

