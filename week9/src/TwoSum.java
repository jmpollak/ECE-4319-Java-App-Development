import java.util.Arrays;
import java.util.HashMap;

public class TwoSum
{
    public int[] nums = {2,7,11,15};
    public int target = 9;

    public static void main(String[] args)
    {
        TwoSum twoSum = new TwoSum();
        int[] ans1 = twoSum.solutionBruteForce(twoSum.nums, twoSum.target);
        int[] ans2 = twoSum.solution(twoSum.nums, twoSum.target);

        System.out.println("Answer is index: " + Arrays.toString(ans1));
        System.out.println("Answer is index: " + Arrays.toString(ans2));

    }

    // O(n^2)
    public int[] solutionBruteForce(int[] num, int target)
    {
        // Searches through the entire array and checking if the add up to the target one at a time
        for(int i = 0; i <= num.length; i++)
        {
            for(int j = i + 1; j < num.length; j++)
            {
                if((num[i] + num[j]) == target)
                {
                    return new int[] {i, j};
                }
            }
        }
        // If there is no found solution
        return new int[] {};
    }

    // O(n) but it does not return the index
    public int[] solution(int[] num, int target)
    {
        // Uses Memory to check if there is a previous number we have seen to reach our target
        HashMap<Integer, Integer> map = new HashMap<>();
        for(int i = 0; i < nums.length; i++)
        {
            if(map.containsKey(target - nums[i]))
            {
                return new int[] {map.get(target - nums[i]), i};
            }
            map.put(nums[i], i);
        }
        return new int[] {};
    }
}