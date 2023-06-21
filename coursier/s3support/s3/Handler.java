package coursier.s3support.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

// Once Setup updated the java.protocol.handler.pkgs Java property,
// java.net.URL is going to load this class by reflection in order to open URLs like s3://...
// When updating to Java 9, we might need to setup "services" via some files under META-INF/services
// in order for this to work.
public class Handler extends URLStreamHandler {
  @Override protected URLConnection openConnection(URL url) {
    return new URLConnection(url) {
      @Override public void connect() {}
      @Override public InputStream getInputStream() {
        AmazonS3 s3Client = AmazonS3Client.builder().build();
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
        return s3Client.getObject(new GetObjectRequest(bucket, key)).getObjectContent();
      }
    };
  }
}
