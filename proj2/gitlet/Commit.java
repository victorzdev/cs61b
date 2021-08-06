package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.util.*;
import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    public String message;
    public Date timestamp;
    // someting with the files I track?
    String parent1;
    String parent2;

    /** mapping fileName =======> blob name. */
    Map<String, String> blobMap;

    /** SHA-1 Value of this commit. */
    String name;

    public Commit(String msg, Date date, String p1, String p2){
        this.message = msg;
        this.timestamp = date;
        parent1 = p1;
        parent2 = p2;
        blobMap = new HashMap<>();
        name = null;
    }

    public void saveCommit(){
        String cName = commitName();
        name = cName;
        File outFile = Utils.join(Repository.COMMITS_DIR, cName);
        writeObject(outFile, this);
    }

    public String commitName(){
        List<String> nameList = new ArrayList<>();
        nameList.add(this.message);
        nameList.add(this.timestamp.toString());
        for (Map.Entry<String, String> fileBlob: blobMap.entrySet()){
            nameList.add(fileBlob.getKey());
            nameList.add(fileBlob.getValue());
        }
        return sha1(nameList.toArray());
    }

    public static String createCommit(String m, Date d, String p1, String p2) {
        Commit c = new Commit(m, d, p1, p2);
        String name = c.commitName();
        c.saveCommit();
        return name;
    }

    public static Commit readCommit(String commitId) {
        if (commitId == null) {
            return null;
        }

        File file = join(Repository.COMMITS_DIR, commitId);

        if  (!file.exists()) {
            System.out.printf("No commit with that id exists.");
            System.exit(0);
        }

        return readObject(file, Commit.class);
    }

    public boolean containsFile(String fileName) {
        return blobMap.containsKey(fileName);
    }

    public boolean isIdentical(String fileName, String sha1Value){
        return sha1Value.equals(blobMap.get(fileName));
    }

    public static String makeCommit(String current, Map<String, String> staged, Set<String> removed, String msg, String parent2){
        Commit cmt = readCommit(current);
        Date d = new Date();

        Commit newCmt = new Commit(msg, d, current, parent2);
        newCmt.blobMap = cmt.blobMap;

        for (Map.Entry<String, String> entry: staged.entrySet()){
            newCmt.blobMap.put(
                    entry.getKey(), entry.getValue());
        }

        for (String fileName: removed){
            newCmt.blobMap.remove(fileName);
        }

        cmt.saveCommit();
        return cmt.commitName();
    }

    /* TODO: fill in the rest of this class. */
}
