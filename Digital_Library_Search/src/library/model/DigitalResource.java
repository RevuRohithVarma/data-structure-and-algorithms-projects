package library.model;
public class DigitalResource {
    public final String type,title,author,id,category,keywords,content,year,language;
    public DigitalResource(String type,String title,String author,String id,String category,String keywords,String content,String year,String language){this.type=type;this.title=title;this.author=author;this.id=id;this.category=category;this.keywords=keywords;this.content=content;this.year=year;this.language=language;}
    public String searchable(){return title+" "+author+" "+category+" "+keywords+" "+content;}
    public String toString(){return title+" — "+author+" ["+type+"]";}
}
