package library.parallel; public class ParallelPrimitives {public long reduce(int[]a){long s=0;for(int x:a)s+=x;return s;}public void prefixSum(int[]a){for(int i=1;i<a.length;i++)a[i]+=a[i-1];}}
