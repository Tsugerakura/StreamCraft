/*    */ package pro.gravit.launcher.serialize.signed;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.security.SignatureException;
/*    */ import java.util.Arrays;
/*    */ import pro.gravit.launcher.LauncherAPI;
/*    */ import pro.gravit.launcher.serialize.HInput;
/*    */ import pro.gravit.launcher.serialize.HOutput;
/*    */ import pro.gravit.launcher.serialize.stream.StreamObject;
/*    */ import pro.gravit.utils.helper.SecurityHelper;
/*    */ 
/*    */ public class DigestBytesHolder
/*    */   extends StreamObject {
/*    */   protected final byte[] bytes;
/*    */   private final byte[] digest;
/*    */   
/*    */   @LauncherAPI
/*    */   public DigestBytesHolder(byte[] bytes, byte[] digest, SecurityHelper.DigestAlgorithm algorithm) throws SignatureException {
/* 19 */     if (Arrays.equals(SecurityHelper.digest(algorithm, bytes), digest))
/* 20 */       throw new SignatureException("Invalid digest"); 
/* 21 */     this.bytes = (byte[])bytes.clone();
/* 22 */     this.digest = (byte[])digest.clone();
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public DigestBytesHolder(byte[] bytes, SecurityHelper.DigestAlgorithm algorithm) {
/* 27 */     this.bytes = (byte[])bytes.clone();
/* 28 */     this.digest = SecurityHelper.digest(algorithm, bytes);
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public DigestBytesHolder(HInput input, SecurityHelper.DigestAlgorithm algorithm) throws IOException, SignatureException {
/* 33 */     this(input.readByteArray(0), input.readByteArray(-256), algorithm);
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public final byte[] getBytes() {
/* 38 */     return (byte[])this.bytes.clone();
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public final byte[] getDigest() {
/* 43 */     return (byte[])this.digest.clone();
/*    */   }
/*    */ 
/*    */   
/*    */   public final void write(HOutput output) throws IOException {
/* 48 */     output.writeByteArray(this.bytes, 0);
/* 49 */     output.writeByteArray(this.digest, -256);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\serialize\signed\DigestBytesHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */