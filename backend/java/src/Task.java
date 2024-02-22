package src;

import java.util.*;

public class Task {
    public record File(
        int id,
        String name,
        List<String> categories,
        int parent,
        int size
    ) {}

    public static class FileTree {
        private File file;
        private List<FileTree> children = new ArrayList<>();

        public FileTree(File file) {
            this.file = file;
        }

        public void addChild(FileTree child) {
            this.children.add(child);
        }

        public File getFile() {
            return file;
        }

        public List<FileTree> getChildren() {
            return children;
        }

        public boolean isLeaf() {
            return children.isEmpty();
        }
    }

    // Inside Task class

    private static FileTree buildFileTree(List<File> files) {
        Map<Integer, FileTree> fileTreeMap = new HashMap<>();
        // Create FileTree nodes for each file
        for (File file : files) {
            fileTreeMap.put(file.id(), new FileTree(file));
        }
        // main root (entry point)
        FileTree root = new FileTree(new File(-1, "root", new ArrayList<>(), -1, 0)); // A pseudo-root to hold top-level files

        // Set up children based on parent IDs, use HashMap for faster search
        for (File file : files) {
            FileTree current = fileTreeMap.get(file.id());
            if (file.parent() == -1) {
                root.addChild(current); // Add to root if no parent
            } else {
                // assign children
                FileTree parent = fileTreeMap.getOrDefault(file.parent(), root);
                parent.addChild(current);
            }
        }

        return root;
    }

    private static void findLeafFiles(FileTree node, List<String> leafFileNames) {
        // terminal condition, if root add to results
        if (node.isLeaf() && node.getFile().id() != -1) { // Exclude pseudo-root
            leafFileNames.add(node.getFile().name());
        } else {
            for (FileTree child : node.getChildren()) {
                // look into each of the children of current file
                findLeafFiles(child, leafFileNames);
            }
        }
    }

    /**
     * Task 1
     */
    public static List<String> leafFiles(List<File> files) {
        // Restructure the files to a new defined file tree structure that enables easier access to children files.
        FileTree root = buildFileTree(files);
        List<String> leafFileNames = new ArrayList<>();

        // recursive iterate to find leaf files
        findLeafFiles(root, leafFileNames);
        return leafFileNames;
    }

    /**
     * Task 2
     */
    public static List<String> kLargestCategories(List<File> files, int k) {
        // use Hashmap to count frequencies and sort
        HashMap<String, Integer> categories = new HashMap<String, Integer>();

        // if categories exist, count += 1, else add new category entry
        for (File file : files){
            for (String category : file.categories()){
                if (categories.containsKey(category)){
                    categories.put(category, categories.get(category) + 1);
                } else {
                    categories.put(category, 0);
                }
            }
        }

        // Sort hashmap based on values
        List<String> result = new ArrayList<String>();
        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(categories.entrySet());
        entryList.sort(Map.Entry.comparingByValue());

        // return the last k element
        for (int i = entryList.size() - 1; i > entryList.size() - 1 - k; i--) {
            result.add(entryList.get(i).getKey());
        }
        return result;
    }

    /**
     * Task 3
     */
    // Inside the Task class

    public static int largestFileSize(List<File> files) {
        FileTree root = buildFileTree(files);
        HashMap<Integer, Integer> fileSizes = new HashMap<>();

        calculateSizes(root, fileSizes);

        // Exclude the root by starting the maximum size at 0
        int maxSize = 0;
        for (Integer size : fileSizes.values()) {
            // compare to find all time maximum value
            if (size > maxSize) {
                maxSize = size;
            }
        }

        return maxSize;
    }

    private static int calculateSizes(FileTree node, HashMap<Integer, Integer> fileSizes) {
        int totalSize = node.getFile().size(); // Start with the current node's size
        for (FileTree child : node.getChildren()) {
            totalSize += calculateSizes(child, fileSizes); // Recursively add the size of children
        }

        // Don't add the pseudo-root size to the map
        if (node.getFile().id() != -1) {
            fileSizes.put(node.getFile().id(), totalSize);
        }

        return totalSize; // Return the total size of THIS subtree
    }


    public static void main(String[] args) {
        List<File> testFiles = List.of(
            new File(1, "Document.txt", List.of("Documents"), 3, 1024),
            new File(2, "Image.jpg", List.of("Media", "Photos"), 34, 2048),
            new File(3, "Folder", List.of("Folder"), -1, 0),
            new File(5, "Spreadsheet.xlsx", List.of("Documents", "Excel"), 3, 4096),
            new File(8, "Backup.zip", List.of("Backup"), 233, 8192),
            new File(13, "Presentation.pptx", List.of("Documents", "Presentation"), 3, 3072),
            new File(21, "Video.mp4", List.of("Media", "Videos"), 34, 6144),
            new File(34, "Folder2", List.of("Folder"), 3, 0),
            new File(55, "Code.py", List.of("Programming"), -1, 1536),
            new File(89, "Audio.mp3", List.of("Media", "Audio"), 34, 2560),
            new File(144, "Spreadsheet2.xlsx", List.of("Documents", "Excel"), 3, 2048),
            new File(233, "Folder3", List.of("Folder"), -1, 4096)
        );
        
        List<String> leafFiles = leafFiles(testFiles);
        leafFiles.sort(null);
        assert leafFiles.equals(List.of(
            "Audio.mp3",
            "Backup.zip",
            "Code.py",
            "Document.txt",
            "Image.jpg",
            "Presentation.pptx",
            "Spreadsheet.xlsx",
            "Spreadsheet2.xlsx",
            "Video.mp4"
        ));

        System.out.println(leafFiles);

        assert kLargestCategories(testFiles, 3).equals(List.of(
            "Documents", "Folder", "Media"
        ));

        System.out.println(kLargestCategories(testFiles, 3));

        assert largestFileSize(testFiles) == 20992;

        System.out.println(largestFileSize(testFiles));
    }
}