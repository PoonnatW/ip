import java.util.Scanner;

public class CortisolBot {

    public static void printLine() {
        System.out.println("-".repeat(55));
    }

    public static void main(String[] args) {
        String banner = "  ____           _   _           _ ____        _   \n"
                + " / ___|___  _ __| |_(_)___  ___ | | __ )  ___ | |_ \n"
                + "| |   / _ \\| '__| __| / __|/ _ \\| |  _ \\ / _ \\| __|\n"
                + "| |__| (_) | |  | |_| \\__ \\ (_) | | |_) | (_) | |_ \n"
                + " \\____\\___/|_|   \\__|_|___/\\___/|_|____/ \\___/ \\__|";
        String name = "CortisolBot";
        System.out.println(banner);
        printLine();
        System.out.printf("Greetings sir/madam, %s humbly at your service.\n",name);
        System.out.println("How may I serve you at this evening?");
        printLine();

        Task[] tasks = new Task[100];
        int tasksAdded = 0;
        Scanner scanner = new Scanner(System.in);
        while(true) {
            String userInput = scanner.nextLine();
            printLine();
            if (userInput.equals("list")) {
                System.out.println("Here are the tasks in your list:");
                for (int i=0;i<tasksAdded;i++) {
                    System.out.printf("%d.%s\n",i+1,tasks[i]);
                }
                printLine();
            }
            else if (userInput.equals("bye")) {
                break;
            }
            else if (userInput.startsWith("mark ")) {
                String[] words = userInput.split("\\s+");
                int taskIndex = Integer.parseInt(words[1]) - 1;
                tasks[taskIndex].markAsDone();
                System.out.println("Nice! I've marked this task as done:");
                System.out.printf("\t%s\n",tasks[taskIndex]);
                printLine();
            }
            else if (userInput.startsWith("unmark ")) {
                String[] words = userInput.split("\\s+");
                int taskIndex = Integer.parseInt(words[1]) - 1;
                tasks[taskIndex].markAsNotDone();
                System.out.println("Ok, I've marked this task as not done yet: ");
                System.out.printf("\t%s\n",tasks[taskIndex]);
                printLine();
            }
            else {
                tasks[tasksAdded] = new Task(userInput);
                System.out.printf("added: %s\n", userInput);
                printLine();
                tasksAdded++;
            }
        }
        System.out.println("Tonight has been an honour. I shall bid thee farewell!");
        printLine();
    }
}