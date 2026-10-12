package library.engine;
import library.model.DigitalResource;
import library.string.*;
import library.dp.EditDistance;
public class SearchEngine {
 private final DigitalResource[] data; private final KMPSearch kmp=new KMPSearch(); private final ZAlgorithm z=new ZAlgorithm(); private final RabinKarp rk=new RabinKarp(); private final AhoCorasick ac=new AhoCorasick(); private final EditDistance ed=new EditDistance();
 public SearchEngine(DigitalResource[] d){data=d;}
 public SearchResults search(String q,String mode,String type){SearchResults r=new SearchResults();String qq=q.trim();for(DigitalResource x:data){if(type.length()>0&&!type.equals("ALL")&&!x.type.equalsIgnoreCase(type))continue;String s=x.searchable();boolean ok=false;if(mode.equals("KMP"))ok=kmp.contains(s,qq);else if(mode.equals("Z"))ok=z.contains(s,qq);else if(mode.equals("RABIN-KARP"))ok=rk.contains(s,qq);else if(mode.equals("MULTI"))ok=ac.containsAny(s,qq.split(","));else if(mode.equals("FUZZY"))ok=fuzzy(x,qq);else ok=s.toLowerCase().contains(qq.toLowerCase());if(ok)r.add(x);}return r;}
 private boolean fuzzy(DigitalResource x,String q){String[] words=x.title.split("\\s+");for(String w:words)if(ed.levenshtein(w,q)<=3)return true;words=x.author.split("\\s+");for(String w:words)if(ed.levenshtein(w,q)<=3)return true;return ed.levenshtein(x.title,q)<=3||ed.levenshtein(x.author,q)<=3;}
}
