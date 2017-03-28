/*
 * Decompiled with CFR 0_114.
 */
package net.launcher.utils.java;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
//import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.SocketPermission;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.CodeSigner;
import java.security.Permission;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

//import net.launcher.utils.java.eURLClassPath.Loader;
import sun.misc.ExtensionDependency;
import sun.misc.FileURLMapper;
import sun.misc.InvalidJarIndexException;
import sun.misc.JarIndex;
import sun.misc.MetaIndex;
import sun.misc.Resource;
import sun.misc.SharedSecrets;
import sun.net.util.URLUtil;
import sun.net.www.ParseUtil;
import sun.security.action.GetPropertyAction;

public class eURLClassPath {
    static final String USER_AGENT_JAVA_VERSION = "UA-Java-Version";
    static final String JAVA_VERSION = (String)AccessController.doPrivileged(new GetPropertyAction("java.version"));
    private static final boolean DEBUG = AccessController.doPrivileged(new GetPropertyAction("net.launcher.utils.java.eURLClassPath.debug")) != null;
    private ArrayList<URL> path = new ArrayList<URL>();
    Stack<URL> urls = new Stack<URL>();
    ArrayList<Loader> loaders = new ArrayList<Loader>();
    HashMap<String, Loader> lmap = new HashMap<String, Loader>();
    private URLStreamHandler jarHandler;
    private boolean closed = false;

    public eURLClassPath(URL[] urls, URLStreamHandlerFactory factory) {
        for (int i = 0; i < urls.length; ++i) {
            this.path.add(urls[i]);
        }
        this.push(urls);
        if (factory != null) {
            this.jarHandler = factory.createURLStreamHandler("jar");
        }
    }

    public eURLClassPath(URL[] urls) {
        this(urls, null);
    }

    public synchronized List<IOException> closeLoaders() {
        if (this.closed) {
            return Collections.emptyList();
        }
        LinkedList<IOException> result = new LinkedList<IOException>();
        for (Loader loader : this.loaders) {
            try {
                loader.close();
            }
            catch (IOException e) {
                result.add(e);
            }
        }
        this.closed = true;
        return result;
    }

    public synchronized void addURL(URL url) {
        if (this.closed) {
            return;
        }
        Stack<URL> stack = this.urls;
        synchronized (stack) {
            if (url == null || this.path.contains(url)) {
                return;
            }
            this.urls.add(0, url);
            this.path.add(url);
        }
    }

    public URL[] getURLs() {
        Stack<URL> stack = this.urls;
        synchronized (stack) {
            return this.path.toArray(new URL[this.path.size()]);
        }
    }

    public URL findResource(String name, boolean check) {
        Loader loader;
        int i = 0;
        while ((loader = this.getLoader(i)) != null) {
            URL url = loader.findResource(name, check);
            if (url != null) {
                return url;
            }
            ++i;
        }
        return null;
    }

    public Resource getResource(String name, boolean check) {
        Loader loader;
        if (DEBUG) {
            System.err.println("eURLClassPath.getResource(\"" + name + "\")");
        }
        int i = 0;
        while ((loader = this.getLoader(i)) != null) {
            Resource res = loader.getResource(name, check);
            if (res != null) {
                return res;
            }
            ++i;
        }
        return null;
    }

