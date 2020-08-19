/*    */ package pro.gravit.repackage.io.netty.handler.ssl;
/*    */ 
/*    */ import pro.gravit.repackage.io.netty.internal.tcnative.SessionTicketKey;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class OpenSslSessionTicketKey
/*    */ {
/*    */   public static final int NAME_SIZE = 16;
/*    */   public static final int HMAC_KEY_SIZE = 16;
/*    */   public static final int AES_KEY_SIZE = 16;
/*    */   public static final int TICKET_KEY_SIZE = 48;
/*    */   final SessionTicketKey key;
/*    */   
/*    */   public OpenSslSessionTicketKey(byte[] name, byte[] hmacKey, byte[] aesKey) {
/* 52 */     this.key = new SessionTicketKey((byte[])name.clone(), (byte[])hmacKey.clone(), (byte[])aesKey.clone());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public byte[] name() {
/* 60 */     return (byte[])this.key.getName().clone();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public byte[] hmacKey() {
/* 68 */     return (byte[])this.key.getHmacKey().clone();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public byte[] aesKey() {
/* 76 */     return (byte[])this.key.getAesKey().clone();
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\OpenSslSessionTicketKey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */