package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Victor Zhang
 */
public class Repository implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    String head;

    Map<String, String> branches;

    /** Staging Area. */
    Map<String, String> staged;
    Set<String> removed;

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    public static final File BlOBS_DIR = join(GITLET_DIR, "blobs");
    public static final File REPO = join(GITLET_DIR, "repo");

    public Repository(){
        head = "master";

        Date date = new Date(0);
        String msg = "initial commit";
        String commit = Commit.createCommit(msg, date, null, null);

        branches = new TreeMap<>();
        branches.put("master", commit);

        staged = new TreeMap<>();
        removed = new TreeSet<>();
    }

    /* TODO: fill in the rest of this class. */
    public static void initCommand(){
        if (GITLET_DIR.exists()){
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        GITLET_DIR.mkdir();
        COMMITS_DIR.mkdir();
        BlOBS_DIR.mkdir();
        writeContents(REPO);

        Repository repo = new Repository();
        saveRepo(repo);
    }

    public static void add(String fileName){
        checkInit();
        File addFile = join(CWD, fileName);
        if (!addFile.exists()){
            System.out.printf("the file does not exist");
            System.exit(0);
        }

        Repository repo = readRepo();
        String fileSha1Value = sha1File(addFile);

        File blobDir = join(BlOBS_DIR, fileName);
        Commit headC = headPtr(repo);
        if (!blobDir.exists()) {
            blobDir.mkdir();
        }

        if (!headC.containsFile(fileName)){
            staging(repo, addFile);
        }else{
            if (!headC.isIdentical(fileName, fileSha1Value)){
                staging(repo, addFile);
            }else{
                if (isStaged(repo, fileName)){
                    offStage(repo, fileName);
                }
            }
        }

        saveRepo(repo);
    }

    public static void commit(String msg, String parent2){
        checkInit();
        Repository repo = readRepo();
        if (repo.staged.isEmpty() && repo.removed.isEmpty()){
            System.out.println("No changes to this commit");
            System.exit(0);
        }

        if (msg == ""){
            System.out.printf("Please enter commit msg");
            System.exit(0);
        }

        String current = repo.branches.get(repo.head);
        String commit = Commit.makeCommit(current, repo.staged, repo.removed, msg, parent2);
        repo.staged.clear();
        repo.removed.clear();

        repo.branches.put(repo.head, commit);

        saveRepo(repo);
    }

    public static void rm(String fileName){
        checkInit();
        Repository repo = readRepo();

        File filePath = join(CWD, fileName);
        Commit c = headPtr(repo);

        if (!isStaged(repo, fileName) && !headPtr(repo).containsFile(fileName)){
            System.out.println("No reason to remove the file");
            System.exit(0);
        }

        if (repo.staged.containsKey(fileName)){
            offStage(repo, fileName);
        }

        if (headPtr(repo).containsFile(fileName)){
            repo.removed.add(fileName);
            if (filePath.exists()){
                restrictedDelete(filePath);
            }
        }

//        saveRepo(repo);
    }

    public static void log(){
        checkInit();
        Repository repo = readRepo();
        Commit c = headPtr(repo);
        while (c != null){
            System.out.println("===");
            System.out.printf("commit ");
            System.out.println(c.name);
            if (c.parent2 != null) {
                System.out.printf("Merge: %a %b", Commit.readCommit(c.parent1).name, Commit.readCommit(c.parent2).name + "\n");

            }
            System.out.printf("Date: ");
            System.out.println(c.timestamp);
            System.out.println(c.message);
            System.out.println();
            if (c.parent1 == null){
                c = null;
            }else {
                c = Commit.readCommit(c.parent1);
            }
        }
    }

    public static boolean isStaged(Repository repo, String fileName){
        return repo.staged.containsKey(fileName);
    }

    public static void offStage(Repository repo, String fileName){
        repo.staged.remove(fileName);
    }

    public static void checkInit(){
        if (!GITLET_DIR.exists()){
            System.out.printf("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    private static void saveRepo(Repository repo){
        writeObject(REPO, repo);
    }

    public static Repository readRepo() {
        return readObject(REPO, Repository.class);
    }

    private static Commit headPtr(Repository repo) {
        String head = repo.branches.get(repo.head);
        return Commit.readCommit(head);
    }

    private static void staging(Repository repo, File infile){
        byte [] contents = readContents(infile);
        String sha1 = sha1(contents);
        String fileName = infile.getName();
        File outFile = join(BlOBS_DIR, fileName, sha1);
        writeContents(outFile, contents);
        repo.staged.put(fileName, sha1);
    }

    private static String sha1File(File file){
        byte [] contents = readContents(file);
        return sha1(contents);
    }

    public static void main(String[] args) {
        Repository repo = readRepo();
        Commit cmt = headPtr(repo);
        System.out.println("1");
    }


}
