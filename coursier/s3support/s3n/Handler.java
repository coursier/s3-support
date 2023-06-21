package coursier.s3support.s3n;

// Once Setup updated the java.protocol.handler.pkgs Java property,
// java.net.URL is going to load this class by reflection in order to open URLs like s3n://...
// When updating to Java 9, we might need to setup "services" via some files under META-INF/services
// in order for this to work.
public class Handler extends coursier.s3support.s3.Handler {
}
