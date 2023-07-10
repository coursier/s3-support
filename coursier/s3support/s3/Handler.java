package coursier.s3support.s3;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

// Once Setup updated the java.protocol.handler.pkgs Java property,
// java.net.URL is going to load this class by reflection in order to open URLs like s3://...
// When updating to Java 9, we might need to setup "services" via some files under META-INF/services
// in order for this to work.
public class Handler extends URLStreamHandler {

  private static Method getObject;
  private static Method getObjectContent;
  private static Method build;
  private static Method builder;
  private static Constructor getObjectRequestConstructor;

  static {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    try {
      getObject = cl
              .loadClass("com.amazonaws.services.s3.AmazonS3")
              .getMethod("getObject", cl.loadClass("com.amazonaws.services.s3.model.GetObjectRequest"));
      getObjectContent = cl
              .loadClass("com.amazonaws.services.s3.model.S3Object")
              .getMethod("getObjectContent");
      build = cl
              .loadClass("com.amazonaws.services.s3.AmazonS3ClientBuilder")
              .getMethod("build");
      builder = cl
              .loadClass("com.amazonaws.services.s3.AmazonS3Client")
              .getMethod("builder");
      getObjectRequestConstructor = cl.loadClass("com.amazonaws.services.s3.model.GetObjectRequest")
              .getConstructor(String.class, String.class);
    }
    catch (ClassNotFoundException ex) {
      throw new RuntimeException(ex);
    }
    catch (NoSuchMethodException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override protected URLConnection openConnection(URL url) {
    return new URLConnection(url) {
      @Override public void connect() {}
      @Override public InputStream getInputStream() {
        try {
          Object s3Client = build.invoke(builder.invoke(null));
          String bucket = null;
          String key = null;
          if (url.getHost() == null || url.getHost().isEmpty()) {
            StringBuilder keyBuilder = new StringBuilder();
            String[] elems = url.getPath().split("/");
            for (int i = 0; i < elems.length; i++) {
              if (elems[i].isEmpty())
                continue;
              if (bucket == null)
                bucket = elems[i];
              else if (keyBuilder.length() == 0)
                keyBuilder.append(elems[i]);
              else {
                keyBuilder.append("/");
                keyBuilder.append(elems[i]);
              }
            }
            key = keyBuilder.toString();
          }
          else {
            bucket = url.getHost();
            key = url.getPath();
            while (key.length() > 0 && key.charAt(0) == '/')
              key = key.substring(1);
          }
          return (InputStream) getObjectContent.invoke(getObject.invoke(s3Client, getObjectRequestConstructor.newInstance(bucket, key)));
        }
        catch (IllegalAccessException ex) {
          throw new RuntimeException(ex);
        }
        catch (InvocationTargetException ex) {
          throw new RuntimeException(ex);
        }
        catch (InstantiationException ex) {
          throw new RuntimeException(ex);
        }
      }
    };
  }
}
