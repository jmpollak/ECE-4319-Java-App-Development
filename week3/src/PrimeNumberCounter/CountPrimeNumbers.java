package PrimeNumberCounter;

public class CountPrimeNumbers
{
    // O(n^2)
    public int countPrimes(int n) {
        int count = 0;
        if (n == 0 || n == 1) {
            return 0;
        }
        count++; // for n == 2 because we always miss the prime 2
        System.out.println(2);
        for (int i = 2; i < n; i++) {
            int j;
            for (j = 2; j < i; j++) {
                if (i % j == 0) {
                    break; //breaks out of the inner loop
                }
                if (j == i - 1) {
                    count++; //found one prime!
                    System.out.println(i);
                }
            }
        }
        return count;
    }
    // O(n)
    public int countPrimesWithArray(int n) {
        int cnt = 0;
        boolean[] notPrime = new boolean[n]; //default false
        for(int i = 2; i<n; i++){
            if(!notPrime[i]) {
                cnt++;
                System.out.println(i);
                for(int j = 2; i*j < n; j++){
                    notPrime[i*j] = true;
                }
            }
        }
        return cnt;
    }
}
