import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HtmlAnalyzer {
    public static void main(String[] args) {
        // Verifica se a quantidade de argumentos está correta
        if (args.length != 1) {
            System.out.println("Usage: java HtmlAnalyzer <URL>");
            return;
        }

        // Obtém a URL a ser analisada do primeiro argumento
        String urlStr = args[0];
        // Obtém o conteúdo HTML da URL
        String htmlContent = fetchHtmlContent(urlStr);

        // Verifica se o conteúdo HTML foi obtido com sucesso
        if (htmlContent == null) {
            System.out.println("URL connection error");
            return;
        }

        // Encontra o trecho mais profundo de texto no conteúdo HTML
        String deepestText = findDeepestText(htmlContent);
        // Imprime o trecho mais profundo encontrado
        System.out.println(deepestText);
    }

    // Método para obter o conteúdo HTML de uma URL
    private static String fetchHtmlContent(String urlString) {
        try {
            // Cria uma URL a partir da string fornecida
            URL url = new URL(urlString);
            // Abre uma conexão HTTP com a URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Define o método de requisição como GET
            connection.setRequestMethod("GET");

            // Obtém o código de resposta da conexão
            int responseCode = connection.getResponseCode();
            // Verifica se a conexão foi bem-sucedida
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Cria um leitor para ler o conteúdo da resposta
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                // StringBuilder para armazenar o conteúdo da resposta
                StringBuilder response = new StringBuilder();
                String line;
                // Lê cada linha da resposta e a adiciona ao StringBuilder
                while ((line = reader.readLine()) != null) {
                    response.append(line).append('\n');
                }
                // Fecha o leitor
                reader.close();
                // Retorna o conteúdo HTML como uma string
                return response.toString();
            } else {
                // Retorna null se a conexão não foi bem-sucedida
                return null;
            }
        } catch (IOException e) {
            // Retorna null em caso de exceção de E/S (por exemplo, falha de conexão)
            return null;
        }
    }

    // Método para encontrar o trecho mais profundo de texto no conteúdo HTML
    private static String findDeepestText(String htmlContent) {
        // Divide o conteúdo HTML em linhas
        String[] lines = htmlContent.split("\n");
        // Variáveis para armazenar o trecho mais profundo e sua profundidade
        String deepestText = null;
        int maxDepth = 0;

        // Variável para rastrear a profundidade atual
        int currentDepth = 0;
        // Itera sobre cada linha do conteúdo HTML
        for (String line : lines) {
            // Remove espaços em branco no início e no final da linha
            line = line.trim();
            // Ignora linhas em branco
            if (line.isEmpty())
                continue;

            // Verifica se a linha é uma tag de fechamento
            if (line.startsWith("</")) {
                // Decrementa a profundidade atual se for uma tag de fechamento
                currentDepth--;
            // Verifica se a linha é uma tag de abertura
            } else if (line.startsWith("<")) {
                // Incrementa a profundidade atual se for uma tag de abertura
                currentDepth++;
            // Se não for uma tag, é considerado um trecho de texto
            } else {
                // Verifica se a profundidade atual é maior do que a profundidade máxima encontrada até agora
                if (currentDepth > maxDepth) {
                    // Atualiza a profundidade máxima
                    maxDepth = currentDepth;
                    // Armazena o trecho de texto como o trecho mais profundo encontrado até agora
                    deepestText = line;
                }
            }
        }

        // Retorna o trecho mais profundo encontrado ou "malformed HTML" se nenhum trecho de texto for encontrado
        if (deepestText == null) {
            return "malformed HTML";
        } else {
            return deepestText;
        }
    }
}
