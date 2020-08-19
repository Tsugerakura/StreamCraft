/*    */ package pro.gravit.launcher.client;
/*    */ 
/*    */ import java.nio.file.Path;
/*    */ import java.util.LinkedList;
/*    */ import java.util.List;
/*    */ import pro.gravit.launcher.profiles.ClientProfile;
/*    */ import pro.gravit.launcher.profiles.PlayerProfile;
/*    */ 
/*    */ public class ClientLauncherContext
/*    */ {
/*    */   public Path javaBin;
/* 12 */   public List<String> args = new LinkedList<>();
/*    */   public String pathLauncher;
/*    */   public ProcessBuilder builder;
/*    */   public ClientProfile clientProfile;
/*    */   public PlayerProfile playerProfile;
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\client\ClientLauncherContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */