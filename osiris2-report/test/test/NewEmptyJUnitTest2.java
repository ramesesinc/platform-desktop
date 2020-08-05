/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.rameses.util.Base64Cipher;
import com.rameses.util.URLStreamHandler;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import org.junit.Test;

/**
 *
 * @author ramesesinc
 */
public class NewEmptyJUnitTest2 {
    
    public NewEmptyJUnitTest2() {
    }

    @Test
    public void test1() throws Exception {
        String encstr = "iVBORw0KGgoAAAANSUhEUgAAAH4AAAAsCAMAAACUu/xGAAAAq1BMVEUAAABlZVJlZVKsrJthYU+zs6Grq5ylpZazs6FlZVJfX01lZVJlZVKsrJurq5urq5xlZVKtrZ1lZVJlZVKvr52zs6GysqCoqJeqqpmzs6Grq5xlZVJgYE6zs6Gnp5mrq5yiopRjY1CRkX2rq5yzs6FlZVKRkX2goJKKineRkX2Pj3yrq5yIiHWRkX2RkX2RkX1lZVKRkX2rq5yzs6GoqJdfX02goJKHh3SHh3VrpzVsAAAAMHRSTlMAQIDHx3+Ax0Ag7qBgIA9AEFCPMLOgMO7bYKBQ24+zYNuzkY9wcAXu0oiocPFBMHYlVbK0AAAD3UlEQVRYw6SW7Y6qMBCGB0IkLfKdnB9ocFmjru7HERL03P+VnXY6bdmWjcF9f2inxjydvjMDcHy99zP693oEpTpQYjBR7W4VmzA81GoZCDn/ycrValVmYOJcKBWL1/4HnUEpupLGxOI47iQmDkfc4GEBEFyNQkClzYDKQQs3VmJBufu6G7zRWNMeUzEHUnLVWs/gy9vg4NNB4wUIPOG2h7e8NcV0HRt7QPDxfzTd4ptleB5F6ro3NtsIc7UnjMKKXyuN30ZS+PuLRMW7PN+l2vlhAZ6yqCZmcrm05stfOrwVpvEBaJWStIOpVk/gC8Rb62tjRj25Fx/fEsgqE27cluKB8GR9hDFzeX44CFbmJb9/Cn8w1ldA5tO9VD/gc8FpveTbxfi1LXWOl10Z80c0Yx7/jpyyjRtd9zuxU8ZL8FEYJjZFpg6yIfOpKsf1FJ+EUkzddKkabQ+o0zCcwMN/vZm+uLh4UmW7nptTCBVq5nUF4Y0CgBaNVip18jsPn370909cfX708/gusF3fkQfrKZHXHh45Wi8meRefvfVCfwGOZ9zx8TZ9TjWY2M6vVf4jm8e3WYrDJ1Vj4N3FHwVd6vKFCxefBMFmq7ub6UI7TMZw0SEv8ryPDVaoxPiWufhL/02zY0cm3ZH1VgxIIYa1U/nIibH/EZjjp4M/9w/x9FijbyuqdzOVH+BbWQJxHMupd4pjINhDPKVH1lslBl9g6OKb73j0wmoBHrMj691nsJ0QLn4l0/09nrIm6wv7nGdQqwjGucvPJSWjN4z8aXyBlkfK+i2gmDI/HENGjXA9uPhsUJ22p2OQFg3daaFx0/9qnWBRbOl9hHlvOw3OW/xs4Hf4rcnYzj+OeFOIHj4dtG7/2y+b3IhBGAqjUiQWQ9JI/ErDpop6gcei9z9ZIXHIhLaLSGRW8zYxIuaTZccxqsGfHDXvH4cf37Z4e3ihxVOTp5bf4E8N2u+3PWB2SP7tXsfsFl80rtOeZX/gvz6//7tmnFFzD2mkxnFgL710ToHH1eCcm/LU2aA9m027v+kBH8ipyHbACxAMWaV5I4v2ZgAzIxkUGXIqkn3xrhw4wVe8hoMmOwBmYJMiJy+lHPriNcSyrvgEgUS2h/vl1BcvSqgcZsPbbABrhgdgvhgvS6hIYsPP8MwTVR5SLZA4573xHMpCV7xGZBFmxyProfR64yNCgKh4hygjXIuvpdcbPyEayA2vsEpRHcgl6gtzr8A9ho0RlgQnBPoK4tV45gBfGQZ6KQBDqzRcjdeAqQwHUfYp+SohcQdc1/Ukm4Gw4dV6vqTkM+uQpRv8E2VPF/sPp9xSb2qlGH4AAAAASUVORK5CYII="; 
        Base64Cipher base64 = new Base64Cipher(); 
        //boolean b = base64.isEncoded(encstr);
        //System.out.println("isEncoded - > "+ b);
        Object obj = base64.decode( encstr, false ); 
        System.out.println("obj class -> " + obj.getClass());
        System.out.println("obj value -> "+ obj);
        System.out.println("");
        
        if ( obj instanceof String ) {
            obj = base64.decode( obj.toString() ); 
            System.out.println("obj class -> " + obj.getClass());
            System.out.println("obj value -> "+ obj);
            System.out.println("");
        }
        //obj = org.apache.commons.net.util.Base64.decodeBase64(encstr); 
        //System.out.println("obj class -> " + obj.getClass());
        //System.out.println("obj value -> "+ obj);
        
        byte[] bytes = (byte[]) obj;
        JOptionPane.showMessageDialog(null, new ImageIcon( bytes ));
        
        //BufferedImage bi = ImageIO.read( new ByteArrayInputStream( bytes ));
        
        //URL url = new URL( null, "bytes:///", new BytesHandler( bytes )); 
        //JOptionPane.showMessageDialog(null, new ImageIcon( url ));
        //JOptionPane.showMessageDialog(null, new ImageIcon((byte[]) obj ));
        
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        ImageIO.write(bi, "PNG", baos);
//        bytes = baos.toByteArray();
//        baos.close(); 
        
        
    }
    
    
    private class BytesHandler extends URLStreamHandler {

        private byte[] bytes; 
        
        public BytesHandler( byte[] bytes ) {
            this.bytes = bytes; 
        }
        
        protected URLConnection openConnection(URL u) throws IOException {
            BytesConnection bc = new BytesConnection(u); 
            bc.handler = this; 
            return bc; 
        }

        public String getProtocol() {
            return "bytes";
        }

        @Override
        public URL getResource(String spath) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    
    private class BytesConnection extends URLConnection {
        
        private BytesHandler handler;
        
        public BytesConnection(URL url) {
            super(url);
        }

        public void connect() throws IOException {
        }

        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream( handler.bytes ); 
        }
    }
}
