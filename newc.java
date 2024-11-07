// package JAVA;

class Solution {
    public int trap(int[] height) {
        int start_level = 0, end_level = 0, start_count = 0, end_count = 0, total = 0;
        boolean start_trap = false, end_trap = false;
        for (int i = 0; i < height.length; i++) {

            if (height[i] < start_level) {
                start_trap = true;
                start_count += start_level - height[i];
            } else {
                start_trap = false;
                start_level = height[i];
            }

            if (height[height.length - 1 - i] < end_level) {
                end_trap = true;
                end_count += end_level - height[height.length - 1 - i];
            } else {
                end_trap = false;
                end_level = height[height.length - 1 - i];
            }

            if (!start_trap) {
                total += start_count;
                start_count = 0;
                System.out.println("start "+total);
            }
            if (!end_trap) {
                if (!start_trap && height[i] == height[height.length - 1 - i])
                    continue;
                total += end_count;
                end_count = 0;
                System.out.println("end "+total);
            }
        }
        return total;
    }

}

class newclass{

    public static void main(String args[]) {
        int[] arr = { 4, 2, 0, 3, 2, 4, 3, 4 };
        Solution obj = new Solution();
        obj.trap(arr);
    }
}