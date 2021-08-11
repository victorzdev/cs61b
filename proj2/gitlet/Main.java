package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Victor Zhang
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

        String firstArg = args[0];
        if (firstArg.isEmpty()){
            System.out.printf("Incorrect operands.");
        }
        switch(firstArg) {
            case "init":
                Repository.initCommand();
                break;
            case "add":
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
            case "global-log":
                Repository.globalLog();
                break;
            case "find":
                Repository.find(args[1]);
                break;
            case "status":
                Repository.status();
                break;
            case "branch":
                Repository.branch(args[1]);
                break;
            case "checkout":
                if (args.length == 3){
                    Repository.checkOut(args[2], "head");
                }else if(args.length == 4){
                    Repository.checkOut(args[3], args[1]);
                }else if (args.length == 2){
                    Repository.checkOut(args[1]);
                }
                break;
            case "rm-branch":
                Repository.rmBranch(args[1]);
                break;
            case "reset":
                Repository.reset(args[1]);
                break;
            case "merge":
                Repository.merge(args[1]);
                break;
            default:
                System.out.println("No command with that name exists.");
                break;
        }
    }
}
