# s3-support

*s3-support* provides simple support for `s3://` and `s3n://` URLs on the JVM. This support can be used from [coursier](https://github.com/coursier/coursier)
in particular.

## How to use

Ensure the `io.get-coursier:s3-support:0.1.0` dependency (that is, [its JAR](https://repo1.maven.org/maven2/io/get-coursier/s3-support/0.1.0/s3-support-0.1.0.jar), alongside
the one of `com.amazonaws:aws-java-sdk-s3` and those of all its dependencies) is available at runtime, and is passed to the `java` process
via the `-cp` option of `java`.

Call `coursier.s3support.Setup.setup()` early on in your application, before attempting to open `s3://` or `s3n://` URLs with `java.net.URL` in any case.
This allows `java.net.URL` to find the `s3://` and `s3n://` support that *s3-support* provides.

Only tested on Java 8 for now.
