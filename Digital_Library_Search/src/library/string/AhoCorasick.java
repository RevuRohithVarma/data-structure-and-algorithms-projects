package library.string;
public class AhoCorasick { public boolean containsAny(String text,String[] patterns){for(int i=0;i<patterns.length;i++)if(patterns[i].length()>0&&text.toLowerCase().contains(patterns[i].toLowerCase()))return true;return false;} }
