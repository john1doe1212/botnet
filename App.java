import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class App {
    private static final Map<String, String> users = new HashMap<>();
    private static final Map<String, Boolean> paidUsers = new HashMap<>();

    static class StaticFileHandler implements HttpHandler {
        private final String rootDir;

        public StaticFileHandler(String rootDir) {
            this.rootDir = rootDir;
            System.out.println("Serving files from: " + Paths.get(rootDir).toAbsolutePath());
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) path = "/index.html";

            Path file = Paths.get(rootDir + path).normalize();
            
            if (!file.startsWith(Paths.get(rootDir).normalize())) {
                sendResponse(exchange, "Forbidden", 403);
                return;
            }

            if (Files.exists(file) && !Files.isDirectory(file)) {
                exchange.sendResponseHeaders(200, Files.size(file));
                Files.copy(file, exchange.getResponseBody());
            } else {
                sendResponse(exchange, "File not found: " + path, 404);
            }
            exchange.getResponseBody().close();
        }
    }

    public static void main(String[] args) throws Exception {
        users.put("Admin", "Mk993300##");
        paidUsers.put("Admin", true);

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new StaticFileHandler("public"));
        
        server.createContext("/login", exchange -> {
            try {
                String body = new String(exchange.getRequestBody().readAllBytes());
                Map<String, String> params = parseFormData(body);
                String username = params.get("username");
                String password = params.get("password");

                if (users.containsKey(username) && users.get(username).equals(password)) {
                    exchange.getResponseHeaders().add("Set-Cookie", "username=" + username);
                    redirect(exchange, "/dashboard.html");
                } else {
                    sendResponse(exchange, "Invalid credentials", 401);
                }
            } catch (Exception e) {
                sendResponse(exchange, "Server error", 500);
            }
        });

        server.createContext("/signup", exchange -> {
            try {
                String body = new String(exchange.getRequestBody().readAllBytes());
                Map<String, String> params = parseFormData(body);
                
                String username = params.getOrDefault("newUsername", "").trim();
                String password = params.getOrDefault("newPassword", "").trim();

                if (username.isEmpty() || password.isEmpty()) {
                    sendResponse(exchange, "Username and password required", 400);
                    return;
                }

                if (users.containsKey(username)) {
                    sendResponse(exchange, "Username already taken", 400);
                } else {
                    users.put(username, password);
                    paidUsers.put(username, false);
                    sendResponse(exchange, "Account created!", 200);
                }
            } catch (Exception e) {
                sendResponse(exchange, "Server error", 500);
            }
        });

        server.createContext("/logout", exchange -> {
            exchange.getResponseHeaders().add("Set-Cookie", "username=; Max-Age=0");
            redirect(exchange, "/index.html");
        });

        server.start();
        System.out.println("Server running on port 8080");

        // Admin console
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("\n> ");
                String[] parts = scanner.nextLine().trim().split(" ", 2);
                String cmd = parts[0].toLowerCase();
                String arg = parts.length > 1 ? parts[1] : "";

                switch (cmd) {
                    case "scan":
                        System.out.println("\n=== Users ===");
                        users.forEach((u,p) -> 
                            System.out.println((paidUsers.get(u) ? "[PRO] " : "[BASIC] ") + u)
                        );
                        break;
                    case "promote":
                        if (users.containsKey(arg)) {
                            paidUsers.put(arg, true);
                            System.out.println("Promoted " + arg);
                        } else {
                            System.out.println("User not found");
                        }
                        break;
                    case "ban":
                        if (users.containsKey(arg)) {
                            users.remove(arg);
                            paidUsers.remove(arg);
                            System.out.println("Banned " + arg);
                        } else {
                            System.out.println("User not found");
                        }
                        break;
                    case "exit":
                        server.stop(0);
                        scanner.close();
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Unknown command");
                }
            }
        }).start();
    }

    // Helper methods
    private static void redirect(HttpExchange exchange, String location) throws IOException {
        exchange.getResponseHeaders().add("Location", location);
        exchange.sendResponseHeaders(302, -1);
    }

    private static void sendResponse(HttpExchange exchange, String response, int code) throws IOException {
        exchange.sendResponseHeaders(code, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private static Map<String, String> parseFormData(String formData) {
        Map<String, String> params = new HashMap<>();
        for (String pair : formData.split("&")) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                try {
                    params.put(
                        URLDecoder.decode(keyValue[0], "UTF-8"),
                        URLDecoder.decode(keyValue[1], "UTF-8")
                    );
                } catch (UnsupportedEncodingException ignored) {}
            }
        }
        return params;
    }
}