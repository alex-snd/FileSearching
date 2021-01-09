import java.util.concurrent.TimeUnit;

public class Main {
    public static Boolean startedThread = false;

    public static void main(String... args) {
        CreateThread t = new CreateThread("Поиск");

        try {
            while (true) {
                CreateThread.updateThreaActivCount();
            }
        } catch (Exception e) {

        }
    }
}






