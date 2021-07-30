package s05.p12a104.mafia.common.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

public class UrlUtils {
  public static Optional<String> getUrlQueryParam(String url, String key) {
    try {
      String value = null;
      List<NameValuePair> params = URLEncodedUtils.parse(new URI(url), Charset.forName("UTF-8"));
      for (NameValuePair param : params) {
        if ("token".equals(param.getName())) {
          return Optional.of(param.getValue());
        }
      }

      return Optional.empty();
    } catch (URISyntaxException e) {
      return Optional.empty();
    }
  }
}
