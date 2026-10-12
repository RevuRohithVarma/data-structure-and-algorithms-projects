package library.string;
public class LCPArray { public int[] build(String s,int[] sa){int n=sa.length;int[] rank=new int[n],lcp=new int[n];for(int i=0;i<n;i++)rank[sa[i]]=i;for(int i=0,k=0;i<n;i++){int r=rank[i];if(r==n-1){k=0;continue;}int j=sa[r+1];while(i+k<n&&j+k<n&&s.charAt(i+k)==s.charAt(j+k))k++;lcp[r]=k;if(k>0)k--;}return lcp;} }
