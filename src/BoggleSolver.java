/*
 * This project is a very good way to learn pruning
 * At the very beginning, I used the TST that the alr4.jar offered
 * It turned out very slow even though I return immediately when the current word is not prefix of any dictionary word
 * Then someone on the forum reminds me to create my own tries
 * So I choose tries with R=26
 * it got a lot faster than before, but still not enough
 * Then the specification of this homework reminded me that when you search for the next(and longer) word
 * you don't have to go all the way from the root every time
 * you can only begin your search from where you were the last time
 * so I add the tries' node as a parameter in the depth-first search, where I can start my searching the last
 * time I finish, which turned out a huge saving of time, to be exact, dozens of time, according to the assessment summary
 * 
 * And what's better is that I realized that I don't even need to do a prefix search
 * because the tries is formed by the dictionary and whe don't have delete operation
 * So every leaf would certainly have value, that means as long as the next Node isn't null, 
 * you can always find a word in the dictionary
 * 
 * So finally I got the full score(it's pity that didn't get the bonus score) and learn a lot about tries and pruning
 */
import java.util.HashSet;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;


public class BoggleSolver
{
    private static final int R = 26;
    private TrieST tries;
    private int M, N;
    private HashSet<String> result;
    private BoggleBoard board;
    
    private static class Node {
        private boolean val;
        private Node[] next = new Node[R];
    }
    
    private class TrieST {
        
        private Node root;

        public boolean contains(String key) {
            Node x = get(root, key, 0);
            if (x == null) return false;
            return x.val;
        }

        private Node get(Node x, String key, int d) {
            if (x == null) return null;
            if (d == key.length()) return x;
            char c = key.charAt(d);
            return get(x.next[c - 'A'], key, d+1);
        }
        
        public Node getNode(String key)
        {
            return get(root, key, 0);
        }
        
        public void put(String key, boolean val) {
            root = put(root, key, val, 0);
        }

        private Node put(Node x, String key, boolean val, int d) {
            if (x == null) x = new Node();
            if (d == key.length()) {
                x.val = val;
                return x;
            }
            char c = key.charAt(d);
            x.next[c - 'A'] = put(x.next[c - 'A'], key, val, d+1);
            return x;
        }
        
    }

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary)
    {
        this.tries = new TrieST();
        for (String word : dictionary)
        {
            if (word.length() > 2)
            tries.put(word, true);
        }
    }
    

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board)
    {
        this.board = board;
        M = board.rows();
        N = board.cols();
        boolean[] marked = new boolean[M * N];
        result = new HashSet<String>();
        for (int i = 0; i < M; i++)
        {
            for (int j = 0; j < N; j++)
            {
                gfs(i, j, marked, getLetter(i, j), this.tries.getNode(getLetter(i, j)));
            }
        }
        
        return result;
    }
    
    private String getLetter(int row, int col)
    {
        char ch = board.getLetter(row, col);
        if (ch == 'Q') return "QU";
        else return String.valueOf(ch);
    }

    private void gfs(int row, int col, boolean[] marked, String wordsofar, Node n)
    {
        if (n == null) return;
        if (n.val) 
            result.add(wordsofar);
        
        marked[rowcol(row, col)] = true;
        
        for (int i = Math.max(0, row-1); i <= row+1 && i < board.rows(); i++)
        {
            for (int j = Math.max(0, col-1); j <= col+1 && j < board.cols(); j++)
            {
                if (!marked[rowcol(i, j)])
                {
                    char c = board.getLetter(i, j);                    
                    Node x = n.next[c - 'A'];
                    if (x != null)
                    {
                        
                        if (c == 'Q')
                        {
                            gfs(i, j, marked, wordsofar + "QU", x.next['U' - 'A']);
                            
                        }
                        else
                            gfs(i, j, marked, wordsofar + c, x);
                    }
                                         
                }
            }
        }
        marked[rowcol(row, col)] = false;
    }
    

    private int rowcol(int i, int j)
    {
        return i * N + j;
    }
    
    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word)
    {
        int sc = 0;
        if (this.tries.contains(word))
        {
            if (word.length() <= 2)
            sc = 0;
            else if (word.length() == 3 || word.length() == 4)
            sc = 1;
            else if (word.length() == 5)
            sc = 2;
            else if (word.length() == 6)
            sc = 3;
            else if (word.length() == 7)
            sc = 5;
            else sc = 11;
        }
        return sc;
    }
    
    public static void main(String[] args)
    {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board))
        {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}