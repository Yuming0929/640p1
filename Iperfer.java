import java.io.*;
import java.net.*;
import java.util.*;


public class Iperfer{

    //client mode
    public static void handle_client(String args[]){
        if(args.length != 7){
            System.out.println("Error: missing or additional arguments");
            System.exit(1);
        }

        String hostname = args[2];
        int server_port = Integer.parseInt(args[4]);
        if(server_port < 1024 || server_port > 65535){
            System.out.println("Error: port number must be in the range 1024 to 65535");
            System.exit(1);
        }
        double time = Integer.parseInt(args[6]) * 1e9;

        //dataSent in KB
        long dataSent = 0;

        try{
            Socket socket = new Socket(hostname, server_port);

            
            DataOutputStream d = new DataOutputStream( socket.getOutputStream());

            //use nanoTime to measure timeElapsed
            long start = System.nanoTime();
            double seconds = 0;
            long elapsed = 0;
            while(elapsed < time){
                byte[] data = new byte[1000];
                d.write(data);
                dataSent++;
                //this time conversion cited from https://stackoverflow.com/questions/924208/how-to-convert-nanoseconds-to-seconds-using-the-timeunit-enum
                elapsed = System.nanoTime() - start;
                
                
            }

            d.close();
            socket.close();

            seconds = (double)elapsed / 1e9;
            //we need Mbps
            double dataRate = ((dataSent * 8.0) / 1e3) / seconds;

            // //for debugging
            // System.out.println("counting second: " + seconds);

            System.out.println("sent=" + dataSent + " KB rate=" + dataRate + " Mbps");
        } catch(Exception e){
            e.printStackTrace(System.out);
        };
        

    }

    //server mode
    public static void handle_server(String args[]){
        if(args.length != 3){
            System.out.println("Error: missing or additional arguments");
            System.exit(1);
        }

        int port = Integer.parseInt(args[2]);
        if(port < 1024 || port > 65535){
            System.out.println("Error: port number must be in the range 1024 to 65535");
            System.exit(1);
        }


        try{
            ServerSocket serverSocket = new ServerSocket(port);
            Socket clientSocket = serverSocket.accept();
            DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());

            long start = System.nanoTime();
            long bytesReceived = 0;
            byte[] data = new byte[1000];

            long end = 0;
            while(true){

                //inputStream.read returns -1 if the input stream is ended
                int perSize = inputStream.read(data);
                if(perSize == -1){
                    end = System.nanoTime();
                    break;
                }
                bytesReceived += perSize;

            }
            double bytesKB = bytesReceived / 1e3;
            //for rate we use bits so mutiply bytesReceived by 8 and divided
            double dataRate = (bytesReceived * 8.0 / 1e6 )  / ( (end - start)/1e9  )  ;

            inputStream.close();
            clientSocket.close();
            serverSocket.close();

            // //for debugging
            // System.out.println("counting second: " + (end - start)/1e9 + ". bytestotal: "+ bytesReceived);

            System.out.println("received=" + (long)bytesKB + " KB rate=" + dataRate + " Mbps");
        }catch(Exception e){
            e.printStackTrace(System.out);
        }
        
    }

    public static void main(String args[]){
		//check input format

        if(args[0].equals("-c")){
            handle_client(args);
        }else if(args[0].equals("-s")){
            handle_server(args);
        }
        
		

    }
}
