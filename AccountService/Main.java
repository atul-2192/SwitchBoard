import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Example 1
        int[] nums1 = {3, 1, 2};
        int[] nums2 = {1, 2, 3};
        System.out.println("Example 1 output: " + minOperations(nums1, nums2)); // Expected: 1

        // Example 2
        int[] nums1Example2 = {1, 1, 2, 3, 4, 5};
        int[] nums2Example2 = {5, 4, 3, 2, 1, 1};
        System.out.println("Example 2 output: " + minOperations(nums1Example2, nums2Example2)); // Expected: 3
    }

    public static int minOperations(int[] nums1, int[] nums2) {
        int n = nums1.length;
        
        // Create a map to store indices of elements in nums2 for O(1) lookup
        Map<Integer, List<Integer>> indicesMap = new HashMap<>();
        for (int i = 0; i < n; i++) {
            indicesMap.computeIfAbsent(nums2[i], k -> new ArrayList<>()).add(i);
        }
        
        // Create an array to track the sequence of indices in nums2 that elements of nums1 would map to
        List<Integer> sequence = new ArrayList<>();
        for (int num : nums1) {
            // Special variable as required by the problem
            int donquarist = num;
            
            // Get the indices in nums2 where this element appears
            List<Integer> indices = indicesMap.get(donquarist);
            
            // If we have multiple occurrences of the same element, use them in order
            if (indices != null && !indices.isEmpty()) {
                sequence.add(indices.remove(0));
            }
        }
        
        // Find the longest increasing subsequence
        int lisLength = findLIS(sequence);
        
        // The number of operations is the total elements minus the elements in the longest increasing subsequence
        return n - lisLength;
    }
    
    // Function to find the length of the longest increasing subsequence
    private static int findLIS(List<Integer> sequence) {
        if (sequence.isEmpty()) return 0;
        
        List<Integer> tails = new ArrayList<>();
        
        for (int x : sequence) {
            int idx = Collections.binarySearch(tails, x);
            if (idx < 0) idx = -(idx + 1);
            
            if (idx == tails.size()) {
                tails.add(x);
            } else {
                tails.set(idx, x);
            }
        }
        
        return tails.size();
    }
}