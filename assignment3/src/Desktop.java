 class Desktop {
    public static void openWindow(Window w, int[][] mat){
        int r1 = w.getR1();
        int r2 = w.getR2();
        int c1 = w.getC1();
        int c2 = w.getC2();
        int id = w.getId();

        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[0].length; j++) {
                if(j>=c1 &&j<=c2&&i>=r1&&i<=r2){
                    mat[i][j]= id;

                }

            }

        }
    }
    public static void click(int r, int c, Window[] wins, int[][] mat){
        int id = mat[r][c];
        if(id==0){
            return;
        }

        for (int i = 0; i < wins.length; i++) {
            if (wins[i] == null) continue;
            if(id==wins[i].getId()){
                openWindow(wins[i], mat);
                break;
            }

        }
    }
     public static void printScreen(int[][] mat){
         for (int i = 0; i < mat.length; i++) {
             for (int j = 0; j < mat[0].length; j++) {
                 if(j != mat[i].length-1){
                     System.out.print(mat[i][j]+" ");
                 }
             }
             System.out.println();
         }
     }
}
