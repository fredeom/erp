import java.net.http.*;
import java.net.http.HttpResponse.*;
import java.net.http.HttpClient.*;
import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class Test
{
    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newBuilder()
        .version(Version.HTTP_1_1)
        .followRedirects(Redirect.NORMAL)
        .connectTimeout(Duration.ofSeconds(20))
        .build();
       HttpRequest request = HttpRequest.newBuilder()
         .uri(URI.create("https://www.google.ru/"))
         .build();
       HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
       System.out.println(response.statusCode());
       System.out.println(response.body()); 
    }
}
