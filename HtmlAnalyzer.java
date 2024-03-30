import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HtmlAnalyzer {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java HtmlAnalyzer <URL>");
            return;
        }

        String urlStr = args[0];
        String htmlContent = fetchHtmlContent(urlStr);

        if (htmlContent == null) {
            System.out.println("URL connection error");
            return;
        }

        String deepestText = findDeepestText(htmlContent);
        System.out.println(deepestText);
    }

    private static String fetchHtmlContent(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line).append('\n');
                }
                reader.close();
                return response.toString();
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

    private static String findDeepestText(String htmlContent) {
        String[] lines = htmlContent.split("\n");
        String deepestText = null;
        int maxDepth = 0;

        int currentDepth = 0;
        for (String line : lines) {
            line = line.trim(); // Remove espaços em branco no início e no final
            if (line.isEmpty()) // Ignora linhas em branco
                continue;

            if (line.startsWith("</")) { // Tag de fechamento, decrementa a profundidade
                currentDepth--;
            } else if (line.startsWith("<")) { // Tag de abertura, incrementa a profundidade
                currentDepth++;
            } else { // Trecho de texto
                if (currentDepth > maxDepth) {
                    maxDepth = currentDepth;
                    deepestText = line;
                }
            }
        }

        if (deepestText == null) {
            return "malformed HTML";
        } else {
            return deepestText;
        }
    }
}
