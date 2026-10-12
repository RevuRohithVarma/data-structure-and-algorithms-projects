package library.ui;

import library.model.DigitalResource;
import library.engine.SearchEngine;
import library.randomized.MillerRabin;
import library.flow.*;
import library.complexity.*;
import library.dp.*;
import library.string.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.util.ArrayList;

/**
 * Presentation layer only. The DSA engine and algorithms are kept unchanged.
 * The UI is intentionally built with core Swing/AWT so the project remains
 * dependency-free and portable for a DSA lab evaluation.
 */
public class Dashboard extends JFrame {
    private static final Color BG = new Color(247, 244, 242);
    private static final Color CARD = Color.WHITE;
    private static final Color TEXT = new Color(32, 35, 40);
    private static final Color MUTED = new Color(113, 118, 126);
    private static final Color ORANGE = new Color(255, 116, 55);
    private static final Color ORANGE_DARK = new Color(236, 88, 36);
    private static final Color BLUE = new Color(38, 166, 230);
    private static final Color PURPLE = new Color(124, 93, 220);
    private static final Color GREEN = new Color(41, 190, 133);
    private static final Color RED = new Color(239, 91, 105);

    private final DigitalResource[] data;
    private final SearchEngine engine;
    private final JTextField search = new JTextField();
    private final JComboBox<String> mode = new JComboBox<>(new String[]{"AUTO", "KMP", "Z", "RABIN-KARP", "MULTI", "FUZZY"});
    private final JComboBox<String> type = new JComboBox<>(new String[]{"ALL", "BOOK", "MAGAZINE", "ARTICLE", "RESEARCH_PAPER", "THESIS", "REPORT", "OTHER"});
    private final JTextArea results = new JTextArea();
    private final JLabel resultTitle = new JLabel("Library overview");
    private final JLabel resultMeta = new JLabel("14,000+ digital resources");
    private final JPanel content = new JPanel(new BorderLayout(16, 16));
    private final JPanel cards = new JPanel(new GridLayout(1, 4, 14, 14));
    private final PieChartPanel pie = new PieChartPanel();
    private final String NL = System.lineSeparator();

