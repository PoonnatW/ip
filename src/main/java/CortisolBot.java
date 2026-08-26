import java.util.Scanner;

public class CortisolBot {
    public static void main(String[] args) {
        String banner = "  ____           _   _           _ ____        _   \n"
                + " / ___|___  _ __| |_(_)___  ___ | | __ )  ___ | |_ \n"
                + "| |   / _ \\| '__| __| / __|/ _ \\| |  _ \\ / _ \\| __|\n"
                + "| |__| (_) | |  | |_| \\__ \\ (_) | | |_) | (_) | |_ \n"
                + " \\____\\___/|_|   \\__|_|___/\\___/|_|____/ \\___/ \\__|";
        String name = "CortisolBot";
        System.out.println(banner);
        System.out.println("-".repeat(55));
        System.out.printf("Greetings sir/madam, %s humbly at your service.\n",name);
        System.out.println("How may I serve you at this evening?");
        System.out.println("-".repeat(55));
        Scanner scanner = new Scanner(System.in);
        while(true) {
            String userInput = scanner.nextLine();
            System.out.println("-".repeat(55));
            if (userInput.equals("bye")) {
                break;
            }
            else {
                System.out.println(userInput);
                System.out.println("-".repeat(55));
            }
        }
        System.out.println("Tonight has been an honour. I shall bid thee farewell!");
    }
}