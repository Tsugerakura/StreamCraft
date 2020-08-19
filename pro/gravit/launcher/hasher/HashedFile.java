/*    */ package pro.gravit.launcher.hasher;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.file.Path;
/*    */ import java.util.Arrays;
/*    */ import pro.gravit.launcher.LauncherAPI;
/*    */ import pro.gravit.launcher.LauncherNetworkAPI;
/*    */ import pro.gravit.launcher.serialize.HInput;
/*    */ import pro.gravit.launcher.serialize.HOutput;
/*    */ import pro.gravit.utils.helper.IOHelper;
/*    */ import pro.gravit.utils.helper.SecurityHelper;
/*    */ import pro.gravit.utils.helper.VerifyHelper;
/*    */ 
/*    */ public final class HashedFile
/*    */   extends HashedEntry
/*    */ {
/* 17 */   public static final SecurityHelper.DigestAlgorithm DIGEST_ALGO = SecurityHelper.DigestAlgorithm.MD5;
/*    */   
/*    */   @LauncherAPI
/*    */   public final long size;
/*    */   
/*    */   @LauncherNetworkAPI
/*    */   private final byte[] digest;
/*    */   
/*    */   @LauncherAPI
/*    */   public HashedFile(HInput input) throws IOException {
/* 27 */     this(input.readVarLong(), input.readBoolean() ? input.readByteArray(-DIGEST_ALGO.bytes) : null);
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public HashedFile(long size, byte[] digest) {
/* 32 */     this.size = VerifyHelper.verifyLong(size, VerifyHelper.L_NOT_NEGATIVE, "Illegal size: " + size);
/* 33 */     this.digest = (digest == null) ? null : (byte[])DIGEST_ALGO.verify(digest).clone();
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public HashedFile(Path file, long size, boolean digest) throws IOException {
/* 38 */     this(size, digest ? SecurityHelper.digest(DIGEST_ALGO, file) : null);
/*    */   }
/*    */ 
/*    */   
/*    */   public HashedEntry.Type getType() {
/* 43 */     return HashedEntry.Type.FILE;
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public boolean isSame(HashedFile o) {
/* 48 */     return (this.size == o.size && (this.digest == null || o.digest == null || Arrays.equals(this.digest, o.digest)));
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public boolean isSame(Path file, boolean digest) throws IOException {
/* 53 */     if (this.size != IOHelper.readAttributes(file).size())
/* 54 */       return false; 
/* 55 */     if (!digest || this.digest == null) {
/* 56 */       return true;
/*    */     }
/*    */     
/* 59 */     byte[] actualDigest = SecurityHelper.digest(DIGEST_ALGO, file);
/* 60 */     return Arrays.equals(this.digest, actualDigest);
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public boolean isSameDigest(byte[] digest) {
/* 65 */     return (this.digest == null || digest == null || Arrays.equals(this.digest, digest));
/*    */   }
/*    */ 
/*    */   
/*    */   public long size() {
/* 70 */     return this.size;
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(HOutput output) throws IOException {
/* 75 */     output.writeVarLong(this.size);
/* 76 */     output.writeBoolean((this.digest != null));
/* 77 */     if (this.digest != null)
/* 78 */       output.writeByteArray(this.digest, -DIGEST_ALGO.bytes); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\hasher\HashedFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */