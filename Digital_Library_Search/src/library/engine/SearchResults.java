package library.engine;
import library.model.DigitalResource;
public class SearchResults { private DigitalResource[] a=new DigitalResource[16]; private int n; public void add(DigitalResource x){if(n==a.length){DigitalResource[]b=new DigitalResource[a.length*2];System.arraycopy(a,0,b,0,n);a=b;}a[n++]=x;} public int size(){return n;} public DigitalResource get(int i){return a[i];}}
