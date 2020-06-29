import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.nio.*;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main implements Runnable {
    private static ExecutorService pool = Executors.newFixedThreadPool(20);
    private static ExecutorService cachedThread = Executors.newCachedThreadPool();
    static int port;
    final static int bufferSize = 32768;
    static byte[] c = new byte[bufferSize];

    public static void main(String[] args) {
        while(port<1025 || port > 65000){
            System.out.println("Введите номер порта(целое число, большее 1024, но меньшее 65000)");
            Scanner scanner = new Scanner(System.in);
            if(scanner.hasNextInt()){
                port=scanner.nextInt();
            }
        }
        Main.start();
    }
    public static void start(){
        Thread stopServer = new Thread(Main::stopServer);
        stopServer.setDaemon(false);
        Thread serverWork = new Thread(() -> {
            try {
                serverWork();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        serverWork.setDaemon(true);
        stopServer.start();
        serverWork.start();
    }

    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream b = new ByteArrayInputStream(bytes)) {
            try (ObjectInputStream o = new ObjectInputStream(b)) {
                return o.readObject();
            }
        }
    }


    public static void stopServer() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите help для получения списка команд на сервере");
        while (true) {
            switch (scanner.nextLine()) {
                case ("help"): {
                    System.out.println("exit: прекратить работу сервера");
                    break;
                }
                case ("exit"): {
                    System.exit(0);
                    break;
                }
                default:
                    System.out.println("Такой команды  у меня нет");
                    break;
            }
        }
    }

    public static void serverWork() throws Exception {
        pool.execute(() -> {
            new Thread(new Main()).start();
        });
        cachedThread.submit(() -> {
            CommandExecution commandExecution = new CommandExecution();
            SocketAddress socketAddress = new InetSocketAddress(port);//комбинация IP-адрес+порт
            DatagramChannel datagramChannel = DatagramChannel.open(); //Запускаем сервер
            try {
                datagramChannel.bind(socketAddress); //Задаем его адрес
            } catch (BindException e) {
                System.out.println("Ой, а порт занят, попробуйте заново");
            }
            while (true) {
                byte[] b = new byte[bufferSize];
                ByteBuffer byteBuffer = ByteBuffer.wrap(b);//Преварщаем массив в буфер
                byteBuffer.clear();
                socketAddress = datagramChannel.receive(byteBuffer);//получаем запрос от клиента
                Commands command = (Commands) deserialize(b);
                command = Identifier.Identify(command);

                send(command, datagramChannel, socketAddress);
            }
        });
    }

    public static void send(Commands command, DatagramChannel datagramChannel, SocketAddress socketAddress) throws Exception {
        byte[] b = serialize(command);
        Arrays.fill(c, (byte) 0);
        System.arraycopy(b, 0, c, 0, b.length);
        ByteBuffer byteBuffer = ByteBuffer.wrap(c);
        byteBuffer.flip();
        byteBuffer = ByteBuffer.wrap(c);
        int i = datagramChannel.send(byteBuffer, socketAddress);//отправляем обработанный резуьтат
        System.out.println(i + " байтов информации отправлено");
    }

    public static byte[] serialize(Commands command) throws IOException {
        try (ByteArrayOutputStream b = new ByteArrayOutputStream()) {
            try (ObjectOutputStream o = new ObjectOutputStream(b)) {
                o.writeObject(command);
            }
            return b.toByteArray();
        }
    }

    @Override
    public void run() {

    }
}

