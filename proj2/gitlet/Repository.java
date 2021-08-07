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

        saveRepo(repo);
    }

    public static void log(){
        checkInit();
        Repository repo = readRepo();
        Commit c = headPtr(repo);
        while (c != null){
            System.out.println(c);
            c = Commit.readCommit(c.parent1);
        }
    }

    public static void globalLog(){
        checkInit();
        List<String> commitsID = plainFilenamesIn(COMMITS_DIR);
        for (String id: commitsID){
            Commit c = Commit.readCommit(id);
            System.out.println(c);
        }
    }

    public static void find(String msg){
        checkInit();
        List<String> commitsID = plainFilenamesIn(COMMITS_DIR);
        boolean flag = false;
        for (String id: commitsID){
            Commit c = Commit.readCommit(id);
            if (c.message.equals(msg)){
                System.out.println(c.name);
                flag = true;
            }
        }
        if (!flag){
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
    }

    public static void status(){
        checkInit();
        Repository repo = readRepo();
        System.out.println(repo);
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

    public static void branch(String bName){
        checkInit();
        Repository repo = readRepo();
        if (repo.branches.containsKey(bName)){
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        String headBranch = repo.branches.get(repo.head);
        repo.branches.put(bName, headBranch);
        saveRepo(repo);
    }

    public static void checkOut(String fileName, String cID){
        checkInit();
        Repository repo = readRepo();
        if (cID.equals("head")){
            cID = repo.branches.get(repo.head);
        }
        Commit cmt = Commit.readCommit(cID);
        if (!cmt.blobMap.containsKey(fileName)){
            System.out.println("File does not exist in that commit");
            System.exit(0);
        }
        String fileID = cmt.blobMap.get(fileName);
        File filePath = join(BlOBS_DIR, fileName, fileID);
        byte [] contents = readContents(filePath);
        writeContents(join(CWD, fileName), contents);
        repo.staged.remove(fileName);
        saveRepo(repo);
    }

    public static void checkOut(String branchName){
        checkInit();
        Repository repo = readRepo();
        Commit cmtOld = headPtr(repo);
        if (!repo.branches.containsKey(branchName)){
            System.out.println("No such branch exists");
            System.exit(0);
        }
        if(repo.head.equals(branchName)){
            System.out.println("No need to checkout the current branch");
            System.exit(0);
        }
        repo.head = branchName;
        saveRepo(repo);
        Commit cmt = Commit.readCommit(repo.branches.get(branchName));
        for (String fileName: cmt.blobMap.keySet()){
            checkOut(fileName, "head");
        }
        for (String fileName: cmtOld.blobMap.keySet()){
            if (!cmt.containsFile(fileName) && join(CWD, fileName).exists()){
                join(CWD, fileName).delete();
            }
        }
    }

    public static void rmBranch(String branchName){
        checkInit();
        Repository repo = readRepo();
        if (!repo.branches.containsKey(branchName)){
            System.out.println("A branch with that name does not exist");
            System.exit(0);
        }
        if (repo.head.equals(branchName)){
            System.out.println("Cannot remove the current branch");
            System.exit(0);
        }
        repo.branches.remove(branchName);
        saveRepo(repo);
    }

    public static void reset(String cmtID){
        checkInit();
        Repository repo = readRepo();
        Commit currentCMT = headPtr(repo);
        Commit cmt = Commit.readCommit(cmtID);
        for (String fileName: cmt.blobMap.keySet()){
            if (join(CWD, fileName).exists() && !currentCMT.blobMap.containsKey(fileName)){
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first");
                System.exit(0);
            }
            checkOut(fileName, cmtID);
        }
        repo.branches.put(repo.head, cmtID);
        saveRepo(repo);
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

    @Override
    public String toString(){
        StringBuffer st = new StringBuffer();
        st.append("=== Branches ===\n");
        Set<String> bKeys = branches.keySet();
        for (String b: bKeys){
            if (b.equals(head)){
                st.append("*");
            }
            st.append(b);
            st.append("\n");
        }

        st.append("\n\n=== Staged Files ===\n");
        for (String k: staged.keySet()){
            st.append(k);
            st.append("\n");
        }

        st.append("\n\n=== Removed Files ===\n");
        for (String rm: removed){
            st.append(rm);
            st.append("\n");
        }

        st.append("\n\n=== Modifications Not Staged For Commit ===\n");

        st.append("\n=== Untracked Files ===" + "\n\n");

        return st.toString();
    }

    public static void main(String[] args) {
        Repository repo = readRepo();
        Commit cmt = headPtr(repo);
        System.out.println("1");
    }
}
