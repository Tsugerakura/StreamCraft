/*    */ package pro.gravit.launcher.profiles;
/*    */ 
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.net.URL;
/*    */ import java.util.Objects;
/*    */ import pro.gravit.launcher.LauncherAPI;
/*    */ import pro.gravit.launcher.serialize.HInput;
/*    */ import pro.gravit.launcher.serialize.HOutput;
/*    */ import pro.gravit.launcher.serialize.stream.StreamObject;
/*    */ import pro.gravit.utils.helper.IOHelper;
/*    */ import pro.gravit.utils.helper.SecurityHelper;
/*    */ 
/*    */ public final class Texture
/*    */   extends StreamObject {
/* 17 */   private static final SecurityHelper.DigestAlgorithm DIGEST_ALGO = SecurityHelper.DigestAlgorithm.SHA256;
/*    */   
/*    */   @LauncherAPI
/*    */   public final String url;
/*    */   
/*    */   @LauncherAPI
/*    */   public final byte[] digest;
/*    */   
/*    */   @LauncherAPI
/*    */   public Texture(HInput input) throws IOException {
/* 27 */     this.url = IOHelper.verifyURL(input.readASCII(2048));
/* 28 */     this.digest = input.readByteArray(-DIGEST_ALGO.bytes);
/*    */   }
/*    */   @LauncherAPI
/*    */   public Texture(String url, boolean cloak) throws IOException {
/*    */     byte[] texture;
/* 33 */     this.url = IOHelper.verifyURL(url);
/*    */ 
/*    */ 
/*    */     
/* 37 */     try (InputStream input = IOHelper.newInput(new URL(url))) {
/* 38 */       texture = IOHelper.read(inputStream);
/*    */     } 
/* 40 */     try (ByteArrayInputStream input = new ByteArrayInputStream(texture)) {
/* 41 */       IOHelper.readTexture(input, cloak);
/*    */     } 
/*    */ 
/*    */     
/* 45 */     this.digest = SecurityHelper.digest(DIGEST_ALGO, new URL(url));
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public Texture(String url, byte[] digest) {
/* 50 */     this.url = IOHelper.verifyURL(url);
/* 51 */     this.digest = Objects.<byte[]>requireNonNull(digest, "digest");
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(HOutput output) throws IOException {
/* 56 */     output.writeASCII(this.url, 2048);
/* 57 */     output.writeByteArray(this.digest, -DIGEST_ALGO.bytes);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\profiles\Texture.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */