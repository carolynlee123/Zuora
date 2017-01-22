import java.util.*;
import java.io.*;

public class Zuora {

    public static void main(String[] args) {
        String fileName = args[0];
        File file = new File(fileName);
        String line;
        List<String> logs = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while ((line = br.readLine()) != null) {
                logs.add(line);
            }
            br.close();
        } catch (FileNotFoundException fne) {
            fne.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        //--Print output--
        List<String> result = topN(logs, 3, 2);
        for (int i = 0; i < result.size(); i++) {
            System.out.println(i+1 + ": " + result.get(i));
        }
    }

    //--Returns top n paths with path length specified as length from list of logs--
    public static List<String> topN(List<String> logs, int length, int n) {
        Map<List<String>, Integer> freq = new HashMap<>(); // Map each valid path to its frequency
        Map<String, List<String>> paths = new HashMap<>(); // user -> valid paths
        // go through each log line by line
        for (String log : logs) {
            String[] s = log.split("\\t");
            String user = s[0];
            String page = s[1];
            List<String> currPath = paths.get(user);
            if (currPath == null) {
                currPath = new ArrayList<String>();
                currPath.add(page);
                paths.put(user, currPath);
            }
            else {
                // check if length of path is specified length
                if (currPath.size() == length) {
                    currPath.remove(0); // pop off top page
                }
                currPath.add(page);
                // add path to paths if length is valid
                if (currPath.size() == length) {
                    paths.put(user, currPath);
                    if (!freq.containsKey(currPath)) {
                        freq.put(new ArrayList<String>(currPath), 1);
                    }
                    else {
                        freq.put(currPath, freq.get(currPath) + 1);
                    }
                }
            }
        }

        // Use priority queue to sort paths by descending frequency
        PriorityQueue<Map.Entry<List<String>, Integer>> pq = new PriorityQueue<>((a, b)->a.getValue() - b.getValue());
        for (Map.Entry<List<String>, Integer> entry : freq.entrySet()) {
            if (pq.size() < n) {
                pq.add(entry);
            }
            else {
                if (entry.getValue() > pq.peek().getValue()) {
                    pq.poll();
                    pq.add(entry);
                }
            }
        }

        // output
        List<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        while (!pq.isEmpty()) {
            for (String s : pq.peek().getKey()) {
                sb.append(s);
                sb.append(" -> ");
            }
            sb.setLength(sb.length() - 4);
            result.add(sb.toString());
            sb.setLength(0);
            pq.poll();
        }

        return result;
    }


}
