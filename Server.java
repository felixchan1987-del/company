import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;

public class Server {
    public static void main(String[] args) throws Exception {
        int port = 3456;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        File root = new File(System.getProperty("user.dir"));
        server.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) path = "/index.html";
            File file = new File(root, path.replace("/", File.separator));
            if (file.exists() && file.isFile()) {
                byte[] data = Files.readAllBytes(file.toPath());
                String ct = path.endsWith(".html") ? "text/html; charset=UTF-8" :
                            path.endsWith(".js")   ? "application/javascript" :
                            path.endsWith(".css")  ? "text/css" : "application/octet-stream";
                exchange.getResponseHeaders().set("Content-Type", ct);
                exchange.sendResponseHeaders(200, data.length);
                exchange.getResponseBody().write(data);
                exchange.getResponseBody().close();
            } else {
                exchange.sendResponseHeaders(404, 0);
                exchange.getResponseBody().close();
            }
        });
        server.start();
        System.out.println("Server started on port " + port);
    }
}
