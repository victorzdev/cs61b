package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0){
            System.out.printf("Please enter a command.");
            System.exit(0);
        }
        // TODO: what if args is empty?
        String firstArg = args[0];
        if (firstArg.isEmpty()){
            System.out.printf("Incorrect operands.");
        }
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                Repository.initCommand();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                if (args.length != 2){
                    System.out.println("Please enter one file per time");
                    System.exit(0);
                }
                Repository.add(args[1]);
                break;
            case "commit":
                Repository.commit(args[1], null);
                break;
            case "rm":
                Repository.rm(args[1]);
                break;
            case "log":
                Repository.log();
                break;
            default:
                System.out.println("No command with that name exists.");
                break;
        }
    }
}