    public Dashboard(DigitalResource[] d) {
        data = d;
        engine = new SearchEngine(d);
        setTitle("DLS — Digital Library System");
        setMinimumSize(new Dimension(1100, 720));
        setSize(1450, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        build();
    }

    private void build() {
        getContentPane().setBackground(BG);
        JPanel root = new JPanel(new BorderLayout(18, 0));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(18, 18, 18, 18));

        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildWorkspace(), BorderLayout.CENTER);
        setContentPane(root);
        overview();
    }

    private JPanel buildSidebar() {
        JPanel side = new JPanel(new BorderLayout(0, 14));
        side.setBackground(CARD);
        side.setBorder(new EmptyBorder(18, 14, 18, 14));
        side.setPreferredSize(new Dimension(210, 0));

        JLabel brand = new JLabel("▣  DLS — DIGITAL LIBRARY SYSTEM");
        brand.setFont(new Font("SansSerif", Font.BOLD, 19));
        brand.setForeground(TEXT);
        side.add(brand, BorderLayout.NORTH);

        JPanel nav = new JPanel();
        nav.setOpaque(false);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        String[] labs = {"Dashboard", "Catalog", "Advanced Search", "Fuzzy Matching", "Similarity", "Flow / Matching", "NP & Approximation", "Randomized Algorithms", "Parallel Primitives", "Algorithm Explorer"};
        for (String s : labs) {
            JButton b = navButton(s, s.equals("Dashboard"));
            nav.add(b);
            nav.add(Box.createVerticalStrut(7));
            wire(b, s);
        }
        side.add(nav, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        bottom.setOpaque(false);
        JLabel note = new JLabel("<html><b>DSA-3 Lab</b><br><font color='#777777'>Advanced algorithms • Search • Analytics</font></html>");
        note.setBorder(new EmptyBorder(10, 10, 10, 10));
        note.setOpaque(true);
        note.setBackground(new Color(255, 238, 225));
        bottom.add(note, BorderLayout.CENTER);
        side.add(bottom, BorderLayout.SOUTH);
        return side;
    }

    private JButton navButton(String text, boolean selected) {
        JButton b = new JButton(text);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setFont(new Font("SansSerif", selected ? Font.BOLD : Font.PLAIN, 13));
        b.setForeground(selected ? Color.WHITE : TEXT);
        b.setBackground(selected ? ORANGE : Color.WHITE);
        b.setOpaque(true);
        b.setBorder(new EmptyBorder(11, 13, 11, 8));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void wire(JButton b, String s) {
        if (s.equals("Dashboard")) b.addActionListener(e -> overview());
        else if (s.equals("Catalog")) b.addActionListener(e -> catalog());
        else if (s.equals("Similarity")) b.addActionListener(e -> similarity());
        else if (s.equals("Flow / Matching")) b.addActionListener(e -> flow());
        else if (s.equals("Randomized Algorithms")) b.addActionListener(e -> randomized());
        else if (s.equals("NP & Approximation")) b.addActionListener(e -> np());
        else if (s.equals("Parallel Primitives")) b.addActionListener(e -> parallel());
        else if (s.equals("Algorithm Explorer")) b.addActionListener(e -> explorer());
        else if (s.equals("Fuzzy Matching")) b.addActionListener(e -> { mode.setSelectedItem("FUZZY"); runSearch(); });
        else b.addActionListener(e -> { search.requestFocusInWindow(); });
    }

    private JPanel buildWorkspace() {
        JPanel work = new JPanel(new BorderLayout(0, 15));
        work.setOpaque(false);
        work.add(buildTopBar(), BorderLayout.NORTH);

        content.setOpaque(false);
        content.add(buildMetrics(), BorderLayout.NORTH);
        content.add(buildMainGrid(), BorderLayout.CENTER);
        work.add(content, BorderLayout.CENTER);
        return work;
    }

    private JPanel buildTopBar() {
        JPanel top = new JPanel(new BorderLayout(12, 8));
        top.setOpaque(false);

        JPanel center = new JPanel(new BorderLayout(8, 7));
        center.setOpaque(false);
        JPanel searchWrap = new JPanel(new BorderLayout(10, 0));
        searchWrap.setBackground(Color.WHITE);
        searchWrap.setBorder(new LineBorder(new Color(232, 228, 224), 1, true));
        search.setFont(new Font("SansSerif", Font.PLAIN, 15));
        search.setBorder(new EmptyBorder(10, 14, 10, 14));
        search.setToolTipText("Search titles, authors, keywords, categories or content");
        searchWrap.add(new JLabel("  🔎  "), BorderLayout.WEST);
        searchWrap.add(search, BorderLayout.CENTER);
        JButton go = roundedButton("SEARCH", ORANGE, Color.WHITE);
        go.addActionListener(e -> runSearch());
        search.addActionListener(e -> runSearch());
        searchWrap.add(go, BorderLayout.EAST);
        center.add(searchWrap, BorderLayout.NORTH);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 0));
        filters.setOpaque(false);
        JLabel al = new JLabel("Algorithm"); al.setForeground(MUTED); al.setFont(new Font("SansSerif", Font.BOLD, 11));
        JLabel ty = new JLabel("Type"); ty.setForeground(MUTED); ty.setFont(new Font("SansSerif", Font.BOLD, 11));
        mode.setFont(new Font("SansSerif", Font.PLAIN, 12));
        type.setFont(new Font("SansSerif", Font.PLAIN, 12));
        mode.setPreferredSize(new Dimension(125, 28));
        type.setPreferredSize(new Dimension(155, 28));
        filters.add(al); filters.add(mode); filters.add(Box.createHorizontalStrut(8));
        filters.add(ty); filters.add(type);
        JLabel hint = new JLabel("  Tip: use MULTI with comma-separated patterns");
        hint.setForeground(MUTED); hint.setFont(new Font("SansSerif", Font.PLAIN, 10));
        filters.add(hint);
        center.add(filters, BorderLayout.SOUTH);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JLabel badge = pill("●  LIVE LIBRARY", new Color(232, 249, 241), GREEN);
        JButton export = roundedButton("Export", Color.WHITE, TEXT);
        export.setBorder(new LineBorder(new Color(225, 221, 217), 1, true));
        export.addActionListener(e -> exportReport());
        actions.add(badge);
        actions.add(export);
        top.add(center, BorderLayout.CENTER);
        top.add(actions, BorderLayout.EAST);
        return top;
    }

    private JPanel buildMetrics() {
        cards.setOpaque(false);
        cards.removeAll();
        cards.add(metricCard("Total Resources", String.format("%,d", data.length), "all digital records", ORANGE));
        cards.add(metricCard("Books", String.format("%,d", count("BOOK")), "catalogued titles", BLUE));
        cards.add(metricCard("Magazines", String.format("%,d", count("MAGAZINE")), "issues & editions", PURPLE));
        cards.add(metricCard("Research", String.format("%,d", count("RESEARCH_PAPER") + count("THESIS")), "papers + thesis", GREEN));
        return cards;
    }

    private JPanel metricCard(String title, String value, String sub, Color accent) {
        JPanel p = cardPanel(new BorderLayout(8, 5));
        p.setBorder(new EmptyBorder(14, 16, 14, 16));
        JLabel t = new JLabel(title.toUpperCase());
        t.setFont(new Font("SansSerif", Font.BOLD, 11));
        t.setForeground(MUTED);
        JLabel v = new JLabel(value);
        v.setFont(new Font("SansSerif", Font.BOLD, 25));
        v.setForeground(TEXT);
        JLabel s = new JLabel(sub);
        s.setFont(new Font("SansSerif", Font.PLAIN, 11));
        s.setForeground(MUTED);
        JPanel labels = new JPanel(new GridLayout(3, 1));
        labels.setOpaque(false);
        labels.add(t); labels.add(v); labels.add(s);
        JPanel dot = new JPanel();
        dot.setPreferredSize(new Dimension(8, 60));
        dot.setBackground(accent);
        p.add(dot, BorderLayout.WEST);
        p.add(labels, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildMainGrid() {
        JPanel grid = new JPanel(new BorderLayout(15, 15));
        grid.setOpaque(false);

        JPanel left = new JPanel(new BorderLayout(0, 15));
        left.setOpaque(false);
        left.add(buildTrafficCard(), BorderLayout.NORTH);
        left.add(buildResultsCard(), BorderLayout.CENTER);

        JPanel right = new JPanel(new BorderLayout(0, 15));
        right.setOpaque(false);
        right.setPreferredSize(new Dimension(340, 0));
        right.add(buildPieCard(), BorderLayout.CENTER);
        right.add(buildQuickActions(), BorderLayout.SOUTH);

        grid.add(left, BorderLayout.CENTER);
        grid.add(right, BorderLayout.EAST);
        return grid;
    }

    private JPanel buildTrafficCard() {
        JPanel p = cardPanel(new BorderLayout(8, 8));
        p.setBorder(new EmptyBorder(15, 16, 12, 16));
        JLabel title = sectionTitle("Library Activity", "Resource distribution and search-ready corpus");
        p.add(title, BorderLayout.NORTH);
        p.add(new MiniBarChart(data), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildResultsCard() {
        JPanel p = cardPanel(new BorderLayout(8, 8));
        p.setBorder(new EmptyBorder(14, 15, 12, 15));
        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        resultTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        resultTitle.setForeground(TEXT);
        resultMeta.setFont(new Font("SansSerif", Font.PLAIN, 11));
        resultMeta.setForeground(MUTED);
        head.add(resultTitle, BorderLayout.WEST);
        head.add(resultMeta, BorderLayout.EAST);
        p.add(head, BorderLayout.NORTH);

        results.setEditable(false);
        results.setFont(new Font("Monospaced", Font.PLAIN, 12));
        results.setForeground(TEXT);
        results.setBackground(Color.WHITE);
        results.setLineWrap(false);
        JScrollPane sp = new JScrollPane(results);
        sp.setBorder(new LineBorder(new Color(239, 235, 231), 1, true));
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildPieCard() {
        JPanel p = cardPanel(new BorderLayout(5, 8));
        p.setBorder(new EmptyBorder(15, 15, 15, 15));
        p.add(sectionTitle("Collection Mix", "Every resource category in the corpus"), BorderLayout.NORTH);
        p.add(pie, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildQuickActions() {
        JPanel p = cardPanel(new GridLayout(2, 2, 8, 8));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        String[] actions = {"Advanced Search", "Fuzzy Search", "Similarity", "Algorithm Lab"};
        for (String a : actions) {
            JButton b = roundedButton(a, new Color(255, 249, 245), ORANGE_DARK);
            b.setBorder(new LineBorder(new Color(255, 224, 211), 1, true));
            b.addActionListener(e -> {
                if (a.equals("Advanced Search")) { mode.setSelectedItem("KMP"); search.requestFocusInWindow(); }
                else if (a.equals("Fuzzy Search")) { mode.setSelectedItem("FUZZY"); runSearch(); }
                else if (a.equals("Similarity")) similarity();
                else explorer();
            });
            p.add(b);
        }
        return p;
    }

    private JPanel cardPanel(LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setBackground(CARD);
        p.setBorder(new LineBorder(new Color(235, 231, 227), 1, true));
        return p;
    }

    private JLabel sectionTitle(String title, String subtitle) {
        JLabel l = new JLabel("<html><b style='font-size:15px'>" + title + "</b><br><font color='#777777'>" + subtitle + "</font></html>");
        return l;
    }

    private JLabel pill(String text, Color bg, Color fg) {
        JLabel l = new JLabel(text);
        l.setOpaque(true);
        l.setBackground(bg);
        l.setForeground(fg);
        l.setFont(new Font("SansSerif", Font.BOLD, 10));
        l.setBorder(new EmptyBorder(7, 9, 7, 9));
        return l;
    }

    private JButton roundedButton(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setForeground(fg);
        b.setBackground(bg);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setBorder(new EmptyBorder(9, 14, 9, 14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void runSearch() {
        String q = search.getText().trim();
        if (q.isEmpty()) { overview(); return; }
        long start = System.nanoTime();
        String m = String.valueOf(mode.getSelectedItem());
        if (m.equals("AUTO")) m = "KMP";
        String selectedType = String.valueOf(type.getSelectedItem());
        library.engine.SearchResults r = engine.search(q, m, selectedType);
        double ms = (System.nanoTime() - start) / 1_000_000.0;
        StringBuilder sb = new StringBuilder();
        sb.append("SEARCH RESULTS").append(NL)
          .append("──────────────────────────────────────────────────────────────").append(NL)
          .append("Algorithm : ").append(m).append("    Query : ").append(q).append(NL)
          .append("Filter    : ").append(selectedType).append("    Matches : ").append(r.size()).append(NL)
          .append(String.format("Execution : %.3f ms", ms)).append(NL).append(NL);
        int lim = Math.min(80, r.size());
        for (int i = 0; i < lim; i++) {
            DigitalResource x = r.get(i);
            sb.append(String.format("%3d. %-48s | %-15s | %-20s | %s", i + 1, clip(x.title, 48), x.type, clip(x.author, 20), x.category)).append(NL);
        }
        if (r.size() > lim) sb.append(NL).append("Showing first ").append(lim).append(" results. Use the type filter for a narrower view.");
        if (r.size() == 0) sb.append("No matching resource found. Try FUZZY mode for typo-tolerant matching.");
        results.setText(sb.toString());
        resultTitle.setText("Search results");
        resultMeta.setText(r.size() + " matches • " + String.format("%.2f ms", ms));
    }

    private String clip(String s, int n) { return s.length() <= n ? s : s.substring(0, n - 1) + "…"; }

    private void overview() {
        StringBuilder s = new StringBuilder();
        s.append("LIBRARY OVERVIEW").append(NL);
        s.append("══════════════════════════════════════════════════════════════════════").append(NL).append(NL);
        s.append("Total resources : ").append(data.length).append(NL);
        s.append("Books           : ").append(count("BOOK")).append(NL);
        s.append("Magazines       : ").append(count("MAGAZINE")).append(NL);
        s.append("Articles        : ").append(count("ARTICLE")).append(NL);
        s.append("Research Papers : ").append(count("RESEARCH_PAPER")).append(NL);
        s.append("Thesis          : ").append(count("THESIS")).append(NL);
        s.append("Reports         : ").append(count("REPORT")).append(NL);
        s.append("Others          : ").append(count("OTHER")).append(NL).append(NL);
        s.append("DSA-3 SERVICES").append(NL);
        s.append("• KMP / Z / Rabin-Karp pattern search").append(NL);
        s.append("• Aho-Corasick multi-pattern search").append(NL);
        s.append("• Levenshtein / Damerau fuzzy matching").append(NL);
        s.append("• Suffix Array + LCP similarity lab").append(NL);
        s.append("• Max-flow / matching simulator").append(NL);
        s.append("• NP-hardness & approximation lab").append(NL);
        s.append("• Miller-Rabin / randomized sorting / reservoir sampling").append(NL);
        s.append("• Parallel reduce and prefix-sum concepts").append(NL).append(NL);
        s.append("Try searching: algorithms, machine learning, digital library, India, Telugu, Hindi");
        results.setText(s.toString());
        resultTitle.setText("Library overview");
        resultMeta.setText(String.format("%,d resources loaded", data.length));
    }

    private void catalog() {
        results.setText("CATALOG QUICK VIEW" + NL + NL +
                "Books       : " + count("BOOK") + NL +
                "Magazines   : " + count("MAGAZINE") + NL +
                "Articles    : " + count("ARTICLE") + NL +
                "Papers      : " + count("RESEARCH_PAPER") + NL +
                "Thesis      : " + count("THESIS") + NL +
                "Reports     : " + count("REPORT") + NL +
                "Others      : " + count("OTHER") + NL + NL +
                "Use the global search above with the Type filter to browse a category.");
        resultTitle.setText("Catalog");
        resultMeta.setText("Category counts");
    }

    private int count(String t) { int n = 0; for (DigitalResource x : data) if (x.type.equals(t)) n++; return n; }

    private void similarity() {
        if (data.length < 2) return;
        DigitalResource a = data[0], b = data[1];
        int sw = new SmithWaterman().score(a.content, b.content);
        int nw = new NeedlemanWunsch().score(a.content, b.content);
        int[] sa = new SuffixArray().build(a.content);
        int[] lcp = new LCPArray().build(a.content, sa);
        results.setText("SIMILARITY LAB" + NL + NL + "Document A: " + a.title + NL + "Document B: " + b.title + NL + NL +
                "Smith-Waterman local score : " + sw + NL + "Needleman-Wunsch global score: " + nw + NL +
                "Suffix array size           : " + sa.length + NL + "LCP/Kasai values            : " + lcp.length + NL + NL +
                "Purpose: common-substring and document-similarity analysis.");
        resultTitle.setText("Similarity lab");
        resultMeta.setText("Suffix Array • LCP • Alignment");
    }

    private void flow() {
        int[][] c = {{0,8,6,0,0,0},{0,0,0,5,4,0},{0,0,0,3,7,0},{0,0,0,0,0,7},{0,0,0,0,0,6},{0,0,0,0,0,0}};
        int ek = new EdmondsKarp().maxFlow(copy(c), 0, 5);
        Dinic d = new Dinic(6);
        for (int i = 0; i < c.length; i++) for (int j = 0; j < c.length; j++) if (c[i][j] > 0) d.addEdge(i, j, c[i][j]);
        int di = d.maxFlow(0, 5);
        boolean[][] m = {{true,true,false},{false,true,true},{true,false,true}};
        results.setText("NETWORK FLOW LAB" + NL + NL + "Edmonds-Karp max flow : " + ek + NL + "Dinic max flow        : " + di + NL +
                "Bipartite matching    : " + new BipartiteMatching().maxMatching(m) + NL + NL +
                "Application: allocating limited digital-library resources to departments/users.");
        resultTitle.setText("Flow / matching lab");
        resultMeta.setText("Edmonds-Karp • Dinic • Matching");
    }

    private int[][] copy(int[][] a) { int[][] b = new int[a.length][]; for (int i = 0; i < a.length; i++) { b[i] = new int[a[i].length]; System.arraycopy(a[i], 0, b[i], 0, a[i].length); } return b; }

    private void randomized() {
        results.setText("RANDOMIZED ALGORITHMS LAB" + NL + NL +
                "Miller-Rabin 1000000007 → " + new MillerRabin().isPrime(1000000007L) + NL + NL +
                "Reservoir sampling: uniform k-item sampling from a stream with O(k) storage." + NL +
                "Randomized QuickSort: randomized pivots reduce adversarial-input risk." + NL +
                "Universal hashing: randomized hash families reduce systematic collision patterns.");
        resultTitle.setText("Randomized algorithms");
        resultMeta.setText("Miller-Rabin • Hashing • Sampling");
    }

    private void np() {
        int[] w = {2,3,4,5}, v = {3,4,5,7};
        results.setText("NP / APPROXIMATION LAB" + NL + NL +
                "Knapsack optimum, capacity 8: " + new Knapsack().solve(w, v, 8) + NL +
                "Subset Sum target 9: " + new SubsetSum().solve(new int[]{3,4,5,7}, 9) + NL +
                "Vertex Cover 2-approximation via maximal matching is included in the engine." + NL + NL +
                "Use case: selecting high-value digital resources under storage/budget constraints.");
        resultTitle.setText("NP & approximation lab");
        resultMeta.setText("Knapsack • Subset Sum • Vertex Cover");
    }

    private void parallel() {
        int[] a = new int[1000]; for (int i = 0; i < a.length; i++) a[i] = i + 1;
        long sum = new library.parallel.ParallelPrimitives().reduce(a);
        new library.parallel.ParallelPrimitives().prefixSum(a);
        results.setText("PARALLEL ALGORITHM LAB" + NL + NL + "Work (T1) concept: reduction over 1000 values" + NL +
                "Demo sum: " + sum + NL + "Prefix-sum final value: " + a[a.length - 1] + NL + NL +
                "Concepts: work/span, Brent's theorem, reduce, scan, parallel sort.");
        resultTitle.setText("Parallel primitives");
        resultMeta.setText("Reduce • Prefix scan • Work/span");
    }

    private void explorer() {
        results.setText("ALGORITHM EXPLORER" + NL + NL +
                "MODULE 2 — STRING" + NL + "KMP • Z • Rabin-Karp • Aho-Corasick • Suffix Array • LCP/Kasai" + NL + NL +
                "MODULE 3 — ADVANCED DP" + NL + "Levenshtein • Damerau • Needleman-Wunsch • Smith-Waterman • Interval DP • Bitmask DP • Tree DP • SOS DP" + NL + NL +
                "MODULE 4 — FLOW" + NL + "Ford-Fulkerson • Edmonds-Karp • Dinic • Min-Cut • Bipartite Matching" + NL + NL +
                "MODULE 5 — NP / APPROXIMATION" + NL + "SAT/reductions concepts • Vertex Cover 2-approx • Knapsack • Subset Sum • TSP/Hamiltonian concepts" + NL + NL +
                "MODULE 6 — RANDOMIZED / PARALLEL" + NL + "Miller-Rabin • Randomized QuickSort • Universal Hashing • Reservoir Sampling • Reduce • Prefix Scan • Parallel Sort");
        resultTitle.setText("Algorithm explorer");
        resultMeta.setText("DSA-3 modules 2–6");
    }

    private void exportReport() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("digital-library-report.txt"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try (java.io.PrintWriter out = new java.io.PrintWriter(chooser.getSelectedFile())) {
            out.println("DIGITAL LIBRARY MANAGEMENT SYSTEM — DSA-3 REPORT");
            out.println("Total resources: " + data.length);
            out.println("Books: " + count("BOOK"));
            out.println("Magazines: " + count("MAGAZINE"));
            out.println("Articles: " + count("ARTICLE"));
            out.println("Research Papers: " + count("RESEARCH_PAPER"));
            out.println("Thesis: " + count("THESIS"));
            out.println("Reports: " + count("REPORT"));
            out.println("Others: " + count("OTHER"));
            JOptionPane.showMessageDialog(this, "Report exported successfully.", "Export", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not export report: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class PieChartPanel extends JPanel {
        private final Color[] colors = {ORANGE, BLUE, PURPLE, GREEN, new Color(245, 181, 60), new Color(94, 116, 181), RED};
        private final String[] names = {"Books", "Magazines", "Articles", "Research Papers", "Thesis", "Reports", "Others"};
        PieChartPanel() { setOpaque(false); setPreferredSize(new Dimension(310, 410)); }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int total = data.length;
            int size = Math.min(getWidth() - 40, 220);
            int x = (getWidth() - size) / 2, y = 18;
            int start = 90;
            for (int i = 0; i < names.length; i++) {
                int value = count(typeFor(i));
                int angle = (int)Math.round(360.0 * value / total);
                g2.setColor(colors[i]);
                g2.fill(new Arc2D.Double(x, y, size, size, start, -angle, Arc2D.PIE));
                start -= angle;
            }
            g2.setColor(CARD);
            int hole = (int)(size * .54);
            g2.fillOval(x + (size-hole)/2, y + (size-hole)/2, hole, hole);
            g2.setColor(TEXT);
            g2.setFont(new Font("SansSerif", Font.BOLD, 20));
            String totalText = String.format("%,d", total);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(totalText, x + (size-fm.stringWidth(totalText))/2, y + size/2 + 2);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g2.setColor(MUTED);
            String sub = "resources";
            g2.drawString(sub, x + (size-g2.getFontMetrics().stringWidth(sub))/2, y + size/2 + 20);

            int ly = y + size + 28;
            for (int i = 0; i < names.length; i++) {
                int value = count(typeFor(i));
                g2.setColor(colors[i]);
                g2.fillRoundRect(8, ly + 1, 9, 9, 5, 5);
                g2.setColor(TEXT);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                g2.drawString(names[i], 25, ly + 10);
                String n = String.format("%,d", value);
                g2.setColor(MUTED);
                int width = g2.getFontMetrics().stringWidth(n);
                g2.drawString(n, getWidth() - width - 8, ly + 10);
                ly += 22;
            }
            g2.dispose();
        }
        private String typeFor(int i) { return new String[]{"BOOK","MAGAZINE","ARTICLE","RESEARCH_PAPER","THESIS","REPORT","OTHER"}[i]; }
    }

    private class MiniBarChart extends JPanel {
        private final String[] labels = {"Books", "Mag", "Articles", "Papers", "Thesis", "Reports", "Other"};
        MiniBarChart(DigitalResource[] ignored) { setOpaque(false); setPreferredSize(new Dimension(500, 160)); }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int[] vals = {count("BOOK"), count("MAGAZINE"), count("ARTICLE"), count("RESEARCH_PAPER"), count("THESIS"), count("REPORT"), count("OTHER")};
            int max = 1; for (int v : vals) max = Math.max(max, v);
            int base = getHeight() - 32, left = 20, width = Math.max(18, (getWidth() - 45) / vals.length - 8);
            g2.setColor(new Color(235,232,229));
            for (int j=0;j<4;j++) { int gy = 18 + j*(base-18)/3; g2.drawLine(left, gy, getWidth()-10, gy); }
            for (int i=0;i<vals.length;i++) {
                int h = (int)((base-30) * (vals[i]/(double)max));
                int x = left + i*(width+10);
                int y = base-h;
                g2.setColor(i==0 ? ORANGE : new Color(255, 184, 142));
                g2.fillRoundRect(x, y, width, h, 8, 8);
                g2.setColor(MUTED); g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
                String lab = labels[i]; g2.drawString(lab, x, base+15);
            }
            g2.dispose();
        }
    }
}
