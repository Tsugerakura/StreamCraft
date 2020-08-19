/*    */ package pro.gravit.launcher;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.StringJoiner;
/*    */ import pro.gravit.launcher.serialize.HInput;
/*    */ import pro.gravit.launcher.serialize.HOutput;
/*    */ 
/*    */ public class ClientPermissions
/*    */ {
/* 10 */   public static final ClientPermissions DEFAULT = new ClientPermissions();
/*    */   @LauncherAPI
/*    */   public boolean canAdmin;
/*    */   @LauncherAPI
/*    */   public boolean canServer;
/*    */   @LauncherAPI
/*    */   public boolean canUSR1;
/*    */   @LauncherAPI
/*    */   public boolean canUSR2;
/*    */   @LauncherAPI
/*    */   public boolean canUSR3;
/*    */   @LauncherAPI
/*    */   public boolean canBot;
/*    */   
/*    */   public ClientPermissions(HInput input) throws IOException {
/* 25 */     this(input.readLong());
/*    */   }
/*    */   
/*    */   public ClientPermissions() {
/* 29 */     this.canAdmin = false;
/* 30 */     this.canServer = false;
/* 31 */     this.canUSR1 = false;
/* 32 */     this.canUSR2 = false;
/* 33 */     this.canUSR3 = false;
/* 34 */     this.canBot = false;
/*    */   }
/*    */   
/*    */   public ClientPermissions(long data) {
/* 38 */     this.canAdmin = ((data & 0x1L) != 0L);
/* 39 */     this.canServer = ((data & 0x2L) != 0L);
/* 40 */     this.canUSR1 = ((data & 0x4L) != 0L);
/* 41 */     this.canUSR2 = ((data & 0x8L) != 0L);
/* 42 */     this.canUSR3 = ((data & 0x10L) != 0L);
/* 43 */     this.canBot = ((data & 0x20L) != 0L);
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public long toLong() {
/* 48 */     long result = 0L;
/* 49 */     result |= !this.canAdmin ? 0L : 1L;
/* 50 */     result |= !this.canServer ? 0L : 2L;
/* 51 */     result |= !this.canUSR1 ? 0L : 4L;
/* 52 */     result |= !this.canUSR2 ? 0L : 8L;
/* 53 */     result |= !this.canUSR3 ? 0L : 16L;
/* 54 */     result |= !this.canBot ? 0L : 32L;
/* 55 */     return result;
/*    */   }
/*    */   
/*    */   public static ClientPermissions getSuperuserAccount() {
/* 59 */     ClientPermissions perm = new ClientPermissions();
/* 60 */     perm.canServer = true;
/* 61 */     perm.canAdmin = true;
/* 62 */     return perm;
/*    */   }
/*    */   
/*    */   public void write(HOutput output) throws IOException {
/* 66 */     output.writeLong(toLong());
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 71 */     return (new StringJoiner(", ", ClientPermissions.class.getSimpleName() + "[", "]"))
/* 72 */       .add("canAdmin=" + this.canAdmin)
/* 73 */       .add("canServer=" + this.canServer)
/* 74 */       .add("canUSR1=" + this.canUSR1)
/* 75 */       .add("canUSR2=" + this.canUSR2)
/* 76 */       .add("canUSR3=" + this.canUSR3)
/* 77 */       .add("canBot=" + this.canBot)
/* 78 */       .toString();
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\ClientPermissions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */