// package service2.DAL;
// import java.io.*;
// import java.net.*;

// public class MultiThreadedEchoServer {
//     private static final int PORT = 12345;

//     public static void main(String[] args) {
//         System.out.println("Echo server starting on port " + PORT);
//         try (ServerSocket serverSocket = new ServerSocket(PORT)) {
//             while (true) {
//                 // Wait for a client connection
//                 Socket clientSocket = serverSocket.accept();
//                 System.out.println("Client connected from " + 
//                                    clientSocket.getRemoteSocketAddress());
//                 // Handle the client in a new thread
//                 new Thread(new ClientHandler(clientSocket)).start();
//             }
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }

//     // Inner class to handle each client
//     private static class ClientHandler implements Runnable {
//         private final Socket socket;

//         ClientHandler(Socket socket) {
//             this.socket = socket;
//         }

//         public void run() {
//             try (
//                 BufferedReader in = new BufferedReader(
//                     new InputStreamReader(socket.getInputStream()));
//                 PrintWriter out = new PrintWriter(
//                     socket.getOutputStream(), true);
//             ) {
//                 String line;
//                 // Echo back each received line
//                 while ((line = in.readLine()) != null) {
//                     System.out.println("Received: " + line);
//                     out.println("Echo: " + line);
//                 }
//             } catch (IOException e) {
//                 System.err.println("Connection error: " + e.getMessage());
//             } finally {
//                 try {
//                     socket.close();
//                     System.out.println("Client disconnected.");
//                 } catch (IOException ignored) {}
//             }
//         }
//     }
// }
