# Material Icons generator

The `generator` module, in `generator/` - this module processes and generates Kotlin source files as part of the build step of the other modules. This module is not shipped as an artifact, and caches its outputs based on the input icons (found in `generator/raw-icons`).