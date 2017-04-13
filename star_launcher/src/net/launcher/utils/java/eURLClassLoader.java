/*
 * Decompiled with CFR 0_114.
 */
package net.launcher.utils.java;

import java.io.Closeable;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.SocketPermission;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.URLStreamHandlerFactory;
import java.nio.ByteBuffer;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import net.launcher.utils.java.FactoryURLClassLoader;
import net.launcher.utils.java.eJavaNetAccess;
import net.launcher.utils.java.eSharedSecrets;
import net.launcher.utils.java.eURLClassPath;
import sun.misc.PerfCounter;
import sun.misc.Resource;
import sun.net.www.ParseUtil;
import sun.net.www.protocol.file.FileURLConnection;

public class eURLClassLoader extends URLClassLoader implements Closeable {
	
    private final eURLClassPath ucp;
    private final AccessControlContext acc;
    private WeakHashMap<Closeable, Void> closeables = new WeakHashMap<Closeable, Void>();

    public eURLClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkCreateClassLoader();
        }
        this.ucp = new eURLClassPath(urls);
        this.acc = AccessController.getContext();
    }

    eURLClassLoader(URL[] urls, ClassLoader parent, AccessControlContext acc) {
        super(urls, parent);
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkCreateClassLoader();
        }
        this.ucp = new eURLClassPath(urls);
        this.acc = acc;
    }

    public eURLClassLoader(URL[] urls) {
        super(urls);
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkCreateClassLoader();
        }
        this.ucp = new eURLClassPath(urls);
        this.acc = AccessController.getContext();
    }

    eURLClassLoader(URL[] urls, AccessControlContext acc) {
        super(urls);
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkCreateClassLoader();
        }
        this.ucp = new eURLClassPath(urls);
        this.acc = acc;
    }

    public eURLClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkCreateClassLoader();
        }
        this.ucp = new eURLClassPath(urls, factory);
        this.acc = AccessController.getContext();
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        URL url = this.getResource(name);
        try {
            if (url == null) {
                return null;
            }
            URLConnection urlc = url.openConnection();
            InputStream is = urlc.getInputStream();
            if (urlc instanceof JarURLConnection) {
                JarURLConnection juc = (JarURLConnection)urlc;
                JarFile jar = juc.getJarFile();
                WeakHashMap<Closeable, Void> weakHashMap = this.closeables;
                synchronized (weakHashMap) {
                    if (!this.closeables.containsKey(jar)) {
                        this.closeables.put(jar, null);
                    }
                }
            }
            if (urlc instanceof FileURLConnection) {
                WeakHashMap<Closeable, Void> juc = this.closeables;
                synchronized (juc) {
                    this.closeables.put(is, null);
                }
            }
            return is;
        }
        catch (IOException e) {
            return null;
        }
    }

    @Override
    public void close() throws IOException {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkPermission(new RuntimePermission("closeClassLoader"));
        }
        List<IOException> errors = this.ucp.closeLoaders();
        WeakHashMap<Closeable, Void> weakHashMap = this.closeables;
        synchronized (weakHashMap) {
            Set<Closeable> keys = this.closeables.keySet();
            Iterator<Closeable> iterator = keys.iterator();
            while (iterator.hasNext()) {
                Closeable c = iterator.next();
                try {
                    c.close();
                }
                catch (IOException ioex) {
                    errors.add(ioex);
                }
            }
            this.closeables.clear();
        }
        if (errors.isEmpty()) {
            return;
        }
        IOException firstex = errors.remove(0);
        for (IOException error : errors) {
            firstex.addSuppressed(error);
        }
        throw firstex;
    }

    @Override
    protected void addURL(URL url) {
        this.ucp.addURL(url);
    }

    @Override
    public URL[] getURLs() {
        return this.ucp.getURLs();
    }

    @SuppressWarnings("rawtypes")
	@Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        try {
            return (Class<?>)AccessController.doPrivileged(new PrivilegedExceptionAction<Class>(){

                @Override
                public Class<?> run() throws ClassNotFoundException {
                    String path = name.replace('.', '/').concat(".class");
                    Resource res = eURLClassLoader.this.ucp.getResource(path, false);
                    if (res != null) {
                        try {
                            return eURLClassLoader.this.defineClass(name, res);
                        }
                        catch (IOException e) {
                            throw new ClassNotFoundException(name, e);
                        }
                    }
                    throw new ClassNotFoundException(name);
                }
            }, this.acc);
        }
        catch (PrivilegedActionException pae) {
            throw (ClassNotFoundException)pae.getException();
        }
    }

    private Package getAndVerifyPackage(String pkgname, Manifest man, URL url) {
        Package pkg = this.getPackage(pkgname);
        if (pkg != null) {
            if (pkg.isSealed()) {
                if (!pkg.isSealed(url)) {
                    throw new SecurityException("sealing violation: package " + pkgname + " is sealed");
                }
            } else if (man != null && this.isSealed(pkgname, man)) {
                throw new SecurityException("sealing violation: can't seal package " + pkgname + ": already loaded");
            }
        }
        return pkg;
    }

    private Class<?> defineClass(String name, Resource res) throws IOException {
        ByteBuffer bb;
        URL url;
        long t0;
        block6 : {
            Manifest man;
            String pkgname;
            t0 = System.nanoTime();
            int i = name.lastIndexOf(46);
            url = res.getCodeSourceURL();
            if (i != -1 && this.getAndVerifyPackage(pkgname = name.substring(0, i), man = res.getManifest(), url) == null) {
                try {
                    if (man != null) {
                        this.definePackage(pkgname, man, url);
                    } else {
                        this.definePackage(pkgname, null, null, null, null, null, null, null);
                    }
                }
                catch (IllegalArgumentException iae) {
                    if (this.getAndVerifyPackage(pkgname, man, url) != null) break block6;
                    throw new AssertionError((Object)("Cannot find package " + pkgname));
                }
            }
        }
        if ((bb = res.getByteBuffer()) != null) {
            CodeSigner[] signers = res.getCodeSigners();
            CodeSource cs = new CodeSource(url, signers);
            PerfCounter.getReadClassBytesTime().addElapsedTimeFrom(t0);
            return this.defineClass(name, bb, cs);
        }
        byte[] b = res.getBytes();
        CodeSigner[] signers = res.getCodeSigners();
        CodeSource cs = new CodeSource(url, signers);
        PerfCounter.getReadClassBytesTime().addElapsedTimeFrom(t0);
        return this.defineClass(name, b, 0, b.length, cs);
    }

    @Override
    protected Package definePackage(String name, Manifest man, URL url) throws IllegalArgumentException {
        String path = name.replace('.', '/').concat("/");
        String specTitle = null;
        String specVersion = null;
        String specVendor = null;
        String implTitle = null;
        String implVersion = null;
        String implVendor = null;
        String sealed = null;
        URL sealBase = null;
        Attributes attr = man.getAttributes(path);
        if (attr != null) {
            specTitle = attr.getValue(Attributes.Name.SPECIFICATION_TITLE);
            specVersion = attr.getValue(Attributes.Name.SPECIFICATION_VERSION);
            specVendor = attr.getValue(Attributes.Name.SPECIFICATION_VENDOR);
            implTitle = attr.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
            implVersion = attr.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
            implVendor = attr.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
            sealed = attr.getValue(Attributes.Name.SEALED);
        }
        if ((attr = man.getMainAttributes()) != null) {
            if (specTitle == null) {
                specTitle = attr.getValue(Attributes.Name.SPECIFICATION_TITLE);
            }
            if (specVersion == null) {
                specVersion = attr.getValue(Attributes.Name.SPECIFICATION_VERSION);
            }
            if (specVendor == null) {
                specVendor = attr.getValue(Attributes.Name.SPECIFICATION_VENDOR);
            }
            if (implTitle == null) {
                implTitle = attr.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
            }
            if (implVersion == null) {
                implVersion = attr.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
            }
            if (implVendor == null) {
                implVendor = attr.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
            }
            if (sealed == null) {
                sealed = attr.getValue(Attributes.Name.SEALED);
            }
        }
        if ("true".equalsIgnoreCase(sealed)) {
            sealBase = url;
        }
        return this.definePackage(name, specTitle, specVersion, specVendor, implTitle, implVersion, implVendor, sealBase);
    }

    private boolean isSealed(String name, Manifest man) {
        String path = name.replace('.', '/').concat("/");
        Attributes attr = man.getAttributes(path);
        String sealed = null;
        if (attr != null) {
            sealed = attr.getValue(Attributes.Name.SEALED);
        }
        if (sealed == null && (attr = man.getMainAttributes()) != null) {
            sealed = attr.getValue(Attributes.Name.SEALED);
        }
        return "true".equalsIgnoreCase(sealed);
    }

    @Override
    public URL findResource(final String name) {
        URL url = (URL)AccessController.doPrivileged(new PrivilegedAction<URL>(){

            @Override
            public URL run() {
                return eURLClassLoader.this.ucp.findResource(name, true);
            }
        }, this.acc);
        return url != null ? this.ucp.checkURL(url) : null;
    }

    @Override
    public Enumeration<URL> findResources(String name) throws IOException {
        final Enumeration<URL> e = this.ucp.findResources(name, true);
        return new Enumeration<URL>(){
            private URL url;

            private boolean next() {
                URL u;
                if (this.url != null) {
                    return true;
                }
                while ((u = (URL)AccessController.doPrivileged(new PrivilegedAction<URL>(){

                    @Override
                    public URL run() {
                        if (!e.hasMoreElements()) {
                            return null;
                        }
                        return (URL)e.nextElement();
                    }
                }, eURLClassLoader.this.acc)) != null) {
                    this.url = eURLClassLoader.this.ucp.checkURL(u);
                    if (this.url == null) continue;
                }
                return this.url != null;
            }

            @Override
            public URL nextElement() {
                if (!this.next()) {
                    throw new NoSuchElementException();
                }
                URL u = this.url;
                this.url = null;
                return u;
            }

            @Override
            public boolean hasMoreElements() {
                return this.next();
            }

        };
    }

    @Override
    protected PermissionCollection getPermissions(CodeSource codesource) {
        Permission p;
        String path;
        URLConnection urlConnection;
        PermissionCollection perms = super.getPermissions(codesource);
        URL url = codesource.getLocation();
        try {
            urlConnection = url.openConnection();
            p = urlConnection.getPermission();
        }
        catch (IOException ioe) {
            p = null;
            urlConnection = null;
        }
        if (p instanceof FilePermission) {
            path = p.getName();
            if (path.endsWith(File.separator)) {
                path = path + "-";
                p = new FilePermission(path, "read");
            }
        } else if (p == null && url.getProtocol().equals("file")) {
            path = url.getFile().replace('/', File.separatorChar);
            if ((path = ParseUtil.decode(path)).endsWith(File.separator)) {
                path = path + "-";
            }
            p = new FilePermission(path, "read");
        } else {
            String host;
            URL locUrl = url;
            if (urlConnection instanceof JarURLConnection) {
                locUrl = ((JarURLConnection)urlConnection).getJarFileURL();
            }
            if ((host = locUrl.getHost()) != null && host.length() > 0) {
                p = new SocketPermission(host, "connect,accept");
            }
        }
        if (p != null) {
            final SecurityManager sm = System.getSecurityManager();
            if (sm != null) {
                final Permission fp = p;
                AccessController.doPrivileged(new PrivilegedAction<Void>(){

                    @Override
                    public Void run() throws SecurityException {
                        sm.checkPermission(fp);
                        return null;
                    }
                }, this.acc);
            }
            perms.add(p);
        }
        return perms;
    }

    public static eURLClassLoader newInstance(final URL[] urls, final ClassLoader parent) {
        final AccessControlContext acc = AccessController.getContext();
        eURLClassLoader ucl = (eURLClassLoader)AccessController.doPrivileged(new PrivilegedAction<eURLClassLoader>(){

            @Override
            public eURLClassLoader run() {
                return new FactoryURLClassLoader(urls, parent, acc);
            }
        });
        return ucl;
    }

    public static eURLClassLoader newInstance(final URL[] urls) {
        final AccessControlContext acc = AccessController.getContext();
        eURLClassLoader ucl = (eURLClassLoader)AccessController.doPrivileged(new PrivilegedAction<eURLClassLoader>(){

            @Override
            public eURLClassLoader run() {
                return new FactoryURLClassLoader(urls, acc);
            }
        });
        return ucl;
    }

    static {
        eSharedSecrets.seteJavaNetAccess(new eJavaNetAccess(){

            @Override
            public eURLClassPath geteURLClassPath(eURLClassLoader u) {
                return u.ucp;
            }
        });
        ClassLoader.registerAsParallelCapable();
    }

}