    public Enumeration<URL> findResources(final String name, final boolean check) {
        return new Enumeration<URL>(){
            private int index;
            private URL url;

            private boolean next() {
                Loader loader;
                if (this.url != null) {
                    return true;
                }
                while ((loader = eURLClassPath.this.getLoader(this.index++)) != null) {
                    this.url = loader.findResource(name, check);
                    if (this.url == null) continue;
                    return true;
                }
                return false;
            }

            @Override
            public boolean hasMoreElements() {
                return this.next();
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
        };
    }

    public Resource getResource(String name) {
        return this.getResource(name, true);
    }

    public Enumeration<Resource> getResources(final String name, final boolean check) {
        return new Enumeration<Resource>(){
            private int index;
            private Resource res;

            private boolean next() {
                Loader loader;
                if (this.res != null) {
                    return true;
                }
                while ((loader = eURLClassPath.this.getLoader(this.index++)) != null) {
                    this.res = loader.getResource(name, check);
                    if (this.res == null) continue;
                    return true;
                }
                return false;
            }

            @Override
            public boolean hasMoreElements() {
                return this.next();
            }

            @Override
            public Resource nextElement() {
                if (!this.next()) {
                    throw new NoSuchElementException();
                }
                Resource r = this.res;
                this.res = null;
                return r;
            }
        };
    }

    public Enumeration<Resource> getResources(String name) {
        return this.getResources(name, true);
    }

    private synchronized Loader getLoader(int index) {
        if (this.closed) {
            return null;
        }
        while (this.loaders.size() < index + 1) {
            String urlNoFragString;
            Loader loader;
            block8 : {
                URL url;
                Stack<URL> stack = this.urls;
                synchronized (stack) {
                    if (this.urls.empty()) {
                        return null;
                    }
                    url = this.urls.pop();
                }
                urlNoFragString = URLUtil.urlNoFragString(url);
                if (this.lmap.containsKey(urlNoFragString)) continue;
                try {
                    loader = this.getLoader(url);
                    URL[] urls = loader.getClassPath();
                    if (urls == null) break block8;
                    this.push(urls);
                }
                catch (IOException e) {
                    continue;
                }
            }
            this.loaders.add(loader);
            this.lmap.put(urlNoFragString, loader);
        }
        return this.loaders.get(index);
    }

    private Loader getLoader(final URL url) throws IOException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<Loader>(){

                @Override
                public Loader run() throws IOException {
                    String file = url.getFile();
                    if (file != null && file.endsWith("/")) {
                        if ("file".equals(url.getProtocol())) {
                            return new FileLoader(url);
                        }
                        return new Loader(url);
                    }
                    return new JarLoader(url, eURLClassPath.this.jarHandler, eURLClassPath.this.lmap);
                }
            });
        }
        catch (PrivilegedActionException pae) {
            throw (IOException)pae.getException();
        }
    }

    private void push(URL[] us) {
        Stack<URL> stack = this.urls;
        synchronized (stack) {
            for (int i = us.length - 1; i >= 0; --i) {
                this.urls.push(us[i]);
            }
        }
    }

    public static URL[] pathToURLs(String path) {
        StringTokenizer st = new StringTokenizer(path, File.pathSeparator);
        URL[] urls = new URL[st.countTokens()];
        int count = 0;
        while (st.hasMoreTokens()) {
            File f = new File(st.nextToken());
            try {
                f = new File(f.getCanonicalPath());
            }
            catch (IOException x) {
                // empty catch block
            }
            try {
                urls[count++] = ParseUtil.fileToEncodedURL(f);
            }
            catch (IOException x) {}
        }
        if (urls.length != count) {
            URL[] tmp = new URL[count];
            System.arraycopy(urls, 0, tmp, 0, count);
            urls = tmp;
        }
        return urls;
    }

    public URL checkURL(URL url) {
        try {
            eURLClassPath.check(url);
        }
        catch (Exception e) {
            return null;
        }
        return url;
    }

    static void check(URL url) throws IOException {
        Permission perm;
        URLConnection urlConnection;
        SecurityManager security = System.getSecurityManager();
        if (security != null && (perm = (urlConnection = url.openConnection()).getPermission()) != null) {
            try {
                security.checkPermission(perm);
            }
            catch (SecurityException se) {
                if (perm instanceof FilePermission && perm.getActions().indexOf("read") != -1) {
                    security.checkRead(perm.getName());
                }
                if (perm instanceof SocketPermission && perm.getActions().indexOf("connect") != -1) {
                    URL locUrl = url;
                    if (urlConnection instanceof JarURLConnection) {
                        locUrl = ((JarURLConnection)urlConnection).getJarFileURL();
                    }
                    security.checkConnect(locUrl.getHost(), locUrl.getPort());
                }
                throw se;
            }
        }
    }

    private static class FileLoader
    extends Loader {
        private File dir;

        FileLoader(URL url) throws IOException {
            super(url);
            if (!"file".equals(url.getProtocol())) {
                throw new IllegalArgumentException("url");
            }
            String path = url.getFile().replace('/', File.separatorChar);
            path = ParseUtil.decode(path);
            this.dir = new File(path).getCanonicalFile();
        }

        @Override
        URL findResource(String name, boolean check) {
            Resource rsc = this.getResource(name, check);
            if (rsc != null) {
                return rsc.getURL();
            }
            return null;
        }

        @Override
        Resource getResource(final String name, boolean check) {
            try {
                final File file;
                URL normalizedBase = new URL(this.getBaseURL(), ".");
                final URL url = new URL(this.getBaseURL(), ParseUtil.encodePath(name, false));
                if (!url.getFile().startsWith(normalizedBase.getFile())) {
                    return null;
                }
                if (check) {
                    eURLClassPath.check(url);
                }
                if (name.indexOf("..") != -1) {
                    file = new File(this.dir, name.replace('/', File.separatorChar)).getCanonicalFile();
                    if (!file.getPath().startsWith(this.dir.getPath())) {
                        return null;
                    }
                } else {
                    file = new File(this.dir, name.replace('/', File.separatorChar));
                }
                if (file.exists()) {
                    return new Resource(){

                        @Override
                        public String getName() {
                            return name;
                        }

                        @Override
                        public URL getURL() {
                            return url;
                        }

                        @Override
                        public URL getCodeSourceURL() {
                            return FileLoader.this.getBaseURL();
                        }

                        @Override
                        public InputStream getInputStream() throws IOException {
                            return new FileInputStream(file);
                        }

                        @Override
                        public int getContentLength() throws IOException {
                            return (int)file.length();
                        }
                    };
                }
            }
            catch (Exception e) {
                return null;
            }
            return null;
        }

    }

    static class JarLoader
    extends Loader {
        private JarFile jar;
        private URL csu;
        private JarIndex index;
        private MetaIndex metaIndex;
        private URLStreamHandler handler;
        private HashMap<String, Loader> lmap;
        private boolean closed = false;

        JarLoader(URL url, URLStreamHandler jarHandler, HashMap<String, Loader> loaderMap) throws IOException {
            super(new URL("jar", "", -1, url + "!/", jarHandler));
            this.csu = url;
            this.handler = jarHandler;
            this.lmap = loaderMap;
            if (!this.isOptimizable(url)) {
                this.ensureOpen();
            } else {
                String fileName = url.getFile();
                if (fileName != null) {
                    fileName = ParseUtil.decode(fileName);
                    File f = new File(fileName);
                    this.metaIndex = MetaIndex.forJar(f);
                    if (this.metaIndex != null && !f.exists()) {
                        this.metaIndex = null;
                    }
                }
                if (this.metaIndex == null) {
                    this.ensureOpen();
                }
            }
        }

        @Override
        public void close() throws IOException {
            if (!this.closed) {
                this.closed = true;
                this.ensureOpen();
                this.jar.close();
            }
        }

        JarFile getJarFile() {
            return this.jar;
        }

        private boolean isOptimizable(URL url) {
            return "file".equals(url.getProtocol());
        }

        private void ensureOpen() throws IOException {
            if (this.jar == null) {
                try {
                    AccessController.doPrivileged(new PrivilegedExceptionAction<Void>(){

                        @Override
                        public Void run() throws IOException {
                            if (DEBUG) {
                                System.err.println("Opening " + JarLoader.this.csu);
                                Thread.dumpStack();
                            }
                            JarLoader.this.jar = JarLoader.this.getJarFile(JarLoader.this.csu);
                            JarLoader.this.index = JarIndex.getJarIndex(JarLoader.this.jar, JarLoader.this.metaIndex);
                            if (JarLoader.this.index != null) {
                                String[] jarfiles = JarLoader.this.index.getJarFiles();
                                for (int i = 0; i < jarfiles.length; ++i) {
                                    try {
                                        URL jarURL = new URL(JarLoader.this.csu, jarfiles[i]);
                                        String urlNoFragString = URLUtil.urlNoFragString(jarURL);
                                        if (JarLoader.this.lmap.containsKey(urlNoFragString)) continue;
                                        JarLoader.this.lmap.put(urlNoFragString, null);
                                        continue;
                                    }
                                    catch (MalformedURLException e) {
                                        // empty catch block
                                    }
                                }
                            }
                            return null;
                        }
                    });
                }
                catch (PrivilegedActionException pae) {
                    throw (IOException)pae.getException();
                }
            }
        }

        private JarFile getJarFile(URL url) throws IOException {
            if (this.isOptimizable(url)) {
                FileURLMapper p = new FileURLMapper(url);
                if (!p.exists()) {
                    throw new FileNotFoundException(p.getPath());
                }
                return new JarFile(p.getPath());
            }
            URLConnection uc = this.getBaseURL().openConnection();
            uc.setRequestProperty("UA-Java-Version", eURLClassPath.JAVA_VERSION);
            return ((JarURLConnection)uc).getJarFile();
        }

        JarIndex getIndex() {
            try {
                this.ensureOpen();
            }
            catch (IOException e) {
                throw (InternalError)new InternalError().initCause(e);
            }
            return this.index;
        }

        Resource checkResource(final String name, boolean check, final JarEntry entry) {
            final URL url;
            try {
                url = new URL(this.getBaseURL(), ParseUtil.encodePath(name, false));
                if (check) {
                    eURLClassPath.check(url);
                }
            }
            catch (MalformedURLException e) {
                return null;
            }
            catch (IOException e) {
                return null;
            }
            catch (AccessControlException e) {
                return null;
            }
            return new Resource(){

                @Override
                public String getName() {
                    return name;
                }

                @Override
                public URL getURL() {
                    return url;
                }

                @Override
                public URL getCodeSourceURL() {
                    return JarLoader.this.csu;
                }

                @Override
                public InputStream getInputStream() throws IOException {
                    return JarLoader.this.jar.getInputStream(entry);
                }

                @Override
                public int getContentLength() {
                    return (int)entry.getSize();
                }

                @Override
                public Manifest getManifest() throws IOException {
                    return JarLoader.this.jar.getManifest();
                }

                @Override
                public Certificate[] getCertificates() {
                    return entry.getCertificates();
                }

                @Override
                public CodeSigner[] getCodeSigners() {
                    return entry.getCodeSigners();
                }
            };
        }

        boolean validIndex(String name) {
            String packageName = name;
            int pos = name.lastIndexOf("/");
            if (pos != -1) {
                packageName = name.substring(0, pos);
            }
            Enumeration<JarEntry> enum_ = this.jar.entries();
            while (enum_.hasMoreElements()) {
                ZipEntry entry = enum_.nextElement();
                String entryName = entry.getName();
                pos = entryName.lastIndexOf("/");
                if (pos != -1) {
                    entryName = entryName.substring(0, pos);
                }
                if (!entryName.equals(packageName)) continue;
                return true;
            }
            return false;
        }

        @Override
        URL findResource(String name, boolean check) {
            Resource rsc = this.getResource(name, check);
            if (rsc != null) {
                return rsc.getURL();
            }
            return null;
        }

        @Override
        Resource getResource(String name, boolean check) {
            if (this.metaIndex != null && !this.metaIndex.mayContain(name)) {
                return null;
            }
            try {
                this.ensureOpen();
            }
            catch (IOException e) {
                throw (InternalError)new InternalError().initCause(e);
            }
            JarEntry entry = this.jar.getJarEntry(name);
            if (entry != null) {
                return this.checkResource(name, check, entry);
            }
            if (this.index == null) {
                return null;
            }
            HashSet<String> visited = new HashSet<String>();
            return this.getResource(name, check, visited);
        }

//        @SuppressWarnings("unchecked")
		Resource getResource(String name, boolean check, Set<String> visited) {
            int count = 0;
            LinkedList<String> jarFilesList = null;
            jarFilesList = (LinkedList<String>)this.index.get(name);
            if (jarFilesList == null) {
                return null;
            }
            do {
                Object[] jarFiles = jarFilesList.toArray();
                int size = jarFilesList.size();
                while (count < size) {
                    JarLoader newLoader;
                    Resource res;
                    final URL url;
                    boolean visitedURL;
                    block12 : {
                        String jarName = (String)jarFiles[count++];
                        try {
                            url = new URL(this.csu, jarName);
                            String urlNoFragString = URLUtil.urlNoFragString(url);
                            newLoader = (JarLoader)this.lmap.get(urlNoFragString);
                            if (newLoader != null) break block12;
                            newLoader = AccessController.doPrivileged(new PrivilegedExceptionAction<JarLoader>(){

                                @Override
                                public JarLoader run() throws IOException {
                                    return new JarLoader(url, JarLoader.this.handler, JarLoader.this.lmap);
                                }
                            });
                            JarIndex newIndex = newLoader.getIndex();
                            if (newIndex != null) {
                                int pos = jarName.lastIndexOf("/");
                                newIndex.merge(this.index, pos == -1 ? null : jarName.substring(0, pos + 1));
                            }
                            this.lmap.put(urlNoFragString, newLoader);
                        }
                        catch (PrivilegedActionException pae) {
                            continue;
                        }
                        catch (MalformedURLException e) {
                            continue;
                        }
                    }
                    @SuppressWarnings("unused")
					boolean bl = visitedURL = !visited.add(URLUtil.urlNoFragString(url));
                    if (!visitedURL) {
                        try {
                            newLoader.ensureOpen();
                        }
                        catch (IOException e) {
                            throw (InternalError)new InternalError().initCause(e);
                        }
                        JarEntry entry = newLoader.jar.getJarEntry(name);
                        if (entry != null) {
                            return newLoader.checkResource(name, check, entry);
                        }
                        if (!newLoader.validIndex(name)) {
                            throw new InvalidJarIndexException("Invalid index");
                        }
                    }
                    if (visitedURL || newLoader == this || newLoader.getIndex() == null || (res = newLoader.getResource(name, check, visited)) == null) continue;
                    return res;
                }
            } while (count < (jarFilesList = this.index.get(name)).size());
            return null;
        }

        @Override
        URL[] getClassPath() throws IOException {
            String value;
            Manifest man;
            Attributes attr;
            if (this.index != null) {
                return null;
            }
            if (this.metaIndex != null) {
                return null;
            }
            this.ensureOpen();
            this.parseExtensionsDependencies();
            if (SharedSecrets.javaUtilJarAccess().jarFileHasClassPathAttribute(this.jar) && (man = this.jar.getManifest()) != null && (attr = man.getMainAttributes()) != null && (value = attr.getValue(Attributes.Name.CLASS_PATH)) != null) {
                return this.parseClassPath(this.csu, value);
            }
            return null;
        }

        private void parseExtensionsDependencies() throws IOException {
            ExtensionDependency.checkExtensionsDependencies(this.jar);
        }

        private URL[] parseClassPath(URL base, String value) throws MalformedURLException {
            StringTokenizer st = new StringTokenizer(value);
            URL[] urls = new URL[st.countTokens()];
            int i = 0;
            while (st.hasMoreTokens()) {
                String path = st.nextToken();
                urls[i] = new URL(base, path);
                ++i;
            }
            return urls;
        }

    }

    private static class Loader
    implements Closeable {
        private final URL base;
        private JarFile jarfile;

        Loader(URL url) {
            this.base = url;
        }

        URL getBaseURL() {
            return this.base;
        }

        URL findResource(String name, boolean check) {
            URL url;
            try {
                url = new URL(this.base, ParseUtil.encodePath(name, false));
            }
            catch (MalformedURLException e) {
                throw new IllegalArgumentException("name");
            }
            try {
                URLConnection uc;
                if (check) {
                    eURLClassPath.check(url);
                }
                if ((uc = url.openConnection()) instanceof HttpURLConnection) {
                    HttpURLConnection hconn = (HttpURLConnection)uc;
                    hconn.setRequestMethod("HEAD");
                    if (hconn.getResponseCode() >= 400) {
                        return null;
                    }
                } else {
                    InputStream is = url.openStream();
                    is.close();
                }
                return url;
            }
            catch (Exception e) {
                return null;
            }
        }

        Resource getResource(final String name, boolean check) {
            final URLConnection uc;
            final URL url;
            try {
                url = new URL(this.base, ParseUtil.encodePath(name, false));
            }
            catch (MalformedURLException e) {
                throw new IllegalArgumentException("name");
            }
            try {
                if (check) {
                    eURLClassPath.check(url);
                }
                uc = url.openConnection();
//                InputStream in = uc.getInputStream();
                if (uc instanceof JarURLConnection) {
                    JarURLConnection juc = (JarURLConnection)uc;
                    this.jarfile = juc.getJarFile();
                }
            }
            catch (Exception e) {
                return null;
            }
            return new Resource(){

                @Override
                public String getName() {
                    return name;
                }

                @Override
                public URL getURL() {
                    return url;
                }

                @Override
                public URL getCodeSourceURL() {
                    return Loader.this.base;
                }

                @Override
                public InputStream getInputStream() throws IOException {
                    return uc.getInputStream();
                }

                @Override
                public int getContentLength() throws IOException {
                    return uc.getContentLength();
                }
            };
        }

        Resource getResource(String name) {
            return this.getResource(name, true);
        }

        @Override
        public void close() throws IOException {
            if (this.jarfile != null) {
                this.jarfile.close();
            }
        }

        URL[] getClassPath() throws IOException {
            return null;
        }

    }

}

