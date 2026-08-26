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

        String[] tasks = new String[100];
        int tasksAdded = 0;
        Scanner scanner = new Scanner(System.in);
        while(true) {
            String userInput = scanner.nextLine();
            System.out.println("-".repeat(55));
            if (userInput.equals("list")) {
                for (int i=0;i<tasksAdded;i++) {
                    System.out.printf("%d. %s\n",i+1,tasks[i]);
                }
                System.out.println("-".repeat(55));
            }
            else if (userInput.equals("bye")) {
                break;
            }
            else {
                tasks[tasksAdded] = userInput;
                System.out.printf("added: %s\n", userInput);
                System.out.println("-".repeat(55));
                tasksAdded++;
            }
        }
        System.out.println("Tonight has been an honour. I shall bid thee farewell!");
        System.out.println("-".repeat(55));
    }
}