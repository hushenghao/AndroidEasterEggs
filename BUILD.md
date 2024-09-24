# Building and Installing

## Modularization

[graph](./assets/image/modularization-graph.drawio.png)

## Requested

* Java 17
* Android SDK

Specify your Android SDK path either using the `ANDROID_HOME` environment variable, or by filling out the `sdk.dir` property in `local.properties`.

Signing can be done automatically using `key.properties` as follows:

```properties
storeFile=path/to/keystore.jks
storePassword=store-password
keyAlias=key-alias
keyPassword=key-password
```

## Build

Run `./gradlew assembleRelease` to build the package, which can be installed using the Android package manager.
