# Digital Library Management System — DSA-3

A Java/Swing digital-library project designed around the DSA-3 syllabus. It contains **14,000 explicit synthetic records**: 2,000 each for books, magazines, articles, research papers, thesis, reports, and other resources.

## Run

### Windows
Run `run.bat`.

### Command line
```text
javac -d out $(find src -name "*.java")
java -cp out library.Main
```

On Windows PowerShell, compile all `.java` files listed by `Get-ChildItem -Recurse src -Filter *.java`.

## Dashboard
- Advanced search with KMP, Z, Rabin-Karp, multi-pattern and fuzzy modes.
- Resource-type filters.
- Similarity lab using suffix arrays, LCP/Kasai, Smith-Waterman and Needleman-Wunsch.
- Network-flow lab with Edmonds-Karp, Dinic and bipartite matching.
- NP/approximation lab with Knapsack, Subset Sum and Vertex Cover approximation.
- Randomized lab with Miller-Rabin, reservoir sampling and randomized QuickSort concepts.
- Parallel primitives lab with reduce and prefix-sum concepts.
- Algorithm Explorer maps the application to all six DSA-3 modules.

## Architecture
`model` → resource model; `loader` → explicit corpus loader; `engine` → search orchestration; `string`, `dp`, `flow`, `complexity`, `randomized`, `parallel` → hand-built algorithm implementations; `ui` → Swing dashboard.

The algorithm engine does not use `java.util.*`; custom arrays are used in core algorithm structures where practical. Java collections are isolated mainly to loading/UI orchestration.

## Dataset
Each resource contains resource type, title, author, identifier, category, keywords, year, language, and content. The data is synthetic and intended for academic demonstration, testing, and benchmarking—not as a claim of real publication metadata.
