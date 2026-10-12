package library.engine;
public class IntList { private int[] a=new int[8]; private int n; public void add(int x){if(n==a.length){int[] b=new int[a.length*2];System.arraycopy(a,0,b,0,n);a=b;}a[n++]=x;} public int size(){return n;} public int get(int i){return a[i];} }
