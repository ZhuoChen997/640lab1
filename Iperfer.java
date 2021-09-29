import java.lang.Exception;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.io.*;

class Iperfer {
    static String USAGE_INFO = "Usage: client mode: java Iperfer -c -h <server hostname> -p <server port> -t <time>\n"
            + "       server mode: java Iperfer -s -p <listen port>\n";

    static byte[] bytes = new byte[1000];
    
    public static void main(String[] args) throws IOException {
        if (args.length == 7 && args[0].equals("-c") && args[1].equals("-h") && args[3].equals("-p")
                && args[5].equals("-t")) {
            // range check for port number
            int portNum = Integer.parseInt(args[4]);
            int time = Integer.parseInt(args[6]);
            if (portNum < 1024 || portNum > 65535) {
                System.out.println("Error: port number must be in the range 1024 to 65535");
                System.exit(1);
            }
            // Enter client mode
            launchClient(args[2], portNum, time);
        } else if (args.length == 3 && args[0].equals("-s") && args[1].equals("-p")) {
            // range check for port number
            int portNum = Integer.parseInt(args[2]);
            if (portNum < 1024 || portNum > 65535) {
                System.out.println("Error: port number must be in the range 1024 to 65535");
                return;
            }
            // Enter server mode
            launchServer(portNum);
        } else {
            System.out.print("Error: invalid arguments");
        }
    }

    public static void launchServer(int port) {
        System.out.println("port: " + port);
        long count = 0;
        long time = -1;
        try ( 
            ServerSocket serverSocket = new ServerSocket(port);
            Socket clientSocket = serverSocket.accept();
            OutputStream out = clientSocket.getOutputStream();
            InputStream in = clientSocket.getInputStream();
        ) {
            System.out.println("connected");
            long start_time = System.nanoTime();
            byte[] temp_buf = new byte[1000];
            while (in.read(temp_buf, 0, 1000) >= 0){
                count++;
            } 
            time = (System.nanoTime() - start_time) / 1000000000;
        }catch (IOException e) {
            System.out.println("exception");
        }
        System.out.println(time);
        double rate = (double)count*8/1000/time;
        System.out.println("received=" + count + "KB rate=" + String.format("%.3f", rate) + " Mbps");
        return;
    }

    public static void launchClient(String hostname, int port, int time) {
        System.out.println("hostname: " + hostname + "\n" + "port: " + port + "\n" + "time: " + time);
        long count = 0;
        try (
            Socket soc = new Socket(hostname, port);
            OutputStream out = soc.getOutputStream();
            InputStream in = soc.getInputStream();
        ){

            for (long stop=System.nanoTime()+TimeUnit.SECONDS.toNanos(time);stop>System.nanoTime();) {
                out.write(bytes);
                count++;
            }

        }catch (IOException e) {
            System.out.println("exception");
        }
        double rate = (double)count*8/1000/time;
        System.out.println("sent=" + count + "KB rate=" + String.format("%.3f", rate) + " Mbps");
        return;
    }
}
