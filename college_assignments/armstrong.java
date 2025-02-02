public class armstrong {
    static boolean isArmstrong(String num){
        int[] dp = {0, 1, -1, -1, -1, -1, -1, -1, -1, -1};
        int sum = 0, num_int = Integer.parseInt(num), cur;
        for(char x : num.toCharArray()){
            cur = (int)x - '0';
            if(dp[cur] == -1) dp[cur] = (int)Math.pow(cur, num.length());
            sum += dp[cur];
            if(sum > num_int) return false;
        }
        if(sum == num_int) return true;
        return false;
    }
    public static void main(String args[]){
        String str = "371";
        if(isArmstrong(str)) System.out.println("True");
        else System.out.println("False");
    }
}
