//> using lib "com.amazonaws:aws-java-sdk-s3:1.12.492"
//> using jvm "8"

package coursier.s3support.s3;

import java.net.URL;

public final class Setup {

  private Setup() {}

  // Inspired from https://github.com/coursier/coursier/blob/f272c87bcc26657f7d55533e2adae8d1d8b2a932/modules/bootstrap-launcher/src/main/java/coursier/bootstrap/launcher/jar/JarFile.java#L379-L402,
  // itself based on https://github.com/spring-projects/spring-boot/blob/32b14f30987c4fb40d1ac69ecba47c7b876b60ce/spring-boot-project/spring-boot-tools/spring-boot-loader/src/main/java/org/springframework/boot/loader/jar/JarFile.java#L383-L406

  private static void registerPackage() {
    String currentOpt = System.getProperty("java.protocol.handler.pkgs");
    String updatedValue = "";
    if (currentOpt != null)
      updatedValue = currentOpt + "|";
    updatedValue = updatedValue + "coursier.s3support";
    System.setProperty("java.protocol.handler.pkgs", updatedValue);
  }

  private static void resetHandlers() {
    try {
      URL.setURLStreamHandlerFactory(null);
    }
    catch (Error e) {
      // Ignore
    }
  }

  /**
   * Registers the "coursier.s3support" package for URL handlers,
   * so that java.net.URL loads coursier.s3support.s3.Handler when
   * it tries to open s3://... URLs.
   */
  public static void setup() {
    registerPackage();
    resetHandlers();
  }
}
