 class Window {
    private int r1;
    private int r2;
    private int c1;
    private int c2;
    private int id;
    public Window(int r1,int c1,int r2,int c2,int id){
        this.r1=r1;
        this.r2=r2;
        this.c1=c1;
        this.c2=c2;
        this.id=id;
    }
     public int getR1(){
        if(r1>0){
            return r1;
        }
     }
     public int getR2(){
         if(r1>0){
             return r2;
         }
     }
     public int getC1(){
         if(r1>0){
             return c1;
         }
     }
     public int getC2(){
         if(r1>0){
             return c2;
         }
     }

     public int getId() {
         return id;
     }
 }
